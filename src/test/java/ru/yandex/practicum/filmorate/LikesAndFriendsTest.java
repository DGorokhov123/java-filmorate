package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LikesAndFriendsTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void LikesAndFriends() {
        jsonPost("/users", "{\"email\":\"oleg@ya.ru\",\"login\":\"olezhe\",\"name\":\"olen oleg\",\"birthday\":\"2003-06-21\"}");
        jsonPost("/users", "{\"email\":\"ivan@ya.ru\",\"login\":\"vanya\",\"name\":\"ivan durak\",\"birthday\":\"2003-06-21\"}");
        jsonPost("/users", "{\"email\":\"anna@ya.ru\",\"login\":\"anka\",\"name\":\"anna pulemet\",\"birthday\":\"2003-06-21\"}");

        jsonPost("/films", "{\"name\":\"Anora\",\"description\":\"has oscar\",\"releaseDate\":\"2024-05-21\",\"duration\":\"PT2H19M\"}");
        jsonPost("/films", "{\"name\":\"Terminator\",\"description\":\"no fate\",\"releaseDate\":\"1984-10-26\",\"duration\":\"PT1H48M\"}");
        jsonPost("/films", "{\"name\":\"Inception\",\"description\":\"leonardo\",\"releaseDate\":\"2010-07-08\",\"duration\":\"PT2H28M\"}");

        // NORMAL FRIENDS

        simplePut("/users/1/friends/2");
        simplePut("/users/3/friends/1");
        assertTrue(simpleGet("/users/1/friends").contains("vanya"));
        assertTrue(simpleGet("/users/1/friends").contains("anka"));

        assertTrue(simpleGet("/users/2/friends").contains("olezhe"));
        assertFalse(simpleGet("/users/2/friends").contains("anka"));

        assertTrue(simpleGet("/users/3/friends").contains("olezhe"));
        assertFalse(simpleGet("/users/2/friends").contains("vanya"));

        assertTrue(simpleGet("/users/2/friends/common/3").contains("olezhe"));

        simpleDelete("/users/1/friends/2");
        simpleDelete("/users/3/friends/1");

        assertFalse(simpleGet("/users/1/friends").contains("vanya"));
        assertFalse(simpleGet("/users/1/friends").contains("anka"));
        assertFalse(simpleGet("/users/2/friends").contains("olezhe"));
        assertFalse(simpleGet("/users/2/friends").contains("anka"));
        assertFalse(simpleGet("/users/3/friends").contains("olezhe"));
        assertFalse(simpleGet("/users/2/friends").contains("vanya"));
        assertFalse(simpleGet("/users/2/friends/common/3").contains("olezhe"));

        // BAD FRIENDS

        String[][] puts = {
                {"/users/1/friends/9", "404"},
                {"/users/9/friends/1", "404"},
                {"/users/f/friends/1", "400"},
                {"/users/1/friends/g", "400"}
        };
        for (int i = 0; i < puts.length; i++) assertEquals(puts[i][1], simplePut(puts[i][0]));

        String[][] dels = {
                {"/users/1/friends/9", "404"},
                {"/users/9/friends/1", "404"},
                {"/users/f/friends/1", "400"},
                {"/users/1/friends/g", "400"}
        };
        for (int i = 0; i < puts.length; i++) assertEquals(dels[i][1], simpleDelete(dels[i][0]));

        String[][] gets = {
                {"/users/9/friends", "NOT_FOUND"},
                {"/users/f/friends", "Illegal"}
        };
        for (int i = 0; i < gets.length; i++) {
            String ans = simpleGet(gets[i][0]);
            assertTrue(ans.contains(gets[i][1]));
        }

        // NORMAL LIKES

        simplePut("/films/1/like/1");
        simplePut("/films/1/like/2");
        simplePut("/films/1/like/3");
        simplePut("/films/2/like/1");

        String ans = simpleGet("/films/popular?count=1");
        assertTrue(ans.contains("Anora"));
        assertFalse(ans.contains("Terminator"));
        assertFalse(ans.contains("Inception"));

        ans = simpleGet("/films/popular?count=2");
        assertTrue(ans.contains("Anora"));
        assertTrue(ans.contains("Terminator"));
        assertFalse(ans.contains("Inception"));

        ans = simpleGet("/films/popular");
        assertTrue(ans.contains("Anora"));
        assertTrue(ans.contains("Terminator"));
        assertTrue(ans.contains("Inception"));

        simpleDelete("/films/1/like/1");
        simpleDelete("/films/1/like/2");
        simpleDelete("/films/1/like/3");

        ans = simpleGet("/films/popular?count=1");
        assertFalse(ans.contains("Anora"));
        assertTrue(ans.contains("Terminator"));
        assertFalse(ans.contains("Inception"));

        // BAD LIKES

        puts = new String[][]{
                {"/films/1/like/9", "404"},
                {"/films/9/like/1", "404"},
                {"/films/1/like/f", "400"},
                {"/films/f/like/1", "400"}
        };
        for (int i = 0; i < puts.length; i++) assertEquals(puts[i][1], simplePut(puts[i][0]));

        dels = new String[][]{
                {"/films/1/like/9", "404"},
                {"/films/9/like/1", "404"},
                {"/films/1/like/f", "400"},
                {"/films/f/like/1", "400"}
        };
        for (int i = 0; i < puts.length; i++) assertEquals(dels[i][1], simpleDelete(dels[i][0]));

        gets = new String[][]{
                {"/films/popular?count=-2", "Illegal"}
        };
        for (int i = 0; i < gets.length; i++) {
            ans = simpleGet(gets[i][0]);
            assertTrue(ans.contains(gets[i][1]));
        }

    }

    String jsonPost(String endPoint, String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(endPoint, HttpMethod.POST, request, String.class);
        return String.valueOf(response.getStatusCode().value());
    }

    String simpleGet(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        return response.getBody();
    }

    String simplePut(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        return String.valueOf(response.getStatusCode().value());
    }

    String simpleDelete(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        return String.valueOf(response.getStatusCode().value());
    }

}

package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.FilmApiDto;

import static org.junit.jupiter.api.Assertions.*;

class LikesAndFriendsTest extends HttpAbstractTest {

    @BeforeEach
    void setUp() {
        assertTrue(checkPost("/users", """
                {
                    "email" : "oleg@ya.ru",
                    "login" : "olezhe",
                    "name" : "olen oleg",
                    "birthday" : "2003-06-21"
                }
                """, 200, "olezhe"));
        assertTrue(checkPost("/users", """
                {
                    "email" : "ivan@ya.ru",
                    "login" : "vanya",
                    "name" : "ivan durak",
                    "birthday" : "2003-06-21"
                }
                """, 200, "vanya"));
        assertTrue(checkPost("/users", """
                {
                    "email" : "anna@ya.ru",
                    "login" : "anka",
                    "name" : "anna pulemet",
                    "birthday" : "2003-06-21"
                }
                """, 200, "anka"));

        assertTrue(checkPost("/films", """
                {
                    "name" : "Anora",
                    "description" : "has oscar",
                    "releaseDate" : "2024-05-21",
                    "duration" : 139
                }
                """, 200, "Anora"));
        assertTrue(checkPost("/films", """
                {
                    "name" : "Terminator",
                    "description" : "no fate",
                    "releaseDate" : "1984-10-26",
                    "duration" : 108
                }
                """, 200, "Terminator"));
        assertTrue(checkPost("/films", """
                {
                    "name" : "Inception",
                    "description" : "leonardo",
                    "releaseDate" : "2010-07-08",
                    "duration" : 148
                }
                """, 200, "Inception"));

    }

    @Test
    void likesAndFriends() {

        // NORMAL FRIENDS

        assertTrue(checkPut("/users/1/friends/2", null, 204, null));
        assertTrue(checkPut("/users/1/friends/3", null, 204, null));

        assertTrue(checkGet("/users/1/friends", 200, "vanya"));
        assertTrue(checkGet("/users/1/friends", 200, "anka"));
        assertFalse(checkGet("/users/2/friends", 200, "olezhe"));
        assertFalse(checkGet("/users/3/friends", 200, "olezhe"));

        assertTrue(checkPut("/users/2/friends/1", null, 204, null));
        assertTrue(checkPut("/users/3/friends/1", null, 204, null));

        assertTrue(checkGet("/users/2/friends", 200, "olezhe"));
        assertTrue(checkGet("/users/3/friends", 200, "olezhe"));

        assertTrue(checkGet("/users/2/friends/common/3", 200, "olezhe"));
        assertTrue(checkGet("/users/3/friends/common/2", 200, "olezhe"));

        assertTrue(checkDelete("/users/1/friends/2", 204, null));
        assertTrue(checkDelete("/users/1/friends/3", 204, null));

        assertFalse(checkGet("/users/1/friends", 200, "vanya"));
        assertFalse(checkGet("/users/1/friends", 200, "anka"));
        assertTrue(checkGet("/users/2/friends", 200, "olezhe"));
        assertFalse(checkGet("/users/2/friends", 200, "anka"));
        assertTrue(checkGet("/users/3/friends", 200, "olezhe"));
        assertFalse(checkGet("/users/3/friends", 200, "vanya"));
        assertTrue(checkGet("/users/2/friends/common/3", 200, "olezhe"));
        assertTrue(checkGet("/users/3/friends/common/2", 200, "olezhe"));


        // BAD FRIENDS

        assertTrue(checkPut("/users/1/friends/9", null, 404, null));
        assertTrue(checkPut("/users/9/friends/1", null, 404, null));
        assertTrue(checkPut("/users/f/friends/1", null, 400, null));
        assertTrue(checkPut("/users/1/friends/g", null, 400, null));

        assertTrue(checkDelete("/users/1/friends/9", 404, null));
        assertTrue(checkDelete("/users/9/friends/1", 404, null));
        assertTrue(checkDelete("/users/f/friends/1", 400, null));
        assertTrue(checkDelete("/users/1/friends/g", 400, null));

        assertTrue(checkGet("/users/9/friends", 404, "NOT_FOUND"));
        assertTrue(checkGet("/users/f/friends", 400, "Illegal"));

        // NORMAL LIKES

        assertTrue(checkPut("/films/1/like/1", null, 204, null));
        assertTrue(checkPut("/films/1/like/2", null, 204, null));
        assertTrue(checkPut("/films/1/like/3", null, 204, null));
        assertTrue(checkPut("/films/2/like/1", null, 204, null));

        try {
            String film1Json = simpleGet("/films/1");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            FilmApiDto film1Dto = mapper.readValue(film1Json, FilmApiDto.class);
            assertTrue(film1Dto.getLikes().contains(1L));
            assertTrue(film1Dto.getLikes().contains(2L));
            assertTrue(film1Dto.getLikes().contains(3L));
        } catch (JsonProcessingException e) {
            assertNull(e);
        }

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

        assertTrue(checkDelete("/films/1/like/1", 204, null));
        assertTrue(checkDelete("/films/1/like/2", 204, null));
        assertTrue(checkDelete("/films/1/like/3", 204, null));

        ans = simpleGet("/films/popular?count=1");
        assertFalse(ans.contains("Anora"));
        assertTrue(ans.contains("Terminator"));
        assertFalse(ans.contains("Inception"));

        // BAD LIKES

        assertTrue(checkPut("/films/1/like/9", null, 404, null));
        assertTrue(checkPut("/films/9/like/1", null, 404, null));
        assertTrue(checkPut("/films/1/like/f", null, 400, null));
        assertTrue(checkPut("/films/f/like/1", null, 400, null));

        assertTrue(checkDelete("/films/1/like/9", 404, null));
        assertTrue(checkDelete("/films/9/like/1", 404, null));
        assertTrue(checkDelete("/films/1/like/f", 400, null));
        assertTrue(checkDelete("/films/f/like/1", 400, null));

        // BAD COUNT

        assertTrue(checkGet("/films/popular?count=-2", 400, "Illegal"));

    }


}

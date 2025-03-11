package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmorateApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;


    /**
     * Endpoint: /users
     */
    @Test
    void usersEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request;
        ResponseEntity<String> response;
        String body;

        // ###### POST ######

        Map<String, String[]> postJsons = new HashMap<>();
        // Bad jsons
        postJsons.put("", new String[]{"400", "Bad Request"});
        postJsons.put("hrtfhyrth", new String[]{"400", "Bad Request"});
        postJsons.put("{}", new String[]{"400", "@Valid"});
        postJsons.put("{\"king\":\"sauron\"}", new String[]{"400", "@Valid"});
        // Empty email + bad email
        postJsons.put("{\"email\":\"ivanya.ru\",\"login\":\"ivandur\",\"name\":\"ivan durak\",\"birthday\":\"2000-02-24\"}",
                new String[]{"400", "@Valid"});
        postJsons.put("{\"email\":\"\",\"login\":\"ivandur\",\"name\":\"ivan durak\",\"birthday\":\"2000-02-24\"}",
                new String[]{"400", "UserEmailValidator"});
        // Empty login + login with spaces
        postJsons.put("{\"email\":\"ivan@ya.ru\",\"login\":\"\",\"name\":\"ivan durak\",\"birthday\":\"2000-02-24\"}",
                new String[]{"400", "@Valid"});
        postJsons.put("{\"email\":\"ivan@ya.ru\",\"login\":\"iva ndur\",\"name\":\"ivan durak\",\"birthday\":\"2000-02-24\"}",
                new String[]{"400", "UserLoginValidator"});
        // Birthday from future
        postJsons.put("{\"email\":\"ivan@ya.ru\",\"login\":\"ivandur\",\"name\":\"ivan durak\",\"birthday\":\"2040-02-24\"}",
                new String[]{"400", "UserBirthdayValidator"});
        // Empty name + without name
        postJsons.put("{\"email\":\"ivan@ya.ru\",\"login\":\"ivandur\",\"name\":\"\",\"birthday\":\"2000-02-24\"}",
                new String[]{"200", "ivandur"});
        postJsons.put("{\"email\":\"sofi@ya.ru\",\"login\":\"sofochka\",\"birthday\":\"2001-04-14\"}",
                new String[]{"200", "sofochka"});
        // Normal json (with id + without id)
        postJsons.put("{\"email\":\"oleg@ya.ru\",\"login\":\"olezhe\",\"name\":\"olen oleg\",\"birthday\":\"2003-06-21\"}",
                new String[]{"200", "olezhe"});
        postJsons.put("{\"id\":1,\"email\":\"otto@ya.ru\",\"login\":\"bismark\",\"name\":\"otto bismark\",\"birthday\":\"1815-04-01\"}",
                new String[]{"200", "bismark"});

        for (Map.Entry<String, String[]> entry : postJsons.entrySet()) {
            request = new HttpEntity<>(entry.getKey(), headers);
            response = restTemplate.exchange("/users", HttpMethod.POST, request, String.class);
            assertEquals(entry.getValue()[0], String.valueOf(response.getStatusCode().value()));
            body = response.getBody();
            assertNotNull(body);
            assertTrue(body.contains(entry.getValue()[1]));
        }

        // ###### PUT ######

        Map<String, String[]> putJsons = new HashMap<>();
        // Bad jsons
        putJsons.put("", new String[]{"400", "Bad Request"});
        putJsons.put("hrtfhyrth", new String[]{"400", "Bad Request"});
        putJsons.put("{}", new String[]{"400", "@Valid"});
        putJsons.put("{\"king\":\"sauron\"}", new String[]{"400", "@Valid"});
        // Empty email + bad email
        putJsons.put("{\"email\":\"ivanya.ru\",\"login\":\"ivandur\",\"name\":\"ivan durak\",\"birthday\":\"2000-02-24\"}",
                new String[]{"400", "@Valid"});
        putJsons.put("{\"email\":\"\",\"login\":\"ivandur\",\"name\":\"ivan durak\",\"birthday\":\"2000-02-24\"}",
                new String[]{"400", "UserEmailValidator"});
        // Empty login + login with spaces
        putJsons.put("{\"email\":\"ivan@ya.ru\",\"login\":\"\",\"name\":\"ivan durak\",\"birthday\":\"2000-02-24\"}",
                new String[]{"400", "@Valid"});
        putJsons.put("{\"email\":\"ivan@ya.ru\",\"login\":\"iva ndur\",\"name\":\"ivan durak\",\"birthday\":\"2000-02-24\"}",
                new String[]{"400", "UserLoginValidator"});
        // Birthday from future
        putJsons.put("{\"email\":\"ivan@ya.ru\",\"login\":\"ivandur\",\"name\":\"ivan durak\",\"birthday\":\"2040-02-24\"}",
                new String[]{"400", "UserBirthdayValidator"});
        // Empty name + without name
        putJsons.put("{\"email\":\"ivan@ya.ru\",\"login\":\"ivandur\",\"name\":\"\",\"birthday\":\"2000-02-24\"}",
                new String[]{"404", "not found"});
        putJsons.put("{\"email\":\"sofi@ya.ru\",\"login\":\"sofochka\",\"birthday\":\"2001-04-14\"}",
                new String[]{"404", "not found"});
        // Normal json (with wrong id + without id)
        putJsons.put("{\"email\":\"oleg@ya.ru\",\"login\":\"olezhe\",\"name\":\"olen oleg\",\"birthday\":\"2003-06-21\"}",
                new String[]{"404", "not found"});
        putJsons.put("{\"id\":1045,\"email\":\"otto@ya.ru\",\"login\":\"bismark\",\"name\":\"otto bismark\",\"birthday\":\"1815-04-01\"}",
                new String[]{"404", "not found"});
        // Normal json with id
        putJsons.put("{\"id\":1,\"email\":\"bobo@ya.ru\",\"login\":\"djbobo\",\"name\":\"dj bobo\",\"birthday\":\"1968-01-05\"}",
                new String[]{"200", "djbobo"});

        for (Map.Entry<String, String[]> entry : putJsons.entrySet()) {
            request = new HttpEntity<>(entry.getKey(), headers);
            response = restTemplate.exchange("/users", HttpMethod.PUT, request, String.class);
            assertEquals(entry.getValue()[0], String.valueOf(response.getStatusCode().value()));
            body = response.getBody();
            assertNotNull(body);
            assertTrue(body.contains(entry.getValue()[1]));
        }

        // ###### GET ALL ######

        request = new HttpEntity<>("", headers);
        response = restTemplate.exchange("/users", HttpMethod.GET, request, String.class);
        assertEquals(200, response.getStatusCode().value());
        body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("djbobo"));
        assertTrue(body.contains("bismark"));
        assertTrue(body.contains("sofochka"));
        assertTrue(body.contains("olezhe"));
        assertFalse(body.contains("ivandur"));

        // ###### GET BY ID ######

        Map<String, String[]> getUrls = new HashMap<>();
        // Bad id
        getUrls.put("/users/f", new String[]{"400", "Bad Request"});
        // Non-existing id
        getUrls.put("/users/-1", new String[]{"404", "not found"});
        getUrls.put("/users/0", new String[]{"404", "not found"});
        getUrls.put("/users/10", new String[]{"404", "not found"});
        // Existing id
        getUrls.put("/users/1", new String[]{"200", "djbobo"});
        getUrls.put("/users/2", new String[]{"200", "sofochka"});
        getUrls.put("/users/3", new String[]{"200", "olezhe"});
        getUrls.put("/users/4", new String[]{"200", "bismark"});

        request = new HttpEntity<>("", headers);
        for (Map.Entry<String, String[]> entry : getUrls.entrySet()) {
            response = restTemplate.exchange(entry.getKey(), HttpMethod.GET, request, String.class);
            assertEquals(entry.getValue()[0], String.valueOf(response.getStatusCode().value()));
            body = response.getBody();
            assertNotNull(body);
            assertTrue(body.contains(entry.getValue()[1]));
        }
    }


    /**
     * Endpoint: /films
     */
    @Test
    void filmsEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request;
        ResponseEntity<String> response;
        String body;

        // ###### POST ######

        Map<String, String[]> postJsons = new HashMap<>();
        // Bad jsons
        postJsons.put("", new String[]{"400", "Bad Request"});
        postJsons.put("hrtfhyrth", new String[]{"400", "Bad Request"});
        postJsons.put("{}", new String[]{"400", "@Valid"});
        postJsons.put("{\"king\":\"sauron\"}", new String[]{"400", "@Valid"});
        // Empty name + blank name
        postJsons.put("{\"description\":\"has oscar\",\"releaseDate\":\"2024-05-21\",\"duration\":\"PT2H19M\"}",
                new String[]{"400", "@Valid"});
        postJsons.put("{\"name\":\" \",\"description\":\"has oscar\",\"releaseDate\":\"2024-05-21\",\"duration\":\"PT2H19M\"}",
                new String[]{"400", "@Valid"});
        // Early release date
        postJsons.put("{\"name\":\"Anora\",\"description\":\"has oscar\",\"releaseDate\":\"1895-05-21\",\"duration\":\"PT2H19M\"}",
                new String[]{"400", "FilmReleaseDateValidator"});
        // Too big description
        postJsons.put("{\"name\":\"Anora\",\"description\":\"The quick brown fox jumps over the lazy dog near the riverbank. " +
                        "Sunny hills bloom with vivid colors, while birds sing sweetly. " +
                        "Time flies fast as the wind carries dreams across the vast, open and blue sky.\"," +
                        "\"releaseDate\":\"2024-05-21\",\"duration\":\"PT2H19M\"}",
                new String[]{"400", "FilmDescriptionValidator"});
        // Zero and negative duration
        postJsons.put("{\"name\":\"Anora\",\"description\":\"has oscar\",\"releaseDate\":\"2024-05-21\",\"duration\":0}",
                new String[]{"400", "FilmDurationValidator"});
        postJsons.put("{\"name\":\"Anora\",\"description\":\"has oscar\",\"releaseDate\":\"2024-05-21\",\"duration\":-10}",
                new String[]{"400", "FilmDurationValidator"});
        // Normal json (with id + without id)
        postJsons.put("{\"name\":\"Anora\",\"description\":\"has oscar\",\"releaseDate\":\"2024-05-21\",\"duration\":\"PT2H19M\"}",
                new String[]{"200", "Anora"});
        postJsons.put("{\"id\":1,\"name\":\"Terminator\",\"description\":\"no fate\",\"releaseDate\":\"1984-10-26\",\"duration\":\"PT1H48M\"}",
                new String[]{"200", "Terminator"});

        for (Map.Entry<String, String[]> entry : postJsons.entrySet()) {
            request = new HttpEntity<>(entry.getKey(), headers);
            response = restTemplate.exchange("/films", HttpMethod.POST, request, String.class);
            assertEquals(entry.getValue()[0], String.valueOf(response.getStatusCode().value()));
            body = response.getBody();
            assertNotNull(body);
            assertTrue(body.contains(entry.getValue()[1]));
        }

        // ###### PUT ######

        Map<String, String[]> putJsons = new HashMap<>();
        // Bad jsons
        putJsons.put("", new String[]{"400", "Bad Request"});
        putJsons.put("hrtfhyrth", new String[]{"400", "Bad Request"});
        putJsons.put("{}", new String[]{"400", "@Valid"});
        putJsons.put("{\"king\":\"sauron\"}", new String[]{"400", "@Valid"});
        // Empty name + blank name
        putJsons.put("{\"id\":1,\"description\":\"has oscar\",\"releaseDate\":\"2024-05-21\",\"duration\":\"PT2H19M\"}",
                new String[]{"400", "@Valid"});
        putJsons.put("{\"id\":1,\"name\":\" \",\"description\":\"has oscar\",\"releaseDate\":\"2024-05-21\",\"duration\":\"PT2H19M\"}",
                new String[]{"400", "@Valid"});
        // Early release date
        putJsons.put("{\"id\":1,\"name\":\"Anora\",\"description\":\"has oscar\",\"releaseDate\":\"1895-05-21\",\"duration\":\"PT2H19M\"}",
                new String[]{"400", "FilmReleaseDateValidator"});
        // Too big description
        putJsons.put("{\"id\":1,\"name\":\"Anora\",\"description\":\"The quick brown fox jumps over the lazy dog near the riverbank. " +
                        "Sunny hills bloom with vivid colors, while birds sing sweetly. " +
                        "Time flies fast as the wind carries dreams across the vast, open and blue sky.\"," +
                        "\"releaseDate\":\"2024-05-21\",\"duration\":\"PT2H19M\"}",
                new String[]{"400", "FilmDescriptionValidator"});
        // Zero and negative duration
        putJsons.put("{\"id\":1,\"name\":\"Anora\",\"description\":\"has oscar\",\"releaseDate\":\"2024-05-21\",\"duration\":0}",
                new String[]{"400", "FilmDurationValidator"});
        putJsons.put("{\"id\":1,\"name\":\"Anora\",\"description\":\"has oscar\",\"releaseDate\":\"2024-05-21\",\"duration\":-10}",
                new String[]{"400", "FilmDurationValidator"});
        // Normal json (with wrong id + without id)
        putJsons.put("{\"id\":9,\"name\":\"Terminator\",\"description\":\"no fate\",\"releaseDate\":\"1984-10-26\",\"duration\":\"PT1H48M\"}",
                new String[]{"404", "not found"});
        putJsons.put("{\"name\":\"Anora\",\"description\":\"has oscar\",\"releaseDate\":\"2024-05-21\",\"duration\":\"PT2H19M\"}",
                new String[]{"404", "not found"});
        // Normal json with id
        putJsons.put("{\"id\":1,\"name\":\"Inception\",\"description\":\"leonardo\",\"releaseDate\":\"2010-07-08\",\"duration\":\"PT2H28M\"}",
                new String[]{"200", "Inception"});

        for (Map.Entry<String, String[]> entry : putJsons.entrySet()) {
            request = new HttpEntity<>(entry.getKey(), headers);
            response = restTemplate.exchange("/films", HttpMethod.PUT, request, String.class);
            assertEquals(entry.getValue()[0], String.valueOf(response.getStatusCode().value()));
            body = response.getBody();
            assertNotNull(body);
            assertTrue(body.contains(entry.getValue()[1]));
        }

        // ###### GET ALL ######

        request = new HttpEntity<>("", headers);
        response = restTemplate.exchange("/films", HttpMethod.GET, request, String.class);
        assertEquals(200, response.getStatusCode().value());
        body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("Terminator"));
        assertTrue(body.contains("Inception"));
        assertFalse(body.contains("Anora"));

        // ###### GET BY ID ######

        Map<String, String[]> getUrls = new HashMap<>();
        // Bad id
        getUrls.put("/films/f", new String[]{"400", "Bad Request"});
        // Non-existing id
        getUrls.put("/films/-1", new String[]{"404", "not found"});
        getUrls.put("/films/0", new String[]{"404", "not found"});
        getUrls.put("/films/10", new String[]{"404", "not found"});
        // Existing id
        getUrls.put("/films/1", new String[]{"200", "Inception"});
        getUrls.put("/films/2", new String[]{"200", "Terminator"});

        request = new HttpEntity<>("", headers);
        for (Map.Entry<String, String[]> entry : getUrls.entrySet()) {
            response = restTemplate.exchange(entry.getKey(), HttpMethod.GET, request, String.class);
            assertEquals(entry.getValue()[0], String.valueOf(response.getStatusCode().value()));
            body = response.getBody();
            assertNotNull(body);
            assertTrue(body.contains(entry.getValue()[1]));
        }
    }


}

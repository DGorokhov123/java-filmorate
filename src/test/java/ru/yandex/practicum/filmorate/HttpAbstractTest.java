package ru.yandex.practicum.filmorate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class HttpAbstractTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    protected boolean checkPost(String endPoint, String json, int code, String search) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(endPoint, HttpMethod.POST, request, String.class);
        if (code != response.getStatusCode().value()) return false;
        if (search == null) return true;
        if (response.getBody() == null) return false;
        return response.getBody().contains(search);
    }

    protected boolean checkPut(String endPoint, String json, int code, String search) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(endPoint, HttpMethod.PUT, request, String.class);
        if (code != response.getStatusCode().value()) return false;
        if (search == null) return true;
        if (response.getBody() == null) return false;
        return response.getBody().contains(search);
    }

    protected boolean checkDelete(String endPoint, int code, String search) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("", headers);
        ResponseEntity<String> response = restTemplate.exchange(endPoint, HttpMethod.DELETE, request, String.class);
        if (code != response.getStatusCode().value()) return false;
        if (search == null) return true;
        if (response.getBody() == null) return false;
        return response.getBody().contains(search);
    }

    protected boolean checkGet(String endPoint, int code, String search) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("", headers);
        ResponseEntity<String> response = restTemplate.exchange(endPoint, HttpMethod.GET, request, String.class);
        if (code != response.getStatusCode().value()) return false;
        if (search == null) return true;
        if (response.getBody() == null) return false;
        return response.getBody().contains(search);
    }

    protected boolean checkGetAll(String endPoint, int code, String[] search) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("", headers);
        ResponseEntity<String> response = restTemplate.exchange(endPoint, HttpMethod.GET, request, String.class);
        if (code != response.getStatusCode().value()) return false;
        if (search == null) return true;
        String body = response.getBody();
        if (body == null) return false;
        for (String s : search) {
            if (!body.contains(s)) return false;
        }
        return true;
    }

    protected String simpleGet(String endPoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("", headers);
        ResponseEntity<String> response = restTemplate.exchange(endPoint, HttpMethod.GET, request, String.class);
        return response.getBody();
    }


}

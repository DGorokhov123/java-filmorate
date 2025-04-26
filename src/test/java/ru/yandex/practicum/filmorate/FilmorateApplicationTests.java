package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmorateApplicationTests extends HttpAbstractTest {


    /**
     * Endpoint: /users
     */
    @Test
    void usersEndpoint() {

        // #################################### POST ##########################################

        assertTrue(checkPost("/users", "", 400, "Illegal Argument"));        // empty json
        assertTrue(checkPost("/users", "hrtfhyrth", 400, "Illegal Argument"));        // random string
        assertTrue(checkPost("/users", "{}", 400, "@Valid"));           // empty object
        assertTrue(checkPost("/users", """
                {
                    "king" : "sauron"
                }
                """, 400, "@Valid"));                          // other object

        assertTrue(checkPost("/users", "{" + """
                    "email" : "ivanya.ru",
                    "login" : "ivandur",
                    "name" : "ivan durak",
                    "birthday" : "2000-02-24"
                }
                """, 400, "@Valid"));                          // bad email

        assertTrue(checkPost("/users", "{" + """
                    "email" : "",
                    "login" : "ivandur",
                    "name" : "ivan durak",
                    "birthday" : "2000-02-24"
                }
                """, 400, "UserEmailValidator"));                // empty email

        assertTrue(checkPost("/users", "{" + """
                    "email" : "ivan@ya.ru",
                    "login" : "",
                    "name" : "ivan durak",
                    "birthday" : "2000-02-24"
                }
                """, 400, "@Valid"));                     // empty login

        assertTrue(checkPost("/users", "{" + """
                    "email" : "ivan@ya.ru",
                    "login" : "iva ndur",
                    "name" : "ivan durak",
                    "birthday" : "2000-02-24"
                }
                """, 400, "UserLoginValidator"));              // login with spaces

        assertTrue(checkPost("/users", "{" + """
                    "email" : "ivan@ya.ru",
                    "login" : "ivandur",
                    "name" : "ivan durak",
                    "birthday" : "2240-02-24"
                }
                """, 400, "UserBirthdayValidator"));              // Birthday from future

        assertTrue(checkPost("/users", "{" + """
                    "email" : "ivan@ya.ru",
                    "login" : "ivandur",
                    "name" : "",
                    "birthday" : "2000-02-24"
                }
                """, 200, "ivandur"));                     // Empty name

        assertTrue(checkPost("/users", "{" + """
                    "email" : "sofi@ya.ru",
                    "login" : "sofochka",
                    "birthday" : "2001-04-14"
                }
                """, 200, "sofochka"));                   // without name

        assertTrue(checkPost("/users", "{" + """
                    "email" : "oleg@ya.ru",
                    "login" : "olezhe",
                    "name" : "olen oleg",
                    "birthday" : "2003-06-21"
                }
                """, 200, "olezhe"));                   // Normal json without id

        assertTrue(checkPost("/users", "{" + """
                    "id" : 1,
                    "email" : "otto@ya.ru",
                    "login" : "bismark",
                    "name" : "otto bismark",
                    "birthday" : "1815-04-01"
                }
                """, 200, "bismark"));                   // Normal json with id


        // #################################### PUT ##########################################

        assertTrue(checkPut("/users", "", 400, "Illegal Argument"));        // empty string
        assertTrue(checkPut("/users", "hrtfhyrth", 400, "Illegal Argument"));    // random string
        assertTrue(checkPut("/users", "{}", 400, "@Valid"));           // empty json

        assertTrue(checkPut("/users", "{" + """
                    "king" : "sauron"
                }
                """, 400, "@Valid"));                   // other json

        assertTrue(checkPut("/users", "{" + """
                    "id" : 1,
                    "email" : "ivanya.ru",
                    "login" : "ivandur",
                    "name" : "ivan durak",
                    "birthday" : "2000-02-24"
                }
                """, 400, "@Valid"));                   // bad email

        assertTrue(checkPut("/users", "{" + """
                    "id" : 1,
                    "email" : "",
                    "login" : "ivandur",
                    "name" : "ivan durak",
                    "birthday" : "2000-02-24"
                }
                """, 400, "UserEmailValidator"));                   // Empty email

        assertTrue(checkPut("/users", "{" + """
                    "id" : 1,
                    "email" : "ivan@ya.ru",
                    "login" : "",
                    "name" : "ivan durak",
                    "birthday" : "2000-02-24"
                }
                """, 400, "@Valid"));                   // Empty login

        assertTrue(checkPut("/users", "{" + """
                    "id" : 1,
                    "email" : "ivan@ya.ru",
                    "login" : "iva ndur",
                    "name" : "ivan durak",
                    "birthday" : "2000-02-24"
                }
                """, 400, "UserLoginValidator"));                   // login with spaces

        assertTrue(checkPut("/users", "{" + """
                    "id" : 1,
                    "email" : "ivan@ya.ru",
                    "login" : "ivandur",
                    "name" : "ivan durak",
                    "birthday" : "2040-02-24"
                }
                """, 400, "UserBirthdayValidator"));                   // Birthday from future

        assertTrue(checkPut("/users", "{" + """
                    "id" : 1,
                    "email" : "ivan@ya.ru",
                    "login" : "ivandur",
                    "name" : "",
                    "birthday" : "2000-02-24"
                }
                """, 200, "ivandur"));                   // Empty name

        assertTrue(checkPut("/users", "{" + """
                    "id" : 2,
                    "email" : "sofi@ya.ru",
                    "login" : "sofochka",
                    "birthday" : "2001-04-14"
                }
                """, 200, "sofochka"));                   // without name

        assertTrue(checkPut("/users", "{" + """
                    "email" : "oleg@ya.ru",
                    "login" : "olezhe",
                    "name" : "olen oleg",
                    "birthday" : "2003-06-21"
                }
                """, 400, "Illegal Argument"));                   // without id

        assertTrue(checkPut("/users", "{" + """
                    "id" : 1045,
                    "email" : "otto@ya.ru",
                    "login" : "bismark",
                    "name" : "otto bismark",
                    "birthday" : "1815-04-01"
                }
                """, 404, "not found"));                   // wrong id

        assertTrue(checkPut("/users", "{" + """
                    "id" : 1,
                    "email" : "bobo@ya.ru",
                    "login" : "djbobo",
                    "name" : "dj bobo",
                    "birthday" : "1968-01-05"
                }
                """, 200, "djbobo"));                   // Normal json with id


        // #################################### GET ALL ##########################################

        String[] searches = {"djbobo", "bismark", "sofochka", "olezhe"};
        assertTrue(checkGetAll("/users", 200, searches));


        // #################################### GET BY ID ##########################################

        // Bad id
        assertTrue(checkGet("/users/f", 400, "Illegal Argument"));

        // Non-existing id
        assertTrue(checkGet("/users/-1", 400, "Illegal Argument"));
        assertTrue(checkGet("/users/0", 400, "Illegal Argument"));
        assertTrue(checkGet("/users/10", 404, "not found"));

        // Existing id
        assertTrue(checkGet("/users/1", 200, "djbobo"));
        assertTrue(checkGet("/users/2", 200, "sofochka"));
        assertTrue(checkGet("/users/3", 200, "olezhe"));
        assertTrue(checkGet("/users/4", 200, "bismark"));


        // #################################### DELETE BY ID ##########################################

        // Bad id
        assertTrue(checkDelete("/users/f", 400, "Illegal Argument"));
        // Non-existing id
        assertTrue(checkDelete("/users/-1", 400, "Illegal Argument"));
        assertTrue(checkDelete("/users/0", 400, "Illegal Argument"));
        assertTrue(checkDelete("/users/10", 404, "not found"));
        // Existing id
        assertTrue(checkDelete("/users/1", 200, "djbobo"));
        assertTrue(checkDelete("/users/2", 200, "sofochka"));
        assertTrue(checkDelete("/users/3", 200, "olezhe"));
        assertTrue(checkDelete("/users/4", 200, "bismark"));
        // Deleted id
        assertTrue(checkDelete("/users/1", 404, "not found"));
        assertTrue(checkDelete("/users/2", 404, "not found"));
        assertTrue(checkDelete("/users/3", 404, "not found"));
        assertTrue(checkDelete("/users/4", 404, "not found"));

    }


    /**
     * Endpoint: /films
     */
    @Test
    void filmsEndpoint() {

        // #################################### POST ##########################################

        assertTrue(checkPost("/films", "", 400, "Illegal Argument"));         // empty string
        assertTrue(checkPost("/films", "hrtfhyrth", 400, "Illegal Argument"));       // random string
        assertTrue(checkPost("/films", "{}", 400, "@Valid"));             // empty json object

        assertTrue(checkPost("/films", "{" + """
                    "king" : "sauron"
                }
                """, 400, "@Valid"));                          // other object

        assertTrue(checkPost("/films", "{" + """
                    "description" : "has oscar",
                    "releaseDate" : "2024-05-21",
                    "duration" : 139
                }
                """, 400, "@Valid"));                          // without name

        assertTrue(checkPost("/films", "{" + """
                   "name" : " ",
                   "description" : "has oscar",
                   "releaseDate" : "2024-05-21",
                   "duration" : 139
                }
                """, 400, "@Valid"));                          // blank name

        assertTrue(checkPost("/films", "{" + """
                    "name" : "Anora",
                    "description" : "has oscar",
                    "releaseDate" : "1895-05-21",
                    "duration" : 139
                }
                """, 400, "FilmReleaseDateValidator"));                          // early date

        assertTrue(checkPost("/films", "{" + """
                    "name" : "Anora",
                    "description" : "The quick brown fox jumps over the lazy dog near the riverbank.\
                                     Sunny hills bloom with vivid colors, while birds sing sweetly.\
                                     Time flies fast as the wind carries dreams across the vast, open and blue sky.",
                    "releaseDate" : "2024-05-21",
                    "duration" : 139
                }
                """, 400, "FilmDescriptionValidator"));                          // long description

        assertTrue(checkPost("/films", "{" + """
                    "name" : "Anora",
                    "description" : "has oscar",
                    "releaseDate" : "2024-05-21",
                    "duration" : -1
                }
                """, 400, "FilmDurationValidator"));                          // negative duration

        assertTrue(checkPost("/films", "{" + """
                    "name" : "Anora",
                    "description" : "has oscar",
                    "releaseDate" : "2024-05-21",
                    "duration" : 139,
                    "mpa": {
                        "id": 500
                    }
                }
                """, 404, "FilmRatingValidator"));                          // rating not found

        assertTrue(checkPost("/films", "{" + """
                    "name" : "Anora",
                    "description" : "has oscar",
                    "releaseDate" : "2024-05-21",
                    "duration" : 139,
                    "genres": [
                        {
                            "id": 600
                        }
                    ]
                }
                """, 404, "FilmGenresValidator"));                          // genre not found

        assertTrue(checkPost("/films", "{" + """
                    "name" : "Anora",
                    "description" : "has oscar",
                    "releaseDate" : "2024-05-21",
                    "duration" : 139
                }
                """, 200, "Anora"));                          // Normal json without id

        assertTrue(checkPost("/films", "{" + """
                    "id" : 1,
                    "name" : "Terminator",
                    "description" : "no fate",
                    "releaseDate" : "1984-10-26",
                    "duration" : 139
                }
                """, 200, "Terminator"));                          // Normal json with id


        // #################################### PUT ##########################################

        assertTrue(checkPut("/films", "", 400, "Illegal Argument"));         // empty string
        assertTrue(checkPut("/films", "hrtfhyrth", 400, "Illegal Argument"));       // random string
        assertTrue(checkPut("/films", "{}", 400, "@Valid"));             // empty json object

        assertTrue(checkPut("/films", "{" + """
                    "king" : "sauron"
                }
                """, 400, "@Valid"));                          // other object

        assertTrue(checkPut("/films", "{" + """
                    "id" : 1,
                    "description" : "has oscar",
                    "releaseDate" : "2024-05-21",
                    "duration" : 139
                }
                """, 400, "@Valid"));                          // without name

        assertTrue(checkPost("/films", "{" + """
                   "id" : 1,
                   "name" : " ",
                   "description" : "has oscar",
                   "releaseDate" : "2024-05-21",
                   "duration" : 139
                }
                """, 400, "@Valid"));                          // blank name

        assertTrue(checkPut("/films", "{" + """
                    "id" : 1,
                    "name" : "Anora",
                    "description" : "has oscar",
                    "releaseDate" : "1895-05-21",
                    "duration" : 139
                }
                """, 400, "FilmReleaseDateValidator"));                          // early date

        assertTrue(checkPut("/films", "{" + """
                    "id" : 1,
                    "name" : "Anora",
                    "description" : "The quick brown fox jumps over the lazy dog near the riverbank.\
                                     Sunny hills bloom with vivid colors, while birds sing sweetly.\
                                     Time flies fast as the wind carries dreams across the vast, open and blue sky.",
                    "releaseDate" : "2024-05-21",
                    "duration" : 139
                }
                """, 400, "FilmDescriptionValidator"));                          // long description

        assertTrue(checkPut("/films", "{" + """
                    "id" : 1,
                    "name" : "Anora",
                    "description" : "has oscar",
                    "releaseDate" : "2024-05-21",
                    "duration" : -1
                }
                """, 400, "FilmDurationValidator"));                          // negative duration

        assertTrue(checkPut("/films", "{" + """
                    "id" : 1,
                    "name" : "Anora",
                    "description" : "has oscar",
                    "releaseDate" : "2024-05-21",
                    "duration" : 139,
                    "mpa": {
                        "id": 500
                    }
                }
                """, 404, "FilmRatingValidator"));                          // rating not found

        assertTrue(checkPut("/films", "{" + """
                    "id" : 1,
                    "name" : "Anora",
                    "description" : "has oscar",
                    "releaseDate" : "2024-05-21",
                    "duration" : 139,
                    "genres": [
                        {
                            "id": 600
                        }
                    ]
                }
                """, 404, "FilmGenresValidator"));                          // genre not found

        assertTrue(checkPut("/films", "{" + """
                    "name" : "Anora",
                    "description" : "has oscar",
                    "releaseDate" : "2024-05-21",
                    "duration" : 139
                }
                """, 400, "Illegal Argument"));                          // without id

        assertTrue(checkPut("/films", "{" + """
                    "id" : 10000,
                    "name" : "Anora",
                    "description" : "has oscar",
                    "releaseDate" : "2024-05-21",
                    "duration" : 139
                }
                """, 404, "not found"));                          // wrong id

        assertTrue(checkPut("/films", "{" + """
                    "id" : 1,
                    "name" : "Inception",
                    "description" : "leonardo",
                    "releaseDate" : "2010-07-08",
                    "duration" : 148
                }
                """, 200, "Inception"));                          // Normal json with id


        // #################################### GET ALL ##########################################

        String[] namesYes = {"Terminator", "Inception"};
        String[] namesNot = {"Anora"};
        assertTrue(checkGetAll("/films", 200, namesYes));
        assertFalse(checkGetAll("/films", 200, namesNot));


        // #################################### GET BY ID ##########################################

        // Bad id
        assertTrue(checkGet("/films/f", 400, "Illegal Argument"));

        // Non-existing id
        assertTrue(checkGet("/films/-1", 400, "Illegal Argument"));
        assertTrue(checkGet("/films/0", 400, "Illegal Argument"));
        assertTrue(checkGet("/films/10", 404, "not found"));

        // Existing id
        assertTrue(checkGet("/films/1", 200, "Inception"));
        assertTrue(checkGet("/films/2", 200, "Terminator"));


        // #################################### DELETE BY ID ##########################################

        // Bad id
        assertTrue(checkDelete("/films/f", 400, "Illegal Argument"));
        // Non-existing id
        assertTrue(checkDelete("/films/-1", 400, "Illegal Argument"));
        assertTrue(checkDelete("/films/0", 400, "Illegal Argument"));
        assertTrue(checkDelete("/films/10", 404, "not found"));
        // Existing id
        assertTrue(checkDelete("/films/1", 200, "Inception"));
        assertTrue(checkDelete("/films/2", 200, "Terminator"));
        // Deleted id
        assertTrue(checkDelete("/films/1", 404, "not found"));
        assertTrue(checkDelete("/films/2", 404, "not found"));

    }


}

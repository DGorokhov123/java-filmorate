package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmServiceTest {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    FilmServiceTest() {
        this.userStorage = new InMemoryUserStorage();
        this.filmStorage = new InMemoryFilmStorage();
        this.filmService = new FilmService(filmStorage, userStorage);
    }

    @BeforeEach
    void setUp() {
        User u1 = new User();
        u1.setName("Ivan");
        u1.setLogin("vanchik");
        u1.setEmail("ivan@ya.ru");
        userStorage.createUser(u1);

        User u2 = new User();
        u2.setName("Mark");
        u2.setLogin("marco");
        u2.setEmail("marik@ya.ru");
        userStorage.createUser(u2);

        Film f1 = new Film();
        f1.setName("Omen");
        f1.setDescription("sweet child");
        filmStorage.createFilm(f1);

        Film f2 = new Film();
        f2.setName("Terminator");
        f2.setDescription("grok 5 released");
        filmStorage.createFilm(f2);

        Film f3 = new Film();
        f3.setName("Titanic");
        f3.setDescription("he sank, she not");
        filmStorage.createFilm(f3);
    }

    @Test
    void likeOps() {
        User u1 = userStorage.getUserById(1L);
        User u2 = userStorage.getUserById(2L);
        Film f1 = filmStorage.getFilmById(1L);
        Film f2 = filmStorage.getFilmById(2L);
        Film f3 = filmStorage.getFilmById(3L);

        filmService.addLike(f1.getId(), u1.getId());
        filmService.addLike(f1.getId(), u2.getId());
        filmService.addLike(f2.getId(), u1.getId());

        assertEquals(2, f1.getLikes().size());
        assertEquals(1, f2.getLikes().size());
        assertEquals(0, f3.getLikes().size());

        List<Film> pops = filmService.getPopular(10);
        assertEquals(3, pops.size());
        assertEquals(f1.getName(), pops.get(0).getName());
        assertEquals(f2.getName(), pops.get(1).getName());
        assertEquals(f3.getName(), pops.get(2).getName());

        filmService.removeLike(f1.getId(), u1.getId());
        filmService.removeLike(f1.getId(), u2.getId());
        filmService.removeLike(f2.getId(), u1.getId());

        assertEquals(0, f1.getLikes().size());
        assertEquals(0, f2.getLikes().size());
        assertEquals(0, f3.getLikes().size());
    }

}

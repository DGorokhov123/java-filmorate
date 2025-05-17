package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.FilmApiDto;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.UserApiDto;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserService.class, UserDbStorage.class,
        FilmService.class, FilmDbStorage.class,
        RatingService.class, RatingDBStorage.class,
        GenreService.class, GenreDBStorage.class,
        EventService.class, EventDbStorage.class,
        DirectorService.class, DirectorDbStorage.class, DirectorRowMapper.class})
class FilmServiceTest {

    private final UserService userService;
    private final FilmService filmService;

    @BeforeEach
    void setUp() {
        UserApiDto u1 = new UserApiDto();
        u1.setName("Ivan");
        u1.setLogin("vanchik");
        u1.setEmail("ivan@ya.ru");
        userService.createUser(u1);

        UserApiDto u2 = new UserApiDto();
        u2.setName("Mark");
        u2.setLogin("marco");
        u2.setEmail("marik@ya.ru");
        userService.createUser(u2);

        FilmApiDto f1 = new FilmApiDto();
        f1.setName("Omen");
        f1.setDescription("sweet child");
        filmService.createFilm(f1);

        FilmApiDto f2 = new FilmApiDto();
        f2.setName("Terminator");
        f2.setDescription("grok 5 released");
        filmService.createFilm(f2);

        FilmApiDto f3 = new FilmApiDto();
        f3.setName("Titanic");
        f3.setDescription("he sank, she not");
        filmService.createFilm(f3);
    }

    @Test
    void likeOps() {
        filmService.addLike(1L, 1L);
        filmService.addLike(1L, 2L);
        filmService.addLike(2L, 1L);

        assertEquals(2, filmService.getFilmById(1L).getLikes().size());
        assertEquals(1, filmService.getFilmById(2L).getLikes().size());
        assertEquals(0, filmService.getFilmById(3L).getLikes().size());

        filmService.removeLike(1L, 1L);
        filmService.removeLike(1L, 2L);
        filmService.removeLike(2L, 1L);

        assertEquals(0, filmService.getFilmById(1L).getLikes().size());
        assertEquals(0, filmService.getFilmById(2L).getLikes().size());
        assertEquals(0, filmService.getFilmById(3L).getLikes().size());
    }

    @Test
    void getFilmsAndPopular() {
        Collection<FilmApiDto> films = filmService.getFilms();
        List<String> names = films.stream().filter(Objects::nonNull).map(FilmApiDto::getName).toList();
        assertEquals(3, names.size());
        assertEquals("Omen", names.get(0));
        assertEquals("Terminator", names.get(1));
        assertEquals("Titanic", names.get(2));

        filmService.addLike(3L, 1L, 6.0);
        filmService.addLike(3L, 2L, 6.2);
        filmService.addLike(2L, 1L, 6.0);

        Collection<FilmApiDto> popular = filmService.getPopular(1000, null, null);
        List<String> popularNames = popular.stream().filter(Objects::nonNull).map(FilmApiDto::getName).toList();
        assertEquals(3, popularNames.size());
        assertEquals("Titanic", popularNames.get(0));
        assertEquals("Terminator", popularNames.get(1));
        assertEquals("Omen", popularNames.get(2));

        popular = filmService.getPopular(2, null, null);
        assertEquals(2, popular.size());

        assertThrows(IllegalArgumentException.class, () -> {
            filmService.getPopular(-1, null, null);
        });
    }

    @Test
    void filmCrudOps() {
        FilmApiDto film = new FilmApiDto();
        film.setName("Anora");
        film.setDescription("Slut story");
        film.setDuration(Duration.of(2, ChronoUnit.HOURS));
        film.setReleaseDate(LocalDate.of(2025, 3, 14));
        Rating mpa = new Rating();
        mpa.setId(1L);
        film.setMpa(mpa);
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        Set<Genre> genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        film.setGenres(genres);
        FilmApiDto createdFilm = filmService.createFilm(film);

        Long id = createdFilm.getId();

        createdFilm = filmService.getFilmById(id);
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getDescription(), createdFilm.getDescription());
        assertEquals(film.getDuration(), createdFilm.getDuration());
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
        assertEquals(film.getMpa().getId(), createdFilm.getMpa().getId());
        assertEquals(film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()),
                createdFilm.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));

        FilmApiDto nextFilm = new FilmApiDto();
        nextFilm.setId(id);
        nextFilm.setName("Borat");
        nextFilm.setDescription("goes to asashay");
        nextFilm.setDuration(Duration.of(3, ChronoUnit.HOURS));
        nextFilm.setReleaseDate(LocalDate.of(2010, 3, 14));
        Rating mpa2 = new Rating();
        mpa2.setId(2L);
        nextFilm.setMpa(mpa2);
        Genre genre3 = new Genre();
        genre3.setId(3L);
        Genre genre4 = new Genre();
        genre4.setId(4L);
        Set<Genre> genres2 = new HashSet<>();
        genres2.add(genre3);
        genres2.add(genre4);
        nextFilm.setGenres(genres2);

        filmService.updateFilm(nextFilm);
        FilmApiDto updatedFilm = filmService.getFilmById(id);
        assertEquals(nextFilm.getId(), updatedFilm.getId());
        assertEquals(nextFilm.getName(), updatedFilm.getName());
        assertEquals(nextFilm.getDescription(), updatedFilm.getDescription());
        assertEquals(nextFilm.getDuration(), updatedFilm.getDuration());
        assertEquals(nextFilm.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(nextFilm.getMpa().getId(), updatedFilm.getMpa().getId());
        assertEquals(nextFilm.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()),
                updatedFilm.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));

        filmService.deleteFilmById(id);
        assertThrows(NotFoundException.class, () -> {
            filmService.deleteFilmById(id);
        });

        // Wrong operations

        film = new FilmApiDto();
        //dto.setName("Bad film");                                  // without name
        film.setDescription("even don't try");
        film.setDuration(Duration.of(24, ChronoUnit.HOURS));
        film.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        film.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        film.setGenres(genres);
        FilmApiDto finalDto = film;
        assertThrows(ValidationException.class, () -> {
            filmService.createFilm(finalDto);
        });

        film = new FilmApiDto();                             // long desc
        film.setName("Bad film");
        film.setDescription("""
                The quick brown fox jumps over the lazy dog near the riverbank.
                Sunny hills bloom with vivid colors, while birds sing sweetly.
                Time flies fast as the wind carries dreams across the vast, open and blue sky.
                """);
        film.setDuration(Duration.of(24, ChronoUnit.HOURS));
        film.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        film.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        film.setGenres(genres);
        FilmApiDto finalDto1 = film;
        assertThrows(ValidationException.class, () -> {
            filmService.createFilm(finalDto1);
        });

        film = new FilmApiDto();
        film.setName("Bad film");
        film.setDescription("even don't try");
        film.setDuration(Duration.of(24, ChronoUnit.HOURS));
        film.setReleaseDate(LocalDate.of(1777, 3, 14));                  // early date
        mpa = new Rating();
        mpa.setId(1L);
        film.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        film.setGenres(genres);
        FilmApiDto finalDto2 = film;
        assertThrows(ValidationException.class, () -> {
            filmService.createFilm(finalDto2);
        });

        film = new FilmApiDto();
        film.setName("Bad film");
        film.setDescription("even don't try");
        film.setDuration(Duration.of(-24, ChronoUnit.HOURS));              // negative duration
        film.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        film.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        film.setGenres(genres);
        FilmApiDto finalDto3 = film;
        assertThrows(ValidationException.class, () -> {
            filmService.createFilm(finalDto3);
        });

        film = new FilmApiDto();
        film.setName("Bad film");
        film.setDescription("even don't try");
        film.setDuration(Duration.of(24, ChronoUnit.HOURS));
        film.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1000L);                                 // unknown rating
        film.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        film.setGenres(genres);
        FilmApiDto finalDto4 = film;
        assertThrows(NotFoundException.class, () -> {
            filmService.createFilm(finalDto4);
        });

        film = new FilmApiDto();
        film.setName("Bad film");
        film.setDescription("even don't try");
        film.setDuration(Duration.of(24, ChronoUnit.HOURS));
        film.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        film.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1000L);                          // unknown genre
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        film.setGenres(genres);
        FilmApiDto finalDto5 = film;
        assertThrows(NotFoundException.class, () -> {
            filmService.createFilm(finalDto5);
        });


        assertThrows(NotFoundException.class, () -> {
            filmService.getFilmById(3000L);                     // unknown id
        });


        film = new FilmApiDto();                       // update without id
        film.setName("Bad film");
        film.setDescription("even don't try");
        film.setDuration(Duration.of(24, ChronoUnit.HOURS));
        film.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        film.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        film.setGenres(genres);
        FilmApiDto finalDto6 = film;
        assertThrows(IllegalArgumentException.class, () -> {
            filmService.updateFilm(finalDto6);
        });


        film = new FilmApiDto();
        film.setId(1L);
        //dto.setName("Bad film");                                      // update without name
        film.setDescription("even don't try");
        film.setDuration(Duration.of(24, ChronoUnit.HOURS));
        film.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        film.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        film.setGenres(genres);
        FilmApiDto finalDto7 = film;
        assertThrows(ValidationException.class, () -> {
            filmService.updateFilm(finalDto7);
        });


        film = new FilmApiDto();
        film.setId(1000L);                                        // wrong id
        film.setName("Bad film");
        film.setDescription("even don't try");
        film.setDuration(Duration.of(24, ChronoUnit.HOURS));
        film.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        film.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        film.setGenres(genres);
        FilmApiDto finalDto8 = film;
        assertThrows(NotFoundException.class, () -> {
            filmService.updateFilm(finalDto8);
        });


        assertThrows(NotFoundException.class, () -> {
            filmService.deleteFilmById(3000L);                     // unknown id
        });


    }

}

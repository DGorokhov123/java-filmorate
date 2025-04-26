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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDBStorage;
import ru.yandex.practicum.filmorate.storage.RatingDBStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserService.class, UserDbStorage.class,
        FilmService.class, FilmDbStorage.class,
        RatingService.class, RatingDBStorage.class,
        GenreService.class, GenreDBStorage.class})
class FilmServiceTest {

    private final UserService userService;
    private final FilmService filmService;

    @BeforeEach
    void setUp() {
        User u1 = new User();
        u1.setName("Ivan");
        u1.setLogin("vanchik");
        u1.setEmail("ivan@ya.ru");
        userService.createUser(u1);

        User u2 = new User();
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
        Collection<FilmApiDto> dtos = filmService.getFilms();
        List<String> names = dtos.stream().filter(Objects::nonNull).map(FilmApiDto::getName).toList();
        assertEquals(3, names.size());
        assertEquals("Omen", names.get(0));
        assertEquals("Terminator", names.get(1));
        assertEquals("Titanic", names.get(2));

        filmService.addLike(3L, 1L);
        filmService.addLike(3L, 2L);
        filmService.addLike(2L, 1L);

        Collection<FilmApiDto> popular = filmService.getPopular(1000);
        List<String> popularNames = popular.stream().filter(Objects::nonNull).map(FilmApiDto::getName).toList();
        assertEquals(3, popularNames.size());
        assertEquals("Titanic", popularNames.get(0));
        assertEquals("Terminator", popularNames.get(1));
        assertEquals("Omen", popularNames.get(2));

        popular = filmService.getPopular(2);
        assertEquals(2, popular.size());

        assertThrows(IllegalArgumentException.class, () -> {
            filmService.getPopular(-1);
        });
    }

    @Test
    void filmCrudOps() {
        FilmApiDto dto = new FilmApiDto();
        dto.setName("Anora");
        dto.setDescription("Slut story");
        dto.setDuration(Duration.of(2, ChronoUnit.HOURS));
        dto.setReleaseDate(LocalDate.of(2025, 3, 14));
        Rating mpa = new Rating();
        mpa.setId(1L);
        dto.setMpa(mpa);
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        List<Genre> genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);
        dto.setGenres(genres);
        FilmApiDto createdFilmDto = filmService.createFilm(dto);

        Long id = createdFilmDto.getId();

        createdFilmDto = filmService.getFilmById(id);
        assertEquals(dto.getName(), createdFilmDto.getName());
        assertEquals(dto.getDescription(), createdFilmDto.getDescription());
        assertEquals(dto.getDuration(), createdFilmDto.getDuration());
        assertEquals(dto.getReleaseDate(), createdFilmDto.getReleaseDate());
        assertEquals(dto.getMpa().getId(), createdFilmDto.getMpa().getId());
        for (int i = 0; i < dto.getGenres().size(); i++) {
            assertEquals(dto.getGenres().get(i).getId(), createdFilmDto.getGenres().get(i).getId());
        }

        FilmApiDto nextDto = new FilmApiDto();
        nextDto.setId(id);
        nextDto.setName("Borat");
        nextDto.setDescription("goes to asashay");
        nextDto.setDuration(Duration.of(3, ChronoUnit.HOURS));
        nextDto.setReleaseDate(LocalDate.of(2010, 3, 14));
        Rating mpa2 = new Rating();
        mpa2.setId(2L);
        nextDto.setMpa(mpa2);
        Genre genre3 = new Genre();
        genre3.setId(3L);
        Genre genre4 = new Genre();
        genre4.setId(4L);
        List<Genre> genres2 = new ArrayList<>();
        genres2.add(genre3);
        genres2.add(genre4);
        nextDto.setGenres(genres2);

        filmService.updateFilm(nextDto);
        FilmApiDto updatedFilmDto = filmService.getFilmById(id);
        assertEquals(nextDto.getId(), updatedFilmDto.getId());
        assertEquals(nextDto.getName(), updatedFilmDto.getName());
        assertEquals(nextDto.getDescription(), updatedFilmDto.getDescription());
        assertEquals(nextDto.getDuration(), updatedFilmDto.getDuration());
        assertEquals(nextDto.getReleaseDate(), updatedFilmDto.getReleaseDate());
        assertEquals(nextDto.getMpa().getId(), updatedFilmDto.getMpa().getId());
        for (int i = 0; i < dto.getGenres().size(); i++) {
            assertEquals(nextDto.getGenres().get(i).getId(), updatedFilmDto.getGenres().get(i).getId());
        }

        filmService.deleteFilmById(id);
        assertThrows(NotFoundException.class, () -> {
            filmService.deleteFilmById(id);
        });

        // Wrong operations

        dto = new FilmApiDto();
        //dto.setName("Bad film");                                  // without name
        dto.setDescription("even don't try");
        dto.setDuration(Duration.of(24, ChronoUnit.HOURS));
        dto.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        dto.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);
        dto.setGenres(genres);
        FilmApiDto finalDto = dto;
        assertThrows(ValidationException.class, () -> {
            filmService.createFilm(finalDto);
        });

        dto = new FilmApiDto();                             // long desc
        dto.setName("Bad film");
        dto.setDescription("""
                The quick brown fox jumps over the lazy dog near the riverbank.
                Sunny hills bloom with vivid colors, while birds sing sweetly.
                Time flies fast as the wind carries dreams across the vast, open and blue sky.
                """);
        dto.setDuration(Duration.of(24, ChronoUnit.HOURS));
        dto.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        dto.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);
        dto.setGenres(genres);
        FilmApiDto finalDto1 = dto;
        assertThrows(ValidationException.class, () -> {
            filmService.createFilm(finalDto1);
        });

        dto = new FilmApiDto();
        dto.setName("Bad film");
        dto.setDescription("even don't try");
        dto.setDuration(Duration.of(24, ChronoUnit.HOURS));
        dto.setReleaseDate(LocalDate.of(1777, 3, 14));                  // early date
        mpa = new Rating();
        mpa.setId(1L);
        dto.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);
        dto.setGenres(genres);
        FilmApiDto finalDto2 = dto;
        assertThrows(ValidationException.class, () -> {
            filmService.createFilm(finalDto2);
        });

        dto = new FilmApiDto();
        dto.setName("Bad film");
        dto.setDescription("even don't try");
        dto.setDuration(Duration.of(-24, ChronoUnit.HOURS));              // negative duration
        dto.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        dto.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);
        dto.setGenres(genres);
        FilmApiDto finalDto3 = dto;
        assertThrows(ValidationException.class, () -> {
            filmService.createFilm(finalDto3);
        });

        dto = new FilmApiDto();
        dto.setName("Bad film");
        dto.setDescription("even don't try");
        dto.setDuration(Duration.of(24, ChronoUnit.HOURS));
        dto.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1000L);                                 // unknown rating
        dto.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);
        dto.setGenres(genres);
        FilmApiDto finalDto4 = dto;
        assertThrows(NotFoundException.class, () -> {
            filmService.createFilm(finalDto4);
        });

        dto = new FilmApiDto();
        dto.setName("Bad film");
        dto.setDescription("even don't try");
        dto.setDuration(Duration.of(24, ChronoUnit.HOURS));
        dto.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        dto.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1000L);                          // unknown genre
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);
        dto.setGenres(genres);
        FilmApiDto finalDto5 = dto;
        assertThrows(NotFoundException.class, () -> {
            filmService.createFilm(finalDto5);
        });


        assertThrows(NotFoundException.class, () -> {
            filmService.getFilmById(3000L);                     // unknown id
        });


        dto = new FilmApiDto();                       // update without id
        dto.setName("Bad film");
        dto.setDescription("even don't try");
        dto.setDuration(Duration.of(24, ChronoUnit.HOURS));
        dto.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        dto.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);
        dto.setGenres(genres);
        FilmApiDto finalDto6 = dto;
        assertThrows(IllegalArgumentException.class, () -> {
            filmService.updateFilm(finalDto6);
        });


        dto = new FilmApiDto();
        dto.setId(1L);
        //dto.setName("Bad film");                                      // update without name
        dto.setDescription("even don't try");
        dto.setDuration(Duration.of(24, ChronoUnit.HOURS));
        dto.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        dto.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);
        dto.setGenres(genres);
        FilmApiDto finalDto7 = dto;
        assertThrows(ValidationException.class, () -> {
            filmService.updateFilm(finalDto7);
        });


        dto = new FilmApiDto();
        dto.setId(1000L);                                        // wrong id
        dto.setName("Bad film");
        dto.setDescription("even don't try");
        dto.setDuration(Duration.of(24, ChronoUnit.HOURS));
        dto.setReleaseDate(LocalDate.of(1977, 3, 14));
        mpa = new Rating();
        mpa.setId(1L);
        dto.setMpa(mpa);
        genre1 = new Genre();
        genre1.setId(1L);
        genre2 = new Genre();
        genre2.setId(2L);
        genres = new ArrayList<>();
        genres.add(genre1);
        genres.add(genre2);
        dto.setGenres(genres);
        FilmApiDto finalDto8 = dto;
        assertThrows(NotFoundException.class, () -> {
            filmService.updateFilm(finalDto8);
        });


        assertThrows(NotFoundException.class, () -> {
            filmService.deleteFilmById(3000L);                     // unknown id
        });


    }

}

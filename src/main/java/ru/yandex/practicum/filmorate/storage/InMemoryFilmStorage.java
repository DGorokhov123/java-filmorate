package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new ConcurrentHashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = films.get(id);
        if (film == null) throw new NotFoundException("Film not found", id);
        return film;
    }

    @Override
    public void checkFilmById(Long id) {
        if (!films.containsKey(id)) throw new NotFoundException("Film not found", id);
    }

    @Override
    public Film deleteFilmById(Long id) {
        Film film = films.get(id);
        films.remove(id);
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Film oldFilm = films.get(film.getId());
        if (oldFilm == null) throw new NotFoundException("Film not found", film);
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());
        return oldFilm;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        film.getLikes().add(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        return getFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .toList();
    }

    private Long getNextId() {
        return films.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }


    //add-director feature
    @Override
    public Collection<Film> getDirectorFilm(Integer id, String sortBy) {
        return null;
    }


}

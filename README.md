# java-filmorate
Template repository for Filmorate project.

![DB schema](/schema.png)

### Table "users"
```declarative
user_id        // bigint, Primary Key, autoincrement, not null
email          // varchar(50), unique, not null
login          // varchar(50), unique, not null
name           // varchar(50), not null
birthday       // date
```
### Table "friends"
```declarative
user_id        // bigint, not null, Foreign Key -> users.user_id
friend_id      // bigint, not null, Foreign Key -> users.user_id
  // + Unique key (user_id, friend_id)
```
### Table "films"
```declarative
film_id        // bigint, Primary Key, autoincrement, not null
name           // varchar(50), not null
description    // varchar(200)
release_date   // date
duration       // bigint
```
### Table "likes"
```declarative
user_id        // bigint, not null, Foreign Key -> users.user_id
film_id        // bigint, not null, Foreign Key -> films.film_id
  // + Unique key (user_id, film_id)
```
### Table "genres"
```declarative
genre_id       // bigint, Primary Key, autoincrement, not null
name           // varchar(50), not null
```
### Table "film_genres"
```declarative
film_id        // bigint, not null, Foreign Key -> films.film_id
genre_id       // bigint, not null, Foreign Key -> genres.genre_id
  // + Unique key (user_id, film_id)
```
### Table "ratings"
```declarative
rating_id      // bigint, Primary Key, autoincrement, not null
name           // varchar(50), not null
```
### Table "film_ratings"
```declarative
film_id        // bigint, not null, Foreign Key -> films.film_id
rating_id      // bigint, not null, Foreign Key -> ratings.rating_id
  // + Unique key (user_id, film_id)
```

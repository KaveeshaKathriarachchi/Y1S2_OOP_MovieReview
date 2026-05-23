# CineRent Movie Platform

This workspace contains a synchronized movie rental platform with a Spring Boot backend and a plain HTML/CSS/JS frontend.



## Main Updates Added Beyond the Supplied Segments

- Replaced file-based login with MySQL-backed users through JPA.
- Added a unified `users` table with `userId`, `name`, `email`, encrypted `passwordHash`, `paid`, and `role`.
- Added paid/non-paid access rules:
  - Non-paid users can view movies, banners, trailers, ratings, contributors, and reviews.
  - Paid users can also load full movies and write/update/delete reviews.
- Added payment processing that records payment rows and updates `users.paid` to `yes` on success or `no` on failure.
- Added a shared login endpoint for users and admins.
- Added profile view and profile edit support.
- Added admin-only movie create, update, and delete using the `X-Admin-User` request header.
- Added contributor CRUD using the existing contributor idea, now organized under `/api/contributors`.
- Added startup sample data:
  - Admin login: `admin` / `admin123`
  - Sample movies and contributors for first-run testing.


## MySQL Database

The backend uses:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/realistic_user
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

Update these values in `backend/src/main/resources/application.properties` if your local MySQL username or password is different.

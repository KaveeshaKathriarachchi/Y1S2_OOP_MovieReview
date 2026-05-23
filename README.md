# CineRent Movie Platform

This workspace contains a synchronized movie rental platform with a Spring Boot backend and a plain HTML/CSS/JS frontend.

## Structure

```text
backend/
  src/main/java/com/movieplatform/api/
    config/        CORS, password encoder, sample data
    controller/    REST API controllers
    dto/           Request and response objects
    model/         JPA database entities
    repository/    Spring Data repositories
    service/       Business rules
  src/main/resources/application.properties
frontend/
  index.html       Landing page with login, sign-up, contributors, and linked movie banners
  signup.html      User registration with paid-customer option
  login.html       Shared user/admin login
  payment.html     Payment success/failure flow
  movies.html      Profile, movie access, trailers, reviews, paid movie loading
  contributors.html
  admin.html       Add/update/delete movies
docs/
  SPRING_BOOT_MOVIE_API_MAPPING_REPORT.md
```

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

## Run Backend

```powershell
cd backend
mvn spring-boot:run
```

The API runs at `http://localhost:8080/api`.

## Open Frontend

Open `frontend/index.html` in the browser. The frontend calls `http://localhost:8080/api`.

## MySQL Database

The backend uses:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/realistic_user?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=0112413740Kavee
spring.jpa.hibernate.ddl-auto=update
```

Update these values in `backend/src/main/resources/application.properties` if your local MySQL username or password is different.

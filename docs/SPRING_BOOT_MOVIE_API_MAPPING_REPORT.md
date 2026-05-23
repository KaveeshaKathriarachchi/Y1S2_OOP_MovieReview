# Spring Boot Movie API Mapping Report

## Authentication API

| Method | Endpoint | Controller | Purpose | Database Effect |
|---|---|---|---|---|
| POST | `/api/auth/register` | `AuthController.register` | Registers a new user from sign-up page. | Inserts into `users` with `paid = no` by default. |
| POST | `/api/auth/login` | `AuthController.login` | Logs in normal users and admins using user ID and password. | Reads from `users`; returns role and paid status. |

## User/Profile API

| Method | Endpoint | Controller | Purpose | Database Effect |
|---|---|---|---|---|
| GET | `/api/users/{userId}` | `UserController.getProfile` | Loads profile details for the top-left profile panel. | Reads one row from `users`. |
| PUT | `/api/users/{userId}` | `UserController.updateProfile` | Allows user to edit name, email, and optional password. | Updates `users`. |
| GET | `/api/users` | `UserController.allUsers` | Lists users for admin/reporting use. | Reads all rows from `users`. |

## Payment API

| Method | Endpoint | Controller | Purpose | Database Effect |
|---|---|---|---|---|
| POST | `/api/payments/process` | `PaymentController.process` | Processes success/failure payment flow. | Inserts into `payments`; updates `users.paid` to `yes` on success or `no` on failure. |
| GET | `/api/payments` | `PaymentController.allPayments` | Lists all payments. | Reads all rows from `payments`. |
| GET | `/api/payments/user/{userId}` | `PaymentController.paymentsByUser` | Lists payments for one user. | Reads matching rows from `payments`. |

## Movie API

| Method | Endpoint | Controller | Purpose | Access Rule | Database Effect |
|---|---|---|---|---|---|
| GET | `/api/movies` | `MovieController.allMovies` | Shows movie list, banners, trailers, ratings, and metadata. | All logged-in users can use it. | Reads all rows from `movies`. |
| GET | `/api/movies/{imdbId}` | `MovieController.getMovie` | Shows one movie's details. | All logged-in users can use it. | Reads one row from `movies`. |
| GET | `/api/movies/genre/{genre}` | `MovieController.byGenre` | Filters movies by genre. | All logged-in users can use it. | Reads `movies` and `movie_genres`. |
| GET | `/api/movies/release-date/{releaseDate}` | `MovieController.byReleaseDate` | Filters movies by release date. | All logged-in users can use it. | Reads rows from `movies`. |
| GET | `/api/movies/{imdbId}/watch/{userId}` | `MovieController.watchMovie` | Loads full movie link. | Paid users only. | Reads `users` and `movies`. |
| POST | `/api/movies` | `MovieController.addMovie` | Admin adds a new movie. | Admin only through `X-Admin-User`. | Inserts into `movies`, `movie_genres`, and `movie_backdrops`. |
| PUT | `/api/movies/{imdbId}` | `MovieController.updateMovie` | Admin updates movie details. | Admin only through `X-Admin-User`. | Updates movie tables. |
| DELETE | `/api/movies/{imdbId}` | `MovieController.deleteMovie` | Admin deletes movie. | Admin only through `X-Admin-User`. | Deletes movie row and related element collections. |

## Review API

| Method | Endpoint | Controller | Purpose | Access Rule | Database Effect |
|---|---|---|---|---|---|
| POST | `/api/reviews/{userId}` | `ReviewController.create` | Paid user writes a review while watching/browsing. | Paid users only. | Inserts into `reviews`. |
| GET | `/api/reviews/movie/{movieId}` | `ReviewController.byMovie` | Shows uploaded reviews for a movie to all users. | All users can view. | Reads matching rows from `reviews`. |
| GET | `/api/reviews/user/{userId}` | `ReviewController.byUser` | Shows reviews written by one user. | All users can view. | Reads matching rows from `reviews`. |
| PUT | `/api/reviews/{reviewId}/{userId}` | `ReviewController.update` | Paid user edits their own review. | Paid users only; own review only. | Updates one row in `reviews`. |
| DELETE | `/api/reviews/{reviewId}/{userId}` | `ReviewController.delete` | Paid user deletes their own review. | Paid users only; own review only. | Deletes one row from `reviews`. |

## Contributor API

| Method | Endpoint | Controller | Purpose | Database Effect |
|---|---|---|---|---|
| POST | `/api/contributors` | `ContributorController.add` | Adds actor/director/contributor. | Inserts into `contributors`. |
| GET | `/api/contributors` | `ContributorController.all` | Lists contributors for frontend display. | Reads all rows from `contributors`. |
| PUT | `/api/contributors/{id}` | `ContributorController.update` | Updates contributor details. | Updates one row in `contributors`. |
| DELETE | `/api/contributors/{id}` | `ContributorController.delete` | Deletes a contributor. | Deletes one row from `contributors`. |

## Recommended Dependencies

### Backend Movie Platform Module

Use these dependencies in `backend/pom.xml`:

| Dependency | Why It Is Used |
|---|---|
| `spring-boot-starter-web` | REST controllers, JSON API, embedded Tomcat. |
| `spring-boot-starter-data-jpa` | MySQL entity mapping and repository support. |
| `spring-boot-starter-validation` | Validates request bodies such as login, register, payment, and review requests. |
| `spring-security-crypto` | BCrypt password hashing without forcing full Spring Security login flow. |
| `mysql-connector-j` | Connects Spring Boot to MySQL. |
| `h2` with test scope | Lightweight database for future automated tests. |
| `spring-boot-starter-test` | Unit and integration testing. |

### Frontend Module

No build tool is required. The frontend uses plain:

| Technology | Why It Is Used |
|---|---|
| HTML | Page structure. |
| CSS | Responsive layout and visual styling. |
| JavaScript Fetch API | Calls the backend REST API. |
| Local Storage | Keeps the logged-in user's current browser session details. |

## Suggested Future Hardening

- Replace the simple `X-Admin-User` header with JWT or Spring Security sessions before production.
- Add payment gateway integration instead of the demo success/failure buttons.
- Add automated tests for paid access, admin movie management, and review restrictions.
- Move database passwords into environment variables.

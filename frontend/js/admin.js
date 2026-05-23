const adminForm = document.querySelector("#movieAdminForm");
const adminMessage = document.querySelector("#adminMessage");

function moviePayload() {
    const form = new FormData(adminForm);
    return {
        adminUserId: form.get("adminUserId"),
        movie: {
            imdbId: form.get("imdbId"),
            title: form.get("title"),
            releaseDate: form.get("releaseDate") || null,
            trailerLink: form.get("trailerLink"),
            movieLink: form.get("movieLink"),
            poster: form.get("poster"),
            banner: form.get("banner"),
            rating: form.get("rating") ? Number(form.get("rating")) : null,
            genres: form.get("genres").split(",").map(value => value.trim()).filter(Boolean),
            backdrops: []
        }
    };
}

adminForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const payload = moviePayload();
    try {
        await apiRequest("/movies", {
            method: "POST",
            headers: { "X-Admin-User": payload.adminUserId },
            body: JSON.stringify(payload.movie)
        });
        adminMessage.textContent = "Movie added successfully.";
    } catch (error) {
        adminMessage.textContent = error.message;
    }
});

document.querySelector("#updateMovieButton").addEventListener("click", async () => {
    const payload = moviePayload();
    try {
        await apiRequest(`/movies/${payload.movie.imdbId}`, {
            method: "PUT",
            headers: { "X-Admin-User": payload.adminUserId },
            body: JSON.stringify(payload.movie)
        });
        adminMessage.textContent = "Movie updated successfully.";
    } catch (error) {
        adminMessage.textContent = error.message;
    }
});

document.querySelector("#deleteMovieButton").addEventListener("click", async () => {
    const payload = moviePayload();
    try {
        await apiRequest(`/movies/${payload.movie.imdbId}`, {
            method: "DELETE",
            headers: { "X-Admin-User": payload.adminUserId }
        });
        adminMessage.textContent = "Movie deleted successfully.";
    } catch (error) {
        adminMessage.textContent = error.message;
    }
});

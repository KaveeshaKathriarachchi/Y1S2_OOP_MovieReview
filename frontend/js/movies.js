const previewMode = new URLSearchParams(window.location.search).get("preview") === "1";
const user = currentUser() || (previewMode ? {
    userId: "preview-user",
    name: "Preview User",
    email: "preview@cinerent.local",
    paid: "no",
    role: "USER"
} : null);
const movieList = document.querySelector("#movieList");
const template = document.querySelector("#movieCardTemplate");
const profilePanel = document.querySelector("#profilePanel");
const profileDetails = document.querySelector("#profileDetails");
const profileForm = document.querySelector("#profileForm");
const profilePayButton = document.querySelector("#profilePayButton");
const genreFilters = document.querySelector("#genreFilters");
const movieSearch = document.querySelector("#movieSearch");
const theaterModal = document.querySelector("#theaterModal");
const streamFrame = document.querySelector("#streamFrame");

let allMovies = [];
let selectedGenre = "All";
let totalVisibleReviews = 0;

if (!user) {
    window.location.href = "login.html";
}

document.documentElement.dataset.theme = localStorage.getItem("cineTheme") || "dark";
document.querySelector("#profileButton").textContent = user?.name?.slice(0, 1).toUpperCase() || "P";
document.querySelector("#accessNote").textContent = user?.paid === "yes"
    ? "Paid access enabled: stream movies, watch trailers, read reviews, and write your own reviews."
    : "Free access: trailers, banners, ratings, contributors, and paid-user reviews. Upgrade to stream full movies.";

document.querySelector("#paidShortcut").classList.toggle("hidden", user?.paid === "yes");
document.querySelector("#paidShortcut").href = `payment.html?userId=${encodeURIComponent(user?.userId || "")}`;

document.querySelector("#themeButton").addEventListener("click", () => {
    const nextTheme = document.documentElement.dataset.theme === "light" ? "dark" : "light";
    document.documentElement.dataset.theme = nextTheme;
    localStorage.setItem("cineTheme", nextTheme);
});

document.querySelector("#logoutButton").addEventListener("click", () => {
    clearUser();
    window.location.href = "index.html";
});

document.querySelector("#profileButton").addEventListener("click", () => {
    profilePanel.classList.toggle("hidden");
    renderProfile();
});

document.querySelector("#closeTheater").addEventListener("click", closeTheater);
theaterModal.addEventListener("click", (event) => {
    if (event.target === theaterModal) closeTheater();
});

movieSearch.addEventListener("input", renderMovies);

profileForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(profileForm).entries());
    try {
        const updated = await apiRequest(`/users/${user.userId}`, {
            method: "PUT",
            body: JSON.stringify(payload)
        });
        Object.assign(user, updated);
        saveUser(user);
        renderProfile();
    } catch (error) {
        profileDetails.innerHTML += `<p class="message">${error.message}</p>`;
    }
});

function renderProfile() {
    profileDetails.innerHTML = `
        <p><strong>User ID:</strong> ${escapeHtml(user.userId)}</p>
        <p><strong>Name:</strong> ${escapeHtml(user.name)}</p>
        <p><strong>Email:</strong> ${escapeHtml(user.email)}</p>
        <p><strong>Paid:</strong> ${escapeHtml(user.paid)}</p>
        <p><strong>Role:</strong> ${escapeHtml(user.role)}</p>
    `;
    profileForm.elements.name.value = user.name;
    profileForm.elements.email.value = user.email;
    profilePayButton.classList.toggle("hidden", user.paid === "yes");
    profilePayButton.href = `payment.html?userId=${encodeURIComponent(user.userId)}`;
}

async function loadMovies() {
    try {
        allMovies = await apiRequest("/movies");
    } catch (error) {
        if (!previewMode) throw error;
        allMovies = previewMovies();
    }
    renderGenreFilters();
    renderMovies();
}

function renderGenreFilters() {
    const genres = uniqueGenres();
    genreFilters.innerHTML = ["All", ...genres].map(genre => `
        <button class="genre-pill ${genre === selectedGenre ? "active" : ""}" type="button" data-genre="${escapeHtml(genre)}">
            ${escapeHtml(genre)}
        </button>
    `).join("");

    genreFilters.querySelectorAll(".genre-pill").forEach(button => {
        button.addEventListener("click", () => {
            selectedGenre = button.dataset.genre;
            renderGenreFilters();
            renderMovies();
        });
    });
}

function renderMovies() {
    const query = movieSearch.value.trim().toLowerCase();
    const filtered = allMovies.filter(movie => {
        const genres = movie.genres || [];
        const matchesGenre = selectedGenre === "All" || genres.some(genre => genre.toLowerCase() === selectedGenre.toLowerCase());
        const searchable = [movie.title, movie.releaseDate, ...genres].join(" ").toLowerCase();
        return matchesGenre && searchable.includes(query);
    });

    movieList.innerHTML = "";
    if (!filtered.length) {
        movieList.innerHTML = `<div class="empty-state">No movies match this selection.</div>`;
    }

    filtered.forEach(renderMovie);
    updateStats(filtered);
}

function renderMovie(movie) {
    const node = template.content.cloneNode(true);
    const card = node.querySelector(".movie-card");
    card.dataset.id = movie.imdbId;

    node.querySelector(".poster-image").src = movie.poster || movie.banner || "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=900&q=80";
    node.querySelector(".poster-image").alt = `${movie.title} poster`;
    node.querySelector(".rating-badge").textContent = `★ ${movie.rating || "N/A"}`;
    node.querySelector(".card-title").textContent = movie.title;
    node.querySelector(".release").textContent = movie.releaseDate ? `Released ${movie.releaseDate}` : "Release date not added";
    node.querySelector(".card-chips").innerHTML = (movie.genres || [])
        .map(genre => `<span class="card-chip">${escapeHtml(genre)}</span>`)
        .join("");

    const watchlistButton = node.querySelector(".watchlist-trigger");
    watchlistButton.classList.toggle("active", isWatchlisted(movie.imdbId));
    watchlistButton.addEventListener("click", () => toggleWatchlist(movie.imdbId, watchlistButton));

    const trailerButton = node.querySelector(".trailer");
    trailerButton.addEventListener("click", () => openTheater(movie.trailerLink || "https://www.youtube.com/"));

    const watchButton = node.querySelector(".watch");
    const reviewForm = node.querySelector(".review-form");
    if (user.paid === "yes") {
        reviewForm.classList.remove("hidden");
    } else {
        watchButton.disabled = true;
        watchButton.textContent = "Paid Only";
    }

    watchButton.addEventListener("click", async () => {
        try {
            const fullMovie = await apiRequest(`/movies/${movie.imdbId}/watch/${user.userId}`);
            openTheater(fullMovie.movieLink || fullMovie.trailerLink || "https://www.youtube.com/");
        } catch (error) {
            alert(error.message);
        }
    });

    reviewForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const form = new FormData(reviewForm);
        await apiRequest(`/reviews/${user.userId}`, {
            method: "POST",
            body: JSON.stringify({
                movieId: movie.imdbId,
                rating: Number(form.get("rating")),
                reviewComment: form.get("reviewComment")
            })
        });
        reviewForm.reset();
        loadReviews(movie.imdbId, card.querySelector(".reviews"));
    });

    movieList.appendChild(node);
    loadReviews(movie.imdbId, card.querySelector(".reviews"));
}

async function loadReviews(movieId, target) {
    try {
        const reviews = await apiRequest(`/reviews/movie/${movieId}`);
        totalVisibleReviews += reviews.length;
        document.querySelector("#statReviews").textContent = totalVisibleReviews;
        target.innerHTML = reviews.length
            ? reviews.slice(0, 3).map(review => `
                <div class="review-item">
                    <strong>${review.rating}/5</strong>
                    <p>${escapeHtml(review.reviewComment)}</p>
                    <small>${escapeHtml(review.userId)}</small>
                </div>
            `).join("")
            : "<p class='muted'>No reviews yet.</p>";
    } catch {
        if (!previewMode) {
            target.innerHTML = "<p class='muted'>Reviews unavailable.</p>";
            return;
        }
        const reviews = previewReviews(movieId);
        totalVisibleReviews += reviews.length;
        document.querySelector("#statReviews").textContent = totalVisibleReviews;
        target.innerHTML = reviews.map(review => `
            <div class="review-item">
                <strong>${review.rating}/5</strong>
                <p>${escapeHtml(review.reviewComment)}</p>
                <small>${escapeHtml(review.userId)}</small>
            </div>
        `).join("");
    }
}

function updateStats(filteredMovies) {
    totalVisibleReviews = 0;
    document.querySelector("#statMovies").textContent = filteredMovies.length;
    document.querySelector("#statReviews").textContent = "0";
    document.querySelector("#statGenres").textContent = uniqueGenres().length;
    document.querySelector("#statAccess").textContent = user.paid === "yes" ? "Paid" : "Free";
}

function uniqueGenres() {
    return [...new Set(allMovies.flatMap(movie => movie.genres || []))].sort();
}

function openTheater(url) {
    streamFrame.innerHTML = `<iframe src="${toEmbeddableUrl(url)}" title="Movie player" allowfullscreen></iframe>`;
    theaterModal.classList.add("active");
    theaterModal.setAttribute("aria-hidden", "false");
}

function closeTheater() {
    theaterModal.classList.remove("active");
    theaterModal.setAttribute("aria-hidden", "true");
    streamFrame.innerHTML = "";
}

function toEmbeddableUrl(url) {
    if (!url) return "about:blank";
    if (url.includes("youtube.com/watch?v=")) {
        return url.replace("watch?v=", "embed/");
    }
    if (url.includes("youtu.be/")) {
        return url.replace("youtu.be/", "www.youtube.com/embed/");
    }
    return url;
}

function isWatchlisted(movieId) {
    return getWatchlist().includes(movieId);
}

function toggleWatchlist(movieId, button) {
    const list = getWatchlist();
    const next = list.includes(movieId)
        ? list.filter(id => id !== movieId)
        : [...list, movieId];
    localStorage.setItem("cineWatchlist", JSON.stringify(next));
    button.classList.toggle("active", next.includes(movieId));
}

function getWatchlist() {
    return JSON.parse(localStorage.getItem("cineWatchlist") || "[]");
}

function escapeHtml(value = "") {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function previewMovies() {
    return [
        {
            imdbId: "CR001",
            title: "Shadow Run",
            releaseDate: "2026-04-18",
            trailerLink: "https://www.youtube.com/embed/dQw4w9WgXcQ",
            poster: "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=900&q=80",
            rating: 8.2,
            genres: ["Action", "Thriller"]
        },
        {
            imdbId: "CR002",
            title: "Midnight Frame",
            releaseDate: "2026-05-06",
            trailerLink: "https://www.youtube.com/embed/dQw4w9WgXcQ",
            poster: "https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?auto=format&fit=crop&w=900&q=80",
            rating: 7.9,
            genres: ["Drama", "Mystery"]
        },
        {
            imdbId: "CR003",
            title: "After the Cut",
            releaseDate: "2026-06-12",
            trailerLink: "https://www.youtube.com/embed/dQw4w9WgXcQ",
            poster: "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&q=80",
            rating: 8.6,
            genres: ["Drama", "Cinema"]
        }
    ];
}

function previewReviews(movieId) {
    const reviews = {
        CR001: [{ rating: 5, reviewComment: "Fast, stylish, and worth the full stream.", userId: "paid-kav" }],
        CR002: [{ rating: 4, reviewComment: "A moody thriller with beautiful pacing.", userId: "paid-ama" }],
        CR003: [{ rating: 5, reviewComment: "A love letter to cinema with a strong final act.", userId: "paid-neo" }]
    };
    return reviews[movieId] || [];
}

loadMovies().catch(error => {
    movieList.innerHTML = `<p class="message">${error.message}</p>`;
});

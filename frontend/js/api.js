const API_BASE = "http://localhost:8080/api";

async function apiRequest(path, options = {}) {
    let response;
    try {
        response = await fetch(`${API_BASE}${path}`, {
            headers: {
                "Content-Type": "application/json",
                ...(options.headers || {})
            },
            ...options
        });
    } catch {
        throw new Error("Backend API is not reachable. Start Spring Boot on http://localhost:8080 and check MySQL is running.");
    }

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        throw new Error(data?.message || "Request failed");
    }

    return data;
}

function currentUser() {
    return JSON.parse(localStorage.getItem("cineUser") || "null");
}

function saveUser(user) {
    localStorage.setItem("cineUser", JSON.stringify(user));
}

function clearUser() {
    localStorage.removeItem("cineUser");
}

const signupForm = document.querySelector("#signupForm");
const loginForm = document.querySelector("#loginForm");
const payAfterSignup = document.querySelector("#payAfterSignup");

if (signupForm) {
    signupForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const form = new FormData(signupForm);
        const payload = Object.fromEntries(form.entries());

        try {
            const result = await apiRequest("/auth/register", {
                method: "POST",
                body: JSON.stringify(payload)
            });
            saveUser(result.user);
            document.querySelector("#signupMessage").textContent =
                `Registration saved. Your User ID is ${result.user.userId}. You can pay now or login.`;
        } catch (error) {
            document.querySelector("#signupMessage").textContent = error.message;
        }
    });
}

if (payAfterSignup) {
    payAfterSignup.addEventListener("click", () => {
        const user = currentUser();
        if (!user) {
            document.querySelector("#signupMessage").textContent = "Register first, then continue to payment.";
            return;
        }
        window.location.href = `payment.html?userId=${encodeURIComponent(user.userId)}`;
    });
}

if (loginForm) {
    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const payload = Object.fromEntries(new FormData(loginForm).entries());

        try {
            const result = await apiRequest("/auth/login", {
                method: "POST",
                body: JSON.stringify(payload)
            });
            saveUser(result.user);
            window.location.href = result.user.role === "ADMIN" ? "admin.html" : "movies.html";
        } catch (error) {
            document.querySelector("#loginMessage").textContent = error.message;
        }
    });
}

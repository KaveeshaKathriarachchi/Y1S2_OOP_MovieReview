const paymentForm = document.querySelector("#paymentForm");
const params = new URLSearchParams(window.location.search);
const userId = params.get("userId") || currentUser()?.userId || "";

if (userId) {
    paymentForm.elements.userId.value = userId;
}

paymentForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const submitter = event.submitter;
    const form = new FormData(paymentForm);
    const success = submitter.value === "true";
    const payload = {
        userId: form.get("userId"),
        amount: Number(form.get("amount")),
        method: form.get("method"),
        currency: "USD",
        success,
        transactionId: `TX-${Date.now()}`
    };

    try {
        await apiRequest("/payments/process", {
            method: "POST",
            body: JSON.stringify(payload)
        });

        const user = currentUser();
        if (user && user.userId === payload.userId) {
            user.paid = success ? "yes" : "no";
            saveUser(user);
        }

        document.querySelector("#paymentMessage").textContent = success
            ? "Payment successful. Your account is now paid."
            : "Payment was not completed. Your account remains non-paid.";

        setTimeout(() => window.location.href = "login.html", 900);
    } catch (error) {
        document.querySelector("#paymentMessage").textContent = error.message;
    }
});

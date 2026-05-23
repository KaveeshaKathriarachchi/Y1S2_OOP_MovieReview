const contributorsList = document.querySelector("#contributorsList");

apiRequest("/contributors")
    .catch(() => [
        {
            name: "Maya Stone",
            role: "Actor",
            country: "USA",
            photo: "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?auto=format&fit=crop&w=900&q=80",
            description: "Lead performer in the platform's opening action collection.",
            awards: "Festival Choice Award",
            notableWorks: "Shadow Run, Harbor Lights"
        },
        {
            name: "Arun Blake",
            role: "Director",
            country: "Sri Lanka",
            photo: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=900&q=80",
            description: "Director focused on high-tension thrillers and cinematic drama.",
            awards: "Best New Director",
            notableWorks: "Midnight Frame, The Last Reel"
        },
        {
            name: "Elena Cross",
            role: "Producer",
            country: "UK",
            photo: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=900&q=80",
            description: "Producer behind intimate dramas and festival-focused releases.",
            awards: "Audience Spotlight",
            notableWorks: "After the Cut"
        }
    ])
    .then(contributors => {
        contributorsList.innerHTML = contributors.map(contributor => `
            <article class="movie-card">
                <div class="poster-container">
                    <img class="poster-image" src="${contributor.photo || "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=900&q=80"}" alt="${contributor.name}">
                    <div class="rating-badge">${contributor.role || "Creator"}</div>
                </div>
                <div class="card-body">
                    <h2 class="card-title">${contributor.name}</h2>
                    <p class="card-subtext">${contributor.country ? "From " + contributor.country : "Country not added"}</p>
                    <p class="muted">${contributor.description || ""}</p>
                    <div class="review-box">
                        <p><strong>Awards:</strong> ${contributor.awards || "Not added"}</p>
                        <p><strong>Notable works:</strong> ${contributor.notableWorks || "Not added"}</p>
                    </div>
                </div>
            </article>
        `).join("");
    })
    .catch(error => {
        contributorsList.innerHTML = `<p class="message">${error.message}</p>`;
    });

// ----------- GLOBAL CHART VARIABLES ----------------
let pieChart;
let lineChart;
// ----------- VIEW BLOCKCHAIN ----------------------
document.getElementById("viewBlockchainBtn").addEventListener("click", async () => {
    try {
        const res = await fetch("http://localhost:3000/api/chain");
        const chain = await res.json();

        // Use the same modal popup function you already have
        showResult(chain);
    } catch (err) {
        alert("Error fetching blockchain: " + err.message);
    }
});


// ----------- ANALYZE & SAVE ------------------------
document.getElementById("analyzeBtn").addEventListener("click", async () => {
    const entity = document.getElementById("entityInput").value.trim();
    const text = document.getElementById("textInput").value.trim();

    if (!entity || !text) {
        alert("Please enter both Entity and Text.");
        return;
    }

    // Compute score and label (or you may let backend do sentiment analysis)
    const score = 0.5;   // placeholder, backend can override
    const label = "neutral";  // placeholder

    try {
        const res = await fetch("/api/analyze", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ entity, text, score, label })
        });

        const data = await res.json();

        alert(`Saved!\n\nEntity: ${data.entity}\nLabel: ${data.label}\nScore: ${data.score}`);

        // reload dashboard stats
        loadSummary();
        loadPieChart();
        loadTrendChart();
        loadCalendar();
    } catch (err) {
        alert("Error saving sentiment: " + err.message);
    }
});

// ----------- DASHBOARD SUMMARY ---------------------
async function loadSummary() {
    const res = await fetch("/api/dashboard/summary");
    const data = await res.json();

    document.querySelector("#totalRecords span").innerText = data.total;
    document.querySelector("#positive span").innerText = data.positive;
    document.querySelector("#negative span").innerText = data.negative;
    document.querySelector("#neutral span").innerText = data.neutral;
}

// ----------- PIE CHART -----------------------------
async function loadPieChart() {
    const res = await fetch("/api/dashboard/pie");
    const data = await res.json();

    const ctx = document.getElementById("pieChart").getContext("2d");
    if (pieChart) pieChart.destroy();

    pieChart = new Chart(ctx, {
        type: "pie",
        data: {
            labels: Object.keys(data),
            datasets: [{ data: Object.values(data) }]
        }
    });
}

// ----------- TREND CHART ---------------------------
async function loadTrendChart() {
    const res = await fetch("/api/dashboard/trend");
    const data = await res.json();

    const labels = data.map(i => i.date);
    const values = data.map(i => i.avgScore ?? 0);

    const ctx = document.getElementById("lineChart").getContext("2d");
    if (lineChart) lineChart.destroy();

    lineChart = new Chart(ctx, {
        type: "line",
        data: {
            labels,
            datasets: [{
                label: "Average Sentiment Score",
                data: values,
                fill: false,
                tension: 0.2
            }]
        }
    });
}

// ----------- CALENDAR ------------------------------
async function loadCalendar() {
    const res = await fetch("/api/dashboard/calendar");
    const entries = await res.json();

    const calendarDiv = document.getElementById("calendarDates");
    calendarDiv.innerHTML = "";

    let days = 30;
    for (let d = 1; d <= days; d++) {
        const box = document.createElement("div");
        const dateStr = `2025-11-${String(d).padStart(2, "0")}`;

        const found = entries.find(e => e.date === dateStr);
        box.innerText = found ? `${d}\n(${found.events.length})` : d;
        box.classList.add("calendar-date-box");
        calendarDiv.appendChild(box);
    }
}

// ----------- VIEW ALL SENTIMENTS ------------------
document.getElementById("navViewAll").addEventListener("click", async () => {
    const res = await fetch("/api/all-sentiments");
    const list = await res.json();
    showResult(list);
});

// ----------- SEARCH BY ENTITY ----------------------
document.getElementById("navSearchEntity").addEventListener("click", async () => {
    const ent = prompt("Enter entity to search:");
    if (!ent) return;

    const res = await fetch(`/api/search/${ent}`);
    const list = await res.json();
    showResult(list);
});

// ----------- SORTED BY SCORE -----------------------
document.getElementById("navSortedScore").addEventListener("click", async () => {
    const res = await fetch("/api/sorted-sentiments");
    const list = await res.json();
    showResult(list);
});

// ----------- FILTER BY LABEL -----------------------
document.getElementById("navFilterLabel").addEventListener("click", async () => {
    const label = prompt("Enter label: positive / negative / neutral");
    if (!label) return;

    const res = await fetch(`/api/filter/${label}`);
    const list = await res.json();
    showResult(list);
});

// ----------- LATEST PER ENTITY --------------------
document.getElementById("navLatestEntity").addEventListener("click", async () => {
    const res = await fetch("/api/latest-all");
    const list = await res.json();
    showResult(list);
});

// ----------- FULL ENTITY HISTORY ------------------
document.getElementById("navEntityHistory").addEventListener("click", async () => {
    const ent = prompt("Enter entity to get full history:");
    if (!ent) return;

    const res = await fetch(`/api/history/${ent}`);
    const list = await res.json();
    showResult(list);
});

// ----------- EXIT -------------------------------
document.getElementById("navExit").addEventListener("click", () => {
    alert("Dashboard Closed.");
});

// ----------- MODAL DISPLAY ------------------------
function showResult(list) {
    if (!Array.isArray(list)) {
        list = [{ error: "Backend error", details: list }];
    }

    let modal = document.getElementById("resultModal");
    modal.style.display = "block";
    modal.innerHTML = `
        <div style="
            background:white;
            padding:20px;
            border:2px solid black;
            width:60%;
            margin:40px auto;
        ">
            <h2>Results (${Array.isArray(list) ? list.length : 1})</h2>
            <button onclick="document.getElementById('resultModal').style.display='none'">Close</button>
            <hr>
            <pre>${JSON.stringify(list, null, 2)}</pre>
        </div>
    `;
}

// ----------- INITIAL LOAD -------------------------
window.onload = () => {
    loadSummary();
    loadPieChart();
    loadTrendChart();
    loadCalendar();
};

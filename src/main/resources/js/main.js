// Sample data (replace with your backend fetch)
let sentimentData = {
    total: 0,
    positive: 0,
    negative: 0,
    neutral: 0
};

// Update cards
function updateCards() {
    document.getElementById("totalRecords").querySelector("span").textContent = sentimentData.total;
    document.getElementById("positive").querySelector("span").textContent = sentimentData.positive;
    document.getElementById("negative").querySelector("span").textContent = sentimentData.negative;
    document.getElementById("neutral").querySelector("span").textContent = sentimentData.neutral;
}
updateCards();

// Multiple time zone clocks
function updateClocks() {
    const now = new Date();
    
    document.getElementById("utcClock").textContent = now.toLocaleTimeString("en-US", { timeZone: "UTC" });
    document.getElementById("istClock").textContent = now.toLocaleTimeString("en-US", { timeZone: "Asia/Kolkata" });
    document.getElementById("estClock").textContent = now.toLocaleTimeString("en-US", { timeZone: "America/New_York" });
}
setInterval(updateClocks, 1000);
updateClocks();

// Calendar
function renderCalendar() {
    const now = new Date();
    const year = now.getFullYear();
    const month = now.getMonth();
    const firstDay = new Date(year, month, 1).getDay();
    const lastDate = new Date(year, month + 1, 0).getDate();

    let calendarHTML = `<table class="calendar-table">
        <thead>
            <tr>
                <th>Sun</th><th>Mon</th><th>Tue</th><th>Wed</th>
                <th>Thu</th><th>Fri</th><th>Sat</th>
            </tr>
        </thead>
        <tbody>
            <tr>`;

    for (let i = 0; i < firstDay; i++) calendarHTML += `<td></td>`;
    for (let day = 1; day <= lastDate; day++) {
        if ((day + firstDay - 1) % 7 === 0 && day !== 1) calendarHTML += `</tr><tr>`;
        calendarHTML += `<td>${day}</td>`;
    }
    calendarHTML += `</tr></tbody></table>`;
    document.getElementById("calendar").innerHTML = calendarHTML;
}
renderCalendar();

function renderCalendar() {
    const now = new Date();
    const month = now.getMonth();
    const year = now.getFullYear();

    const monthYear = document.getElementById("monthYear");
    monthYear.textContent = now.toLocaleString('default', { month: 'long', year: 'numeric' });

    const firstDay = new Date(year, month, 1).getDay();
    const lastDate = new Date(year, month + 1, 0).getDate();

    const calendarDates = document.getElementById("calendarDates");
    calendarDates.innerHTML = "";

    // Empty slots for first week
    for(let i = 0; i < firstDay; i++) {
        const empty = document.createElement("div");
        calendarDates.appendChild(empty);
    }

    // Fill dates
    for(let d = 1; d <= lastDate; d++) {
        const dateDiv = document.createElement("div");
        dateDiv.textContent = d;
        if(d === now.getDate()) {
            dateDiv.classList.add("today");
        }
        calendarDates.appendChild(dateDiv);
    }
}

renderCalendar();


// Charts
const pieCtx = document.getElementById("pieChart").getContext("2d");
const pieChart = new Chart(pieCtx, {
    type: 'pie',
    data: {
        labels: ['Positive', 'Negative', 'Neutral'],
        datasets: [{
            data: [sentimentData.positive, sentimentData.negative, sentimentData.neutral],
            backgroundColor: ['#2ecc71','#e74c3c','#f1c40f']
        }]
    }
});

const lineCtx = document.getElementById("lineChart").getContext("2d");
const lineChart = new Chart(lineCtx, {
    type: 'line',
    data: {
        labels: [], // timestamps
        datasets: [{
            label: 'Sentiment Score',
            data: [], // scores
            borderColor: '#3498db',
            fill: false
        }]
    }
});

// Call this whenever backend updates
function refreshCharts(newData) {
    sentimentData = newData;
    updateCards();
    
    pieChart.data.datasets[0].data = [newData.positive, newData.negative, newData.neutral];
    pieChart.update();
    
    // lineChart update logic here...
}


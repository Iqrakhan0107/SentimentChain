# 🧠 SentimentChain  
*A decentralized sentiment oracle connecting public emotion to transparent market insights.*

---

## 📜 Overview
**SentimentChain** analyzes public sentiment toward entities such as companies, artists, or crypto projects.  
It stores these results for retrieval, ranking, and analysis — paving the way for **transparent market emotion tracking** using decentralized principles.

This system forms the **core logic** for a blockchain-integrated oracle that could feed market mood data to smart contracts or dApps in real time.

---

## ⚙️ Core Features
- 🔍 **Sentiment Analysis & Storage** — Analyze and persist sentiment data in structured CSV and text files.  
- 🧾 **Entity Search** — Retrieve sentiment records by entity name.  
- 📊 **View Sorted by Score** — Sort and analyze entities by sentiment positivity or negativity.  
- 🕓 **Timeline History** — View sentiment changes of an entity over time.  
- 🧠 **Latest Records** — Fetch the most recent sentiment entry per entity.  
- 🏷️ **Filter by Label** — Filter sentiments based on polarity (Positive, Negative, Neutral).  
- 🔐 **Data Integrity (Planned)** — Hashing mechanism for tamper-proof sentiment storage.

---

## 🧩 Tech Stack
| Layer | Technology |
|:------|:------------|
| Core Language | Java |
| Database | Flat-file (CSV + TXT) — JDBC planned for later |
| Future Backend | REST APIs (Spring Boot / Polkadot SDK Integration) |
| Planned Frontend | Java Swing / Web GUI |
| Blockchain Layer | Polkadot / Substrate (Concept & Integration Phase) |

---

## 🧱 Project Structure

SentimentChainCore/
├── src/
│ └── com/sentimentchain/
│ ├── MainApp.java
│ ├── Sentiment.java
│ ├── SentimentDAO.java
│ └── Utils.java
├── sentiments.csv
├── sentiments.txt
├── README.md
├── LICENSE
└── .gitignore

---

## 🧠 How to Run
1. Clone the repository:
   
   git clone https://github.com/Iqrakhan0107/SentimentChain.git
   cd SentimentChainCore

2. Compile and run:

javac -d bin src/com/sentimentchain/*.java
java -cp bin com.sentimentchain.MainApp

3. Follow the CLI menu to analyze and store new sentiment data.


🌍 Future Roadmap

 Integrate real-time sentiment analysis API (Twitter/X, news, etc.)

 Add GUI (Swing / JavaFX) for interactive visualization

 Build REST APIs to expose sentiment data

 Connect to Polkadot via oracle or off-chain worker model

 Implement hashing & blockchain storage for transparency

🧑‍💻 Author

Iqra Khan
CS Student | Java Developer | Polkadot Sub0 Hackathon Participant

📬 Contact: iiqra0107@gmail.com

🔗 GitHub: github.com/Iqrakhan0107

🪪 License

This project is licensed under the MIT License — see the LICENSE
 file for details.






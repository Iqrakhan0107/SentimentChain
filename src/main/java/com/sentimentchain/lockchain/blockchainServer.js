const express = require('express');
const bodyParser = require('body-parser');
const crypto = require('crypto');
const fetch = require('node-fetch'); // npm i node-fetch@2 if needed

const app = express();
app.use(bodyParser.json());

const PORT = 3000;

// Simple blockchain structure
let blockchain = [];

// Create a new block
function createBlock(data, previousHash = '') {
    const block = {
        index: blockchain.length,
        timestamp: new Date().toISOString(),
        data,
        previousHash,
    };
    block.hash = crypto.createHash('sha256').update(JSON.stringify(block)).digest('hex');
    return block;
}

// Add block to blockchain
function addBlock(data) {
    const previousHash = blockchain.length ? blockchain[blockchain.length - 1].hash : '0';
    const newBlock = createBlock(data, previousHash);
    blockchain.push(newBlock);
    return newBlock;
}

// Push sentiment to Spring Boot DAO
async function pushToSpring(sentiment) {
    try {
        const res = await fetch('http://localhost:8080/api/analyze', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(sentiment),
        });
        const json = await res.json();
        console.log('Sentiment saved in Spring DAO:', json);
    } catch (err) {
        console.error('Error pushing sentiment to Spring DAO:', err.message);
    }
}

// Endpoint to push sentiment to blockchain and Spring DAO
app.post('/api/pushToChain', async (req, res) => {
    const sentiment = req.body;
    if (!sentiment.entity || !sentiment.text || !sentiment.label) {
        return res.status(400).json({ error: 'Invalid sentiment data' });
    }

    // Add to blockchain
    const block = addBlock(sentiment);
    console.log('New block added:', block);

    // Push to Spring DAO
    await pushToSpring(sentiment);

    res.json({ message: 'Sentiment added to blockchain and DAO', block });
});

// Get the full chain
app.get('/api/chain', (req, res) => {
    res.json(blockchain);
});

// Optional: fetch latest from Spring DAO
app.get('/api/fetchFromDAO', async (req, res) => {
    try {
        const response = await fetch('http://localhost:8080/api/all-sentiments');
        const data = await response.json();
        res.json(data);
    } catch (err) {
        res.status(500).json({ error: 'Failed to fetch from Spring DAO' });
    }
});

app.listen(PORT, () => {
    console.log(`Blockchain server running on http://localhost:${PORT}`);
});

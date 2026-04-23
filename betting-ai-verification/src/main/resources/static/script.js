const API = 'http://localhost:8080'; // Убедись, что порт правильный

// Clock
function updateClock() { document.getElementById('clock').textContent = new Date().toLocaleTimeString('en-GB'); }
setInterval(updateClock, 1000); updateClock();

// Navigation
function showPage(name, el) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    document.getElementById('page-' + name).classList.add('active');
    if(el && el.classList) el.classList.add('active');

    let label = el ? el.textContent.trim().replace(/[^a-zA-Z\s]/g, '').trim() : name;
    if(name === 'profile') label = 'User Profile';
    document.getElementById('pageLabel').textContent = label;

    if (name === 'dashboard') loadDashboard();
    if (name === 'users') loadUsers();
    if (name === 'bets') loadBets();
    if (name === 'matches') loadMatches();
    if (name === 'verification') loadVerificationUsers();
}

// Risk badge helper
function riskBadge(level) {
    if (!level) return '<span class="risk-badge">—</span>';
    const l = level.toUpperCase();
    const cls = l === 'HIGH' ? 'high' : l === 'MEDIUM' ? 'med' : 'low';
    return `<span class="risk-badge ${cls}">${l}</span>`;
}

// DASHBOARD 
async function loadDashboard() {
    try {
        const [users, bets, rawResults] = await Promise.all([
            fetch(`${API}/api/users`).then(r => r.json()).catch(() => []),
            fetch(`${API}/api/bets`).then(r => r.json()).catch(() => []),
            fetch(`${API}/api/verification/results`).then(r => r.json()).catch(() => []),
        ]);

        // Deduplicate logic
        const resultsMap = new Map();
        rawResults.forEach(r => {
            if(r.bet && r.bet.id) {
                if(!resultsMap.has(r.bet.id) || resultsMap.get(r.bet.id).id < r.id) {
                    resultsMap.set(r.bet.id, r);
                }
            }
        });
        const uniqueResults = Array.from(resultsMap.values());

        document.getElementById('stat-users').textContent = users.length || 0;
        document.getElementById('stat-bets').textContent = bets.length || 0;

        const suspicious = uniqueResults.filter(r => r.suspicious).length;
        const blocked = users.filter(u => u.blocked).length;

        document.getElementById('stat-suspicious').textContent = suspicious;
        document.getElementById('stat-blocked').textContent = blocked;
        document.getElementById('stat-users-change').textContent = `${users.length} registered`;
        document.getElementById('stat-bets-change').textContent = `${bets.length} total`;
        document.getElementById('stat-susp-change').textContent = suspicious > 0 ? `▲ ${suspicious} flagged` : 'None flagged';
        document.getElementById('stat-blocked-change').textContent = blocked > 0 ? `▲ ${blocked} blocked` : 'None blocked';

        // Risk distribution
        const low = uniqueResults.filter(r => r.riskLevel === 'LOW').length;
        const med = uniqueResults.filter(r => r.riskLevel === 'MEDIUM').length;
        const high = uniqueResults.filter(r => r.riskLevel === 'HIGH').length;
        const total = uniqueResults.length || 1;

        const pLow = Math.round(low/total*100);
        const pMed = Math.round(med/total*100);
        const pHigh = Math.round(high/total*100);

        document.getElementById('pct-low').textContent = pLow + '%';
        document.getElementById('pct-med').textContent = pMed + '%';
        document.getElementById('pct-high').textContent = pHigh + '%';
        setTimeout(() => {
            document.getElementById('bar-low').style.width = pLow + '%';
            document.getElementById('bar-med').style.width = pMed + '%';
            document.getElementById('bar-high').style.width = pHigh + '%';
        }, 100);

        // Recent table
        document.getElementById('decisions-count').textContent = uniqueResults.length + ' UNIQUE ANALYZED';
        const recent = [...uniqueResults].sort((a,b) => b.id - a.id).slice(0, 10);

        document.getElementById('recent-table').innerHTML = recent.length ? recent.map(r => `
      <tr>
        <td class="id-cell">#${r.bet?.id || r.id}</td>
        <td>${r.bet?.user?.username || '—'}</td>
        <td style="font-family:var(--mono);font-size:12px">${r.bet?.amount || 0}€</td>
        <td style="font-size:12px">${r.bet?.match?.homeTeam?.name || '?'} vs ${r.bet?.match?.awayTeam?.name || '?'}</td>
        <td>${riskBadge(r.riskLevel)}</td>
        <td class="verdict-cell ${r.suspicious ? 'flagged' : ''}">${r.suspicious ? '⚑ suspicious' : 'clear'}</td>
      </tr>
    `).join('') : '<tr><td colspan="6" class="empty-state">No verification results yet</td></tr>';

    } catch(e) { console.error('Dashboard load error:', e); }
}

// USERS & PROFILE
async function loadUsers() {
    try {
        const users = await fetch(`${API}/api/users`).then(r => r.json()).catch(() => []);
        document.getElementById('users-table').innerHTML = users.length ? users.map(u => `
      <tr class="user-row">
        <td class="id-cell">#${u.id}</td>
        <td style="font-family:var(--mono);font-size:12px">${u.username}</td>
        <td style="font-family:var(--mono);font-size:12px">${u.balance?.toFixed(2) || '0.00'}€</td>
        <td><span class="status-badge ${u.blocked ? 'blocked' : 'active'}">${u.blocked ? 'BLOCKED' : 'ACTIVE'}</span></td>
        <td>${riskBadge(u.riskLevel || 'LOW')}</td>
        <td style="display:flex;gap:8px">
          <button class="action-btn" onclick="verifyUserNav(${u.id})">VERIFY</button>
          <button class="action-btn" onclick="openProfile(${u.id})">PROFILE</button>
        </td>
      </tr>
    `).join('') : '<tr><td colspan="6" class="empty-state">No users found</td></tr>';
    } catch(e) {}
}

async function openProfile(userId) {
    showPage('profile', null);
    document.getElementById('profile-header-content').innerHTML = '<div class="empty-state">Loading...</div>';
    document.getElementById('profile-bets-table').innerHTML = '<tr><td colspan="5" class="empty-state">Loading...</td></tr>';

    try {
        const users = await fetch(`${API}/api/users`).then(r => r.json());
        const u = users.find(x => x.id === userId);

        if(!u) throw new Error("User not found");

        document.getElementById('profile-header-content').innerHTML = `
      <div class="profile-info">
        <h2>${u.username} <span class="id-cell" style="font-size:14px; margin-left:8px">#${u.id}</span></h2>
        <div style="font-size:12px; color:var(--muted); font-family:var(--mono);">
           Balance: <span style="color:var(--text)">${u.balance?.toFixed(2) || '0.00'}€</span> &nbsp;|&nbsp; 
           Status: <span class="status-badge ${u.blocked ? 'blocked' : 'active'}">${u.blocked ? 'BLOCKED' : 'ACTIVE'}</span>
        </div>
      </div>
      <div>${riskBadge(u.riskLevel || 'LOW')}</div>
    `;

        const allB = await fetch(`${API}/api/bets`).then(r => r.json());
        const userBets = allB.filter(b => b.user?.id === userId);

        document.getElementById('profile-bets-table').innerHTML = userBets.length ? userBets.map(b => `
      <tr>
        <td class="id-cell">#${b.id}</td>
        <td style="font-size:12px">${b.match?.homeTeam?.name || '?'} vs ${b.match?.awayTeam?.name || '?'}</td>
        <td style="font-family:var(--mono);font-size:12px">${b.amount}€</td>
        <td style="font-size:12px">${b.outcome || '—'}</td>
        <td>${riskBadge(b.riskLevel || 'LOW')}</td>
      </tr>
    `).join('') : '<tr><td colspan="5" class="empty-state">No bets history</td></tr>';

    } catch(e) {
        document.getElementById('profile-header-content').innerHTML = `<div class="empty-state" style="color:var(--high)">Error loading profile</div>`;
    }
}

// BETS WITH FILTERS AND SORTING
let allBetsData = [];
let currentRiskFilter = 'all';

async function loadBets() {
    try {
        allBetsData = await fetch(`${API}/api/bets`).then(r => r.json()).catch(() => []);
        applyBetFilters();
    } catch(e) {}
}

function setBetRiskFilter(level, el) {
    document.querySelectorAll('#page-bets .filter-btn').forEach(b => b.classList.remove('active'));
    el.classList.add('active');
    currentRiskFilter = level;
    applyBetFilters();
}

function applyBetFilters() {
    const min = parseFloat(document.getElementById('bet-min').value) || 0;
    const max = parseFloat(document.getElementById('bet-max').value) || Infinity;
    const sort = document.getElementById('bet-sort').value;

    let filtered = allBetsData.filter(b => {
        const map = { high: 'HIGH', med: 'MEDIUM', low: 'LOW' };
        if (currentRiskFilter !== 'all' && (b.riskLevel || 'LOW').toUpperCase() !== map[currentRiskFilter]) return false;
        if (b.amount < min || b.amount > max) return false;
        return true;
    });

    if(sort) {
        filtered.sort((a, b) => {
            if (sort === 'user_asc') return (a.user?.username || '').localeCompare(b.user?.username || '');
            if (sort === 'match_asc') {
                const matchA = `${a.match?.homeTeam?.name} vs ${a.match?.awayTeam?.name}`;
                const matchB = `${b.match?.homeTeam?.name} vs ${b.match?.awayTeam?.name}`;
                return matchA.localeCompare(matchB);
            }
            if (sort === 'amount_asc') return a.amount - b.amount;
            if (sort === 'amount_desc') return b.amount - a.amount;
            return 0;
        });
    }

    renderBets(filtered);
}

function renderBets(bets) {
    document.getElementById('bets-table').innerHTML = bets.length ? bets.map(b => `
    <tr>
      <td class="id-cell">#${b.id}</td>
      <td style="font-size:12px">${b.user?.username || '—'}</td>
      <td style="font-size:12px">${b.match?.homeTeam?.name || '?'} vs ${b.match?.awayTeam?.name || '?'}</td>
      <td style="font-family:var(--mono);font-size:12px">${b.amount}€</td>
      <td style="font-size:12px">${b.outcome || '—'}</td>
      <td>${riskBadge(b.riskLevel || 'LOW')}</td>
    </tr>
  `).join('') : '<tr><td colspan="6" class="empty-state">No matching bets</td></tr>';
}

// MATCHES WITH SEARCH & AI PREDICTION
let allMatchesData = [];

async function loadMatches() {
    try {
        allMatchesData = await fetch(`${API}/api/matches`).then(r => r.json()).catch(() => []);
        applyMatchFilters();
    } catch(e) {}
}

function applyMatchFilters() {
    const text = document.getElementById('match-search').value.toLowerCase();
    const dateStr = document.getElementById('match-date').value;
    const sort = document.getElementById('match-sort').value;

    let filtered = allMatchesData.filter(m => {
        const hName = (m.homeTeam?.name || '').toLowerCase();
        const aName = (m.awayTeam?.name || '').toLowerCase();
        const matchName = hName.includes(text) || aName.includes(text);

        let matchDate = true;
        if(dateStr && m.startTime) {
            matchDate = m.startTime.startsWith(dateStr);
        }
        return matchName && matchDate;
    });

    filtered.sort((a, b) => {
        const d1 = new Date(a.startTime).getTime() || 0;
        const d2 = new Date(b.startTime).getTime() || 0;
        return sort === 'asc' ? d1 - d2 : d2 - d1;
    });

    renderMatches(filtered);
}

function renderMatches(matches) {
    document.getElementById('matches-list').innerHTML = matches.length ? matches.map(m => `
    <div class="match-card-wrap">
      <div class="match-card">
        <div class="match-teams">
          <span>${m.homeTeam?.name || '?'}</span>
          <span class="match-vs">VS</span>
          <span>${m.awayTeam?.name || '?'}</span>
        </div>
        <div class="match-meta">
          <span class="match-time">${m.startTime ? new Date(m.startTime).toLocaleString('en-GB') : '—'}</span>
          <span class="match-status ${(m.status || '').toLowerCase()}">${m.status || 'UNKNOWN'}</span>
          <button class="verify-btn" onclick="predictMatch(${m.id}, this)" style="border-color:var(--accent); color:var(--accent)">🤖 AI ANALYSIS</button>
        </div>
      </div>
      <div class="prediction-box" id="pred-box-${m.id}"></div>
    </div>
  `).join('') : '<div class="empty-state">No matches found</div>';
}

async function predictMatch(matchId, btn) {
    const box = document.getElementById(`pred-box-${matchId}`);
    if (box.classList.contains('visible')) {
        box.classList.remove('visible');
        return;
    }

    btn.textContent = 'ANALYZING...';
    btn.style.opacity = '0.5';
    btn.disabled = true;

    try {
        const res = await fetch(`${API}/api/predictions/${matchId}`, { method: 'POST' });
        if(!res.ok) throw new Error('API Error');
        const data = await res.json();

        const homeWinValue = data.homeWinProb !== undefined ? data.homeWinProb : '???';
        const drawValue = data.drawProb !== undefined ? data.drawProb : '???';
        const awayWinValue = data.awayWinProb !== undefined ? data.awayWinProb : '???';
        const explanationText = data.aiExplanation || 'No explanation provided.';

        box.innerHTML = `
            <div style="font-family:var(--mono); font-size:10px; color:var(--accent); letter-spacing:2px; margin-bottom:10px;">AI PREDICTION RESULT</div>
            <div class="pred-stats">
               <div class="pred-stat-item">Home Win: <span>${homeWinValue}%</span></div>
               <div class="pred-stat-item">Draw: <span>${drawValue}%</span></div>
               <div class="pred-stat-item">Away Win: <span>${awayWinValue}%</span></div>
            </div>
            <div style="color:var(--muted); line-height:1.5;"><strong>Explanation:</strong> ${explanationText}</div>
        `;
        box.classList.add('visible');
    } catch(e) {
        console.error("Ошибка при получении предсказания:", e);
        box.innerHTML = '<div style="color:var(--high)">Failed to get prediction from AI.</div>';
        box.classList.add('visible');
    } finally {
        btn.textContent = '🤖 AI ANALYSIS';
        btn.style.opacity = '1';
        btn.disabled = false;
    }
}

// VERIFICATION WITH SEARCHABLE DATALIST
async function loadVerificationUsers() {
    try {
        const users = await fetch(`${API}/api/users`).then(r => r.json()).catch(() => []);
        const list = document.getElementById('verify-users-list');
        list.innerHTML = users.map(u => `<option value="${u.username} (#${u.id})">`).join('');
    } catch(e) {}
}

async function runVerification() {
    const inputVal = document.getElementById('verify-user-input').value;
    const matchId = inputVal.match(/#(\d+)\)$/);
    const userId = matchId ? matchId[1] : null;

    if (!userId) { alert('Please select a valid user from the dropdown list'); return; }

    document.getElementById('empty-verify').style.display = 'none';
    document.getElementById('ai-result').classList.remove('visible');
    document.getElementById('loading-state').classList.add('visible');
    document.getElementById('run-btn').disabled = true;

    try {
        const res = await fetch(`${API}/api/verification/user/${userId}`);
        if(!res.ok) throw new Error('Verification request failed');
        const data = await res.json();

        document.getElementById('loading-state').classList.remove('visible');

        const risk = (data.riskLevel || 'LOW').toUpperCase();
        const riskClass = risk === 'HIGH' ? 'high' : risk === 'MEDIUM' ? 'med' : 'low';

        const resultEl = document.getElementById('ai-result');
        resultEl.className = 'ai-result visible ' + riskClass + '-risk';

        document.getElementById('result-risk').className = 'risk-level-display ' + riskClass;
        document.getElementById('result-risk').textContent = risk + ' RISK';
        document.getElementById('result-reason').textContent = data.reason || '—';
        document.getElementById('result-explanation').textContent = data.explanation || '—';

        const flags = [
            { label: 'SUSPICIOUS', active: data.suspicious },
            { label: 'GAMBLING ADDICTION', active: data.gamblingAddictionRisk },
            { label: 'MATCH FIXING', active: data.matchFixingRisk },
        ];

        document.getElementById('result-flags').innerHTML = flags.map(f =>
            `<span class="flag-chip ${f.active ? 'active' : 'inactive'}">${f.active ? '⚑' : '✓'} ${f.label}</span>`
        ).join('');

    } catch(e) {
        document.getElementById('loading-state').classList.remove('visible');
        document.getElementById('empty-verify').style.display = 'block';
        document.getElementById('empty-verify').innerHTML = `
      <div style="font-family:var(--mono);font-size:11px;color:var(--high);letter-spacing:2px">
        ✕ VERIFICATION FAILED
      </div>
      <div style="font-size:12px;color:var(--muted);margin-top:8px">${e.message}</div>
    `;
    } finally {
        document.getElementById('run-btn').disabled = false;
    }
}

function verifyUserNav(userId) {
    fetch(`${API}/api/users`).then(r=>r.json()).then(users => {
        const u = users.find(x => x.id === userId);
        if(u) document.getElementById('verify-user-input').value = `${u.username} (#${u.id})`;
    }).catch(()=>{});

    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    const verifyNav = document.querySelector('[onclick="showPage(\'verification\', this)"]');
    if (verifyNav) verifyNav.classList.add('active');

    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.getElementById('page-verification').classList.add('active');
    document.getElementById('pageLabel').textContent = 'Verification';
    loadVerificationUsers();
}

// Initial load
loadDashboard();
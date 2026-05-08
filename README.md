# InvestTrack

InvestTrack is a personal finance web application for tracking and managing investments. It provides a clear dashboard, detailed investment views, interactive charts, and CRUD operations for investments and entries.

## ✨ Features

### Frontend (UI)
- 📊 Dashboard summary: Total invested, obtained value, benefit, profitability
- 🗂️ Investment list: Active/inactive investments with details
- 🔍 Investment details: Drill down into entries and performance
- 📈 Interactive charts: Visualize performance and benefit
- 📝 CRUD operations: Create, edit, delete investments and entries
- 📱 Responsive UI: Built with Tailwind CSS, React, and Astro

### 🔮 Forecasting
- 📊 Multiple strategies: Pessimist, Neutral, Optimist scenarios per forecast
- 📅 Arbitrary start dates: Pick any date to begin a forecast
- 📈 Granular visualization: Compare real results (daily interpolated) with multiple forecast scenarios
- 🔗 Comparative Analysis: Combined and individual strategy graph views
- 🛠️ Full CRUD operations: Create, edit, and delete forecasts

## Backend (API)
- 🔗 RESTful endpoints for investment management
- 🗃️ Google Sheets integration for data storage and sync
- Endpoints:
  - `GET /investments`: List all investments
  - `POST /investments`: Create investment
  - `DELETE /investments/{id}`: Delete investment
  - `POST /investments/entry/{id}`: Add entry to investment
  - `DELETE /investments/entry/{investmentId}/{entryId}`: Delete entry
  - `GET /investments/summary`: Investment summary
  - `GET /investments/forecast/{investmentId}`: Get investment forecasts
  - `POST /investments/forecast/{investmentId}`: Create forecast
  - `PUT /investments/forecast/{investmentId}/{forecastId}`: Update forecast
  - `DELETE /investments/forecast/{investmentId}/{forecastId}`: Delete forecast
  - `GET /heartbeat`: Health check

## 🛠️ Tech Stack
- **Frontend:** Astro, React, Tailwind CSS, ApexCharts, Recharts
- **Backend:** Java, Spring Boot, Lombok, Google Sheets API

## 📁 Folder Structure
- `api/` — Spring Boot backend
  - `src/main/java/com/investTrack/` — Main application, controllers, models, services
  - `src/main/resources/` — Configuration files
- `ui/` — Astro/React frontend
  - `src/components/` — UI components
  - `src/pages/` — Astro pages
  - `src/lib/` — Utility functions and API service
  - `public/` — Static assets

## 🚀 Getting Started

### Backend (API)
1. Navigate to `api/`
2. Install dependencies and run:
   ```bash
   ./gradlew bootRun
   ```
   The API will run at `http://localhost:8080`

### Frontend (UI)
1. Navigate to `ui/`
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
   The app will run at `http://localhost:4321`

## 🔑 Google Sheets Setup
To enable Google Sheets integration, follow these steps:

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project (or select an existing one).
3. Enable the "Google Sheets API" for your project.
4. Go to "APIs & Services" > "Credentials" and click "Create Credentials" > "OAuth client ID".
   - Choose "Desktop app" as the application type.
   - Download the generated `credentials.json` file.
5. Place `credentials.json` in `api/src/main/resources/credentials/`.
6. On first run, the backend will prompt you to authorize access:
   - It will open a browser window for Google login and consent.
   - After authorization, a token will be saved for future use.
   - The receiver port (default: 8081) can be changed in `application.properties`.

**Note:** Make sure your Google account has access to the spreadsheet ID set in `application.properties`.

## ⚙️ Local Backend Configuration (CORS)
To allow the frontend to communicate with the backend when accessing from different IPs (like a smartphone or another computer), you need to configure the allowed origins.

1. Create a file named `application-local.properties` in `api/src/main/resources/`.
2. Add the following property, including both `localhost` and your local network IP:
   ```properties
   cors.allowed-origins=http://localhost:4321,http://192.165.0.2:4321
   ```
   *Replace `192.165.0.2` with your computer's local IP address.*

**Note:** This file is already added to `.gitignore`, so your local environment settings won't be committed to the repository.

## 🛠️ Cross-Device Local Access (WSL2)

### Goal
Access the Astro frontend and Spring Boot backend running inside WSL2 (Ubuntu) from external devices (e.g., smartphone) on the same Wi-Fi network.

### 1. Environment Setup
- **Host IP (Windows):** e.g., 192.x.x.x (your PC's IP)
- **Guest IP (WSL2):** Dynamic (e.g., 172.x.x.x)
- **Frontend:** Astro (Port 4321)
- **Backend:** Spring Boot (Port 8080)

### 2. Networking Strategy: Port Proxying
Since WSL2 uses a virtual network, Windows acts as a gateway. Use `netsh` to bridge ports from Windows to WSL2:

**A. Identify WSL2 IP**
```bash
hostname -I
```

**B. Create the Windows Tunnel** (PowerShell as Administrator):
```powershell
# Proxy for Frontend (Astro)
netsh interface portproxy add v4tov4 listenport=4321 listenaddress=0.0.0.0 connectport=4321 connectaddress=[YOUR_WSL_IP]
# Proxy for Backend (Spring Boot)
netsh interface portproxy add v4tov4 listenport=8080 listenaddress=0.0.0.0 connectport=8080 connectaddress=[YOUR_WSL_IP]
```

### 3. Firewall Configuration
Allow inbound traffic on these ports:
```powershell
New-NetFirewallRule -DisplayName "Astro-Dev" -Direction Inbound -LocalPort 4321 -Protocol TCP -Action Allow
New-NetFirewallRule -DisplayName "Spring-Boot-Dev" -Direction Inbound -LocalPort 8080 -Protocol TCP -Action Allow
```
Ensure your Wi-Fi network is set to Private, and check any antivirus settings.

### 4. Application Configuration
- **Frontend:**
  - Start dev server with: `npm run dev -- --host 0.0.0.0`
  - Set API URL to `http://<YOUR_HOST_IP>:8080` in `.env`.
- **Backend:**
  - Follow the instructions in the [Local Backend Configuration (CORS)](#️-local-backend-configuration-cors) section to enable access for your Host IP.

### 5. Troubleshooting Checklist
- Can your phone ping the PC? (`ping <YOUR_HOST_IP>`)
- Test connection: `Test-NetConnection -ComputerName <YOUR_HOST_IP> -Port 4321`
- If issues after reboot: `netsh interface portproxy reset`

### 6. Stability Note
Reserve your PC's IP in your router's DHCP settings to keep it static (e.g., <YOUR_HOST_IP>).

## 🌐 Reverse Proxy Development (Nginx)

For a more production-like development environment or to use custom domains (e.g., `http://apps.home/invest-track/`), the project is configured to work behind an Nginx reverse proxy.

### 1. Nginx Configuration
The application is served via Nginx on port 80. The configuration handles:
- **Frontend:** Proxies `/invest-track/` to the Astro dev server (port 4321).
- **Backend:** Proxies `/invest-track/api/` to the Spring Boot server (port 8080).
- **Vite/HMR:** Proxies root-relative assets and WebSocket connections to ensure Hot Module Replacement works through the proxy.

### 2. Astro Configuration (`ui/astro.config.mjs`)
To support the `/invest-track/` subpath and the proxy:
- `base: '/invest-track/'`: Ensures Astro generates paths relative to the subpath.
- `server.allowedHosts`: Includes `apps.home`.
- `server.hmr`: Configured to use port 80 and a specific path (`invest-track/`) for WebSocket stability.

### 3. Frontend Adaptations
The UI is built to be "subpath-aware":
- **Links:** Uses `import.meta.env.BASE_URL` in `Header.astro` for dynamic routing.
- **Assets:** Uses relative paths for static assets (e.g., `favicon.svg` in `Layout.astro`).
- **API:** The `API_BASE_URL` in `ui/src/lib/config.js` defaults to `/invest-track/api` to route requests through the proxy.

## 📜 License
MIT
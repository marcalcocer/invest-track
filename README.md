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

### Backend (API)
- 🔗 RESTful endpoints for investment management
- 🗃️ Google Sheets integration for data storage and sync
- Endpoints:
  - `GET /investments`: List all investments
  - `POST /investments`: Create investment
  - `DELETE /investments/{id}`: Delete investment
  - `POST /investments/entry/{id}`: Add entry to investment
  - `DELETE /investments/entry/{investmentId}/{entryId}`: Delete entry
  - `GET /investments/summary`: Investment summary
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

## 📜 License
MIT

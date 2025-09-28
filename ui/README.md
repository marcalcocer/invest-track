# InvestTrack

InvestTrack is a personal finance web application designed to help you monitor and manage your investments. You can track stocks, funds, real estate, and more, with a simple and clear interface to view performance, profitability, and long-term growth.

## ✨ Features
- **Dashboard summary**: See total invested amount, obtained value, benefit, and profitability.
- **Investment list**: View active and inactive investments with details and performance metrics.
- **Investment details**: Drill down into each investment, view all entries, and see performance over time.
- **Interactive charts**: Visualize investment performance and benefit with dynamic graphs.
- **CRUD operations**: Create, edit, and delete investments and entries.
- **Modals**: User-friendly modals for creating investments, adding entries, confirming deletions, and viewing charts.
- **Responsive UI**: Built with Tailwind CSS for a modern look and mobile support.

## 🛠️ Tech Stack
- [Astro](https://astro.build/)
- [React](https://react.dev/)
- [Tailwind CSS](https://tailwindcss.com/)
- [ApexCharts](https://apexcharts.com/) for data visualization

## 📁 Main Structure
- `src/components/` — React components for UI, investments, modals, and charts
- `src/pages/` — Astro pages for routing (`index`, `about`, `investment`, `404`)
- `src/lib/` — Utility functions and API service
- `src/layouts/` — Main layout
- `public/` — Static assets

## 🚀 Getting Started
1. Install dependencies:
   ```bash
   npm install
   ```

2. Start the development server:
   ```bash
   npm run dev
   ```
   The app will run at `http://localhost:4321`.

3. Build for production:
   ```bash
   npm run build
   ```

4. Preview the build:
   ```bash
   npm run preview
   ```
   This will serve the production build locally.

## 🔗 API
InvestTrack fetches investment data from a backend API. By default: `http://localhost:8080/investments`.

You can override this by setting an environment variable in a `.env` file:

```bash
PUBLIC_API_URL=http://192.123.1.23:8080/investments
```


## 📦 Folder Overview
- `src/components/` — UI elements, investment cards, modals (create, delete, graph, entry)
- `src/pages/` — Main pages: Home, About, Investment details, 404
- `src/lib/` — Utility functions for currency formatting, date formatting, and API service
- `src/layouts/` — Main layout wrapper
- `public/` — Static assets (images, favicon)

## 🧩 Main UI Components
- **Home.jsx**: Dashboard with summary and investment details
- **Header.astro**: Navigation bar
- **LoadingSpinner.jsx**: Loading indicator
- **Investments/Details.jsx**: List and actions for investments
- **Investments/Summary.jsx**: Summary stats
- **Investments/Investment.jsx**: Investment entry management
- **Modals**: CreateInvestmentModal, CreateEntryModal, ConfirmDeleteModal, InvestmentGraphModal

## 📄 Pages
- **index.astro**: Main dashboard
- **about.astro**: App description
- **investment.astro**: Investment details and entries
- **404.astro**: Not found page


   
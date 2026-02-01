# Forecast Feature Implementation Plan

## Problem Statement
Enable forecasting for investments with multiple strategies, each containing three scenarios (pessimist, neutral, optimist), each with its own monthly growth rate but sharing a common end date. Users can create multiple forecasts per investment, compare them to real results, and visualize all together.

## Approach
- Add a new backend model for forecasts, not stored in Google Sheets.
- Each forecast has a name, start date, end date, and three scenario rates.
- Allow multiple forecasts per investment.
- Expose CRUD API endpoints for forecasts.
- Update UI to display and compare forecasts vs real data, supporting multiple strategies.

## Workplan

## Technical Design

### Backend (Java, Spring Boot)

#### Forecast Model
- `Forecast` class: id, investmentId, name, startDate (LocalDate), endDate (LocalDate), scenarioRates (object with pessimist, neutral, optimist), createdAt, updatedAt.
- `ForecastScenario` (optional): encapsulate scenario name and rate if needed for extensibility.
- Each forecast is linked to an investment by investmentId.

#### Persistence
- Store all forecasts in a dedicated "Forecasts" tab in the same Google Sheets file as investments, using the backend's GoogleSheetsService/Client.
- Forecasts are kept separate from investment and entry data by tab name; no data is exposed in the public repository.
- Each row in the "Forecasts" tab represents a single forecast scenario for an investment, with columns:
  - Forecast ID, Investment ID, Forecast Name, Scenario (Pessimist/Neutral/Optimist), Start Date, End Date, Monthly Growth Rate (%), Created At, Updated At
- To support multiple scenarios per forecast, each scenario (pessimist, neutral, optimist) is a separate row, grouped by Forecast ID and Investment ID.
- Optionally, store calculated forecasted values as a JSON string or as additional columns (e.g., Value Month 1, Value Month 2, ... Value Month N) if needed for fast graphing.
- On first read, load all forecasts from the "Forecasts" tab into an in-memory repository, grouping by Forecast ID and Investment ID.
- On update (create/edit/delete), update the in-memory repository and then rebuild the full list of forecast rows (with headers), using writeToSheet to clear and rewrite the entire "Forecasts" tab—mirroring the investment flow.
- No persistent database is used; all persistence is via Google Sheets and in-memory storage during runtime.
- `ForecastRepository`: CRUD operations, manages in-memory storage, reads/writes to the "Forecasts" tab, manages unique IDs and scenario grouping.
- `ForecastService`: Business logic for creating, updating, deleting, and listing forecasts. Validates input, calculates forecasted values per scenario.

#### API Layer
- `ForecastController`: REST endpoints:
  - `GET /forecasts/{investmentId}`: List all forecasts for an investment
  - `POST /forecasts/{investmentId}`: Create forecast
  - `PUT /forecasts/{forecastId}`: Update forecast
  - `DELETE /forecasts/{forecastId}`: Delete forecast
- Integrate with existing investment endpoints as needed for combined data.

#### Unit Testing
- Use JUnit and Mockito as in existing tests (see `InvestmentServiceTest`, `InvestmentControllerTest`).
- Test repository (file I/O, CRUD), service (validation, calculation), and controller (API contract, error handling).

### Frontend (React, Astro)

#### Data Model
- Extend investment details to include forecasts (fetched via new API endpoints).
- Forecast: id, name, startDate, endDate, scenarioRates, scenarioResults (array of values per month per scenario).

#### Components
- `ForecastModal`: Create/edit forecast (select start entry, set rates, set end date/months, name).
- `ForecastList`: List all forecasts for an investment, with edit/delete actions.
- `ForecastGraph`: Show one forecast (all scenarios vs real), and a combined graph (all forecasts/scenarios vs real).
- Update `Investment.jsx` and `InvestmentGraphModal.tsx` to add "Forecast" button and integrate new graphs.

#### Service Layer
- Extend `InvestmentService.js` to add forecast CRUD methods (calls new API endpoints).

#### Unit/Integration Testing
- Follow patterns from `CreateInvestmentModal`, `CreateEntryModal`, and existing tests.
- Test modal validation, API calls, graph rendering, and scenario calculations.

---

- [x] Analyze current investment and entry models
- [x] Analyze UI structure for investment display
- [x] Clarify requirements with user
- [x] Design forecast data model (Java)
- [x] Design forecast repository/service (Java)
- [x] Add forecast CRUD endpoints (Java, Spring)
- [x] Update API to serve forecasts with investments
- [x] Integrate backend with Google Sheets for reading/writing forecasts (follow the pattern used for investments: create a GoogleSheetsService for forecasts, adapt model for sheet conversion, and update service/repository to use it)
  - Implemented GoogleSheetsForecastService, ForecastAdapter, and utility methods
  - Added and verified unit tests for all new integration logic
  - [x] Create a Spring @Bean for GoogleSheetsForecastService in a configuration class
  - [x] Inject GoogleSheetsForecastService into ForecastService (not ForecastRepository)
  - [x] Update ForecastService to load/save forecasts from/to Google Sheets, keeping ForecastRepository as in-memory only (mirroring InvestmentService pattern)
- [x] Update UI: add forecast creation modal/form (choose investment entry as start date, set monthly growth rates, set end date by number of months/years)
    - Implemented as CreateForecastModal.jsx, integrated in Investment.jsx with full form and validation.
- [x] Update UI: allow deleting forecasts
- [ ] Implement backend: allow editing forecasts (update endpoint, service, repository)
- [ ] Update UI: allow editing forecasts (deferred until backend ready)
- [ ] Update UI: display forecasted vs real data in graphs (one graph per forecast vs real, and a combined graph with all forecasts/scenarios vs real)
- [ ] Update UI: allow switching/comparing multiple forecasts
- [ ] Add "Forecast" button in Investment Details view for forecast graphs
- [ ] Document feature in README

### Next Steps
- Implement backend model, repository, and service for forecasts.
- Add REST endpoints and test coverage.
- Implement frontend modal, list, and graph components for forecasts.
- Integrate with investment details and update UI flows.


## Notes
- Forecasts are static after creation but should be editable for corrections.
- Each scenario in a forecast has its own monthly growth rate, but all share the same end date.
- Forecasts are not stored in Google Sheets, but in a local forecasts.json file in the backend repository for persistence across restarts.
- When creating a forecast, users select an investment entry as the start date, set monthly growth rates for each scenario, and specify the end date (by number of months/years).
- Users can edit or delete forecasts and all their parameters.
- UI should allow users to select, display, and compare forecasts. In the Investment Details view, a new "Forecast" button shows:
  - One graph per forecast (all scenarios vs real)
  - A combined graph with all forecasts/scenarios and the real performance
- Graphs should show real data and all selected forecast scenarios.

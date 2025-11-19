# Portfolio Tracker - Clean Architecture Implementation

This project implements a portfolio tracking system using Clean Architecture principles based on the provided UML diagram.

## Architecture Overview

The application follows Clean Architecture with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                         View Layer                          │
│  (PortfolioPage - Swing UI)                                 │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│                   Interface Adapters                        │
│  Controllers: TradingController                             │
│  Presenters:  PortfolioPresenter                            │
│  ViewModels:  PortfolioViewModel, PositionView              │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│                      Use Cases                              │
│  TrackPortfolioInteractor                                   │
│  Input/Output Boundaries & DTOs                             │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────┐
│                  Domain Entities                            │
│  Portfolio, Position, Trade, PricePoint                     │
└─────────────────────────────────────────────────────────────┘
                     ▲
                     │
┌────────────────────┴────────────────────────────────────────┐
│                   Data Access Layer                         │
│  Repositories: SupabasePortfolioRepository (REST API)       │
│  Gateways:     AlphaVantageGateway                         │
│  Interfaces:   PortfolioRepository, StockDataGateway        │
│  Database:     Supabase (PostgreSQL via PostgREST)          │
└─────────────────────────────────────────────────────────────┘
```

## Package Structure

```
src/main/java/
├── app/                                    # Application entry point
│   ├── Main.java                          # Dependency injection & setup
│   └── MainFrame.java                     # Main window
│
├── entity/                                 # Domain entities (business logic)
│   ├── Portfolio.java                     # Aggregate root
│   ├── Position.java                      # Stock position
│   ├── Trade.java                         # Individual trade
│   └── PricePoint.java                    # Price data point
│
├── use_case/                              # Application business rules
│   └── track_portfolio/
│       ├── TrackPortfolioInputBoundary.java
│       ├── TrackPortfolioOutputBoundary.java
│       ├── TrackPortfolioInputData.java
│       ├── TrackPortfolioOutputData.java
│       └── TrackPortfolioInteractor.java  # Use case implementation
│
├── interface_adapter/                     # Interface adapters
│   ├── controller/
│   │   ├── PortfolioController.java       # Controller interface (abstraction)
│   │   └── TradingController.java         # Handles UI events (implements PortfolioController)
│   ├── presenter/
│   │   └── PortfolioPresenter.java        # Transforms data for view (depends on PortfolioView)
│   ├── view/
│   │   └── PortfolioView.java             # View interface (abstraction)
│   └── view_model/
│       ├── PortfolioViewModel.java        # View-specific data
│       └── PositionView.java              # Position display data
│
├── view/                                  # UI implementation (Swing)
│   └── PortfolioPage.java                 # Portfolio display panel
│
└── data_access/                           # External interfaces & implementations
    ├── PriceProvider.java                 # Interface for price data
    ├── PortfolioRepository.java           # Portfolio persistence interface
    ├── StockDataGateway.java              # Stock data interface
    ├── InMemoryPortfolioRepository.java   # In-memory implementation
    ├── AlphaVantageGateway.java           # Stock data API (stub)
    └── SupabasePortfolioRepository.java   # Database implementation (placeholder)
```

## Key Components

### Entities (Domain Layer)

- **Portfolio**: Manages positions, cash, and calculates gains
- **Position**: Represents stock holdings with trades
- **Trade**: Individual buy/sell transaction
- **PricePoint**: Historical price data

### Use Case: Track Portfolio

- **Input**: Portfolio ID, User ID, Date
- **Output**: Positions, Realized/Unrealized Gains, Snapshot Time
- **Interactor**: Fetches portfolio, gets prices, computes gains

### Interface Adapters

- **TradingController**: Converts UI events to use case calls
- **PortfolioPresenter**: Transforms output data to view models
- **ViewModels**: UI-friendly data structures

### Data Access

- **SupabaseClient**: REST API client for Supabase (PostgreSQL via PostgREST)
- **SupabasePortfolioRepository**: Production portfolio storage using Supabase
- **SupabasePositionRepository**: Position data with Row Level Security (RLS)
- **SupabaseTradeRepository**: Trade history storage
- **SupabasePriceRepository**: Price data storage
- **SupabaseCompanyRepository**: Company information (read-only for regular users)
- **AlphaVantageGateway**: Stock price API integration
- **PostgreSQL Repositories**: Legacy JDBC implementations (used in tests)

## Running the Application

### Prerequisites

1. **Supabase Account**: Create a project at [supabase.com](https://supabase.com)
2. **Environment Variables**: Create a `.env` file in the project root:

```env
# Supabase Configuration
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key-here
SUPABASE_SERVICE_ROLE_KEY=your-service-role-key

# API Keys
ALPHA_VANTAGE_API_KEY=your-api-key

# Application Settings
APP_ENV=development
```

3. **Database Schema**: Run the schema migration in your Supabase project (see `migrations/` folder)

### Compile and Run

```bash
# Compile the project
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="app.Main"

# Or package and run JAR
mvn package
java -jar target/TeamProject-1.0-SNAPSHOT.jar
```

### Sample Data

The application includes sample portfolio data:

- Portfolio ID: `portfolio-001`
- User ID: `user-001`
- Sample positions: AAPL, GOOGL, MSFT

## TODO Items

### High Priority

1. **Authentication & User Management**

   - [ ] Implement Supabase Auth integration
   - [ ] Add user login/signup UI
   - [ ] Handle JWT tokens for authenticated requests
   - [ ] Implement user session management

2. **Price Data Integration**

   - [ ] Implement real Alpha Vantage API calls
   - [ ] Handle API rate limits
   - [ ] Implement price caching in Supabase
   - [ ] Add scheduled price updates

3. **Gain Calculations**

   - [ ] Verify realized gains logic
   - [ ] Implement FIFO/LIFO cost basis methods
   - [ ] Add tax lot tracking

4. **Database Migration**
   - [x] Design Supabase schema
   - [x] Implement Supabase repositories
   - [x] Update entity classes for new schema
   - [ ] Create test data seeding scripts
   - [ ] Set up Row Level Security (RLS) policies

### Medium Priority

4. **UI Enhancements**

   - [ ] Add portfolio creation/editing
   - [ ] Add buy/sell trade entry
   - [ ] Add charts and visualizations
   - [ ] Implement multi-portfolio support

5. **Error Handling**

   - [ ] Add comprehensive validation
   - [ ] Implement error logging
   - [ ] Add user-friendly error messages

6. **Testing**
   - [ ] Write unit tests for entities
   - [ ] Write integration tests for use cases
   - [ ] Add UI tests

### Low Priority

7. **Features**

   - [ ] Export portfolio to CSV/PDF
   - [ ] Add performance metrics
   - [ ] Implement portfolio comparison
   - [ ] Add alerts for price changes

8. **Configuration**
   - [ ] Externalize configuration
   - [ ] Add environment profiles (dev/prod)
   - [ ] Implement proper DI framework

## Design Principles

This implementation follows:

- ✅ **Clean Architecture**: Dependencies point inward, entities are infrastructure-independent
- ✅ **Dependency Inversion**: Abstractions defined by use cases, not infrastructure
- ✅ **Single Responsibility**: Each class has one reason to change
- ✅ **Interface Segregation**: Entities don't depend on infrastructure interfaces
- ✅ **Liskov Substitution**: No problematic inheritance relationships
- ✅ **Open/Closed**: Open for extension, closed for modification

## Dependencies

- Java 11
- Maven 3.x
- JUnit 5 (for testing)
- Swing (for UI)

## Notes

- All TODO comments in code indicate incomplete functionality
- Mock data is used for stock prices (replace with real API)
- In-memory storage is used (add database for production)
- Error handling is minimal (needs improvement)

## Contributing

When adding features:

1. Start from the domain entities if adding business logic
2. Create/modify use cases for application logic
3. Add controllers/presenters for UI coordination
4. Update views as needed
5. Maintain the dependency rule (dependencies point inward)

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
│  Repositories: InMemoryPortfolioRepository                  │
│  Gateways:     AlphaVantageGateway                         │
│  Interfaces:   PortfolioRepository, StockDataGateway        │
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

- **InMemoryPortfolioRepository**: Development/testing storage
- **AlphaVantageGateway**: Stock price API (currently mock data)
- **SupabasePortfolioRepository**: Production database (placeholder)

## Running the Application

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

1. **Price Data Integration**

   - [ ] Implement real Alpha Vantage API calls
   - [ ] Add API key configuration
   - [ ] Handle API rate limits
   - [ ] Implement price caching

2. **Gain Calculations**

   - [ ] Verify realized gains logic
   - [ ] Implement FIFO/LIFO cost basis methods
   - [ ] Add tax lot tracking

3. **Database Integration**
   - [ ] Complete Supabase repository implementation
   - [ ] Design database schema
   - [ ] Implement migration scripts

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

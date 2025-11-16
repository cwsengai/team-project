package data_access;

import java.util.Optional;

import entity.Company;

/**
 * Test the PostgresCompanyRepository CRUD operations.
 */
public class TestCompanyRepository {
    
    public static void main(String[] args) {
        System.out.println("=== Company Repository Test ===\n");
        
        PostgresCompanyRepository repo = new PostgresCompanyRepository();
        
        try {
            // Test 1: Create a company
            System.out.println("Test 1: Creating Apple Inc...");
            String appleId = java.util.UUID.randomUUID().toString();
            Company apple = new Company(
                appleId,
                "AAPL",
                "Apple Inc.",
                "Technology",
                "Consumer Electronics",
                "NASDAQ",
                3000000000000.0, // $3T
                "Apple designs and manufactures consumer electronics, software, and online services.",
                java.time.LocalDateTime.now()
            );
            
            repo.save(apple);
            System.out.println("✓ Company saved\n");
            
            // Test 2: Find by ticker
            System.out.println("Test 2: Finding AAPL by ticker...");
            Optional<Company> found = repo.findByTicker("AAPL");
            
            if (found.isPresent()) {
                Company c = found.get();
                System.out.println("✓ Found company:");
                System.out.println("  ID: " + c.getId());
                System.out.println("  Ticker: " + c.getTicker());
                System.out.println("  Name: " + c.getName());
                System.out.println("  Sector: " + c.getSector());
                System.out.println("  Market Cap: $" + c.getMarketCap());
            } else {
                System.out.println("✗ Company not found");
            }
            System.out.println();
            
            // Test 3: Update company (upsert)
            System.out.println("Test 3: Updating AAPL market cap...");
            Company appleUpdated = new Company(
                apple.getId(),
                apple.getTicker(),
                apple.getName(),
                apple.getSector(),
                apple.getIndustry(),
                apple.getExchange(),
                3100000000000.0, // $3.1T
                apple.getDescription(),
                apple.getCreatedAt()
            );
            repo.save(appleUpdated);
            
            Optional<Company> updated = repo.findByTicker("AAPL");
            if (updated.isPresent()) {
                System.out.println("✓ Updated market cap: $" + updated.get().getMarketCap());
            }
            System.out.println();
            
            // Test 4: Create another company
            System.out.println("Test 4: Creating Microsoft...");
            String msftId = java.util.UUID.randomUUID().toString();
            Company msft = new Company(
                msftId,
                "MSFT",
                "Microsoft Corporation",
                "Technology",
                "Software",
                "NASDAQ",
                2800000000000.0,
                "Microsoft develops and licenses software, services, devices and solutions.",
                java.time.LocalDateTime.now()
            );
            repo.save(msft);
            System.out.println("✓ Company saved\n");
            
            // Test 5: Find by sector
            System.out.println("Test 5: Finding all Technology companies...");
            var techCompanies = repo.findBySector("Technology");
            System.out.println("✓ Found " + techCompanies.size() + " companies:");
            for (Company c : techCompanies) {
                System.out.println("  - " + c.getTicker() + ": " + c.getName());
            }
            
            System.out.println("\n✅ All tests passed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

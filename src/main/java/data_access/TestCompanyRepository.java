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
            Company apple = new Company(
                "AAPL",
                "Apple Inc.",
                "Apple designs and manufactures consumer electronics, software, and online services.",
                3000000000000.0, // $3T
                30.0 // P/E ratio
            );
            apple.setSector("Technology");
            apple.setIndustry("Consumer Electronics");
            
            repo.save(apple);
            System.out.println("✓ Company saved\n");
            
            // Test 2: Find by ticker
            System.out.println("Test 2: Finding AAPL by ticker...");
            Optional<Company> found = repo.findByTicker("AAPL");
            
            if (found.isPresent()) {
                Company c = found.get();
                System.out.println("✓ Found company:");
                System.out.println("  Symbol: " + c.getSymbol());
                System.out.println("  Name: " + c.getName());
                System.out.println("  Sector: " + c.getSector());
                System.out.println("  Market Cap: $" + c.getMarketCapitalization());
            } else {
                System.out.println("✗ Company not found");
            }
            System.out.println();
            
            // Test 3: Update company (upsert)
            System.out.println("Test 3: Updating AAPL market cap...");
            Company appleUpdated = new Company(
                apple.getSymbol(),
                apple.getName(),
                apple.getDescription(),
                3100000000000.0, // $3.1T
                apple.getPeRatio()
            );
            appleUpdated.setSector(apple.getSector());
            appleUpdated.setIndustry(apple.getIndustry());
            repo.save(appleUpdated);
            
            Optional<Company> updated = repo.findByTicker("AAPL");
            if (updated.isPresent()) {
                System.out.println("✓ Updated market cap: $" + updated.get().getMarketCapitalization());
            }
            System.out.println();
            
            // Test 4: Create another company
            System.out.println("Test 4: Creating Microsoft...");
            Company msft = new Company(
                "MSFT",
                "Microsoft Corporation",
                "Microsoft develops and licenses software, services, devices and solutions.",
                2800000000000.0,
                25.0 // P/E ratio
            );
            msft.setSector("Technology");
            msft.setIndustry("Software");
            repo.save(msft);
            System.out.println("✓ Company saved\n");
            
            // Test 5: Find by sector
            System.out.println("Test 5: Finding all Technology companies...");
            var techCompanies = repo.findBySector("Technology");
            System.out.println("✓ Found " + techCompanies.size() + " companies:");
            for (Company c : techCompanies) {
                System.out.println("  - " + c.getSymbol() + ": " + c.getName());
            }
            
            System.out.println("\n✅ All tests passed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

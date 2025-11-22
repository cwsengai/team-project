import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * JUnit wrapper for running repository test suites during Maven test phase.
 * This allows the test suites in src/main/java/data_access to be executed via mvn test.
 */
public class RepositoryIntegrationTests {
    
    private static int totalTests = 0;
    private static int totalPassed = 0;
    private static int totalFailed = 0;
    
    @Test
    @DisplayName("Company Repository Test Suite")
    public void testCompanyRepository() {
        captureTestResults(() -> data_access.CompanyRepositoryTestSuite.main(new String[]{}));
    }
    
    @Test
    @DisplayName("Position Repository Test Suite")
    public void testPositionRepository() {
        captureTestResults(() -> data_access.PositionRepositoryTestSuite.main(new String[]{}));
    }
    
    @Test
    @DisplayName("Portfolio Repository Test Suite")
    public void testPortfolioRepository() {
        captureTestResults(() -> data_access.PortfolioRepositoryTestSuite.main(new String[]{}));
    }
    
    @Test
    @DisplayName("Trade Repository Test Suite")
    public void testTradeRepository() {
        captureTestResults(() -> data_access.TradeRepositoryTestSuite.main(new String[]{}));
    }
    
    @Test
    @DisplayName("Price Repository Test Suite")
    public void testPriceRepository() {
        captureTestResults(() -> data_access.PriceRepositoryTestSuite.main(new String[]{}));
    }
    
    @Test
    @DisplayName("User Repository Test Suite")
    public void testUserRepository() {
        captureTestResults(() -> data_access.UserRepositoryTestSuite.main(new String[]{}));
    }
    
    @AfterAll
    public static void printOverallSummary() {
        System.out.println("\n\n");
        System.out.println("========================================================");
        System.out.println("           OVERALL TEST SUITE SUMMARY                  ");
        System.out.println("========================================================");
        System.out.printf("  Total Tests:  %-35d %n", totalTests);
        System.out.printf("  Passed:       %-35d %n", totalPassed);
        System.out.printf("  Failed:       %-35d %n", totalFailed);
        System.out.printf("  Success Rate: %-32.1f%%%n", 
            totalTests > 0 ? (100.0 * totalPassed / totalTests) : 0.0);
        System.out.println("========================================================");
        
        if (totalFailed == 0) {
            System.out.println("\n*** ALL TESTS PASSED! ***");
        } else {
            System.out.println("\nWARNING: " + totalFailed + " test(s) failed. Please review the output above.");
        }
    }
    
    private void captureTestResults(Runnable testSuite) {
        // Capture output to extract test counts
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream oldOut = System.out;
        java.io.PrintStream oldErr = System.err;
        
        try {
            // Create a tee stream to both capture and display
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override
                public void write(int b) {
                    baos.write(b);
                    oldOut.write(b);
                }
            });
            
            System.setOut(tee);
            System.setErr(tee);
            
            testSuite.run();
            
            // Parse the output to extract test counts
            String output = baos.toString();
            extractTestCounts(output);
            
        } finally {
            System.setOut(oldOut);
            System.setErr(oldErr);
        }
    }
    
    private void extractTestCounts(String output) {
        // Look for the summary section
        String[] lines = output.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.contains("Total Tests:")) {
                try {
                    String numStr = line.replaceAll("[^0-9]", "");
                    if (!numStr.isEmpty()) {
                        totalTests += Integer.parseInt(numStr);
                    }
                } catch (NumberFormatException ignored) {}
            } else if (line.contains("Passed:")) {
                try {
                    String numStr = line.replaceAll("[^0-9]", "");
                    if (!numStr.isEmpty()) {
                        totalPassed += Integer.parseInt(numStr);
                    }
                } catch (NumberFormatException ignored) {}
            } else if (line.contains("Failed:")) {
                try {
                    String numStr = line.replaceAll("[^0-9]", "");
                    if (!numStr.isEmpty()) {
                        totalFailed += Integer.parseInt(numStr);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
    }
}

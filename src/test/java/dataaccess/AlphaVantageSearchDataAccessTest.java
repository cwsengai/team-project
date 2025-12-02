package dataaccess;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import entity.Company;

class AlphaVantageSearchDataAccessTest {

    private AlphaVantageSearchDataAccess searchDataAccess;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        List<Company> testCompanies = Arrays.asList(
                createCompany("AAPL", "Apple Inc"),
                createCompany("MSFT", "Microsoft Corporation"),
                createCompany("GOOGL", "Alphabet Inc")
        );
        searchDataAccess = new AlphaVantageSearchDataAccess(testCompanies);
    }

    @Test
    void testSearchByTicker() {
        List<Company> results = searchDataAccess.searchCompanies("AAPL");

        assertEquals(1, results.size());
        assertEquals("AAPL", results.get(0).getSymbol());
    }

    @Test
    void testSearchByName() {
        List<Company> results = searchDataAccess.searchCompanies("Apple");

        assertEquals(1, results.size());
        assertEquals("Apple Inc", results.get(0).getName());
    }

    @Test
    void testSearchPartialMatch() {
        List<Company> results = searchDataAccess.searchCompanies("micro");

        // Should find Microsoft (either from cached or from Top100Companies ticker list)
        // The actual number might be 1 or 2 depending on if MSFT is in Top100Companies
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(c ->
                c.getSymbol().equals("MSFT") || c.getName().contains("Microsoft")
        ));
    }

    @Test
    void testSearchCaseInsensitive() {
        List<Company> results = searchDataAccess.searchCompanies("apple");

        assertEquals(1, results.size());
        assertEquals("AAPL", results.get(0).getSymbol());
    }

    @Test
    void testSearchEmptyQuery() {
        List<Company> results = searchDataAccess.searchCompanies("");

        assertEquals(3, results.size());
    }

    @Test
    void testSearchNoResults() {
        List<Company> results = searchDataAccess.searchCompanies("NONEXISTENT12345");

        // Should return 0 if ticker doesn't exist in Top100Companies
        assertEquals(0, results.size());
    }

    @Test
    void testUpdateCache() {
        Company newCompany = createCompany("TSLA", "Tesla Inc");
        searchDataAccess.updateCache(List.of(newCompany));

        List<Company> results = searchDataAccess.searchCompanies("TSLA");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(c -> c.getSymbol().equals("TSLA")));
    }

    @Test
    void testSearchByExactTicker() {
        List<Company> results = searchDataAccess.searchCompanies("GOOGL");

        assertEquals(1, results.size());
        assertEquals("GOOGL", results.get(0).getSymbol());
    }

    private Company createCompany(String symbol, String name) {
        return new Company(
                symbol, name, "", "", "", "USA",
                1000000000L, 0, 0, 0, 0, 0,
                List.of(), List.of()
        );
    }
}
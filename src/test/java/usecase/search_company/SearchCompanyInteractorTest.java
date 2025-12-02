package usecase.search_company;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import entity.Company;

class SearchCompanyInteractorTest {

    private SearchCompanyInteractor interactor;
    private MockSearchDataAccess dataAccess;
    private MockSearchPresenter presenter;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        dataAccess = new MockSearchDataAccess();
        presenter = new MockSearchPresenter();
        interactor = new SearchCompanyInteractor(dataAccess, presenter);
    }

    @Test
    void testExecute_FindsCompanies() {
        // Arrange
        Company apple = createTestCompany("AAPL", "Apple Inc");
        dataAccess.setSearchResults(List.of(apple));

        // Act
        SearchCompanyInputData inputData = new SearchCompanyInputData("AAPL");
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessCalled());
        assertEquals(1, presenter.getResults().size());
        assertEquals("AAPL", presenter.getResults().get(0).getSymbol());
    }

    @Test
    void testExecute_NoResults() {
        // Arrange
        dataAccess.setSearchResults(List.of());

        // Act
        SearchCompanyInputData inputData = new SearchCompanyInputData("NONEXISTENT");
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasSuccessCalled());
        assertEquals(0, presenter.getResults().size());
    }

    @Test
    void testExecute_EmptyQuery() {
        // Arrange - Set some cached companies that would be returned for empty query
        Company apple = createTestCompany("AAPL", "Apple Inc");
        Company microsoft = createTestCompany("MSFT", "Microsoft");
        dataAccess.setSearchResults(Arrays.asList(apple, microsoft));

        // Act - Should not throw exception for empty query
        SearchCompanyInputData inputData = new SearchCompanyInputData("");

        // Assert - Just verify it doesn't crash
        assertDoesNotThrow(() -> interactor.execute(inputData));
    }

    @Test
    void testExecute_DataAccessThrowsException() {
        // Arrange
        dataAccess.setShouldThrowException(true);

        // Act
        SearchCompanyInputData inputData = new SearchCompanyInputData("AAPL");
        interactor.execute(inputData);

        // Assert
        assertTrue(presenter.wasFailureCalled());
        assertNotNull(presenter.getErrorMessage());
    }

    // Helper method
    private Company createTestCompany(String symbol, String name) {
        return new Company(
                symbol, name, "", "", "", "USA",
                1000000000L, 0, 0, 0, 0, 0,
                List.of(), List.of()
        );
    }

    // Mock implementations
    private static class MockSearchDataAccess implements SearchCompanyDataAccess {
        private List<Company> searchResults;
        private boolean shouldThrowException = false;

        public void setSearchResults(List<Company> results) {
            this.searchResults = results;
        }

        public void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }

        @Override
        public List<Company> searchCompanies(String query) {
            if (shouldThrowException) {
                throw new RuntimeException("Search error");
            }
            return searchResults;
        }
    }

    private static class MockSearchPresenter implements SearchCompanyOutputBoundary {
        private boolean successCalled = false;
        private boolean failureCalled = false;
        private List<Company> results;
        private String errorMessage;

        @Override
        public void presentSearchResults(SearchCompanyOutputData outputData) {
            this.successCalled = true;
            this.results = outputData.companies();
        }

        @Override
        public void presentError(String error) {
            this.failureCalled = true;
            this.errorMessage = error;
        }

        public boolean wasSuccessCalled() { return successCalled; }
        public boolean wasFailureCalled() { return failureCalled; }
        public List<Company> getResults() { return results; }
        public String getErrorMessage() { return errorMessage; }
    }
}
package usecase.company_list;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import entity.Company;

class CompanyListInteractorTest {

    private CompanyListInteractor interactor;
    private MockCompanyListDataAccess dataAccess;
    private MockCompanyListPresenter presenter;

    @BeforeEach
    void setUp() {
        dataAccess = new MockCompanyListDataAccess();
        presenter = new MockCompanyListPresenter();
        interactor = new CompanyListInteractor(dataAccess, presenter);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        Company company1 = createTestCompany("AAPL", "Apple Inc");
        Company company2 = createTestCompany("MSFT", "Microsoft");
        dataAccess.setCompanies(Arrays.asList(company1, company2));

        // Act
        CompanyListInputData inputData = new CompanyListInputData();
        interactor.execute();

        // Assert
        assertTrue(presenter.wasSuccessCalled());
        assertFalse(presenter.wasFailureCalled());
        assertEquals(2, presenter.getCompanies().size());
        assertEquals("AAPL", presenter.getCompanies().get(0).getSymbol());
    }

    @Test
    void testExecute_EmptyList() {
        // Arrange
        dataAccess.setCompanies(List.of());

        // Act
        CompanyListInputData inputData = new CompanyListInputData();
        interactor.execute();

        // Assert
        assertTrue(presenter.wasSuccessCalled());
        assertEquals(0, presenter.getCompanies().size());
    }

    @Test
    void testExecute_DataAccessThrowsException() {
        // Arrange
        dataAccess.setShouldThrowException(true);

        // Act
        CompanyListInputData inputData = new CompanyListInputData();
        interactor.execute();

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
    private static class MockCompanyListDataAccess implements CompanyListDataAccess {
        private List<Company> companies;
        private boolean shouldThrowException = false;

        public void setCompanies(List<Company> companies) {
            this.companies = companies;
        }

        public void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }

        @Override
        public List<Company> getCompanyList() {
            if (shouldThrowException) {
                throw new RuntimeException("Data access error");
            }
            return companies;
        }
    }

    private static class MockCompanyListPresenter implements CompanyListOutputBoundary {
        private boolean successCalled = false;
        private boolean failureCalled = false;
        private List<Company> companies;
        private String errorMessage;

        @Override
        public void presentCompanyList(CompanyListOutputData outputData) {
            this.successCalled = true;
            this.companies = outputData.getCompanies();
        }

        @Override
        public void presentError(String error) {
            this.failureCalled = true;
            this.errorMessage = error;
        }

        public boolean wasSuccessCalled() { return successCalled; }
        public boolean wasFailureCalled() { return failureCalled; }
        public List<Company> getCompanies() { return companies; }
        public String getErrorMessage() { return errorMessage; }
    }
}
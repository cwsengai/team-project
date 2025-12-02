package usecase.company;

import entity.Company;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompanyInteractorTest {

    @Test
    void testCompanyNotFoundTriggersError() {
        // Fake gateway returning a company with no name
        CompanyGateway fakeGateway = symbol -> new Company(symbol, "");

        // Capture output from presenter
        final StringBuilder errorCaptured = new StringBuilder();

        CompanyOutputBoundary fakePresenter = new CompanyOutputBoundary() {
            @Override
            public void presentCompany(CompanyOutputData data) {
                fail("Should not present company when name is missing");
            }

            @Override
            public void presentError(String message) {
                errorCaptured.append(message);
            }
        };

        CompanyInteractor interactor = new CompanyInteractor(fakeGateway, fakePresenter);

        interactor.execute(new CompanyInputData("FAKE"));

        assertTrue(errorCaptured.toString().contains("Company not found"),
                "Expected error message when company name is missing");
    }

    @Test
    void testValidCompanyTriggersPresenter() {
        CompanyGateway fakeGateway = symbol -> new Company(symbol, "Tesla");

        final CompanyOutputData[] captured = new CompanyOutputData[1];

        CompanyOutputBoundary fakePresenter = new CompanyOutputBoundary() {
            @Override
            public void presentCompany(CompanyOutputData data) {
                captured[0] = data;
            }

            @Override
            public void presentError(String message) {
                fail("Should not show error for valid company");
            }
        };

        CompanyInteractor interactor = new CompanyInteractor(fakeGateway, fakePresenter);

        interactor.execute(new CompanyInputData("TSLA"));

        assertNotNull(captured[0], "Presenter should receive output data");
        assertEquals("Tesla", captured[0].name());
        assertEquals("TSLA", captured[0].symbol());
    }
}

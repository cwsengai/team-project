package usecase.financial_statement;

import entity.FinancialStatement;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinancialStatementInteractorTest {

    @Test
    void testNoStatementsTriggersError() {
        // Fake gateway returns null
        FinancialStatementGateway fakeGateway = symbol -> null;

        StringBuilder errorCaptured = new StringBuilder();

        FinancialStatementOutputBoundary fakePresenter = new FinancialStatementOutputBoundary() {
            @Override
            public void presentFinancialStatement(FinancialStatementOutputData data) {
                fail("Should not present financial statements when none exist");
            }

            @Override
            public void presentError(String message) {
                errorCaptured.append(message);
            }
        };

        FinancialStatementInteractor interactor =
                new FinancialStatementInteractor(fakeGateway, fakePresenter);

        interactor.execute(new FinancialStatementInputData("FAKE"));

        assertTrue(errorCaptured.toString().contains("No financial statements found"),
                "Expected an error message when statements list is null");
    }

    @Test
    void testValidStatementsTriggerPresenter() {
        // Create a fake FinancialStatement entity
        FinancialStatement fs = new FinancialStatement(
                "TSLA", "USD",
                LocalDate.of(2023, 12, 31),
                1000, 500, 300,
                2000, 1500, 300, 800, 400, 600,
                900, 200, 100, 50, 20
        );

        FinancialStatementGateway fakeGateway = symbol -> List.of(fs);

        final FinancialStatementOutputData[] captured = new FinancialStatementOutputData[1];

        FinancialStatementOutputBoundary fakePresenter = new FinancialStatementOutputBoundary() {
            @Override
            public void presentFinancialStatement(FinancialStatementOutputData data) {
                captured[0] = data;
            }

            @Override
            public void presentError(String message) {
                fail("Should not present error for valid statements");
            }
        };

        FinancialStatementInteractor interactor =
                new FinancialStatementInteractor(fakeGateway, fakePresenter);

        interactor.execute(new FinancialStatementInputData("TSLA"));

        assertNotNull(captured[0], "Output should be captured");
        assertEquals("TSLA", captured[0].symbol());
        assertFalse(captured[0].statements().isEmpty());
    }
}

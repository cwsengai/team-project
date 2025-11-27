package interface_adapter.price_chart;

/**
 * CompanyDetailInputBoundary is ViewCompanyDetailInteractor 's Input Port)。
 * Controller depend this interface to start loading company's detailed working logic(UC3/UC5)。
 */
public interface CompanyDetailInputBoundary {

    /**
     * according to stock code loading all the company's detailed content,overview,financial and news inclusive.
     * * @param ticker the focused company's stock code (e.g. "AAPL")
     */
    void getCompanyDetail(String ticker);
}
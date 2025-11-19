package use_case;

/**
 * CompanyDetailInputBoundary is ViewCompanyDetailInteractor 's Input Port。
 * Controller depend on this interface to start loading company's detailed working logic(UC3/UC5)。
 */
public interface CompanyDetailInputBoundary {

    /**
     * according to stock code loading all the company's detailed content,overview,financial and news inclusive.
     * * @param ticker the focused company's stock code (e.g. "AAPL")
     */
    void getCompanyDetail(String ticker);
}
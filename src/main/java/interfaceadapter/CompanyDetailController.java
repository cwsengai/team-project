package interfaceadapter;

import usecase.CompanyDetailInputBoundary;

/**
 * CompanyDetailController accept actions from View（e.g. click on company name），
 * call CompanyDetailInputBoundary to start work logic
 */
public class CompanyDetailController {

    private final CompanyDetailInputBoundary detailInteractor;

    public CompanyDetailController(CompanyDetailInputBoundary detailInteractor) {
        this.detailInteractor = detailInteractor;
    }

    /**
     * execute when user click on one of the company in list
     * * @param ticker the stock code being clicked
     */
    public void handleCompanyDetailRequest(String ticker) {
        if (ticker != null && !ticker.trim().isEmpty()) {
            detailInteractor.getCompanyDetail(ticker);
        } else {
            System.err.println("Controller error: the stock code is empty");
        }
    }
}

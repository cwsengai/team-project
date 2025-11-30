package usecase.search_company;

import entity.Company;
import java.util.List;

public class SearchCompanyInteractor implements SearchCompanyInputBoundary {
    private final SearchCompanyOutputBoundary presenter;
    private final SearchCompanyDataAccess dataAccess;

    public SearchCompanyInteractor(SearchCompanyDataAccess dataAccess,
                                   SearchCompanyOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(SearchCompanyInputData inputData) {
        String query = inputData.getQuery();

        //Business Rule: Query should be at least 2 characters
        if (query == null || query.trim().length() < 2) {
            presenter.presentError("Search query must have at least 2 characters");
            return;
        }

        try {
            List<Company> results = dataAccess.searchCompanies(query);
            SearchCompanyOutputData outputData = new SearchCompanyOutputData(results, true);
            presenter.presentSearchResults(outputData);
        }catch (Exception e){
            presenter.presentError("Search Invalid" + e.getMessage());
        }
    }
}

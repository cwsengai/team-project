package use_case.search_company;

/**
 * Input Data for Search Company Use Case
 */

public class SearchCompanyInputData {
    private final String query;

    public SearchCompanyInputData(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}

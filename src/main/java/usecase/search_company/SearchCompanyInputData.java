package usecase.search_company;

/**
 * Input data for the Search Company use case.
 * Contains the search query entered by the user.
 */
public record SearchCompanyInputData(String query) {

    /**
     * Creates a new SearchCompanyInputData instance with the given query.
     *
     * @param query the keyword or text used for searching companies
     */
    public SearchCompanyInputData {
    }

    /**
     * Returns the search query.
     *
     * @return the search keyword entered by the user
     */
    @Override
    public String query() {
        return query;
    }
}

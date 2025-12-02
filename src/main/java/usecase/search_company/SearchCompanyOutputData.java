package usecase.search_company;

import java.util.List;

import entity.Company;

/**
 * Output data for Search Company use case.
 * 
 * @param companies the list of companies matching the search query
 */
public record SearchCompanyOutputData(List<Company> companies) {

}

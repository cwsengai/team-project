package usecase.search_company;

import java.util.List;

import entity.Company;

/**
 * Output data for Search Company use case.
 */
public record SearchCompanyOutputData(List<Company> companies) {

}

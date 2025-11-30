package usecase;

import entity.Company;
import java.util.List;

public interface CompanyGateway {
    //ViewCompanyListInteractorUC1
    List<Company> getTopCompanies() throws Exception;

    //ViewCompanyListInteractorUC2
    List<Company> searchByKeyword(String keyword) throws Exception;

    //ViewCompanyDetailInteractorUC3
    Company getCompanyOverview(String ticker) throws Exception;

}

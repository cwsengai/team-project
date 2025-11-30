package usecase.company;

import entity.Company;

public interface CompanyGateway {
    Company fetchOverview(String symbol);
}

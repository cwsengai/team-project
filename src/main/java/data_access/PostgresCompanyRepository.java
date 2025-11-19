package data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import entity.Company;

/**
 * PostgreSQL/Supabase implementation of CompanyRepository using JDBC.
 */
public class PostgresCompanyRepository implements CompanyRepository {
    
    private final PostgresClient client;
    
    public PostgresCompanyRepository() {
        this.client = new PostgresClient();
    }
    
    @Override
    public Optional<Company> findByTicker(String ticker) {
        String sql = "SELECT * FROM public.companies WHERE ticker = ?";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ticker);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding company by ticker: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Company> findById(String id) {
        String sql = "SELECT * FROM public.companies WHERE id = ?::uuid";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding company by id: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Company> findBySector(String sector) {
        String sql = "SELECT * FROM public.companies WHERE sector = ?";
        List<Company> companies = new ArrayList<>();
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sector);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    companies.add(mapResultSetToCompany(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding companies by sector: " + e.getMessage());
        }
        
        return companies;
    }
    
    @Override
    public Company save(Company company) {
        // Use INSERT ... ON CONFLICT to handle both insert and update
        // Let database auto-generate UUID for id
        String sql = "INSERT INTO public.companies (ticker, name, sector, industry, market_cap, description, country, eps, pe_ratio, dividend_per_share, dividend_yield, beta) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT (ticker) DO UPDATE SET " +
                     "name = EXCLUDED.name, sector = EXCLUDED.sector, industry = EXCLUDED.industry, " +
                     "market_cap = EXCLUDED.market_cap, description = EXCLUDED.description, " +
                     "country = EXCLUDED.country, eps = EXCLUDED.eps, pe_ratio = EXCLUDED.pe_ratio, " +
                     "dividend_per_share = EXCLUDED.dividend_per_share, dividend_yield = EXCLUDED.dividend_yield, beta = EXCLUDED.beta";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, company.getSymbol()); // ticker = symbol
            stmt.setString(2, company.getName());
            stmt.setString(3, company.getSector());
            stmt.setString(4, company.getIndustry());
            stmt.setDouble(5, company.getMarketCapitalization());
            stmt.setString(6, company.getDescription());
            stmt.setString(7, company.getCountry());
            
            // Set financial metrics (newly added columns)
            if (company.getEPS() != null && company.getEPS() != 0) {
                stmt.setDouble(8, company.getEPS());
            } else {
                stmt.setNull(8, java.sql.Types.DOUBLE);
            }
            
            if (company.getPeRatio() != null && company.getPeRatio() != 0) {
                stmt.setDouble(9, company.getPeRatio());
            } else {
                stmt.setNull(9, java.sql.Types.DOUBLE);
            }
            
            if (company.getDividendPerShare() != null && company.getDividendPerShare() != 0) {
                stmt.setDouble(10, company.getDividendPerShare());
            } else {
                stmt.setNull(10, java.sql.Types.DOUBLE);
            }
            
            if (company.getDividendYield() != null && company.getDividendYield() != 0) {
                stmt.setDouble(11, company.getDividendYield());
            } else {
                stmt.setNull(11, java.sql.Types.DOUBLE);
            }
            
            stmt.setDouble(12, company.getBeta() != null ? company.getBeta() : 1.0);
            
            stmt.executeUpdate();
            return company;
        } catch (Exception e) {
            System.err.println("Error saving company: " + e.getMessage());
            throw new RuntimeException("Failed to save company", e);
        }
    }
    
    @Override
    public void saveAll(List<Company> companies) {
        for (Company company : companies) {
            save(company);
        }
    }
    
    private Company mapResultSetToCompany(ResultSet rs) throws Exception {
        // Map to API Company entity using symbol as primary key
        String symbol = rs.getString("ticker"); // Use ticker as symbol
        String name = rs.getString("name");
        String description = rs.getString("description");
        double marketCap = rs.getDouble("market_cap");
        
        // Get financial metrics with NULL handling
        Double peRatio = rs.getObject("pe_ratio") != null ? rs.getDouble("pe_ratio") : null;
        Double eps = rs.getObject("eps") != null ? rs.getDouble("eps") : null;
        Double dividendPerShare = rs.getObject("dividend_per_share") != null ? rs.getDouble("dividend_per_share") : null;
        Double dividendYield = rs.getObject("dividend_yield") != null ? rs.getDouble("dividend_yield") : null;
        Double beta = rs.getObject("beta") != null ? rs.getDouble("beta") : 1.0;
        
        // Create Company using constructor
        Company company = new Company(symbol, name, description, marketCap, peRatio != null ? peRatio : 0.0);
        
        // Set additional fields
        company.setSector(rs.getString("sector"));
        company.setIndustry(rs.getString("industry"));
        company.setCountry(rs.getString("country"));
        company.setEps(eps);
        company.setDividendPerShare(dividendPerShare);
        company.setDividendYield(dividendYield);
        company.setBeta(beta);
        
        return company;
    }
}

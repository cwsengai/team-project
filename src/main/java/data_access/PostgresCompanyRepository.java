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
        String sql = "INSERT INTO public.companies (id, ticker, name, sector, industry, exchange, market_cap, description) " +
                     "VALUES (?::uuid, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT (ticker) DO UPDATE SET " +
                     "name = EXCLUDED.name, sector = EXCLUDED.sector, industry = EXCLUDED.industry, " +
                     "exchange = EXCLUDED.exchange, market_cap = EXCLUDED.market_cap, description = EXCLUDED.description";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, company.getSymbol());
            stmt.setString(2, company.getSymbol()); // ticker = symbol
            stmt.setString(3, company.getName());
            stmt.setString(4, company.getSector());
            stmt.setString(5, company.getIndustry());
            stmt.setString(6, ""); // exchange not in API entity
            stmt.setDouble(7, company.getMarketCapitalization());
            stmt.setString(8, company.getDescription());
            
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
        
        // Create Company using simple constructor (symbol, name, description, marketCap, peRatio)
        Company company = new Company(symbol, name, description, marketCap, 0.0);
        
        // Set additional fields
        company.setSector(rs.getString("sector"));
        company.setIndustry(rs.getString("industry"));
        
        return company;
    }
}

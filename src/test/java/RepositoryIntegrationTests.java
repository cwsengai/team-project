import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

/**
 * JUnit 5 test suite for repository integration tests.
 * Runs all data access layer tests (repositories, client, config).
 */
@DisplayName("Repository Integration Tests")
public class RepositoryIntegrationTests {
    
    // Supabase Repository Tests
    @Nested
    @DisplayName("Supabase Company Repository")
    class CompanyRepositoryTests extends data_access.SupabaseCompanyRepositoryTest {
    }
    
    @Nested
    @DisplayName("Supabase Portfolio Repository")
    class PortfolioRepositoryTests extends data_access.SupabasePortfolioRepositoryTest {
    }
    
    @Nested
    @DisplayName("Supabase User Repository")
    class UserRepositoryTests extends data_access.SupabaseUserRepositoryTest {
    }
    
    @Nested
    @DisplayName("Supabase Trade Repository")
    class TradeRepositoryTests extends data_access.SupabaseTradeRepositoryTest {
    }
    
    @Nested
    @DisplayName("Supabase Price Repository")
    class PriceRepositoryTests extends data_access.SupabasePriceRepositoryTest {
    }
    
    @Nested
    @DisplayName("Supabase Position Repository")
    class PositionRepositoryTests extends data_access.SupabasePositionRepositoryTest {
    }
    
    // In-Memory Repository Tests
    @Nested
    @DisplayName("InMemory Portfolio Repository")
    class InMemoryPortfolioRepositoryTests extends data_access.InMemoryPortfolioRepositoryTest {
    }
    
    // Client and Config Tests
    @Nested
    @DisplayName("Supabase Client")
    class ClientTests extends data_access.SupabaseClientTest {
    }
    
    @Nested
    @DisplayName("Environment Configuration")
    class ConfigTests extends data_access.EnvConfigTest {
    }
}

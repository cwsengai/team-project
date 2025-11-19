import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

/**
 * JUnit 5 test suite for repository integration tests.
 * Runs all Supabase repository tests.
 */
@DisplayName("Repository Integration Tests")
public class RepositoryIntegrationTests {
    
    @Nested
    @DisplayName("Supabase Company Repository")
    class CompanyRepositoryTests extends data_access.SupabaseCompanyRepositoryTest {
    }
    
    @Nested
    @DisplayName("Supabase Portfolio Repository")
    class PortfolioRepositoryTests extends data_access.SupabasePortfolioRepositoryTest {
    }
}

package usecase;

import entity.EconomicIndicator;
import java.util.List;

/**
 * Gateway interface for fetching economic indicators.
 */
public interface EconomicIndicatorGateway {
    /**
     * Fetch all economic indicators.
     * @return List of economic indicators
     */
    List<EconomicIndicator> getEconomicIndicators() throws Exception;
}
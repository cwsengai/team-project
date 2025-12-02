package usecase;

import java.util.List;

import entity.EconomicIndicator;

/**
 * Gateway interface for fetching economic indicators.
 */
public interface EconomicIndicatorGateway {

    /**
     * Fetches all available economic indicators.
     *
     * @return a list of economic indicators retrieved from the data source
     */
    List<EconomicIndicator> getEconomicIndicators();
}

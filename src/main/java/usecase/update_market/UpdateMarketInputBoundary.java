package usecase.update_market;

/**
 * Input boundary for triggering simulated market update ticks.
 *
 * <p>This interface defines the request model used by the controller
 * to instruct the interactor to advance the simulation by one tick.</p>
 */
public interface UpdateMarketInputBoundary {

    /**
     * Executes a single simulated market tick update.
     */
    void executeExecuteTick();
}

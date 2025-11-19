package interface_adapter.controller.simulated_trading;
import entity.PricePoint;
import entity.SimulatedOrder;
import entity.SimulatedTradeRecord;
import use_case.PriceDataAccessInterface;
import use_case.price_simulation.PriceSimulationEngine;
import use_case.simulated_trade.SimulatedTradeEngine;
import entity.TimeInterval;


import java.time.LocalDateTime;
import java.util.List;

public class TradingController {
    // use case dependencies
    private final PriceDataAccessInterface priceGateway;
    private final PriceSimulationEngine simulationEngine;
    private final SimulatedTradeEngine tradeEngine;
    //runtime state
    private SimulatedOrder currentOrder;
    private List<Double> timeline;
    private int pointer = 0;  // timeline pointer

    public TradingController(PriceDataAccessInterface priceGateway,
                             PriceSimulationEngine simulationEngine,
                             SimulatedTradeEngine tradeEngine) {
        this.priceGateway = priceGateway;
        this.simulationEngine = simulationEngine;
        this.tradeEngine = tradeEngine;
    }

    //  Smart Buying and Selling
    public void onBuyOrSell(boolean isBuy, String ticker, int quantity, double limitPrice) {
        if (currentOrder == null || !currentOrder.isFilled()) {
            //If there is no current position, buying = going long and selling = going short
            if (currentOrder == null || !currentOrder.isFilled()) {
                if (isBuy) {
                    openLong(ticker, quantity, limitPrice);
                } else {
                    openShort(ticker, quantity, limitPrice);
                }
                return;
            }
            // if currently in a long position: Sell = flat long
            if (currentOrder.isLong()) {
                if (!isBuy) {
                    onClosePosition();
                }
            } else {
                // if currently in a short position: buy = close out
                if (isBuy) {
                    onClosePosition();
                }
            }
        }
    }
    private void openLong(String ticker, int qty, double limitPrice) {
        System.out.println("Opening LONG position...");
        currentOrder = new SimulatedOrder(
                newOrderId(),
                ticker,
                true,   // long
                qty,
                limitPrice,
                LocalDateTime.now()
        );
    }

    private void openShort(String ticker, int qty, double limitPrice) {
        System.out.println("Opening SHORT position...");
        currentOrder = new SimulatedOrder(
                newOrderId(),
                ticker,
                false,  // short
                qty,
                limitPrice,
                LocalDateTime.now()
        );
    }
    public void onStartSimulation(int compressionFactor) {

        try {
            //get 5min price
            List<PricePoint> raw = priceGateway.getPriceHistory(
                    currentOrder.getTicker(),
                    TimeInterval.FIVE_MINUTES
            );

            // Set the compression factor of the price simulator
            simulationEngine.setCompressionFactor(compressionFactor);
            simulationEngine.loadRawData(raw);

            // generate timeline
            this.timeline = simulationEngine.generateTimeline();
            this.pointer = 0;

            System.out.println("Simulation started. Timeline size = " + timeline.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onTick() {

        if (timeline == null || pointer >= timeline.size()) {
            System.out.println("Simulation finished.");
            return;
        }

        double price = timeline.get(pointer);
        pointer++;

        //  If no transaction is made → Check if the limit price has been triggered
        if (!currentOrder.isFilled()) {
            boolean hit = tradeEngine.checkLimitHit(currentOrder, price);
            if (hit) {
                currentOrder.setFilled(true);
                currentOrder.setEntryPrice(price);
                System.out.println(">>> ORDER FILLED @" + price);
            }
        }

        // === If the transaction has been completed → Display floating profit and loss ===
        if (currentOrder.isFilled()) {
            double pnl = tradeEngine.calculateUnrealizedPnL(currentOrder, price);
            System.out.println("Price: " + price + " | PnL: " + pnl);
        }
    }
    public SimulatedTradeRecord onClosePosition() {

        if (currentOrder == null || !currentOrder.isFilled()) {
            System.out.println("ERROR: No position to close.");
            return null;
        }

        double exitPrice = timeline.get(Math.max(0, pointer - 1));
        double realized = tradeEngine.calculateRealizedPnL(currentOrder, exitPrice);

        SimulatedTradeRecord record = new SimulatedTradeRecord(
                currentOrder.getTicker(),
                currentOrder.isLong(),
                currentOrder.getQuantity(),
                currentOrder.getEntryPrice(),
                exitPrice,
                realized,
                currentOrder.getEntryTime(),
                LocalDateTime.now()
        );

        System.out.println(">>> POSITION CLOSED @" + exitPrice +
                "  Realized PnL=" + realized);

        currentOrder = null;  // reset position

        return record;  // return to history reocrd
    }



    private String newOrderId() {
        return "ORDER-" + System.currentTimeMillis();
    }
}

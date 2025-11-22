package use_case.simulated_trade;
import entity.SimulatedOrder;

public class SimulatedTradeEngine {
    public boolean checkLimitHit(SimulatedOrder order, double currentPrice){
        double limit = order.getLimitPrice();
        if (order.isLong()){
            return currentPrice <= limit;
        }else{
            return currentPrice >= limit;
        }
    }
    public double calculateRealizedPnL(SimulatedOrder order, double exitPrice){
        double entry = order.getEntryPrice();
        int qty = order.getQuantity();

        if (order.isLong()){
            return (exitPrice - entry) * qty;
        }else{
            return (entry -exitPrice) * qty;
        }
    }

    public double calculateUnrealizedPnL(SimulatedOrder order, double currentPrice){
        double entry = order.getEntryPrice();
        int qty = order.getQuantity();

        if (order.isLong()){
            return (currentPrice - entry) * qty;
        } else {
            return (entry - currentPrice) * qty;
        }
    }

}

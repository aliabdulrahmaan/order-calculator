import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sitech.OrderCostCalculator;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderCostCalculatorTest {
    public OrderCostCalculator calculator;



    @BeforeEach
    public void setUp() {
        calculator = new OrderCostCalculator();
    }
    @Test
    public void testCalculateOrderCost() throws IOException {
        calculator.loadShippingRates("rates.csv");
        BigDecimal totalOrderCost = calculator.calculateOrderCost("order.csv");
        BigDecimal expectedTotalOrderCost = new BigDecimal("2841");
        assertEquals(expectedTotalOrderCost, totalOrderCost);
    }

    @Test
    public void testCalculateOrderCostWithoutLoadingShippingRates() {
        String orderFilePath = "order.csv";
        assertThrows(IllegalStateException.class, () -> calculator.calculateOrderCost(orderFilePath));
    }

    @Test
    public void testCalculateOrderCostWithInvalidFilePath() throws IOException {
        String orderFilePath = "invalid_order.csv";
        assertThrows(IllegalStateException.class, () -> calculator.calculateOrderCost(orderFilePath));
    }

    @Test
    public void testCalculateShipmentCost() throws IOException {
        calculator.loadShippingRates("rates.csv");
        // Test case with weight exactly matching a rate
        BigDecimal orderWeight = new BigDecimal("5.0");
        BigDecimal shipmentCost = calculator.calculateShipmentCost(orderWeight);
        // Expected shipment cost based on the provided sample rates
        BigDecimal expectedShipmentCost = new BigDecimal("10");
        assertEquals(expectedShipmentCost, shipmentCost);
    }

    @Test
    public void testCalculateShipmentCostWithInvalidWeight() throws IOException {
        calculator.loadShippingRates("rates.csv");
        // Test case with weight exceeding the maximum rate
        BigDecimal orderWeight = new BigDecimal("15.0");
        BigDecimal shipmentCost = calculator.calculateShipmentCost(orderWeight);
        // Expected shipment cost based on the provided sample rates and the calculation rule
        BigDecimal expectedShipmentCost = new BigDecimal("40");
        assertEquals(expectedShipmentCost, shipmentCost);
    }
}

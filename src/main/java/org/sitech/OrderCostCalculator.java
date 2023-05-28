package org.sitech;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class OrderCostCalculator {
    private NavigableMap<BigDecimal, BigDecimal> shippingRates;

    public OrderCostCalculator() {
        shippingRates = new TreeMap<>();
    }

    public  BigDecimal findRate( BigDecimal number) {
        TreeSet<BigDecimal> set = new TreeSet<>(shippingRates.keySet());
        return set.higher(number);
    }
    public void loadShippingRates(String ratesFilePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(ratesFilePath));
        String line;
        reader.readLine();
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            BigDecimal weight = new BigDecimal(parts[0]);
            BigDecimal rate = new BigDecimal(parts[1]);
            shippingRates.put(weight, rate);
        }
        reader.close();
    }

    public BigDecimal calculateOrderCost(String orderFilePath) throws IOException {
        if (shippingRates.isEmpty()) {
            throw new IllegalStateException("Shipping rates have not been loaded.");
        }

        BigDecimal totalOrderCost = BigDecimal.ZERO;
        BufferedReader reader = new BufferedReader(new FileReader(orderFilePath));
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String itemName = parts[0].trim();
            int quantity = Integer.parseInt(parts[1].trim());
            BigDecimal weightPerItem = new BigDecimal(parts[2].trim());
            BigDecimal pricePerItem = new BigDecimal(parts[3].trim());
            BigDecimal itemTotalCost = pricePerItem.multiply(BigDecimal.valueOf(quantity));
            BigDecimal totalWight=weightPerItem.multiply(BigDecimal.valueOf(quantity));
            BigDecimal shipmentCost = calculateShipmentCost(totalWight);
            BigDecimal itemCostWithShipment = itemTotalCost.add(shipmentCost);
            totalOrderCost = totalOrderCost.add(itemCostWithShipment);
        }
        reader.close();
        return totalOrderCost;
    }

    public BigDecimal calculateShipmentCost(BigDecimal orderWeight) {
        BigDecimal shipmentCost;
        BigDecimal selectedRate ;

        if (shippingRates.containsKey(orderWeight)){
            selectedRate=orderWeight;
        }else {
            selectedRate=findRate(orderWeight);
        }
        if (selectedRate == null) {
            Map.Entry<BigDecimal, BigDecimal> lastEntry = shippingRates.lastEntry();
            selectedRate = lastEntry.getKey();
        }
        shipmentCost = shippingRates.get(selectedRate);
        if (orderWeight.compareTo(selectedRate) > 0) {
            BigDecimal remainingWeight = orderWeight.subtract(selectedRate);
            BigDecimal remainingCost = calculateShipmentCost( remainingWeight);
            shipmentCost = shipmentCost.add(remainingCost);
        }

        return shipmentCost;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please provide the paths of the rates CSV file and the order details CSV file as arguments.");
            return;
        }

        String ratesFilePath = args[0];
        String orderFilePath = args[1];

        try {
            OrderCostCalculator calculator = new OrderCostCalculator();
            calculator.loadShippingRates(ratesFilePath);
            BigDecimal totalOrderCost = calculator.calculateOrderCost(orderFilePath);
            System.out.println("Total Order Cost: " + totalOrderCost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

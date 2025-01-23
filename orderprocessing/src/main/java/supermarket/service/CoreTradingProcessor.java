package supermarket.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import supermarket.model.ItemInventory;
import supermarket.model.Order;

@Component
public class CoreTradingProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CoreTradingProcessor.class);

    private ObjectMapper objectMapper;

    @Value("${inventory.url}")
    private String inventoryUrl;
    
    @Value("${ordermanager.url}")
    private String orderManagerUrl;


    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;

    private RestTemplate restTemplate;

    public CoreTradingProcessor(
        @Value("${inventory.url}") String inventoryUrl,
        @Value("${ordermanager.url}")String orderManagerUrl
    ) {
        this.inventoryUrl = inventoryUrl;
        this.orderManagerUrl = orderManagerUrl;
        this.objectMapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();

        // on startup, initialize the buy and sell order queues
        this.buyOrders = new PriorityQueue<>((o1, o2) -> Long.compare(o2.getUnitPrice(), o1.getUnitPrice())); // Max-heap for buy orders
        this.sellOrders = new PriorityQueue<>(Comparator.comparingLong(Order::getUnitPrice));    // Min-heap for sell orders
        //query all orders book
    }

    @JmsListener(destination = "placedorders", concurrency = "1")
    public void receiveMessage(String message) throws JsonMappingException, JsonProcessingException {
        Order order  = objectMapper.readValue(message, Order.class);
        logger.info("Order: " + order.toString());

        if (order.getType().equals("buy")) {
            placeBuyOrder(order);
        } else {
            placeSellOrder(order);
        }

        if (order.getQuantity() == 0) {
            logger.info("Order fully executed: " + order.toString());
            restTemplate.postForObject(orderManagerUrl + "/deleteOrder", order,Object.class);
            logger.info("Order deleted: " + order.toString());
        }
    }

    public void placeBuyOrder(Order buyOrder) {
        matchOrder(buyOrder, sellOrders);
        if (buyOrder.getQuantity() > 0) {
            buyOrders.add(buyOrder);
        }
    }

    public void placeSellOrder(Order sellOrder) {
        matchOrder(sellOrder, buyOrders);
        if (sellOrder.getQuantity() > 0) {
            sellOrders.add(sellOrder);
        }
    }
    
    private void matchOrder(Order incomingOrder, PriorityQueue<Order> oppositeOrders) {
        while (!oppositeOrders.isEmpty() && incomingOrder.getQuantity() > 0) {
            Order topOrder = oppositeOrders.peek();

            if (("buy".equals(incomingOrder.getType()) && incomingOrder.getUnitPrice() >= topOrder.getUnitPrice()) || ("sell".equals(incomingOrder.getType()) && incomingOrder.getUnitPrice() <= topOrder.getUnitPrice())) {
                long tradeQuantity = Math.min(incomingOrder.getQuantity(), topOrder.getQuantity());
                
                // Update quantities
                incomingOrder.setQuantity(incomingOrder.getQuantity() - tradeQuantity);
                restTemplate.postForObject(orderManagerUrl + "/updateOrder", incomingOrder, Object.class);
                topOrder.setQuantity(topOrder.getQuantity() - tradeQuantity);
                restTemplate.postForObject(orderManagerUrl + "/updateOrder", topOrder, Object.class);

                // Update market price
                Long marketPrice = topOrder.getUnitPrice();
                logger.info("Trade executed: " + tradeQuantity + " @ " + marketPrice);

                ItemInventory input = new ItemInventory();
                input.setItemName(incomingOrder.getItemName());
                input.setUnitMarketPrice(marketPrice);
                ItemInventory inv = restTemplate.postForObject(inventoryUrl + "/updateMarketPrice", input, ItemInventory.class);


                // Remove the top order if fully executed
                if (topOrder.getQuantity() == 0) {
                    restTemplate.postForObject(orderManagerUrl + "/deleteOrder", topOrder, Object.class);
                    oppositeOrders.poll();
                }
            } else {
                break;
            }
        }
    }



}
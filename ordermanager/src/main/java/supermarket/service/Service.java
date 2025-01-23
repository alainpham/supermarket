package supermarket.service;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import supermarket.model.Order;
import supermarket.model.Person;
import supermarket.repo.OrderRepository;
import supermarket.helper.View;

@RestController
public class Service {

    private final static Logger logger = LoggerFactory.getLogger(Service.class);

    @Autowired
    private View view;
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/ping")
    public ResponseEntity<Serializable> ping(RequestEntity<Serializable> requestEntity) {
        logger.info(requestEntity.toString());
        return ResponseEntity.status(200).body(new LinkedHashMap<String,Serializable>(Map.of("msg", "HELLO")));
    }

    @PostMapping("/send-msg")
    public Person sendMsg(@RequestBody Person person) throws JsonProcessingException {
        logger.info(person.toString());
        List<Object> listObjects = List.of(person);
        view.update("person", listObjects);
        return person;
    }

    @GetMapping("/orders")
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/order/{id}")
    public Order getOrder(Long id) {
        return orderRepository.findById(id).get();
    }

    @PostMapping("/createOrder")
    public Order createOrder(@RequestBody Order order) throws JmsException, JsonProcessingException {
        order.setId(null);

        orderRepository.save(order);

        jmsTemplate.convertAndSend("placedorders", objectMapper.writeValueAsString(order) );

        return order;
    }

    @PostMapping("/updateOrder")
    public Order updateOrder(@RequestBody Order order) throws Exception {
        if (orderRepository.findById(order.getId()).isPresent()) {
            return orderRepository.save(order);
        }else {
            throw new Exception("Order not found");
        }
    }

    @PostMapping("/deleteOrder")
    public void deleteOrder(@RequestBody Order order) {
        orderRepository.delete(order);
    }

    @GetMapping("/deleteAllOrders")
    public void deleteAllOrders() {
        orderRepository.deleteAll();
    }

}

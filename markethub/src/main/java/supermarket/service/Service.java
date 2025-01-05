package supermarket.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import supermarket.model.ItemInventory;
import supermarket.model.Order;
import supermarket.model.Person;
import supermarket.helper.View;

@RestController
public class Service {

    private final static Logger logger = LoggerFactory.getLogger(Service.class);

    @Value("${inventory.url}")
    private String inventoryUrl;

    private RestTemplate restTemplate = new RestTemplate();


    @Autowired
    private View view;
    
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

    @GetMapping("/items")
    public List<ItemInventory> getItems() {
        List<ItemInventory> itemInventories = Arrays.asList(restTemplate.getForObject(inventoryUrl + "/items", ItemInventory[].class));
        return itemInventories;
    }

    @PostMapping("/placeOrder")
    public Order placeOrder(@RequestBody Order order) {
        logger.info(order.toString());
        //stub need to call ordermanagement service.
        //order management should send message to the engine to be processed.
        return order;
    }
}

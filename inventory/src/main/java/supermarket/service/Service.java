package supermarket.service;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import supermarket.model.ItemInventory;
import supermarket.model.Person;
import supermarket.repo.ItemInventoryRepository;
import supermarket.helper.View;

@RestController
public class Service {

    private final static Logger logger = LoggerFactory.getLogger(Service.class);

    @Autowired
    private View view;
    
    @Autowired
    private ItemInventoryRepository itemInventoryRepository;

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


    @PostMapping("/createItem")
    public ItemInventory createItem(@RequestBody ItemInventory itemInventory) {
        return itemInventoryRepository.save(itemInventory);
    }

    @GetMapping("/items")
    public List<ItemInventory> getItems() {
        return itemInventoryRepository.findAll();
    }

    @GetMapping("/item/{id}")
    public ItemInventory getItem(Long id) throws Exception {
        return itemInventoryRepository.findById(id).orElseThrow(() -> new Exception("Item not found"));
    }
    
    @PostMapping("/updateItem")
    public ItemInventory updateItem(@RequestBody ItemInventory itemInventory) throws Exception {
        if (itemInventoryRepository.findById(itemInventory.getId()).isPresent()) {
            return itemInventoryRepository.save(itemInventory);
        }else {
            throw new Exception("Item not found");
        }
    }
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @PostMapping("/updateMarketPrice")
    public ItemInventory updateMarketPrice(@RequestBody ItemInventory itemInventory) throws Exception {
        ItemInventory i = itemInventoryRepository.findOneByItemName(itemInventory.getItemName());
        if (i != null) {
            i.setUnitMarketPrice(itemInventory.getUnitMarketPrice());
            itemInventoryRepository.save(i);
            return  i;
        }else {
            throw new Exception("Item not found");
        }
    }

    @PostMapping("/deleteItem")
    public void deleteItem(@RequestBody ItemInventory itemInventory) {
        itemInventoryRepository.delete(itemInventory);
    }
}

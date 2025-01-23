package supermarket.repo;

import org.springframework.data.repository.ListCrudRepository;

import supermarket.model.Order;

public interface OrderRepository  extends ListCrudRepository<Order,Long>{
    
}

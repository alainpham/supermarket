package supermarket.repo;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import supermarket.model.ItemInventory;

public interface ItemInventoryRepository extends PagingAndSortingRepository<ItemInventory, Long>, ListCrudRepository<ItemInventory,Long>{
    
    List<ItemInventory> findByItemNameContaining(@Param("itemName") String itemName);
}

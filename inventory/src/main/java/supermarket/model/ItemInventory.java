package supermarket.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ItemInventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemName;
    private Long quantity;
    private Long unitMarketPrice;
    
    public Long getId() {
        return id; 
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public Long getQuantity() {
        return quantity;
    }
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
    public Long getUnitMarketPrice() {
        return unitMarketPrice;
    }
    public void setUnitMarketPrice(Long unitMarketPrice) {
        this.unitMarketPrice = unitMarketPrice;
    }
    
    @Override
    public String toString() {
        return "ItemInventory [id=" + id + ", itemName=" + itemName + ", quantity=" + quantity + ", unitMarketPrice="
                + unitMarketPrice + "]";
    }

    
}

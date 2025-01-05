package supermarket.model;


public class Order {
    
    private Long id;
    private String itemName;
    
    // buy or sell
    private String type;
    private String quantity;
    private Long unitPrice;

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
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public Long getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "Order [id=" + id + ", itemName=" + itemName + ", type=" + type + ", quantity=" + quantity
                + ", unitPrice=" + unitPrice + "]";
    }

    
}

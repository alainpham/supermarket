package supermarket.model;


public class ItemOwnership {
    
    private Long id;
    private String userName;
    private String itemName;
    private Long quantity;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
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

    @Override
    public String toString() {
        return "ItemOwnership [id=" + id + ", userName=" + userName + ", itemName=" + itemName + ", quantity="
                + quantity + "]";
    }

}

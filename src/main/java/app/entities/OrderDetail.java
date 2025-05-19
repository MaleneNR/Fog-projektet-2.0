package app.entities;

public class OrderDetail {
    private Product product;
    private int quantity;
    private int totalPrice;
    private String assemblyDescription;
    private int materialId;
    private int orderId;

    public OrderDetail(Product product, int quantity, int totalPrice, String assemblyDescription, int materialId, int orderId) {
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.assemblyDescription = assemblyDescription;
        this.materialId = materialId;
        this.orderId = orderId;
    }

    public OrderDetail(Product product, int quantity, String assemblyDescription, int materialId, int orderId) {
        this.product = product;
        this.quantity = quantity;
        this.assemblyDescription = assemblyDescription;
        this.materialId = materialId;
        this.orderId = orderId;
    }

    public OrderDetail(int orderId, Product product, int quantity, String assemblyDescription, int totalPrice) {
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.assemblyDescription = assemblyDescription;
        this.totalPrice = totalPrice;
    }



    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getAssemblyDescription() {
        return assemblyDescription;
    }

    public void setAssemblyDescription(String assemblyDescription) {
        this.assemblyDescription = assemblyDescription;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "product=" + product +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", assemblyDescription='" + assemblyDescription + '\'' +
                ", materialId=" + materialId +
                ", orderId=" + orderId +
                '}';
    }
}

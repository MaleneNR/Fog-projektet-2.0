package app.entities;

public class OrderDetail {
    private int productId;
    private int quantity;
    private int totalPrice;
    private String assemblyDescription;
    private int materialId;
    private int orderId;



    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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
                "productId=" + productId +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", assemblyDescription='" + assemblyDescription + '\'' +
                ", materialId=" + materialId +
                ", orderId=" + orderId +
                '}';
    }
}

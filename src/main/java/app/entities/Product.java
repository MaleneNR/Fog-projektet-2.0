package app.entities;

public class Product {
    private int productId;
    private int length;
    private int materialId;

    public Product(int productId, int length, int materialId) {
        this.productId = productId;
        this.length = length;
        this.materialId = materialId;
    }

    public Product(int length) {
        this.length = length;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", length=" + length +
                ", materialId=" + materialId +
                '}';
    }
}

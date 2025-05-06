package app.entities;

public class Product {
    private int productId;
    private int length;
    private int materialId;

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

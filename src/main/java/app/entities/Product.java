package app.entities;

public class Product {
    private int productId;
    private int length;
    private Material material;

    public Product(int productId, int length, Material material) {
        this.productId = productId;
        this.length = length;
        this.material = material;
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

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", length=" + length +
                ", material=" + material +
                '}';
    }
}

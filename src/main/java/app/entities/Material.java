package app.entities;

public class Material {
    private int materialId;
    private String material;
    private int width;
    private int height;
    private String unit;
    private String description;
    private int pricePerUnit;

    public Material(int materialId, String material, int width, int height, String unit, String description, int pricePerUnit) {
        this.materialId = materialId;
        this.material = material;
        this.width = width;
        this.height = height;
        this.unit = unit;
        this.description = description;
        this.pricePerUnit = pricePerUnit;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(int pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public String toString() {
        return "Material{" +
                "materialId=" + materialId +
                ", material='" + material + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", unit='" + unit + '\'' +
                ", description='" + description + '\'' +
                ", pricePerUnit=" + pricePerUnit +
                '}';
    }
}

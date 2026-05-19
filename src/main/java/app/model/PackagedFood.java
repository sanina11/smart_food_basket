package app.model;

public class PackagedFood extends FoodItem {

    private final String packagingMaterial;

    public PackagedFood(String name, FoodGroup group, double basePrice,
                        double baseCalories, String packagingMaterial, String imagePath) {
        super(name, group, basePrice, baseCalories, imagePath);
        this.packagingMaterial = packagingMaterial;
    }

    @Override
    public double getPrice() {
        return this.getBasePrice();
    }

    @Override
    public double getCalories() {
        return this.getBaseCalories();
    }

    public String getPackagingMaterial() {
        return this.packagingMaterial;
    }

    @Override
    public boolean isBulk() {
        return false;
    }

    @Override
    public boolean hasPlasticPackaging() {
        return this.packagingMaterial.equals("Plastico");
    }

    @Override
    public String getUnitLabel() {
        return "€/un.";
    }
}



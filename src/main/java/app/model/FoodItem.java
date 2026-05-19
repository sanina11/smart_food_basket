package app.model;

public abstract class FoodItem {
    private final String name;
    private final FoodGroup group;
    private final double basePrice;
    private final double baseCalories;
    private final String imagePath;

    public FoodItem(String name, FoodGroup group, double basePrice, double baseCalories, String imagePath){
        this.name = name;
        this.group = group;
        this.basePrice= basePrice;
        this.baseCalories = baseCalories;
        this.imagePath = imagePath;
    }

    public abstract double getPrice();
    public abstract double getCalories();
    public abstract String getUnitLabel();
    public abstract boolean isBulk();
    public abstract boolean hasPlasticPackaging();

    public double getWeightInGrams(){
        return 0.0;
    }

    public String getName() {
        return this.name;
    }

    public FoodGroup getGroup() {
        return this.group;
    }

    public double getBasePrice() {
        return this.basePrice;
    }

    public double getBaseCalories() {
        return this.baseCalories;
    }

    public String getImagePath() { return this.imagePath;}
}

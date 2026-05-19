package app.model;

public class BulkFood extends FoodItem {
    private final double selectedWeight;

    public BulkFood(String name, FoodGroup group, double basePrice, double baseCalories, double selectedWeight, String imagePath){
        super(name, group, basePrice, baseCalories, imagePath);
        this.selectedWeight = selectedWeight;
    }

    @Override
    public double getPrice(){
        return (this.getBasePrice() / 1000.0) * this.selectedWeight;
    }

    @Override
    public double getCalories(){
        return (this.getBaseCalories() / 100.0) * this.selectedWeight;
    }

    @Override
    public double getWeightInGrams(){
        return this.selectedWeight;
    }

    public double getSelectedWeight(){
        return this.selectedWeight;
    }

    @Override
    public boolean isBulk() {
        return true;
    }
    @Override
    public boolean hasPlasticPackaging() {
        return false;
    }

    @Override
    public String getUnitLabel() {
        return "€/kg.";
    }
}

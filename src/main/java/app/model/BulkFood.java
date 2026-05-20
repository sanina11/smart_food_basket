/*
 * Smart Food Basket - ODS Edition
 * Autor: Miguel Sanina - 26874, João Soares - 25961
 * Programação Orientada por Objetos - IPBeja 2025/2026
 *
 * Contém código gerado/refinado com auxílio de IA generativa (Claude).
 * Detalhes no ficheiro relatorio_ia.txt.
 */
package app.model;

/**
 * Represents a food item sold in bulk (by weight in grams).
 * The final price and calorie count depend on the weight chosen by the user.
 */
public class BulkFood extends FoodItem {

    private final double selectedWeight;

    /**
     * Creates a new bulk food item.
     *
     * @param name           the display name of the food item
     * @param group          the food group this item belongs to
     * @param basePrice      the reference price in euros per kilogram
     * @param baseCalories   the reference calorie count per 100 grams
     * @param selectedWeight the weight selected by the user in grams
     * @param imagePath      the file name of the image associated with this item
     */
    public BulkFood(String name, FoodGroup group, double basePrice,
                    double baseCalories, double selectedWeight, String imagePath) {
        super(name, group, basePrice, baseCalories, imagePath);
        this.selectedWeight = selectedWeight;
    }

    /**
     * Calculates the proportional price based on the selected weight.
     * Formula: (basePrice per kg / 1000) multiplied by the weight in grams.
     *
     * @return the price for the selected weight in euros
     */
    @Override
    public double getPrice() {
        return (this.getBasePrice() / 1000.0) * this.selectedWeight;
    }

    /**
     * Calculates the proportional calorie count based on the selected weight.
     * Formula: (baseCalories per 100g / 100) multiplied by the weight in grams.
     *
     * @return the calorie count for the selected weight
     */
    @Override
    public double getCalories() {
        return (this.getBaseCalories() / 100.0) * this.selectedWeight;
    }

    /**
     * Returns the weight of this item in grams. Overrides the default value
     * (0.0) from FoodItem so that the basket can count vegetable weight
     * without using instanceof checks.
     *
     * @return the selected weight in grams
     */
    @Override
    public double getWeightInGrams() {
        return this.selectedWeight;
    }

    /**
     * Returns the user-selected weight for this bulk item.
     *
     * @return the selected weight in grams
     */
    public double getSelectedWeight() {
        return this.selectedWeight;
    }

    /**
     * Indicates that this food is sold in bulk.
     *
     * @return always true for bulk items
     */
    @Override
    public boolean isBulk() {
        return true;
    }

    /**
     * Bulk items have no packaging, therefore never plastic.
     *
     * @return always false for bulk items
     */
    @Override
    public boolean hasPlasticPackaging() {
        return false;
    }

    /**
     * Returns the unit label to be displayed in the user interface.
     *
     * @return the string "€/kg."
     */
    @Override
    public String getUnitLabel() {
        return "€/kg.";
    }
}
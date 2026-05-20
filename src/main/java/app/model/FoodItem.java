package app.model;

/**
 * Abstract base class for every food item in the application.
 * Defines the common attributes (name, group, base price, base calories,
 * image path) and the contract that concrete subclasses must implement.
 * Cannot be instantiated directly — use PackagedFood or BulkFood.
 */
public abstract class FoodItem {

    private final String name;
    private final FoodGroup group;
    private final double basePrice;
    private final double baseCalories;
    private final String imagePath;

    /**
     * Initializes the common attributes shared by every food item.
     *
     * @param name         the display name of the food item
     * @param group        the food group this item belongs to
     * @param basePrice    the base price in euros (per unit or per kilogram)
     * @param baseCalories the base calorie count (per unit or per 100 grams)
     * @param imagePath    the file name of the image associated with this item
     */
    public FoodItem(String name, FoodGroup group, double basePrice,
                    double baseCalories, String imagePath) {
        this.name = name;
        this.group = group;
        this.basePrice = basePrice;
        this.baseCalories = baseCalories;
        this.imagePath = imagePath;
    }

    /**
     * Calculates the effective price of this item.
     * For packaged foods returns the base price; for bulk foods returns the
     * proportional price based on the selected weight.
     *
     * @return the price in euros
     */
    public abstract double getPrice();

    /**
     * Calculates the effective calorie count of this item.
     * For packaged foods returns the base calories; for bulk foods returns
     * the proportional calories based on the selected weight.
     *
     * @return the calorie count
     */
    public abstract double getCalories();

    /**
     * Returns the unit label to display in the user interface.
     * Examples: "€/un." for packaged items or "€/kg." for bulk items.
     *
     * @return the unit label string
     */
    public abstract String getUnitLabel();

    /**
     * Indicates whether this item is sold in bulk (by weight).
     * Used by the basket model to evaluate the SDG 12 sustainability goal
     * without resorting to instanceof checks.
     *
     * @return true if the item is sold in bulk, false if it is packaged
     */
    public abstract boolean isBulk();

    /**
     * Indicates whether this item uses plastic packaging.
     * Used by the basket model to evaluate the SDG 12 sustainability goal.
     *
     * @return true if the packaging material is plastic, false otherwise
     */
    public abstract boolean hasPlasticPackaging();

    /**
     * Returns the weight of this item in grams. Default implementation
     * returns 0.0 so that packaged items do not contribute to weight-based
     * calculations. BulkFood overrides this method to return the real weight.
     *
     * @return the weight in grams, or 0.0 by default
     */
    public double getWeightInGrams() {
        return 0.0;
    }

    /**
     * Returns the display name of this food item.
     *
     * @return the item name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the food group this item belongs to.
     *
     * @return the FoodGroup
     */
    public FoodGroup getGroup() {
        return this.group;
    }

    /**
     * Returns the base price of this item, before any weight adjustments.
     *
     * @return the base price in euros
     */
    public double getBasePrice() {
        return this.basePrice;
    }

    /**
     * Returns the base calorie count of this item, before any weight adjustments.
     *
     * @return the base calorie count
     */
    public double getBaseCalories() {
        return this.baseCalories;
    }

    /**
     * Returns the file name of the image associated with this item.
     *
     * @return the image file name
     */
    public String getImagePath() {
        return this.imagePath;
    }
}









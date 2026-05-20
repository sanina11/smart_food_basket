package app.model;

/**
 * Represents a food item sold in packaged form (per unit).
 * The price and calorie count are fixed values defined at construction time
 * and do not depend on user input. Each packaged item knows the material
 * used in its packaging, which is relevant for the SDG 12 sustainability goal.
 */
public class PackagedFood extends FoodItem {

    private static final String PLASTIC_LABEL = "Plastico";

    private final String packagingMaterial;

    /**
     * Creates a new packaged food item.
     *
     * @param name              the display name of the food item
     * @param group             the food group this item belongs to
     * @param basePrice         the fixed price per unit in euros
     * @param baseCalories      the fixed calorie count per unit
     * @param packagingMaterial the packaging material (e.g. "Plastico", "Cartao")
     * @param imagePath         the file name of the image associated with this item
     */
    public PackagedFood(String name, FoodGroup group, double basePrice,
                        double baseCalories, String packagingMaterial, String imagePath) {
        super(name, group, basePrice, baseCalories, imagePath);
        this.packagingMaterial = packagingMaterial;
    }

    /**
     * Returns the fixed unit price, unchanged from the base price.
     *
     * @return the price in euros
     */
    @Override
    public double getPrice() {
        return this.getBasePrice();
    }

    /**
     * Returns the fixed calorie count, unchanged from the base value.
     *
     * @return the calorie count
     */
    @Override
    public double getCalories() {
        return this.getBaseCalories();
    }

    /**
     * Returns the packaging material used by this item.
     *
     * @return the packaging material name
     */
    public String getPackagingMaterial() {
        return this.packagingMaterial;
    }

    /**
     * Indicates that this food is not sold in bulk.
     *
     * @return always false for packaged items
     */
    @Override
    public boolean isBulk() {
        return false;
    }

    /**
     * Indicates whether this item is packaged in plastic, which is relevant
     * for evaluating the SDG 12 sustainability goal.
     *
     * @return true if the packaging material equals "Plastico"
     */
    @Override
    public boolean hasPlasticPackaging() {
        return this.packagingMaterial.equals(PLASTIC_LABEL);
    }

    /**
     * Returns the unit label to be displayed in the user interface.
     *
     * @return the string "€/un."
     */
    @Override
    public String getUnitLabel() {
        return "€/un.";
    }
}

package app.model;

/**
 * Represents the food groups from the Portuguese Food Wheel (Roda dos Alimentos).
 * Each group has an associated background color used by the user interface to
 * paint food tiles.
 */
public enum FoodGroup {

    GREEN ("#EAF3DE", "🥦"),
    YELLOW("#FEF3DC", "🌾"),
    BLUE  ("#E3F0FB", "🥛"),
    BROWN ("#FBEEE8", "🫘"),
    RED   ("#FCEAEA", "🍗"),
    ORANGE("#FFF0DC", "🫒");

    private final String color;
    private final String emoji;

    /**
     * Initializes a food group with its associated visual properties.
     *
     * @param color the background color in hexadecimal format
     * @param emoji a representative emoji for the group
     */
    FoodGroup(String color, String emoji) {
        this.color = color;
        this.emoji = emoji;
    }

    /**
     * Returns the background color associated with this food group.
     *
     * @return the color as a hexadecimal string
     */
    public String getColor() {
        return this.color;
    }

    /**
     * Returns the representative emoji for this food group.
     *
     * @return a single-character emoji string
     */
    public String getEmoji() {
        return this.emoji;
    }
}
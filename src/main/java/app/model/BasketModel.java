/*
 * Smart Food Basket - ODS Edition
 * Autor: Miguel Sanina - 26874, João Soares - 25961
 * Programação Orientada por Objetos - IPBeja 2025/2026
 *
 * Contém código gerado/refinado com auxílio de IA generativa (Claude).
 * Detalhes no ficheiro relatorio_ia.txt.
 */
package app.model;

import app.ui.View;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Manages the state and business logic of the Smart Food Basket application.
 * Holds the food catalog loaded from CSV, the user basket, and the undo/redo history.
 */
public class BasketModel {

    private final List<FoodItem> catalog;
    private final List<FoodItem> items;
    private final List<String> errorLines;
    private final Deque<FoodItem> undoStack;
    private final Deque<FoodItem> redoStack;
    private View view;

    /**
     * Creates an empty basket model with empty catalog, items list, error log
     * and undo/redo stacks.
     */
    public BasketModel() {
        this.catalog = new ArrayList<>();
        this.items = new ArrayList<>();
        this.errorLines = new ArrayList<>();
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }

    /**
     * Registers the view to be notified when the basket state changes.
     *
     * @param view the view implementation to attach
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * Returns an unmodifiable copy of the catalog (foods loaded from CSV).
     *
     * @return list of all available food items
     */
    public List<FoodItem> getCatalog() {
        return List.copyOf(this.catalog);
    }

    /**
     * Returns an unmodifiable copy of the items currently in the basket.
     *
     * @return list of items in the user's basket
     */
    public List<FoodItem> getItems() {
        return List.copyOf(this.items);
    }

    /**
     * Returns the lines from the CSV that were ignored due to parsing errors.
     *
     * @return list of malformed CSV lines
     */
    public List<String> getErrorLines() {
        return List.copyOf(this.errorLines);
    }

    /**
     * Adds a food item to the basket. Clears the redo history because adding
     * a new item invalidates any previously undone actions.
     *
     * @param item the food item to add
     */
    public void addItem(FoodItem item) {
        this.redoStack.clear();
        this.items.add(item);
        this.notifyView();
    }

    /**
     * Removes the most recently added item from the basket and pushes it onto
     * the redo stack so it can be restored later.
     */
    public void undo() {
        if (this.items.isEmpty()) {
            return;
        }
        FoodItem removed = this.items.remove(this.items.size() - 1);
        this.redoStack.push(removed);
        this.notifyView();
    }

    /**
     * Restores the most recently undone item back to the basket.
     */
    public void redo() {
        if (this.redoStack.isEmpty()) {
            return;
        }
        FoodItem item = this.redoStack.pop();
        this.items.add(item);
        this.notifyView();
    }

    /**
     * Calculates the total price of all items in the basket using polymorphism.
     *
     * @return the sum of prices of all items
     */
    public double getTotalPrice() {
        double total = 0.0;
        for (FoodItem item : this.items) {
            total = total + item.getPrice();
        }
        return total;
    }

    /**
     * Calculates the total calories of all items in the basket using polymorphism.
     *
     * @return the sum of calories of all items
     */
    public double getTotalCalories() {
        double total = 0.0;
        for (FoodItem item : this.items) {
            total = total + item.getCalories();
        }
        return total;
    }

    /**
     * Checks if the basket meets the SDG 3 (Health) goal of at least 400g
     * of vegetables (items belonging to the GREEN food group).
     *
     * @return true if the basket contains at least 400g of green items
     */
    public boolean hasMetVegetableGoal() {
        double totalGrams = 0.0;
        for (FoodItem item : this.items) {
            if (item.getGroup() == FoodGroup.GREEN) {
                totalGrams = totalGrams + item.getWeightInGrams();
            }
        }
        return totalGrams >= 400.0;
    }

    /**
     * Checks if the basket meets the SDG 12 (Sustainability) goal: more than
     * 50% of items must be sold in bulk AND no item may have plastic packaging.
     *
     * @return true if the sustainability goal is met
     */
    public boolean hasMetSustainGoal() {
        if (this.items.isEmpty()) {
            return false;
        }
        long bulkCount = 0;
        for (FoodItem item : this.items) {
            if (item.isBulk()) {
                bulkCount = bulkCount + 1;
            }
            if (item.hasPlasticPackaging()) {
                return false;
            }
        }
        return bulkCount > this.items.size() / 2;
    }

    /**
     * Checks if the basket meets the SDG 2 (Nutrition Diversity) goal of
     * containing items from at least 4 different food groups.
     *
     * @return true if the basket has 4 or more distinct food groups
     */
    public boolean hasMetDiversityGoal() {
        List<FoodGroup> uniqueGroups = new ArrayList<>();
        for (FoodItem item : this.items) {
            if (!uniqueGroups.contains(item.getGroup())) {
                uniqueGroups.add(item.getGroup());
            }
        }
        return uniqueGroups.size() >= 4;
    }

    /**
     * Loads the food catalog from a CSV file. Each valid line is parsed into a
     * FoodItem and added to the catalog. Malformed lines are stored separately
     * for later inspection by the user interface.
     *
     * @param path the path to the CSV file
     * @throws IOException if the file cannot be opened or read
     */
    public void loadFromCsv(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line = reader.readLine();

        while (line != null) {
            this.parseCsvLine(line);
            line = reader.readLine();
        }

        reader.close();
    }

    /**
     * Exports the current basket to a text file named "receipt_yyyymmdd.txt".
     * The receipt contains every item, the grand total, and the SDG 3 status.
     *
     * @return the name of the file that was written
     * @throws IOException if the file cannot be written
     */
    public String exportReceipt() throws IOException {
        String fileName = "receipt_" + this.getTodayDate() + ".txt";
        FileWriter writer = new FileWriter(fileName);

        writer.write("=== Smart Food Basket ===\n");
        writer.write("Data: " + this.getTodayDate() + "\n\n");

        for (FoodItem item : this.items) {
            writer.write(this.formatReceiptLine(item));
        }

        writer.write("\n");
        writer.write(String.format("GRANDE TOTAL: %.2f EUR%n", this.getTotalPrice()));
        writer.write("\n");

        if (this.hasMetVegetableGoal()) {
            writer.write("Meta ODS 3 Cumprida!\n");
        } else {
            writer.write("Meta ODS 3 Falhada!\n");
        }

        writer.close();
        return fileName;
    }

    private void notifyView() {
        if (this.view != null) {
            this.view.updateView();
        }
    }

    private void parseCsvLine(String line) {
        try {
            String[] parts = line.split(",");
            String type = parts[0];
            String name = parts[1];

            if (name.isEmpty() || type.isEmpty()) {
                throw new IllegalArgumentException();
            }

            FoodGroup group = FoodGroup.valueOf(parts[2]);
            double price = Double.parseDouble(parts[3]);
            double calories = Double.parseDouble(parts[4]);
            String extra = parts[5];
            String imagePath = parts[6];

            if (price < 0 || calories < 0) {
                throw new IllegalArgumentException();
            }

            this.createAndAddToCatalog(type, name, group, price, calories, extra, imagePath);
        } catch (Exception e) {
            this.errorLines.add(line);
        }
    }

    private void createAndAddToCatalog(String type, String name, FoodGroup group,
                                       double price, double calories,
                                       String extra, String imagePath) {
        if (type.equals("PACKAGED")) {
            this.catalog.add(new PackagedFood(name, group, price, calories, extra, imagePath));
        } else if (type.equals("BULK")) {
            this.catalog.add(new BulkFood(name, group, price, calories, 0.0, imagePath));
        } else {
            throw new IllegalArgumentException();
        }
    }

    private String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private String formatReceiptLine(FoodItem item) {
        double weight = item.getWeightInGrams();
        String quantity = weight > 0 ? (int) weight + "g" : "1 un.";
        return String.format("%-20s %-8s %6.2f EUR%n", item.getName(), quantity, item.getPrice());
    }
}
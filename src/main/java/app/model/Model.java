package app.model;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import app.ui.View;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Model {

    private final List<FoodItem> catalog;
    private final List<FoodItem> items;
    private final List<String> errorLines;
    private final Deque<FoodItem> undoStack;
    private final Deque<FoodItem> redoStack;
    private View view;

    public Model (){
        this.catalog = new ArrayList<>();
        this.items = new ArrayList<>();
        this.errorLines = new ArrayList<>();
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }

    public List<FoodItem> getCatalog() {
        return List.copyOf(this.catalog);
    }

    public void addItem(FoodItem item){
        this.redoStack.clear();
        this.items.add(item);
        if (this.view != null){
            this.view.updateView();
        }
    }

    public void undo(){
        if (this.items.isEmpty()){
            return;
        }
        FoodItem removed = this.items.remove(this.items.size() - 1);
        this.undoStack.push(removed);
        if (this.view != null){
            this.view.updateView();
        }
    }

    public void redo(){
        if (this.undoStack.isEmpty()){
            return;
        }
        FoodItem item = this.undoStack.pop();
        this.items.add(item);
        this.redoStack.push(item);
        if (this.view != null){
                this.view.updateView();
        }
    }

    public List<FoodItem> getItems(){
        return List.copyOf(this.items);
    }

    public double getTotalPrice(){
        double total = 0.0;
        for (FoodItem item : this.items) {
            total = total + item.getPrice();
        }
        return total;
    }

    public double getTotalCalories(){
        double total = 0.0;
        for (FoodItem item : this.items) {
            total = total + item.getCalories();
        }
        return total;
    }

    public boolean hasMetVegetableGoal(){
        double totalGrams = 0.0;
        for (FoodItem item : this.items){
            if (item.getGroup() == FoodGroup.GREEN){
                totalGrams = totalGrams + item.getWeightInGrams();
            }
        }
        return totalGrams >= 400.0;
    }

    public boolean hasMetSustainGoal(){
        if(this.items.isEmpty()){
            return false;
        }
        long bulkCount = 0;
        for (FoodItem item : this.items){
            if(item.isBulk()){
                bulkCount ++;
            }
            if(item.hasPlasticPackaging()){
                return false;
            }
        }
        return bulkCount > this.items.size() / 2;
    }

    public boolean hasMetDiversityGoal(){
        return this.items.stream().map(FoodItem::getGroup).distinct().count() >= 4;
    }

    public void loadFromCsv(String path) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line = reader.readLine();

        while (line != null) {
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

                if (type.equals("PACKAGED")) {
                    this.catalog.add(new PackagedFood(name, group, price, calories, extra, imagePath));
                } else if (type.equals("BULK")) {
                    this.catalog.add(new BulkFood(name, group, price, calories, 0.0,imagePath));
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                this.errorLines.add(line);
            }
            line = reader.readLine();
        }

        reader.close();
    }

    public List<String> getErrorLines() {
        return List.copyOf(this.errorLines);
    }

    public void setView(View view) {
        this.view = view;
    }

    public String exportReceipt() throws IOException {
        String fileName = "receipt_" + this.getTodayDate() + ".txt";
        FileWriter writer = new FileWriter(fileName);

        writer.write("=== Smart Food Basket ===\n");
        writer.write("Data: " + this.getTodayDate() + "\n\n");

        for (FoodItem item : this.items) {
            writer.write(this.formatReceiptLine(item));
        }

        writer.write("\n");
        writer.write(String.format("GRANDE TOTAL: %.2f EUR\n", this.getTotalPrice()));
        writer.write("\n");

        if (this.hasMetVegetableGoal()) {
            writer.write("Meta ODS 3 Cumprida!\n");
        } else {
            writer.write("Meta ODS 3 Falhada!\n");
        }

        writer.close();
        return fileName;
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

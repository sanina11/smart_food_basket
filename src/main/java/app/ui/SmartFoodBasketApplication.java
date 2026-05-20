package app.ui;

import app.model.BasketModel;
import app.model.BulkFood;
import app.model.FoodItem;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Main JavaFX application for the Smart Food Basket project.
 * Builds the user interface, captures user interactions, and delegates all
 * business logic to the BasketModel through the View interface.
 */
public class SmartFoodBasketApplication extends Application implements View {

    private static final String CSV_PATH = "src/main/resources/foods_large.csv";
    private static final String IMAGES_FOLDER = "/emojis/";
    private static final String SOUND_PATH = "/Sounds/cashRegister.mp3";

    private static final String FILTER_ALL = "All";
    private static final String SORT_BY_PRICE = "Ordenar por preço (menor a maior)";

    private static final String UNIT_PACKAGED = "€/un.";

    private BasketModel basketModel;
    private FlowPane grid;
    private Label totalPriceLabel;
    private Label totalCaloriesLabel;
    private Label odsDiversityBadge;
    private Label odsSustainabilityBadge;
    private Label odsHealthBadge;
    private ListView<String> basketList;

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Entry point of the JavaFX application. Initializes the model, loads the
     * food catalog from CSV, and builds the user interface.
     *
     * @param stage the primary stage provided by the JavaFX runtime
     */
    @Override
    public void start(Stage stage) {
        this.basketModel = new BasketModel();
        this.basketModel.setView(this);
        this.loadData();
        this.buildUI(stage);
    }

    /**
     * Updates every visual component to reflect the current basket state.
     * Called automatically by the model when items are added or removed.
     */
    @Override
    public void updateView() {
        this.totalPriceLabel.setText(String.format("Custo total: %.2f €", this.basketModel.getTotalPrice()));
        this.totalCaloriesLabel.setText(String.format("Calorias: %.0f kcal", this.basketModel.getTotalCalories()));
        this.refreshBasketList();
        this.refreshOdsBadges();
    }

    /**
     * Shows an error message to the user in a modal dialog.
     *
     * @param message the error description to display
     */
    @Override
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadData() {
        try {
            this.basketModel.loadFromCsv(CSV_PATH);
        } catch (IOException e) {
            this.showError("Erro ao carregar o ficheiro: " + e.getMessage());
        }
    }

    private void buildUI(Stage stage) {
        BorderPane root = new BorderPane();
        this.grid = this.createGrid();
        VBox sidebar = this.createSidebar();
        HBox filterBar = this.createFilterBar();

        VBox centerArea = new VBox(filterBar, this.grid);
        root.setCenter(centerArea);
        root.setRight(sidebar);

        Scene scene = new Scene(root, 900, 600);
        this.attachKeyboardShortcuts(scene);

        stage.setTitle("Smart Food Basket");
        stage.setScene(scene);
        stage.show();

        this.populateGrid(this.basketModel.getCatalog());
        this.updateView();
    }

    private FlowPane createGrid() {
        FlowPane pane = new FlowPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(14));
        return pane;
    }

    private HBox createFilterBar() {
        Button buttonAll = new Button("Mostrar todos");
        Button buttonVeg = new Button("Apenas vegetais");
        Button buttonProt = new Button("Apenas proteínas");

        buttonAll.setOnAction(e -> this.applyFilter(FILTER_ALL));
        buttonVeg.setOnAction(e -> this.applyFilter("GREEN"));
        buttonProt.setOnAction(e -> this.applyFilter("RED"));

        ComboBox<String> sortBox = this.createSortBox();

        HBox filterBox = new HBox(10, buttonAll, buttonVeg, buttonProt, sortBox);
        filterBox.setPadding(new Insets(10));
        return filterBox;
    }

    private ComboBox<String> createSortBox() {
        ComboBox<String> sortBox = new ComboBox<>();
        sortBox.getItems().addAll(SORT_BY_PRICE, "Ordenar por calorias (maior a menor)");
        sortBox.setPromptText("Ordenar por...");
        sortBox.setOnAction(e -> this.applySort(sortBox.getValue()));
        return sortBox;
    }

    private void attachKeyboardShortcuts(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.Z) {
                this.basketModel.undo();
            }
            if (e.isControlDown() && e.getCode() == KeyCode.Y) {
                this.basketModel.redo();
            }
        });
    }

    private void applyFilter(String filter) {
        List<FoodItem> filtered = new ArrayList<>();
        for (FoodItem item : this.basketModel.getCatalog()) {
            if (filter.equals(FILTER_ALL) || item.getGroup().name().equals(filter)) {
                filtered.add(item);
            }
        }
        this.populateGrid(filtered);
    }

    private void applySort(String sort) {
        List<FoodItem> sorted = new ArrayList<>(this.basketModel.getCatalog());
        if (sort.equals(SORT_BY_PRICE)) {
            sorted.sort((a, b) -> Double.compare(a.getBasePrice(), b.getBasePrice()));
        } else {
            sorted.sort((a, b) -> Double.compare(b.getBaseCalories(), a.getBaseCalories()));
        }
        this.populateGrid(sorted);
    }

    private void populateGrid(List<FoodItem> items) {
        this.grid.getChildren().clear();
        for (FoodItem item : items) {
            this.grid.getChildren().add(this.createTile(item));
        }
    }

    private VBox createSidebar() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(14));
        box.setPrefWidth(220);
        box.setStyle("-fx-background-color: #F5F4F0; -fx-border-color: #DDD; -fx-border-width: 0 0 0 0.5;");

        Label title = this.createSectionTitle("Dashboard");
        this.totalPriceLabel = new Label("Custo total: 0.00 €");
        this.totalCaloriesLabel = new Label("Calorias: 0 kcal");

        this.createOdsBadges();
        Label basketTitle = this.createSectionTitle("Cabaz");
        this.basketList = this.createBasketList();

        Button undoButton = this.createSimpleButton("Desfazer", e -> this.basketModel.undo());
        Button redoButton = this.createSimpleButton("Refazer", e -> this.basketModel.redo());
        Button checkoutButton = this.createCheckoutButton();

        box.getChildren().addAll(
                title,
                this.odsDiversityBadge,
                this.odsSustainabilityBadge,
                this.odsHealthBadge,
                this.totalPriceLabel,
                this.totalCaloriesLabel,
                basketTitle,
                this.basketList,
                undoButton,
                redoButton,
                checkoutButton
        );
        return box;
    }

    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 11px; -fx-text-fill: #888; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        return label;
    }

    private void createOdsBadges() {
        this.odsDiversityBadge = new Label("Diversidade");
        this.odsSustainabilityBadge = new Label("Sustentabilidade");
        this.odsHealthBadge = new Label("Saúde");
        this.odsDiversityBadge.setStyle("-fx-text-fill: grey;");
        this.odsSustainabilityBadge.setStyle("-fx-text-fill: grey;");
        this.odsHealthBadge.setStyle("-fx-text-fill: grey;");
    }

    private ListView<String> createBasketList() {
        ListView<String> list = new ListView<>();
        list.setPrefHeight(300);
        list.setStyle("-fx-font-size: 12px;");
        return list;
    }

    private Button createSimpleButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(handler);
        return button;
    }

    private Button createCheckoutButton() {
        Button button = new Button("Finalizar compra");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(
                "-fx-background-color: #185FA5;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 10;"
                        + "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;"
        );
        button.setOnAction(e -> this.handleCheckout());
        return button;
    }

    private void handleCheckout() {
        if (this.basketModel.getItems().isEmpty()) {
            this.showError("O cabaz está vazio!");
            return;
        }
        try {
            String fileName = this.basketModel.exportReceipt();
            this.showInfo("Recibo gerado: " + fileName);
        } catch (IOException e) {
            this.showError("Erro ao gerar recibo: " + e.getMessage());
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Compra finalizada");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshOdsBadges() {
        this.odsSustainabilityBadge.setStyle(this.colorForGoal(this.basketModel.hasMetSustainGoal()));
        this.odsDiversityBadge.setStyle(this.colorForGoal(this.basketModel.hasMetDiversityGoal()));
        this.odsHealthBadge.setStyle(this.colorForGoal(this.basketModel.hasMetVegetableGoal()));
    }

    private String colorForGoal(boolean achieved) {
        return achieved ? "-fx-text-fill: green;" : "-fx-text-fill: grey;";
    }

    private VBox createTile(FoodItem item) {
        VBox tile = new VBox(4);
        tile.setAlignment(Pos.CENTER);
        tile.setPrefWidth(108);
        tile.setPrefHeight(118);
        tile.setPadding(new Insets(10, 6, 10, 6));
        tile.setStyle(this.tileStyle(item));
        tile.getChildren().addAll(
                this.loadFoodImage(item),
                this.createTileNameLabel(item),
                this.createTilePriceLabel(item),
                this.createTileCaloriesLabel(item)
        );
        tile.setOnMouseClicked(e -> this.onTileClicked(tile, item));
        return tile;
    }

    private String tileStyle(FoodItem item) {
        return "-fx-background-color: " + item.getGroup().getColor() + ";"
                + "-fx-background-radius: 12;"
                + "-fx-border-radius: 12;"
                + "-fx-border-color: rgba(0,0,0,0.07);"
                + "-fx-border-width: 0.5;"
                + "-fx-cursor: hand;";
    }

    private Label createTileNameLabel(FoodItem item) {
        Label name = new Label(item.getName());
        name.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        return name;
    }

    private Label createTilePriceLabel(FoodItem item) {
        Label price = new Label(String.format("%.2f %s", item.getBasePrice(), item.getUnitLabel()));
        price.setStyle("-fx-font-size: 10px; -fx-text-fill: #555;");
        return price;
    }

    private Label createTileCaloriesLabel(FoodItem item) {
        Label calories = new Label(String.format("%.0f kcal", item.getBaseCalories()));
        calories.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");
        return calories;
    }

    private void onTileClicked(VBox tile, FoodItem item) {
        this.animateTile(tile);
        this.handleTileClick(item);
    }

    private void animateTile(VBox tile) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), tile);
        scale.setFromX(1.0);
        scale.setToX(1.1);
        scale.setFromY(1.0);
        scale.setToY(1.1);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }

    private void handleTileClick(FoodItem item) {
        this.playSound();
        if (item.getUnitLabel().equals(UNIT_PACKAGED)) {
            this.basketModel.addItem(item);
            return;
        }
        this.askWeightAndAdd(item);
    }

    private void playSound() {
        try {
            String path = this.getClass().getResource(SOUND_PATH).toExternalForm();
            Media media = new Media(path);
            MediaPlayer player = new MediaPlayer(media);
            player.play();
        } catch (Exception e) {

        }
    }

    private void askWeightAndAdd(FoodItem item) {
        TextInputDialog dialog = new TextInputDialog("300");
        dialog.setTitle("Adicionar " + item.getName());
        dialog.setHeaderText(item.getName() + " — " + item.getBasePrice() + " €/kg");
        dialog.setContentText("Quantas gramas deseja adicionar?");

        Optional<String> response = dialog.showAndWait();
        if (response.isEmpty()) {
            return;
        }
        this.parseAndAddBulk(item, response.get());
    }

    private void parseAndAddBulk(FoodItem item, String weightText) {
        try {
            double grams = Double.parseDouble(weightText);
            BulkFood bulk = new BulkFood(
                    item.getName(),
                    item.getGroup(),
                    item.getBasePrice(),
                    item.getBaseCalories(),
                    grams,
                    item.getImagePath()
            );
            this.basketModel.addItem(bulk);
        } catch (NumberFormatException e) {
            this.showError("Peso inválido: " + weightText);
        }
    }

    private ImageView loadFoodImage(FoodItem item) {
        String path = IMAGES_FOLDER + item.getImagePath();
        try {
            Image image = new Image(this.getClass().getResourceAsStream(path));
            ImageView view = new ImageView(image);
            view.setFitWidth(48);
            view.setFitHeight(48);
            view.setPreserveRatio(true);
            return view;
        } catch (Exception e) {
            return new ImageView();
        }
    }

    private void refreshBasketList() {
        this.basketList.getItems().clear();
        for (FoodItem item : this.basketModel.getItems()) {
            this.basketList.getItems().add(this.formatBasketLine(item));
        }
    }

    private String formatBasketLine(FoodItem item) {
        String quantity = this.formatQuantity(item);
        return String.format("%s %s · %.2f €", item.getName(), quantity, item.getPrice());
    }

    private String formatQuantity(FoodItem item) {
        double weight = item.getWeightInGrams();
        if (weight > 0) {
            return "(" + (int) weight + "g)";
        }
        return "×1";
    }
}
package app.ui;

import app.model.Model;
import app.model.BulkFood;
import app.model.FoodItem;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class SmartFoodBasketApplication extends Application implements View {

    private Model model;
    private FlowPane grid;
    private VBox sidebar;
    private Label totalPriceLabel;
    private Label totalCaloriesLabel;
    private Label odsDiversityBadge;
    private Label odsSustainabilityBadge;
    private Label odsHealthBadge;
    private ListView<String> basketList;

    @Override
    public void start(Stage stage) {
        this.model = new Model();
        this.model.setView(this);

        this.loadData();
        this.buildUI(stage);
    }

    private void loadData() {
        try {
            this.model.loadFromCsv("src/main/resources/foods_large.csv");
        } catch (IOException e) {
            this.showError("Erro ao carregar o ficheiro: " + e.getMessage());
        }
    }

    private void buildUI(Stage stage) {
        BorderPane root = new BorderPane();

        this.grid = new FlowPane();
        this.grid.setHgap(10);
        this.grid.setVgap(10);
        this.grid.setPadding(new javafx.geometry.Insets(14));

        this.sidebar = this.createSidebar();

        root.setCenter(this.grid);
        root.setRight(this.sidebar);

        Button buttonAll = new Button("Mostrar todos");
        Button buttonVeg = new Button("Apenas vegetais");
        Button buttonProt = new Button("Apenas proteínas");

        ComboBox<String> sortBox = new ComboBox<>();
        sortBox.getItems().addAll("Ordenar por preço (menor a maior)", "Ordenar por calorias (maior a menor)");
        sortBox.setPromptText("Ordenar por...");
        sortBox.setOnAction(e -> this.applySort(sortBox.getValue()));

        HBox filterBox = new HBox(10, buttonAll, buttonVeg, buttonProt,sortBox);
        filterBox.setPadding(new javafx.geometry.Insets(10));

        VBox centerArea = new VBox(filterBox, this.grid);
        root.setCenter(centerArea);

        buttonAll.setOnAction(e -> this.applyFilter("All"));
        buttonVeg.setOnAction(e -> this.applyFilter("GREEN"));
        buttonProt.setOnAction(e -> this.applyFilter("RED"));

        Scene scene = new Scene(root, 900, 600);
        scene.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.Z){
                this.model.undo();
            }
            if (e.isControlDown() && e.getCode() == KeyCode.Y){
                this.model.redo();
            }
        });
        stage.setTitle("Smart Food Basket");
        stage.setScene(scene);
        stage.show();

        for (FoodItem item : this.model.getCatalog()) {
            this.grid.getChildren().add(this.createTile(item));
        }

        this.updateView();
    }

    private void applyFilter(String filter){
        this.grid.getChildren().clear();
        for (FoodItem item : this.model.getCatalog()) {
            if(filter.equals("All") || item.getGroup().name().equals(filter)){
                this.grid.getChildren().add(this.createTile(item));
            }
        }
    }

    private void applySort(String sort){
        List<FoodItem> sorted = new ArrayList<>(this.model.getCatalog());
        if (sort.equals("Ordenar por preço (menor a maior)")) {
            sorted.sort((a, b) -> Double.compare(a.getBasePrice(), b.getBasePrice()));
        } else {
            sorted.sort((a, b) -> Double.compare(b.getBaseCalories(), a.getBaseCalories()));
        }
        this.grid.getChildren().clear();
        for (FoodItem item : sorted) {
            this.grid.getChildren().add(this.createTile(item));
        }
    }

    private VBox createSidebar() {
        VBox box = new VBox(10);
        box.setPadding(new javafx.geometry.Insets(14));
        box.setPrefWidth(220);
        box.setStyle("-fx-background-color: #F5F4F0; -fx-border-color: #DDD; -fx-border-width: 0 0 0 0.5;");

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 11px; -fx-text-fill: #888; -fx-font-weight: bold;");

        this.totalPriceLabel = new Label("Custo total: 0.00 €");
        this.totalCaloriesLabel = new Label("Calorias: 0 kcal");

        Label basketTitle = new Label("Cabaz");
        basketTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #888; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        this.basketList = new ListView<>();
        this.basketList.setPrefHeight(300);
        this.basketList.setStyle("-fx-font-size: 12px;");

        Button undoButton = new Button("Desfazer");
        undoButton.setMaxWidth(Double.MAX_VALUE);
        undoButton.setOnAction(e -> this.model.undo());

        Button redoButton = new Button("Refazer");
        redoButton.setMaxWidth(Double.MAX_VALUE);
        redoButton.setOnAction(e -> this.model.redo());

        Button checkoutButton = this.createCheckoutButton();

        this.odsDiversityBadge = new Label("Diversidade");
        this.odsSustainabilityBadge = new Label("Sustentabilidade");
        this.odsHealthBadge = new Label("Saúde");

        this.odsDiversityBadge.setStyle("-fx-text-fill: grey;");
        this.odsSustainabilityBadge.setStyle("-fx-text-fill: grey;");
        this.odsHealthBadge.setStyle("-fx-text-fill: grey;");

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

    private Button createCheckoutButton() {
        Button button = new Button("Finalizar compra");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(
                "-fx-background-color: #185FA5;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        button.setOnAction(e -> this.handleCheckout());
        return button;
    }

    private void handleCheckout() {
        if (this.model.getItems().isEmpty()) {
            this.showError("O cabaz está vazio!");
            return;
        }

        try {
            String fileName = this.model.exportReceipt();
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

    @Override
    public void updateView() {
        this.totalPriceLabel.setText(String.format("Custo total: %.2f €", this.model.getTotalPrice()));
        this.totalCaloriesLabel.setText(String.format("Calorias: %.0f kcal", this.model.getTotalCalories()));
        this.refreshBasketList();
        this.odsSustainabilityBadge.setStyle(this.model.hasMetSustainGoal() ? "-fx-text-fill: green;" : "-fx-text-fill: grey;");
        this.odsDiversityBadge.setStyle(this.model.hasMetDiversityGoal() ? "-fx-text-fill: green;" : "-fx-text-fill: grey;");
        this.odsHealthBadge.setStyle(this.model.hasMetVegetableGoal() ? "-fx-text-fill: green;" : "-fx-text-fill: grey;");
    }

    @Override
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private VBox createTile(FoodItem item) {
        VBox tile = new VBox(4);
        tile.setAlignment(Pos.CENTER);
        tile.setPrefWidth(108);
        tile.setPrefHeight(118);
        tile.setPadding(new javafx.geometry.Insets(10, 6, 10, 6));

        String bgColor = item.getGroup().getColor();
        tile.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: rgba(0,0,0,0.07);" +
                        "-fx-border-width: 0.5;" +
                        "-fx-cursor: hand;"
        );

        ImageView imageView = this.loadFoodImage(item);

        Label name = new Label(item.getName());
        name.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        Label price = new Label(String.format("%.2f %s", item.getBasePrice(), item.getUnitLabel()));
        price.setStyle("-fx-font-size: 10px; -fx-text-fill: #555;");

        Label calories = new Label(String.format("%.0f kcal", item.getBaseCalories()));
        calories.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");

        tile.getChildren().addAll(imageView, name, price,calories);

        tile.setOnMouseClicked(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), tile);
            scale.setFromX(1.0);
            scale.setToX(1.1);
            scale.setFromY(1.0);
            scale.setToY(1.1);
            scale.setCycleCount(2);
            scale.setAutoReverse(true);
            scale.play();
            this.handleTileClick(item);
        });
        return tile;
    }

    private void handleTileClick(FoodItem item) {
        this.playSound();
        if (item.getUnitLabel().equals("€/un.")) {
            this.model.addItem(item);
            return;
        }
        this.askWeightAndAdd(item);
    }

    private void playSound(){
        String path = getClass().getResource("/Sounds/cashRegister.mp3").toExternalForm();
        Media media = new Media(path);
        MediaPlayer player = new MediaPlayer(media);
        player.play();
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

        try {
            double grams = Double.parseDouble(response.get());
            BulkFood bulk = new BulkFood(
                    item.getName(), item.getGroup(),
                    item.getBasePrice(), item.getBaseCalories(),
                    grams, ""
            );
            this.model.addItem(bulk);
        } catch (NumberFormatException e) {
            this.showError("Peso inválido: " + response.get());
        }
    }

    private ImageView loadFoodImage(FoodItem item) {
        String path = "/emojis/" + item.getImagePath();
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
        for (FoodItem item : this.model.getItems()) {
            String line = this.formatBasketLine(item);
            this.basketList.getItems().add(line);
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

    public static void main(String[] args) {
        launch(args);
    }
}
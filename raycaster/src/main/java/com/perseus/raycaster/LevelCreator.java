package com.perseus.raycaster;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Scene;

public class LevelCreator {

    private static final int WINDOW_WIDTH = 750;
    private static final int WINDOW_HEIGHT = 550;
    private static final int GRID_SIZE = 10; // n x n grid
    private static final int CELL_SIZE = 400 / GRID_SIZE;
    private static final int PADDING = 10;

    private Pane gridPane;
    private int[][] gridData = new int[GRID_SIZE][GRID_SIZE]; // 2D array of n x n dimensions

    public Scene createScene(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Left side panel
        VBox buttonPanel = new VBox(PADDING);
        buttonPanel.setPrefWidth(200);
        buttonPanel.setStyle("-fx-background-color: #010;");
        buttonPanel.setPadding(new Insets(PADDING));

        // Brush Picker Section
        Label brushLabel = new Label("Brush Picker");
        styleLabel(brushLabel);

        ToggleGroup brushGroup = new ToggleGroup();
        RadioButton emptyTileBrush = new RadioButton("Empty Tile");
        RadioButton coloredTileBrush = new RadioButton("Colored Tile");
        RadioButton startPointBrush = new RadioButton("Start Point");
        RadioButton endPointBrush = new RadioButton("End Point");
        styleRadioButton(emptyTileBrush);
        styleRadioButton(coloredTileBrush);
        styleRadioButton(startPointBrush);
        styleRadioButton(endPointBrush);
        emptyTileBrush.setToggleGroup(brushGroup);
        coloredTileBrush.setToggleGroup(brushGroup);
        startPointBrush.setToggleGroup(brushGroup);
        endPointBrush.setToggleGroup(brushGroup);
        coloredTileBrush.setSelected(true);

        // Grid Size Section
        Label gridSizeLabel = new Label("Grid Size");
        styleLabel(gridSizeLabel);
        TextField gridSizeInput = new TextField();
        gridSizeInput.setPromptText("Enter grid size (e.g., 10)");
        styleTextField(gridSizeInput);
        Button applyGridSizeButton = new Button("Apply");
        applyGridSizeButton.setPrefWidth(100);
        applyGridSizeButton.setPrefHeight(29);
        styleButton(applyGridSizeButton);

        applyGridSizeButton.setOnAction(e -> {
            String input = gridSizeInput.getText();
            try {
                int newSize = Integer.parseInt(input);
                if (newSize > 0 && newSize <= 100) {
                    System.out.println("Grid size updated to: " + newSize);
                } else {
                    System.out.println("Invalid grid size. Must be 1-100.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter a number.");
            }
        });

        // Player Orientation Section
        Label orientationLabel = new Label("Player Orientation");
        styleLabel(orientationLabel);
        Spinner<Integer> orientationSpinner = new Spinner<>(0, 360, 0, 15);
        orientationSpinner.setPrefWidth(100);
        Tooltip.install(orientationSpinner, new Tooltip("Set the player's initial facing direction (in degrees)."));

        // Reset Button
        Button resetButton = new Button("Reset");
        resetButton.setPrefWidth(180);
        resetButton.setPrefHeight(38);        
        resetButton.setStyle("-fx-background-color: black; -fx-text-fill: firebrick; -fx-border-color: firebrick; -fx-border-width: 2px;");
        resetButton.setOnMouseEntered(e -> resetButton.setStyle("-fx-background-color: firebrick; -fx-text-fill: black; -fx-border-color: firebrick;"));
        resetButton.setOnMouseExited(e -> resetButton.setStyle("-fx-background-color: black; -fx-text-fill: firebrick; -fx-border-color: firebrick;"));
        resetButton.setOnAction(e -> resetGrid());
        
        Button exportButton = new Button("Export");
        exportButton.setPrefWidth(180);
        exportButton.setPrefHeight(38);
        exportButton.setStyle("-fx-background-color: black; -fx-text-fill: orange; -fx-border-color: orange; -fx-border-width: 2px;");
        exportButton.setOnMouseEntered(e -> exportButton.setStyle("-fx-background-color: orange; -fx-text-fill: black; -fx-border-color: orange;"));
        exportButton.setOnMouseExited(e -> exportButton.setStyle("-fx-background-color: black; -fx-text-fill: orange; -fx-border-color: orange;"));
        exportButton.setOnAction(e -> exportGrid());
        
        // Return to Menu Button
        Button returnButton = new Button("Return to Menu");
        returnButton.setPrefWidth(180);
        returnButton.setPrefHeight(38);
        styleButton(returnButton);
        returnButton.setOnAction(e -> returnToMenu(primaryStage));

        // Help Button
        Button helpButton = new Button("Help?");
        helpButton.setPrefWidth(180);
        helpButton.setPrefHeight(38);
        styleButton(helpButton);
        Tooltip helpTooltip = new Tooltip("Hover over controls to see their function.");
        Tooltip.install(helpButton, helpTooltip);

        // Add elements to button panel
        buttonPanel.getChildren().addAll(
                brushLabel, emptyTileBrush, coloredTileBrush, startPointBrush, endPointBrush,
                gridSizeLabel, gridSizeInput, applyGridSizeButton,
                orientationLabel, orientationSpinner,
                resetButton, exportButton, returnButton, helpButton
        );

        buttonPanel.setAlignment(Pos.TOP_CENTER);

        // Create a container for the grid to manage its padding
        StackPane gridContainer = new StackPane();
        gridContainer.setPadding(new Insets(PADDING, 0, 0, 0));

        // Initialize gridPane
        gridPane = new Pane();
        gridContainer.getChildren().add(gridPane);

        root.setCenter(gridContainer);
        root.setLeft(buttonPanel);

        createGrid(gridPane);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateGridSize(gridPane, scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateGridSize(gridPane, scene);
        });

        return scene;
    }


    // Helper methods for styling controls
    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: black; -fx-text-fill: limegreen; -fx-border-color: limegreen; -fx-border-width: 2px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: limegreen; -fx-text-fill: black; -fx-border-color: limegreen;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: black; -fx-text-fill: limegreen; -fx-border-color: limegreen;"));
    }

    private void styleLabel(Label label) {
        label.setStyle("-fx-text-fill: limegreen; -fx-font-size: 14px;");
    }

    private void styleRadioButton(RadioButton radioButton) {
        radioButton.setStyle("-fx-text-fill: limegreen;");
    }

    private void styleTextField(TextField textField) {
        textField.setStyle("-fx-background-color: black; -fx-text-fill: limegreen; -fx-border-color: limegreen; -fx-border-width: 2px;");
    }

    private void createGrid(Pane gridPane) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setFill(Color.BLACK);
                cell.setStroke(Color.GREEN);

                final int r = row;
                final int c = col;

                // Start drag detection
                cell.setOnDragDetected(event -> {
                    cell.startFullDrag(); // Enable full drag mode
                    lightUpCell(cell, r, c); // Light up the initial cell on drag start
                });

                // Handle mouse pressed
                cell.setOnMousePressed(event -> lightUpCell(cell, r, c));

                // Handle mouse drag entering the cell
                cell.setOnMouseDragEntered(event -> lightUpCell(cell, r, c));

                gridPane.getChildren().add(cell);
            }
        }
    }

    private void lightUpCell(Rectangle cell, int row, int col) {
        if (gridData[row][col] != 1) {
            gridData[row][col] = 1;
            cell.setFill(Color.LIMEGREEN);
        }
    }

    private void updateGridSize(Pane gridPane, Scene scene) {
        double availableWidth = scene.getWidth() - 200 - PADDING;
        double availableHeight = scene.getHeight() - 2 * PADDING;
        double gridSize = Math.min(availableWidth, availableHeight);

        gridPane.setPrefSize(gridSize, gridSize);

        double cellSize = gridSize / GRID_SIZE;

        int cellIndex = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Rectangle cell = (Rectangle) gridPane.getChildren().get(cellIndex++);
                cell.setWidth(cellSize);
                cell.setHeight(cellSize);
                cell.setX(col * cellSize);
                cell.setY(row * cellSize);
            }
        }
    }

    private void resetGrid() {
        // Reset the grid data to empty
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                gridData[row][col] = 0; // Empty the grid
            }
        }

        // Reset the visual grid by resetting each cell color
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            Rectangle cell = (Rectangle) gridPane.getChildren().get(i);
            cell.setFill(Color.BLACK);
        }
    }
    
    private void exportGrid() {
    	System.out.println("EXPORTING...");
    	// TODO ADD EXPORT
    }

    private void returnToMenu(Stage primaryStage) {
        Main mainMenu = new Main();
        try {
            mainMenu.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

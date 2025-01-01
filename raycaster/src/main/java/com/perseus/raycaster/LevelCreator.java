package com.perseus.raycaster;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.Scene;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.util.LinkedList;

public class LevelCreator {

    private static final int WINDOW_WIDTH = 750;
    private static final int WINDOW_HEIGHT = 550;
    private static int GRID_SIZE = 10; // n x n grid
    private static final int CELL_SIZE = 400 / GRID_SIZE;
    private static final int PADDING = 10;
    
    private int playerRotation = 0;
    
    private boolean startPointPlaced = false;
    private int brush = 1;
    private Pane gridPane;
    private int[][] gridData = new int[GRID_SIZE][GRID_SIZE]; // 2D array of n x n dimensions
    
    private Label statusLabel;

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
        
        // Default brush
        coloredTileBrush.setSelected(true);
        brush = 1;
        
        brushGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
        	if (newValue == emptyTileBrush) {
        		brush = 0;
        	} else if (newValue == coloredTileBrush) {
        		brush = 1;
        	} else if (newValue == startPointBrush) {
        		brush = 2;
        	} else if (newValue == endPointBrush) {
        		brush = 3;
        	}
        	System.out.println("Brush switched to: " + brush); // Debugging
        	showStatusMessage("Brush switched to: " + brush, false);
        });
        
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
                if (newSize > 2 && newSize <= 50) { // Ensure the size is within a valid range
                    System.out.println("Grid size updated to: " + newSize);
                    showStatusMessage("Grid size updated to: " + newSize, false);
                    
                    // Update GRID_SIZE and recreate gridData
                    GRID_SIZE = newSize;
                    gridData = new int[GRID_SIZE][GRID_SIZE]; // Reset the grid data

                    // Clear existing grid and recreate it
                    gridPane.getChildren().clear();
                    createGrid(gridPane);

                    // Update cell size dynamically
                    updateGridSize(gridPane, gridPane.getScene());
                } else {
                    System.out.println("Invalid grid size. Must be 3-50.");
                    showStatusMessage("Invalid grid size. Must be 3-50.", true);
                }
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter a whole number.");
                showStatusMessage("Invalid input. Please enter a whole number.", true);
            }
        });


        // Player Orientation Section
        Label orientationLabel = new Label("Player Orientation");
        styleLabel(orientationLabel);
        Spinner<Integer> orientationSpinner = new Spinner<>(0, 360, 0, 15);
        orientationSpinner.setPrefWidth(100);
        Tooltip.install(orientationSpinner, new Tooltip("Set the player's initial facing direction from east (in degrees)."));

        orientationSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            playerRotation = newValue; // Update playerRotation when spinner value changes
            System.out.println("Player rotation updated to: " + playerRotation); // Debugging line
        	showStatusMessage("Player rotation updated to: " + playerRotation, false);
        });
        
        
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
        helpButton.setOnAction(e -> openHelpWindow());
        
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

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
        gridContainer.setPadding(new Insets(PADDING, 0, 2*PADDING, 0));


        // Initialize gridPane
        gridPane = new Pane();
        gridContainer.getChildren().add(gridPane);

        root.setCenter(gridContainer);
        root.setLeft(buttonPanel);
        root.setBottom(statusLabel); // Add status label to the bottom of the layout

        createGrid(gridPane);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateGridSize(gridPane, scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateGridSize(gridPane, scene);
        });

        return scene;
    }

    private void openHelpWindow() {
        Stage helpStage = new Stage();
        helpStage.setTitle("Help");

        VBox helpLayout = new VBox(30);
        helpLayout.setStyle("-fx-background-color: black; -fx-alignment: center;");

        String[] helpMessages = {
            "Level Creator",
            "Choose Grid Size and click Apply",
            "Pick Brush with Brush Picker",
            "Click or hold LMB on tiles to paint",
            "There must be one start point and at least one end point",
            "There must be at least one path connecting start point to at least one end point",
            "Choose player orientation (degrees) starting facing east",
            "Reset to clear the board if needed",
            "Export to save level",
            "Once exported, Return to Menu and Play Level",
        };

        for (String message : helpMessages) {
            Text helpText = new Text(message);
            helpText.setFill(Color.LIME);
            helpText.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");
            StackPane lineContainer = new StackPane(helpText);
            lineContainer.setPrefHeight(15);
            helpLayout.getChildren().add(lineContainer);
        }

        Scene helpScene = new Scene(helpLayout, 500, 500);
        helpStage.setScene(helpScene);
        helpStage.show();
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
        if (brush == 2) {
        	if (!startPointPlaced) {
	            // Only place the start point if one hasn't been placed yet
	            gridData[row][col] = brush;
	            cell.setFill(Color.GREEN);
	            startPointPlaced = true; // Set the flag to true after placing the start point
        	} else {
        		showStatusMessage("You may only have one start point.", true);
        	}
        } else if (brush != 2) {
            // For other brushes (empty, colored, etc.), allow the action
            if (gridData[row][col] != brush) {
            	if (gridData[row][col] == 2) startPointPlaced = false;
                gridData[row][col] = brush;
                if (brush == 0) {
                    cell.setFill(Color.BLACK);
                } else if (brush == 1) {
                    cell.setFill(Color.LIMEGREEN);
                } else if (brush == 3) {
                    cell.setFill(Color.FIREBRICK);
                }
            }
        }
    }


    private void updateGridSize(Pane gridPane, Scene scene) {
        double availableWidth = scene.getWidth() - 200 - PADDING;
        double availableHeight = scene.getHeight() - 3 * PADDING;
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
        
        startPointPlaced = false;
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

        // Reset start point flag
        startPointPlaced = false;
    }

    
    /*
     * Conditions to be met for export:
     * - There is one start point
     * - There is at least one end point
     * - There is at least one way to get from the start point to at least one end point
     * 
     * It does not matter if there are multiple end points but only one is accessible
     * 
     * This uses a breadth-first search algorithm to identify if there is a path connecting points
     */
    
    private void exportGrid() {
        // Check if there is at least one start point and one end point
        boolean hasStartPoint = false;
        boolean hasEndPoint = false;
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (gridData[row][col] == 2) {  // Start point
                    hasStartPoint = true;
                }
                if (gridData[row][col] == 3) {  // End point
                    hasEndPoint = true;
                }
            }
        }
        
        // Show error if no start point or end point
        if (!hasStartPoint) {
            showStatusMessage("Error: No start point on the map.", true);
            return;
        }
        if (!hasEndPoint) {
            showStatusMessage("Error: No end point on the map.", true);
            return;
        }
        
        // Ensure there's a path between a start point and an end point
        boolean pathExists = false;
        outerLoop:
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (gridData[row][col] == 2) {  // Start point found, check path
                    pathExists = bfs(row, col);
                    if (pathExists) {
                        break outerLoop;
                    }
                }
            }
        }
        
        // Show error if no path exists between start and end points
        if (!pathExists) {
            showStatusMessage("Error: No path from start point to end point.", true);
            return;
        }

        // Proceed with export
        System.out.println("Grid size: " + GRID_SIZE + "x" + GRID_SIZE);
        System.out.println("Player rotation: " + playerRotation);
        for (int row = 0; row < GRID_SIZE; row++) {
            String debugPrint = "";
            for (int col = 0; col < GRID_SIZE; col++) {
                debugPrint += gridData[row][col] + " ";
            }
            System.out.println(debugPrint);
        }
        exportToXML();
    }

    // Helper method to perform BFS to check if there's a path from start to end
    private boolean bfs(int startRow, int startCol) {
        boolean[][] visited = new boolean[GRID_SIZE][GRID_SIZE];
        LinkedList<int[]> queue = new LinkedList<>();
        
        // Directions: up, down, left, right
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};
        
        // Start from the given start position
        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1];
            
            // Check for end point
            if (gridData[x][y] == 3) {
                return true;
            }
            
            // Explore the neighbors (up, down, left, right)
            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];
                
                // Check boundaries and if cell is empty or an end point
                if (newX >= 0 && newX < GRID_SIZE && newY >= 0 && newY < GRID_SIZE) {
                    if (!visited[newX][newY] && (gridData[newX][newY] == 0 || gridData[newX][newY] == 3)) {
                        visited[newX][newY] = true;
                        queue.add(new int[]{newX, newY});
                    }
                }
            }
        }
        
        return false;
    }
    
    private void exportToXML() {
        try {
            // Create a new document
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Root element
            Element rootElement = doc.createElement("level");
            doc.appendChild(rootElement);

            // Grid size
            Element gridSizeElement = doc.createElement("gridSize");
            gridSizeElement.appendChild(doc.createTextNode(String.valueOf(GRID_SIZE)));
            rootElement.appendChild(gridSizeElement);

            // Player angle
            Element playerAngleElement = doc.createElement("playerAngle");
            playerAngleElement.appendChild(doc.createTextNode(String.valueOf(playerRotation)));
            rootElement.appendChild(playerAngleElement);

            // Map data
            Element mapDataElement = doc.createElement("mapData");
            for (int row = 0; row < GRID_SIZE; row++) {
                Element rowElement = doc.createElement("row");
                StringBuilder rowData = new StringBuilder();
                for (int col = 0; col < GRID_SIZE; col++) {
                    rowData.append(gridData[row][col]).append(" ");
                }
                rowElement.appendChild(doc.createTextNode(rowData.toString().trim()));
                mapDataElement.appendChild(rowElement);
            }
            rootElement.appendChild(mapDataElement);

            // Write to file in the correct resources path
            String filePath = "src/main/resources/com/perseus/raycaster/LevelData.xml";
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

            showStatusMessage("Exported to " + filePath, false);
            System.out.println("Exported to " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            showStatusMessage("Error exporting to XML.", true);
            System.out.println("Error exporting to XML.");
        }
    }
    
    private void showStatusMessage(String message, boolean isError) {
        // Set the text color based on whether it's an error message
        statusLabel.setStyle(isError ? "-fx-text-fill: red; -fx-font-size: 14px;" : "-fx-text-fill: green; -fx-font-size: 14px;");
        statusLabel.setText(message);
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

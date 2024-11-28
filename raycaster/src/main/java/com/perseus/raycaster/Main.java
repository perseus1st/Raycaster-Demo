package com.perseus.raycaster;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create title
        Text title = new Text("Raycaster Game");
        title.getStyleClass().add("title"); // Apply title style from CSS

        // Create "Play Level" button
        Button playButton = new Button("Play Level");
        playButton.getStyleClass().add("button"); // Apply button style from CSS
        playButton.setOnAction(e -> startGame(primaryStage)); // Action for play button

        // Create "Create Level" button
        Button createButton = new Button("Create Level");
        createButton.getStyleClass().add("button"); // Apply button style from CSS
        createButton.setOnAction(e -> createLevel()); // Action for create button

        // VBox layout for stacking buttons and title
        VBox menuLayout = new VBox(20); // 20px spacing between elements
        menuLayout.setAlignment(javafx.geometry.Pos.CENTER); // Center all items
        menuLayout.getChildren().addAll(title, playButton, createButton);

        // Set the root layout container (StackPane) to hold the VBox layout
        StackPane root = new StackPane(menuLayout);

        // Create the scene and apply the CSS stylesheet
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/com/perseus/raycaster/style.css").toExternalForm()); // Link to the CSS file

        // Set the scene to the primary stage and show the window
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // This method will be called when "Play Level" is clicked
    private void startGame(Stage primaryStage) {
        // Launch the Raycaster game scene
        Raycaster raycaster = new Raycaster();
        try {
            raycaster.start(primaryStage);  // Start the Raycaster game scene
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method will be called when "Create Level" is clicked
    private void createLevel() {
        // Your code for level creation logic or screen transition
        System.out.println("Creating Level...");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

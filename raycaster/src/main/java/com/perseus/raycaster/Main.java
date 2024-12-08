package com.perseus.raycaster;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        Text title = new Text("Raycaster v2");
        title.getStyleClass().add("title");
        title.setFill(Color.LIME);

        Button playButton = new Button("Play Level");
        playButton.getStyleClass().add("button");
        playButton.setOnAction(e -> startGame(primaryStage));

        Button createButton = new Button("Create Level");
        createButton.getStyleClass().add("button");
        createButton.setOnAction(e -> createLevel(primaryStage));

        Button helpButton = new Button("Help");
        helpButton.getStyleClass().add("button");
        helpButton.setOnAction(e -> help());
                
        Polygon trapezoid1Highlight = new Polygon();
        trapezoid1Highlight.getPoints().addAll(
            -2.0,  19.0,  // Top-left corner
            102.0, -2.0,  // Top-right corner
            102.0, 102.0, // Bottom-right corner
            -2.0,  81.0   // Bottom-left corner
        );
        trapezoid1Highlight.setFill(Color.LIME);

        Polygon trapezoid1 = new Polygon();
        trapezoid1.getPoints().addAll(
            0.0,  20.0, 
            100.0,  0.0, 
            100.0, 100.0, 
            0.0,  80.0
        );
        trapezoid1.setFill(Color.BLACK);

        StackPane trapezoid1Stack = new StackPane(trapezoid1Highlight, trapezoid1);

        Polygon trapezoid2Highlight = new Polygon();
        trapezoid2Highlight.getPoints().addAll(
            -2.0, -2.0,   // Top-left corner
            102.0,  19.0, // Top-right corner
            102.0,  81.0, // Bottom-right corner
            -2.0, 102.0   // Bottom-left corner
        );
        trapezoid2Highlight.setFill(Color.LIME);

        Polygon trapezoid2 = new Polygon();
        trapezoid2.getPoints().addAll(
            0.0,  0.0,
            100.0,  20.0,
            100.0,  80.0,
            0.0,  100.0
        );
        trapezoid2.setFill(Color.BLACK);

        StackPane trapezoid2Stack = new StackPane(trapezoid2Highlight, trapezoid2);

        HBox trapezoidContainer = new HBox(0);
        trapezoidContainer.setAlignment(Pos.CENTER);
        trapezoidContainer.getChildren().addAll(trapezoid1Stack, trapezoid2Stack);

        
        
        VBox menuLayout = new VBox(15);
        menuLayout.setAlignment(Pos.TOP_CENTER);
        menuLayout.getChildren().addAll(title, playButton, createButton, helpButton, trapezoidContainer);


        StackPane root = new StackPane(menuLayout);

        Scene scene = new Scene(root, 750, 500);
        scene.getStylesheets().add(getClass().getResource("/com/perseus/raycaster/style.css").toExternalForm());

        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(550);
        primaryStage.setTitle("Raycaster_v2");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGame(Stage primaryStage) {
        Raycaster raycaster = new Raycaster();
        try {
            raycaster.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createLevel(Stage primaryStage) {
        LevelCreator levelCreator = new LevelCreator();
        Scene levelCreatorScene = levelCreator.createScene(primaryStage);
        primaryStage.setScene(levelCreatorScene);
        System.out.println("Creating Level...");
    }
    
    private void help() {
    	// code to "help"
    	System.out.println("Helping...");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

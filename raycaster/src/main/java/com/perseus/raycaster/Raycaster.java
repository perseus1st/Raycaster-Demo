package com.perseus.raycaster;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class Raycaster extends Application {

    private final int WIDTH = 700; // Width of the window
    private final int HEIGHT = 500; // Height of the window
    private final int FOV = 90;
    private final int TILE_SIZE = 50; // Size of a map tile

    private Canvas canvas; // Canvas for rendering
    private Player player; // Player instance
    private Map map; // Map instance
    private final Set<KeyCode> keysPressed = new HashSet<>(); // To track pressed keys

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Initialize the player at position (450, 350) facing right (angle 0)
        player = new Player(450, 350, 0);

        // Initialize the map (example layout)
        int[][] layout = {
            {1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1}
        };
        map = new Map(layout);

        // Animation Timer for rendering
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateMovement(); // Update player movement based on keys pressed
                render(gc);
            }
        }.start();

        // Handle key events for player movement
        Scene scene = new Scene(new StackPane(canvas), WIDTH, HEIGHT);

        // Enable keyboard focus on the canvas
        canvas.requestFocus();

        // Set key event handlers
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);

        primaryStage.setTitle("JavaFX Raycaster");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleKeyPressed(KeyEvent event) {
        keysPressed.add(event.getCode()); // Add key to the pressed set
    }

    private void handleKeyReleased(KeyEvent event) {
        keysPressed.remove(event.getCode()); // Remove key from the pressed set
    }

    private void updateMovement() {
        if (keysPressed.contains(KeyCode.W)) player.moveForward(map);
        if (keysPressed.contains(KeyCode.S)) player.moveBackward(map);
        if (keysPressed.contains(KeyCode.A)) player.rotateLeft();
        if (keysPressed.contains(KeyCode.D)) player.rotateRight();
    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH, HEIGHT); // Clear the canvas
        castRays(gc); // Render the 3D view using raycasting
    }

    private void castRays(GraphicsContext gc) {
        double rayAngle;
        double rayStep = Math.toRadians(FOV) / WIDTH;  // Adjust for FOV
    
        for (int x = 0; x < WIDTH; x++) {
            rayAngle = player.getAngle() - Math.toRadians(FOV / 2) + x * rayStep;
            Ray ray = new Ray(rayAngle);
            ray.cast(map, player);
            
            // Calculate distance and fix fisheye
            double distance = ray.getDistance() * Math.cos(rayAngle - player.getAngle());
    
            // Calculate wall slice height
            double wallHeight = (TILE_SIZE / distance) * 400;
    
            // Clamp the distance value to avoid illegal Color values
            // Set a minimum distance to prevent division by zero
            double clampedDistance = Math.max(distance, 0.1); // Avoid 0 distance

        // Calculate shading based on the corrected distance
        double colorValue = Math.min(1.0 / (clampedDistance / 100), 0.8); // Set a maximum brightness of 0.8

        // Draw the wall slice
        gc.setFill(Color.gray(colorValue)); // Shading based on distance
        gc.fillRect(x, (HEIGHT / 2) - (wallHeight / 2), 1, wallHeight);
    }
}



    public static void main(String[] args) {
        launch(args);
    }
}

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
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import java.util.HashSet;
import java.util.Set;

public class Raycaster extends Application {

    private final int WIDTH = 700; // Width of the window
    private final int HEIGHT = 500; // Height of the window
    private final int FOV = 60;
    private final int TILE_SIZE = 100; // Size of a map tile

    private Canvas canvas; // Canvas for rendering
    private Player player; // Player instance
    private Map map; // Map instance
    private final Set<KeyCode> keysPressed = new HashSet<>(); // To track pressed keys

    private Image wallTexture; // Wall texture image
    private PixelReader pixelReader; // PixelReader for the wall texture

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Initialize the player at position (450, 350) facing right (angle 0)
        player = new Player(450, 350, 0);

        // Initialize the map (example layout)
        int[][] layout = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 0, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 1, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        map = new Map(layout);

        // Load the wall texture and create a PixelReader
        wallTexture = new Image(getClass().getResourceAsStream("/com/perseus/raycaster/textures/wall_texture.png"));
        pixelReader = wallTexture.getPixelReader();

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

        // Set the color for the sky (top half)
        gc.setFill(Color.web("#393939")); // Replace with your desired sky color
        gc.fillRect(0, 0, WIDTH, HEIGHT / 2); // Fill the top half with sky color

        // Set the color for the ground (bottom half)
        gc.setFill(Color.web("#717171")); // Replace with your desired ground color
        gc.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT / 2); // Fill the bottom half with ground color

        // Now render the 3D walls using raycasting
        castRays(gc);
    }

    private void castRays(GraphicsContext gc) {
        double rayAngle;
        double rayStep = Math.toRadians(FOV) / WIDTH;  // Adjust for FOV
        int pixelSize = 1;

        for (int x = 0; x < WIDTH; x += pixelSize) {
            rayAngle = player.getAngle() - Math.toRadians(FOV / 2) + x * rayStep;
            Ray ray = new Ray(rayAngle);
            ray.cast(map, player);
    
            // Calculate distance to the wall and correct for fisheye effect
            double distance = ray.getDistance() * Math.cos(rayAngle - player.getAngle());
    
            // Calculate wall height
            double wallHeight = (TILE_SIZE / distance) * 400;
    
            // Calculate texture coordinates based on hit position

            // if there has been a vertical wall hit, use the y coordinate of the hit, else, x
            int texX = ray.getVerticalHit() ? (int) ray.getWallHitY() : (int) ray.getWallHitX();
            texX = Math.min(texX, (int) wallTexture.getWidth() - 1); // Ensure texX is within bounds
            
            // Loop through the height of the wall slice
            for (int y = 0; y < wallHeight; y++) {
                double drawY = (HEIGHT / 2) - (wallHeight / 2) + y; // Calculate y position to draw
                
                if (drawY >= 0 && drawY < HEIGHT) { // Check bounds
                    // Calculate texture Y coordinate
                    int texY = (int) ((y / wallHeight) * wallTexture.getHeight());
                    texY = Math.min(texY, (int) wallTexture.getHeight() - 1); // Ensure texY is within bounds
                    
                    // Get color from texture
                    Color color = pixelReader.getColor(texX, texY);
                    
                    // Set the pixel color to the canvas
                    gc.getPixelWriter().setColor(x, (int) Math.floor(drawY), color);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

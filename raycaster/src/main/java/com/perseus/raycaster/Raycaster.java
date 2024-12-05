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

    private final int WIDTH = 700;
    private final int HEIGHT = 500; 
    private final int FOV = 60;
    private final int TILE_SIZE = 100;
    private final int WALL_HEIGHT_MULTIPLIER = (HEIGHT*4)/5;

    private Canvas canvas;
    private Player player;
    private Map map;
    private final Set<KeyCode> keysPressed = new HashSet<>();

    private Image wallTexture;
    private PixelReader pixelReader;

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        player = new Player(450, 350, 0);

        int[][] layout = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 0, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 1, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        map = new Map(layout);

        wallTexture = new Image(getClass().getResourceAsStream("/com/perseus/raycaster/textures/cool_wall_texture.png"));
        pixelReader = wallTexture.getPixelReader();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateMovement();
                render(gc);
            }
        }.start();

        Scene scene = new Scene(new StackPane(canvas), WIDTH, HEIGHT);

        canvas.requestFocus();

        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);

        primaryStage.setTitle("JavaFX Raycaster");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleKeyPressed(KeyEvent event) {
        keysPressed.add(event.getCode());
    }

    private void handleKeyReleased(KeyEvent event) {
        keysPressed.remove(event.getCode());
    }

    private void updateMovement() {
        if (keysPressed.contains(KeyCode.W) || (keysPressed.contains(KeyCode.UP))) player.moveForward(map);
        if (keysPressed.contains(KeyCode.S) || (keysPressed.contains(KeyCode.DOWN))) player.moveBackward(map);
        if (keysPressed.contains(KeyCode.A) || (keysPressed.contains(KeyCode.LEFT))) player.rotateLeft();
        if (keysPressed.contains(KeyCode.D) || (keysPressed.contains(KeyCode.RIGHT))) player.rotateRight();
    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.web("#000000")); // sky color
        gc.fillRect(0, 0, WIDTH, HEIGHT / 2);

        gc.setFill(Color.web("#000000")); // ground color
        gc.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT / 2);

        castRays(gc);
    }

    private void castRays(GraphicsContext gc) {
        double rayAngle;
        double rayStep = Math.toRadians(FOV) / WIDTH;
        int pixelSize = WIDTH/175;  // The denominator represents how many rays will be cast, lower number = more pixelized
    
        double yOffset = player.getAnimationOffset();
        
        for (int x = 0; x < WIDTH; x += pixelSize) {
            rayAngle = player.getAngle() - Math.toRadians(FOV / 2) + x * rayStep;
            Ray ray = new Ray(rayAngle);
            ray.cast(map, player);
    
            double distance = ray.getDistance() * Math.cos(rayAngle - player.getAngle());
    
            double wallHeight = (TILE_SIZE / distance) * WALL_HEIGHT_MULTIPLIER; 
    
            int texX = ray.getVerticalHit() ? (int) ray.getWallHitY() : (int) ray.getWallHitX();
            texX = Math.min(texX, (int) wallTexture.getWidth() - 1);
    
            for (int y = 0; y < wallHeight; y++) {
                double drawY = (HEIGHT / 2) - (wallHeight / 2) + y + yOffset;
    
                if (drawY >= 0 && drawY < HEIGHT) {
                    int texY = (int) ((y / wallHeight) * wallTexture.getHeight());
                    texY = Math.min(texY, (int) wallTexture.getHeight() - 1);
    
                    Color color = pixelReader.getColor(texX, texY);
    
                    gc.setFill(color);
                    gc.fillRect(x, (int) Math.floor(drawY), pixelSize, 1);
                }
            }
        }
    }  
}

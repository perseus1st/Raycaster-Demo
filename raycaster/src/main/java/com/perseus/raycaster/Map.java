package com.perseus.raycaster;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Map {
    // Define a constant for the tile size
    public static final int TILE_SIZE = 100; // Size of each tile in pixels

    private final int[][] layout; // 2D array representing the map layout

    // Constructor to initialize the map layout
    public Map(int[][] layout) {
        this.layout = layout;
    }

    // Method to render the map
    public void render(GraphicsContext gc) {
        // Set the color for the walls
        gc.setFill(Color.GRAY);

        // Loop through the layout and draw walls
        for (int row = 0; row < layout.length; row++) {
            for (int col = 0; col < layout[row].length; col++) {
                if (layout[row][col] == 1) { // If the cell is a wall
                    gc.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE); // Draw the wall
                }
            }
        }
    }

    // Method to check for wall collisions
    public boolean isWall(int x, int y) {
        // Convert the x and y coordinates to the array indices
        int col = x / TILE_SIZE; // Use the constant tile size
        int row = y / TILE_SIZE; // Use the constant tile size

        // Check if the coordinates are within the bounds of the layout
        if (col < 0 || col >= layout[0].length || row < 0 || row >= layout.length) {
            return false; // Out of bounds
        }

        // Return true if there is a wall at the given position
        return layout[row][col] == 1;
    }

    // Getters for layout dimensions
    public int getWidth() {
        return layout[0].length;
    }

    public int getHeight() {
        return layout.length;
    }

    // Get the type of tile at a specific (x, y) coordinate
    public int getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= layout.length || y >= layout[0].length) {
            return -1; // Return -1 for out of bounds (error or no tile)
        }
        return layout[x][y]; // Return the tile type (e.g., 1 for a wall)
    }

    public int getTileSize() {
        return TILE_SIZE;
    }
}

package com.perseus.raycaster;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Map {
    public static final int TILE_SIZE = 100;

    private final int[][] layout; 

    public Map(int[][] layout) {
        this.layout = layout;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.GRAY);

        for (int row = 0; row < layout.length; row++) {
            for (int col = 0; col < layout[row].length; col++) {
                if (layout[row][col] == 1) { 
                    gc.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE); 
                }
            }
        }
    }

    public boolean isWall(int x, int y) {
        int col = x / TILE_SIZE;
        int row = y / TILE_SIZE;

        if (col < 0 || col >= layout[0].length || row < 0 || row >= layout.length) {
            return false;
        }

        return layout[row][col] == 1;
    }

    public int getWidth() {
        return layout[0].length;
    }

    public int getHeight() {
        return layout.length;
    }

    public int getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= layout.length || y >= layout[0].length) {
            return -1;
        }
        return layout[x][y]; 
    }

    public int getTileSize() {
        return TILE_SIZE;
    }
}

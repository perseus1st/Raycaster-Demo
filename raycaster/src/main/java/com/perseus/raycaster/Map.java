package com.perseus.raycaster;

public class Map {
    public static final int TILE_SIZE = 100;

    private final int[][] layout; 

    public Map(int[][] layout) {
        this.layout = layout;
    }
    
    public boolean isWall(int x, int y) {
        int col = x / TILE_SIZE;
        int row = y / TILE_SIZE;

        if (col < 0 || col >= layout[0].length || row < 0 || row >= layout.length) {
            return false;
        }

        // If the tile is 3 or 1
        return layout[row][col]%2 == 1;
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
        return layout[y][x]; 
    }

    public int getTileSize() {
        return TILE_SIZE;
    }
}

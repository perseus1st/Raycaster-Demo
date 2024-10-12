package com.perseus.raycaster;

public class Ray {
    private final double angle;
    private double endX, endY;
    private double distance;

    public Ray(double angle) {
        this.angle = angle;
    }

    // Calculate the intersection of the ray with the map
    public void cast(Map map, Player player) {
        double x = player.getX();
        double y = player.getY();
        
        // Determine the step size for the ray (small increments)
        double rayStepSize = 0.1;

        while (!map.isWall((int) x, (int) y)) {
            x += Math.cos(angle) * rayStepSize;
            y += Math.sin(angle) * rayStepSize;
        }

        // Calculate the distance from the player to the wall
        distance = Math.sqrt((x - player.getX()) * (x - player.getX()) + (y - player.getY()) * (y - player.getY()));

        // Set the end position of the ray (for debugging or 2D view)
        endX = x;
        endY = y;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }

    public double getDistance() {
        return distance;
    }
}

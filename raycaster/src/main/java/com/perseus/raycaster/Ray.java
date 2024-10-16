package com.perseus.raycaster;

public class Ray {
    private final double angle;  // Angle of the ray
    private double distance;     // Distance from player to wall
    private boolean verticalHit; // Flag to indicate if the hit was on a vertical wall
    private double wallHitX;     // X-coordinate of where the ray hit the wall
    private double wallHitY;     // Y-coordinate of where the ray hit the wall

    public Ray(double angle) {
        this.angle = angle;
    }

    // Calculate the intersection of the ray with the map
    public void cast(Map map, Player player) {
        double x = player.getX();
        double y = player.getY();
        int xbefore = 0;
        // Determine the step size for the ray (small increments)
        double rayStepSize = 0.1;

        while (!map.isWall((int) x, (int) y)) {
            xbefore = (int) Math.floor(x / Map.TILE_SIZE);

            x += Math.cos(angle) * rayStepSize;
            y += Math.sin(angle) * rayStepSize;
        }

        // if the x before the step is different than the x value after the step, there has been a vertical wall hit
        verticalHit = ((xbefore != Math.floor(x / Map.TILE_SIZE)));

        // Calculate the distance from the player to the wall
        distance = Math.sqrt((x - player.getX()) * (x - player.getX()) + (y - player.getY()) * (y - player.getY()));

        // Determine the wall type (you can map this to a specific texture)
        // wallType = map.getTile((int) x, (int) y);

        // Calculate the hit point on the wall to map the correct texture column
        wallHitX = Math.round(x%100); // X hit position relative to the tile (0.0 to 1.0)
        wallHitY = Math.round(y%100); // Y hit position relative to the tile (0.0 to 1.0)
    }

    // Get the corrected distance to avoid fisheye distortion
    public double getDistance() {
        return distance;
    }

    // Determine if the hit was on a vertical wall
    public boolean isVerticalHit() {
        return verticalHit;
    }

    // Get the X-coordinate where the ray hit the wall
    public double getWallHitX() {
        return wallHitX;
    }

    // Get the Y-coordinate where the ray hit the wall
    public double getWallHitY() {
        return wallHitY;
    }

    public boolean getVerticalHit() {
        return verticalHit;
    }
}

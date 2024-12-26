package com.perseus.raycaster;

public class Ray {
    private final double angle;
    private double distance;
    private boolean verticalHit;
    private double wallHitX;
    private double wallHitY;

    public Ray(double angle) {
        this.angle = angle;
    }

    public void cast(Map map, Player player) {
        double x = player.getX();
        double y = player.getY();
        int xbefore = 0;

        double rayStepSize = 1;

        while (!map.isWall((int) x, (int) y)) {
            xbefore = (int) Math.floor(x / Map.TILE_SIZE);

            x += Math.cos(angle) * rayStepSize;
            y += Math.sin(angle) * rayStepSize;
        }

        verticalHit = ((xbefore != Math.floor(x / Map.TILE_SIZE)));

        distance = Math.sqrt((x - player.getX()) * (x - player.getX()) + (y - player.getY()) * (y - player.getY()));
        
        wallHitX = Math.round(x%100);
        wallHitY = Math.round(y%100);
    }

    public double getDistance() {
        return distance;
    }

    public boolean isVerticalHit() {
        return verticalHit;
    }

    public double getWallHitX() {
        return wallHitX;
    }

    public double getWallHitY() {
        return wallHitY;
    }

    public boolean getVerticalHit() {
        return verticalHit;
    }
}

package com.perseus.raycaster;

public class Ray {
    private final double angle;
    private double distance;
    private boolean verticalHit;
    private double wallHitX;
    private double wallHitY;
    private double initialX;
    private double initialY;
    private int finalX;
    private int finalY;
    
    public Ray(double angle) {
        this.angle = angle;
    }

    public void cast(Map map, Player player) {
        initialX = player.getX();
        initialY = player.getY();
    	double x = initialX;
        double y = initialY;
        int xbefore = 0;

        double rayStepSize = 1;

        while (!map.isWall((int) x, (int) y)) {
            xbefore = (int) Math.floor(x / Map.TILE_SIZE);

            x += Math.cos(angle) * rayStepSize;
            y += Math.sin(angle) * rayStepSize;
        }

        verticalHit = ((xbefore != Math.floor(x / Map.TILE_SIZE)));

        distance = Math.sqrt((x - player.getX()) * (x - player.getX()) + (y - player.getY()) * (y - player.getY()));
        
        finalX = (int) Math.round(x);
        finalY = (int) Math.round(y);
        
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
    
    public double getInitialX() {
    	return initialX;
    }
    
    public double getInitialY() {
    	return initialY;
    }
    
    public int getFinalY() {
    	return finalY;
    }
    
    public int getFinalX() {
    	return finalX;
    }

    public boolean getVerticalHit() {
        return verticalHit;
    }
}

package com.perseus.raycaster;

public class Player {
    private double x, y; // Player's position
    private double angle; // Player's viewing angle
    private final double speed = 2; // Movement speed
    private final double rotationSpeed = Math.toRadians(2); // Rotation speed

    public Player(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void moveForward(Map map) {
        double newX = x + Math.cos(angle) * speed;
        double newY = y + Math.sin(angle) * speed;

        if (!map.isWall((int) newX, (int) y)) {
            this.x = newX;
        }

        if (!map.isWall((int) x, (int) newY)) {
            this.y = newY;
        }
    }

    public void moveBackward(Map map) {
        double newX = x - Math.cos(angle) * speed;
        double newY = y - Math.sin(angle) * speed;

        if (!map.isWall((int) newX, (int) y)) {
            this.x = newX;
        }

        if (!map.isWall((int) x, (int) newY)) {
            this.y = newY;
        }
    }

    public void rotateLeft() {
        this.angle -= rotationSpeed;
    }

    public void rotateRight() {
        this.angle += rotationSpeed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }
}

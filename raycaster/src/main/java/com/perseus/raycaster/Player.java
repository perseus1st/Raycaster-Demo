package com.perseus.raycaster;
public class Player {
   private double x, y; // Player's position
   private double angle; // Player's viewing angle
   private final double speed = 2; // Movement speed
   private final double rotationSpeed = Math.toRadians(1); // Rotation speed
   private final int characterPadding = 5;
   public Player(double x, double y, double angle) {
       this.x = x;
       this.y = y;
       this.angle = angle;
   }
   public void moveForward(Map map) {
       double newX = x + Math.cos(angle) * speed;
       double newY = y + Math.sin(angle) * speed;
       if (!isColliding(newX, y, map)) {
           this.x = newX;
       }
       if (!isColliding(x, newY, map)) {
           this.y = newY;
       }
   }
   public void moveBackward(Map map) {
       double newX = x - Math.cos(angle) * speed;
       double newY = y - Math.sin(angle) * speed;
       if (!isColliding(newX, y, map)) {
           this.x = newX;
       }
       if (!isColliding(x, newY, map)) {
           this.y = newY;
       }
   }
   private boolean isColliding(double testX, double testY, Map map) {
       // Check for collisions using padding
       return map.isWall((int) testX + characterPadding, (int) testY) ||
              map.isWall((int) testX - characterPadding, (int) testY) ||
              map.isWall((int) testX, (int) testY + characterPadding) ||
              map.isWall((int) testX, (int) testY - characterPadding);
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


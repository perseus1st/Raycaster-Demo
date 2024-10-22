package com.perseus.raycaster;
public class Player {
   private double x, y; // Player's position
   private double angle; // Player's viewing angle
   private final double speed = 2; // Movement speed
   private final double rotationSpeed = Math.toRadians(1); // Rotation speed
   private final int characterPadding = 5;
   private boolean isMoving = false; // Tracks if the player is moving (for walking animation)
   private double animationTime = 0; // Time to control walking animation (sine wave y component)
   private final double animationSpeed = 0.15; // Controls the speed of the animation
   
   public Player(double x, double y, double angle) {
       this.x = x;
       this.y = y;
       this.angle = angle;
   }
   public void moveForward(Map map) {
       double newX = x + Math.cos(angle) * speed;
       double newY = y + Math.sin(angle) * speed;
       
       boolean moved = false;
       
       if (!isColliding(newX, y, map)) {
           this.x = newX;
           moved = true;
       }
       if (!isColliding(x, newY, map)) {
           this.y = newY;
           moved = true;
       }
       
       isMoving = moved;
       if (isMoving) {
    	   animationTime += animationSpeed;
       }
   }
   
   public void moveBackward(Map map) {
       double newX = x - Math.cos(angle) * speed;
       double newY = y - Math.sin(angle) * speed;
       
       boolean moved = false;
       
       if (!isColliding(newX, y, map)) {
           this.x = newX;
           moved = true;
       }
       if (!isColliding(x, newY, map)) {
           this.y = newY;
           moved = true;
       }
       
       isMoving = moved;
       if (isMoving) {
    	   animationTime += animationSpeed;
       }
   }
   
   public boolean isMoving() {
	   return isMoving;
   }
   
   public double getAnimationOffset() {
	   System.out.println(isMoving);
	   if (isMoving) {
		   return Math.sin(animationTime) * 10; // return the y component of a sine wave taken from where the player is in the animation multiplied by a constant for the intensity of the walking effect
	   } else { // if the character isnt moving
		   System.out.println(animationTime);
		   animationTime = 0; // stop the animation
		   return 0; // return no vertical offset effect
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


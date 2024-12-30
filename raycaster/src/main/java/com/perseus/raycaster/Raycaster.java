package com.perseus.raycaster;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Raycaster extends Application {

	private final int WIDTH = 750;
	private final int HEIGHT = 550;
	private final int FOV = 60;
	private final int TILE_SIZE = 100;
	private final int WALL_HEIGHT_MULTIPLIER = (HEIGHT * 4) / 5;

	private Canvas canvas;
	private Player player;
	private Map map;
	private final Set<KeyCode> keysPressed = new HashSet<>();

	private byte wallTextureTracker = 2;
	private Image wallTexture;
	private PixelReader pixelReader;
	
	private String groundColor = "#717171";
	private String skyColor = "#393939";

	private AnimationTimer timer;

	@Override
	public void start(Stage primaryStage) {
		canvas = new Canvas(WIDTH, HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		loadLevelData(); // Load level data from XML

		switchWallTexture();
		
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				updateMovement(primaryStage);
				render(gc);
			}
		};
		timer.start();

		Scene scene = new Scene(new StackPane(canvas), WIDTH, HEIGHT);

		canvas.requestFocus();

		scene.setOnKeyPressed(this::handleKeyPressed);
		scene.setOnKeyReleased(this::handleKeyReleased);

		primaryStage.setTitle("JavaFX Raycaster");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void handleKeyPressed(KeyEvent event) {
		keysPressed.add(event.getCode());
	}

	private void handleKeyReleased(KeyEvent event) {
		keysPressed.remove(event.getCode());
	}

	private void updateMovement(Stage primaryStage) {
		if (keysPressed.contains(KeyCode.W) || (keysPressed.contains(KeyCode.UP)))
			player.moveForward(map);
		if (keysPressed.contains(KeyCode.S) || (keysPressed.contains(KeyCode.DOWN)))
			player.moveBackward(map);
		if (keysPressed.contains(KeyCode.A) || (keysPressed.contains(KeyCode.LEFT)))
			player.rotateLeft();
		if (keysPressed.contains(KeyCode.D) || (keysPressed.contains(KeyCode.RIGHT)))
			player.rotateRight();
		if (keysPressed.contains(KeyCode.ESCAPE))
			returnToMenu(primaryStage);
		if (keysPressed.contains(KeyCode.R))
			reloadLevelData();
		if (keysPressed.contains(KeyCode.F)) {
			keysPressed.remove(KeyCode.F);
			switchWallTexture();
		}
	}

	private void switchWallTexture() {
		wallTextureTracker++;
		if (wallTextureTracker > 3) {
			wallTextureTracker = 1;
		}
		switch(wallTextureTracker) {
		case 1:
			wallTexture = new Image(
					getClass().getResourceAsStream("/com/perseus/raycaster/textures/wall_texture.png"));
			groundColor = "#717171";
			skyColor = "#393939";
			break;
		case 2:
			wallTexture = new Image(
					getClass().getResourceAsStream("/com/perseus/raycaster/textures/cool_wall_texture.png"));
			groundColor = "#000000";
			skyColor = "#000000";
			break;
		case 3:
			wallTexture = new Image(
					getClass().getResourceAsStream("/com/perseus/raycaster/textures/backrooms.png"));
			groundColor = "#AFA232";
			skyColor = "#766B1B";
			break;
		}
		pixelReader = wallTexture.getPixelReader();			
	}
	
	private void render(GraphicsContext gc) {
		gc.clearRect(0, 0, WIDTH, HEIGHT);

		gc.setFill(Color.web(skyColor)); // sky color
		gc.fillRect(0, 0, WIDTH, HEIGHT / 2);

		gc.setFill(Color.web(groundColor)); // ground color
		gc.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT / 2);

		castRays(gc);
	}

	private void castRays(GraphicsContext gc) {
		double rayAngle;
		double rayStep = Math.toRadians(FOV) / WIDTH;
		int pixelSize = WIDTH / 175;
		// The denominator represents how many rays will be cast
		// lower number = more pixelized

		double yOffset = player.getAnimationOffset();

		for (int x = 0; x < WIDTH; x += pixelSize) {
			rayAngle = player.getAngle() - Math.toRadians(FOV / 2) + x * rayStep;
			Ray ray = new Ray(rayAngle);
			ray.cast(map, player);

			double distance = ray.getDistance() * Math.cos(rayAngle - player.getAngle());

			double wallHeight = (TILE_SIZE / distance) * WALL_HEIGHT_MULTIPLIER;

			int texX = ray.getVerticalHit() ? (int) ray.getWallHitY() : (int) ray.getWallHitX();
			texX = Math.min(texX, (int) wallTexture.getWidth() - 1);

			for (int y = 0; y < wallHeight; y++) {
				double drawY = (HEIGHT / 2) - (wallHeight / 2) + y + yOffset;

				if (drawY >= 0 && drawY < HEIGHT) {
					int texY = (int) ((y / wallHeight) * wallTexture.getHeight());
					texY = Math.min(texY, (int) wallTexture.getHeight() - 1);

					Color color = pixelReader.getColor(texX, texY);

					gc.setFill(color);
					gc.fillRect(x, (int) Math.floor(drawY), pixelSize, 1);
				}
			}
		}
	}

	private void loadLevelData() {
	    try {
	        // Create a DocumentBuilderFactory
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();

	        // Load and parse the XML file
	        File file = new File("src/main/resources/com/perseus/raycaster/LevelData.xml");
	        Document document = builder.parse(file);
	        document.getDocumentElement().normalize(); // Normalize XML structure

	        // Extract grid size
	        NodeList gridSizeNode = document.getElementsByTagName("gridSize");
	        int gridSize = Integer.parseInt(gridSizeNode.item(0).getTextContent());

	        // Extract player angle
	        NodeList playerAngleNode = document.getElementsByTagName("playerAngle");
	        double playerAngle = Math.toRadians(Double.parseDouble(playerAngleNode.item(0).getTextContent()));

	        // Extract map data
	        NodeList rowNodes = document.getElementsByTagName("row");
	        int originalRows = rowNodes.getLength();
	        int originalCols = rowNodes.item(0).getTextContent().trim().split(" ").length;

	        // Create a new padded map
	        int[][] paddedMapData = new int[originalRows + 2][originalCols + 2];

	        System.out.println("Loading map data with padding...");

	        int startCol = 0;
	        int startRow = 0;

	        // Fill the new map with walls (1) as padding
	        for (int i = 0; i < paddedMapData.length; i++) {
	            for (int j = 0; j < paddedMapData[i].length; j++) {
	                // Outer walls
	                if (i == 0 || j == 0 || i == paddedMapData.length - 1 || j == paddedMapData[i].length - 1) {
	                    paddedMapData[i][j] = 1;
	                } else {
	                    // Fill inner area with data from XML
	                    String[] rowValues = rowNodes.item(i - 1).getTextContent().trim().split(" ");
	                    paddedMapData[i][j] = Integer.parseInt(rowValues[j - 1]);
	                    
	                    // Detect player start point (value 2)
	                    if (paddedMapData[i][j] == 2) {
	                        startCol = j;
	                        startRow = i;
	                    }
	                }
	            }
	        }

	        // Print map for debugging
	        for (int[] row : paddedMapData) {
	            for (int val : row) {
	                System.out.print(val + " ");
	            }
	            System.out.println();
	        }

	        // Initialize map and player
	        map = new Map(paddedMapData);
	        player = new Player((startCol * TILE_SIZE + TILE_SIZE / 2), (startRow * TILE_SIZE + TILE_SIZE / 2), playerAngle);

	        System.out.println("Level Data Loaded: ");
	        System.out.println("Original Grid Size: " + gridSize);
	        System.out.println("Padded Grid Size: " + paddedMapData.length + "x" + paddedMapData[0].length);
	        System.out.println("Player Angle: " + playerAngle);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	public void reloadLevelData() {
		loadLevelData();
		System.out.println("Level data reloaded.");
	}

	private void returnToMenu(Stage primaryStage) {
		// The reason this return to menu did not work but the level creator one did is
		// because raycaster uses an animation timer that never stops, which ended up
		// holding the thread.
		// This implementation releases the thread when returning to the menu.
		if (timer != null) {
			timer.stop();
		}
		Main mainMenu = new Main();
		try {
			mainMenu.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

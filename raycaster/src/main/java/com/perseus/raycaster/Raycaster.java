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

	private Image wallTexture;
	private PixelReader pixelReader;

	@Override
	public void start(Stage primaryStage) {
		canvas = new Canvas(WIDTH, HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		loadLevelData(); // Load level data from XML

		wallTexture = new Image(
				getClass().getResourceAsStream("/com/perseus/raycaster/textures/cool_wall_texture.png"));
		pixelReader = wallTexture.getPixelReader();

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				updateMovement(primaryStage);
				render(gc);
			}
		}.start();

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
	}

	private void render(GraphicsContext gc) {
		gc.clearRect(0, 0, WIDTH, HEIGHT);

		gc.setFill(Color.web("#000000")); // sky color
		gc.fillRect(0, 0, WIDTH, HEIGHT / 2);

		gc.setFill(Color.web("#000000")); // ground color
		gc.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT / 2);

		castRays(gc);
	}

	private void castRays(GraphicsContext gc) {
		double rayAngle;
		double rayStep = Math.toRadians(FOV) / WIDTH;
		int pixelSize = WIDTH / 175;
		 // The denominator represents how many rays will be cast, lower number = more pixelized
		
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
			// Must access directly from the file system instead of relying on classpath
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
			int[][] mapData = new int[rowNodes.getLength()][];

			System.out.println("Loading map data...");

			int startCol = 0;
			int startRow = 0;
			
			for (int i = 0; i < rowNodes.getLength(); i++) {
				String rowText = rowNodes.item(i).getTextContent().trim();
				String[] rowValues = rowText.split(" ");
				mapData[i] = new int[rowValues.length];

				for (int j = 0; j < rowValues.length; j++) {
					mapData[i][j] = Integer.parseInt(rowValues[j]);
					if (mapData[i][j] == 2) {
						startCol = j;
						startRow = i;
					}
				}

				// Print the current row to the terminal
				System.out.println(String.join(" ", rowValues));
			}

			// Extracted data
			map = new Map(mapData); // Initialize map with the parsed map data
			player = new Player((startCol * TILE_SIZE + TILE_SIZE/2), (startRow * TILE_SIZE + TILE_SIZE/2), playerAngle); // Initialize player with the parsed angle

			System.out.println("Level Data Loaded: ");
			System.out.println("Grid Size: " + gridSize);
			System.out.println("Player Angle: " + playerAngle);
			System.out.println("startCol: " + startCol);
			System.out.println("startRow: " + startRow);
			System.out.println("startX: " + (startCol * TILE_SIZE + TILE_SIZE/2));
			System.out.println("startRow: " + (startRow * TILE_SIZE + TILE_SIZE/2));
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void reloadLevelData() {
	    loadLevelData();
	    System.out.println("Level data reloaded.");
	}


	private void returnToMenu(Stage primaryStage) {
		Main mainMenu = new Main();
		try {
			mainMenu.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

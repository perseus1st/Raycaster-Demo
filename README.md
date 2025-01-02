# Raycaster Game and Level Editor
## Overview
This project is a javafx based raycaster maze concept game with an integrated level editor. The goal is to create a simple, customizable 2.5D game environment inspired by classic games like Wolfenstein 3D. The project consists of two main components:
1. **Raycasting Engine** - Renders the game world from a first-person perspective using raycasting techniques.
2. **Level Editor** - Provides a GUI to design and export custom game maps.
## Features
### Raycasting Engine
- **Smooth Rendering**: Implements a raycasting algorithm for efficient 3D visualization.
- **Player controls**: Move and rotate the player within the map.
- **Wall Textures**: Supports textured walls and dynamic scaling.
- **Collision Detection**: Prevents the player from passing through walls.
### Level Editor
- **Intuitive Grid Design**: Design maps on a grid-based interface.
- **Map Export**: Export map data to a file, including grid size, player starting angle, and layout.
- **Player Placement**: Adjust player start point, initial direction
- **End points**: Place any number of end points on the map
## Export Format
The export function generates a map xml file with the following structure:
```txt
gridSize
playerAngle
mapData
```
- `gridSize`: Dimensions of the map grid.
- `playerAngle`: Initial player orientation
- `mapData`: A Grid-based representation of the level, where each number represents a tile following this system:
0. Empty tile
1. Wall tile
2. Player Start Point
3. Player End Point(s)
## How to build and run
### Requirements
- Java 11 or higher
- Maven
- JavaFX SDK
### Build Instructions
**In a terminal:**
1. Navigate to the directory where you want to clone the repository. 
```
cd path\to\your\directory
```
2. Clone the repository:
```
git clone https://github.com/perseus1st/Raycaster-Demo.git
```
3. Navigate into the project
```
cd Raycaster-Demo\raycaster
```
4. Verify pom.xml is in the directory by running:
```
dir
```
5. Build the project to download dependencies and compile:
```
mvn clean install
```
6. Run the application:
```
mvn javafx:run
```
The application should now launch and open to the main menu.

## Usage
- **Game Mode:** Start the raycaster and navigate the maze
- **Level Editor:** Design and export maps to be used in the raycaster.
- For more information, click "Help" on the main menu or the level editor

## Acknowledgements
- Inspired by Wolfenstein 3d and classic Doom

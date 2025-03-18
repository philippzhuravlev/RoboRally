# RoboRally
# System Requirements for Java Console Game

## Minimum Requirements
- **OS**: Windows 10, macOS 10.15, or Linux (Ubuntu 20.04)
- **Processor**: 1.5 GHz dual-core CPU
- **Memory**: 2 GB RAM
- **Graphics**: Integrated graphics (no specific CPU required)
- **Storage**: 200 MB free space
- **Java**: JDK 21.1.0

## Recommended Requirements
- **OS**: Windows 10, macOS 11, or Linux (Ubuntu 22.04)
- **Processor**: 2 GHz dual-core CPU
- **Memory**: 4 GB RAM
- **Graphics**: Integrated graphics (no specific GPU required)
- **Storage**: 200 MB free space
- **Java**: JDK 21.1.0


## Additional requirements depend on the method used to run the game:
- **Running from IDE**: Ensure that your IDE supports Maven and JavaFX.
- **Running from Command Line**:
    - Maven must be installed and configured.
    - Use the command `mvn clean install` to build the project.
    - Use the command `mvn exec:java -Dexec.mainClass="dk.dtu.compute.se.pisd.roborally.StartRoboRally"` to run the game.


# RoboRally

## Executing, Compiling, Installing and Running the Game

To run the game, follow these steps:

1. **Clone the Repository**:
    - Use the following Git command to clone the repository:
      ```bash
      git clone https://github.com/yourusername/roborally.git
      ```

2. **Open Project in IDE**:
    - Launch your preferred IDE (e.g., IntelliJ IDEA).
    - Open the cloned repository folder in the IDE.

3. **Ensure Dependencies are Installed**:
    - In the terminal, navigate to the project root and use Maven to download dependencies:
      ```bash
      mvn install
      ```

4. **Run the Game**:
    - In your IDE, run the `dk.dtu.compute.se.pisd.roborally.StartRoboRally` class to start the game.

## Running Tests

To run the tests for the project, follow these steps:

1. **Ensure Dependencies are Installed**:
    - In the terminal, navigate to the project root and use Maven to download dependencies:
      ```bash
      mvn install
      ```

2. **Run Tests**:
    - Use the following Maven command to run all test cases:
      ```bash
      mvn test
      ```

This command will execute all test cases located in the `src/test/java` directory and provide a summary of the test results.

## Class Descriptions

### dk.dtu.compute.se.pisd.roborally.StartRoboRally
This is the main class that starts the RoboRally game. It initializes the game environment and launches the game interface.

### dk.dtu.compute.se.pisd.roborally.Game
This class represents the game logic for RoboRally. It handles the game state, player actions, and game rules.

### dk.dtu.compute.se.pisd.roborally.Board
The `Board` class represents the game board. It manages the layout of the board, including the placement of tiles and obstacles.

### dk.dtu.compute.se.pisd.roborally.Player
This class represents a player in the game. It keeps track of the player's position, health, and actions.

### dk.dtu.compute.se.pisd.roborally.Tile
The `Tile` class represents a single tile on the game board. It can have different properties, such as being a conveyor belt or a pit.

### dk.dtu.compute.se.pisd.roborally.Card
This class represents a card in the game. Cards can be used by players to perform actions, such as moving or rotating.

### dk.dtu.compute.se.pisd.roborally.Deck
The `Deck` class manages a collection of cards. It handles shuffling and dealing cards to players.

### dk.dtu.compute.se.pisd.roborally.Database
This class handles database interactions for the game. It is used to save and load game states and player data.

### dk.dtu.compute.se.pisd.roborally.GUI
The `GUI` class manages the graphical user interface of the game. It handles rendering the game board and player interactions.

### dk.dtu.compute.se.pisd.roborally.Controller
This class acts as a controller in the MVC pattern. It handles user input and updates the game state accordingly.

### dk.dtu.compute.se.pisd.roborally.Utils
The `Utils` class contains utility functions that are used throughout the project. These functions perform common tasks that are needed in multiple places.


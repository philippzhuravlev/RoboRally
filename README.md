# RoboRally

## Github Repository
https://github.com/philippzhuravlev/RoboRally


## System Requirements

### Minimum Requirements
- **OS**: Windows 7, macOS 10.10, or Linux (Ubuntu 16.04)
- **Processor**: 1GHz single-core CPU
- **Memory**: 512 MB RAM
- **Graphics**: Integrated graphics (no specific CPU required)
- **Storage**: 100 MB free space

### Recommended Requirements
- **OS**: Windows 10, macOS 10.15, or Linux (Ubuntu 20.04)
- **Processor**: 1.5 GHz dual-core CPU
- **Memory**: 1 GB RAM
- **Graphics**: Integrated graphics (no specific GPU required)
- **Storage**: 100 MB free space

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   ├── dk/dtu/compute/se/pisd/roborally/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── view/
│   │   │   ├── fileaccess/
│   │   │   ├── dal/
│   │   │   ├── exceptions/
│   │   │   ├── RoboRally.java
│   │   │   └── StartRoboRally.java
│   ├── resources/
└── test/
```

## Assignments

### Assignment 4a: Player Movement and Move Counter

We did player movement via mouse clicks by implementing the `moveCurrentPlayerToSpace` method in `GameController`. The method first verifies that the target space is unoccupied, then updates the player's position using `setSpace()`, and lastly advances to the next player's turn. The second part of the assignment was to add a move counter to the `Board` class to track the total number of moves in the game. This meant adding a `counter` attribute with `getCounter()` and `setCounter()` methods. The `setCounter()` method then calls `notifyChange()` to update the UI through the observer pattern. The last part was that the counter value is displayed in the status bar by modifying the `getStatusMessage()` method to include the counter in its output. 

### Assignment 4b: Board Generation and Selection

In this part, we were to add board selection in the `BoardFactory` class. This first meant a simple getter called `getAvailableBoardNames()`. We looked at previous JavaFX dialog boxes and made a new one that allows you to choose between two board layouts, simple and advanced. They have different layouts of walls, conveyor belts, and checkpoints. The second part was to implement a `CheckPoint` class, itself an extension of `FieldAction`. We had actually drafted the checkpoint logic already by this time. The `updateView()` method in `SpaceView` was implemented to visually render walls, conveyor belts, and checkpoints on the board; they were drawn with simple JavaFX shapes we found, like triangles for conveyor belts; circles for checkpoints and red lines for walls.

### Assignment 4c: Command Cards and Program Execution

We implemented the command card system by creating execution methods in `GameController`:
- `moveForward` - uses recursion. Pushing only succeeds if all robots in the chain can move
- `moveBackward` - 
- `turnLeft` - utilizes pre-existing ENUMs and .next() m
- `turnRight`
- `U-Turn` - Rotates the robot 180°
- `fastForward` - Moves the robot two spaces forward - this just calls moveForward twice.
We changed `getNeighbour` in the `Board` class to handle walls. Then, the GUI buttons were connected to their corresponding actions:
- "Finish Programming" → `finishProgrammingPhase`
- "Execute Program" → `executePrograms`
- "Execute Current Register" → `executeStep`

### Assignment 4d: Field Actions and Robot Pushing

Now we had to do field actions, though we had already done most of the checkpoint logic work, but it wasn't finished. We had to add associated player values for [amount of] `checkpointsReached` and the `lastCheckpoint` to make sure that players won after visiting all checkpoints, until the last. Lastly, we changed the GUI to display these debug-like messages





### Assignment 4e: Winning the Game and Interactive Command Cards

We implemented the winning condition by enhancing the `CheckPoint` class to track the last checkpoint. When a player reaches all checkpoints in order, they win the game. The game transitions to `Phase.FINISHED`, and the winner is displayed in the status message.

A victory pop-up window shows the winner and game statistics. We also implemented the interactive "LEFT_OR_RIGHT" command card that allows players to choose their turning direction during execution.

For interactive cards, we:
1. Added the command to the `Command` enum
2. Modified `executeNextStep()` to change to `Phase.PLAYER_INTERACTION` when needed
3. Implemented parameter passing for user choices
4. Updated `PlayerView.updateView()` to show appropriate buttons during interaction

When a player makes a choice, the game returns to `Phase.ACTIVATION` and continues execution.

#### Extras
We added game enhancements:
- Victory animations
- Sound effects
- Game timer
- Player statistics tracking

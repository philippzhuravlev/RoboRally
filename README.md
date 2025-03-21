# RoboRally

## Github Repository
https://github.com/philippzhuravlev/RoboRally

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

Now we had to do field actions, though we had already done most of the checkpoint logic work, but it wasn't finished. We had to add associated player values for [amount of] checkpointsReached and the lastCheckpoint to make sure that players won after visiting all checkpoints, until the last. Lastly, we changed the GUI to display these debug-like messages.

We also implemented robot pushing mechanics, ensuring that when a robot moves forward, fast-forward, or backward, it pushes any other robot in its path - provided there's no wall blocking the movement. This was handled using recursion, where a robot checks if the space ahead is occupied and recursively pushes the next robot until an open space is found or movement becomes impossible. If a move is blocked by a wall or the board’s edge, an ImpossibleMoveException is thrown.

Additionally, field actions were executed at the correct moment in the game loop. Conveyor belts now move robots in their direction, and checkpoints increase a player's checkpointsReached count only when collected in order.

### Assignment 4e: Winning the Game and Interactive Command Cards

We improved the game by adding a winning condition. Now, when a player reaches all checkpoints in order, they win, and the game transitions to `Phase.FINISHED`. A victory pop-up appears, announcing the winner and showing game stats, like the number of steps the player needed to win.

We also made the LEFT_OR_RIGHT command card interactive, letting players choose whether to turn left or right during execution.

To make this work, we:
1. Added the new command to the Command enum.
2. Modified `executeNextStep()` to change to `Phase.PLAYER_INTERACTION` when needed.
3. Enabled parameter passing so players can make their choice.
4. Modified PlayerView.updateView() to display the correct buttons for interaction.

Once the player chooses, the game continues as usual, returning to `Phase.ACTIVATION` and executing the next step.

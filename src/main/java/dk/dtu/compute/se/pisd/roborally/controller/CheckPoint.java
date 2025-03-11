package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a checkpoint on a space.
 * Players need to reach checkpoints in order to win the game.
 */
public class CheckPoint extends FieldAction {
    
    private int number;
    private boolean isLast; // true if this is the last checkpoint

    public CheckPoint(int number, boolean isLast) {
        this.number = number;
        this.isLast = isLast;
    }

    /**
     * Executes the checkpoint action when a player lands on this space.
     *
     * <p>If the player reaches this checkpoint in the correct order (either it's the first checkpoint
     * or they have reached the previous checkpoint), their checkpoint count is increased.</p>
     *
     * <p>If this checkpoint is the final one, a victory message is displayed.</p>
     *
     * @param gameController the game controller managing the game
     * @param space the space where the checkpoint action occurs
     * @return {@code true} if the checkpoint is successfully reached; {@code false} otherwise
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        if (space.getPlayer() != null) {
            Player player = space.getPlayer();
            if (number == 1 || player.hasReachedCheckpoint(number - 1)) {
                if (!player.hasReachedCheckpoint(number)) {
                    player.setCheckpointsReached(player.getCheckpointsReached() + 1);
                    // VICTORY DIALOG
                    if (isLast) {
                        gameController.board.setPhase(Phase.FINISHED);

                        Platform.runLater(() -> { // i.e. run javaFX code
                            Alert alert = new Alert(Alert.AlertType.INFORMATION); // alert dialog box like in AppController.java
                            alert.setTitle("Victory!");
                            alert.setHeaderText("You Won!");
                            alert.setContentText("Player " + player.getName() + " has emerged victorious! It took "
                                    + gameController.board.getCounter() + " moves to do so.");
                            alert.showAndWait();
                        });
                    }
                }
                return true;
            }
        }
        return false;
    }

    // getters
    public int getNumber() {
        return number;
    }

    public boolean getIsLast() {
        return isLast;
    }
} 
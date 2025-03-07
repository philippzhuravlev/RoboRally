package dk.dtu.compute.se.pisd.roborally.controller;

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

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        if (space.getPlayer() != null) {
            Player player = space.getPlayer();
            if (number == 1 || player.hasReachedCheckpoint(number - 1)) {
                if (!player.hasReachedCheckpoint(number)) {
                    player.setCheckpointsReached(player.getCheckpointsReached() + 1);
                    // VICTORY DIALOG
                    if (isLast) { // i.e. if true
                        Platform.runLater(() -> { // i.e. run javaFX code
                            Alert alert = new Alert(Alert.AlertType.INFORMATION); // alert dialog box like in AppController.java
                            alert.setTitle("Victory!");
                            alert.setHeaderText("You Won!");
                            alert.setContentText("Player " + player.getName() + " has emerged victorious!");
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
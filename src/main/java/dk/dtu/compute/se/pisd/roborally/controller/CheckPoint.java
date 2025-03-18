package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a checkpoint on a space.
 * Players need to reach checkpoints in order to win the game.
 */
public class CheckPoint extends FieldAction {
    
    private int number;
    private boolean isLast; // usually checkpoint nr 3 or 4

    public CheckPoint(int number, boolean isLast) {
        this.number = number;
        this.isLast = isLast;
    }

    /**
     * Executes the checkpoint action when a player lands on this space.
     *
     * <p>If the player reaches this checkpoint in the correct order
     * (i.e., either it is the first checkpoint or they have already
     * reached the previous one), their checkpoint count increases.</p>
     *
     * <p>If this checkpoint is the final one, the game ends,
     * and the winner is determined.</p>
     *
     * @param gameController the game controller managing the game logic
     * @param space the space where the checkpoint action occurs
     * @return {@code true} if the player successfully reaches this checkpoint, {@code false} otherwise
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
                        gameController.handleGameEnd(player);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int getNumber() {
        return number;
    }

} 
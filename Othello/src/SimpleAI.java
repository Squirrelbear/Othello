import java.util.Collections;

/**
 * Othello
 * Author: Peter Mitchell (2021)
 *
 * SimpleAI class:
 * Chooses moves at random.
 */
public class SimpleAI {
    /**
     * Reference to the game grid to choose moves.
     */
    private GameGrid gameGrid;

    /**
     * Sets up the AI ready to play moves.
     *
     * @param gameGrid Reference to the game grid to choose moves.
     */
    public SimpleAI(GameGrid gameGrid) {
        this.gameGrid = gameGrid;
    }

    /**
     * Chooses a move at random for all the valid moves.
     *
     * @return The position selected by the AI to play.
     */
    public Position chooseMove() {
        Collections.shuffle(gameGrid.getAllValidMoves());
        return gameGrid.getAllValidMoves().get(0);
    }
}

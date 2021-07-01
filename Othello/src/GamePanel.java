import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Othello
 * Author: Peter Mitchell (2021)
 *
 * GamePanel class:
 * Controls the game state through clicks to iterate between
 * current turns and changing to a game over state once the game ends.
 */
public class GamePanel extends JPanel implements MouseListener {
    /**
     * The states the game can be in.
     * WTurn means that player 2 is placing a white piece.
     * BTurn means that player 1 is placing a black piece. (if using AI this will be instant)
     * Draw means the game has ended due to all positions being filled.
     * WWins means the white had more pieces at the end of the game.
     * BWins means the black had more pieces at the end of the game.
     */
    public enum GameState {WTurn,BTurn,Draw,WWins,BWins}

    /**
     * Height of the panel.
     */
    private static final int PANEL_HEIGHT = 600;
    /**
     * Width of the panel.
     */
    private static final int PANEL_WIDTH = 500;

    /**
     * The grid of positions controlling maintaining the game state of the board.
     */
    private GameGrid gameGrid;
    /**
     * The current game state.
     */
    private GameState gameState;
    /**
     * A string representing the current game state. It is set when changing game states with setGameState().
     */
    private String gameStateStr;

    /**
     * Null for PvP or set to an AI behaviour to make the AI play out the white turns.
     */
    private SimpleAI aiBehaviour;

    /**
     * Configures the game ready to be played including selection of playing against either
     * AI or another player.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.LIGHT_GRAY);

        gameGrid = new GameGrid(new Position(0,0), PANEL_WIDTH, PANEL_HEIGHT-100, 8, 8);
        setGameState(GameState.BTurn);
        chooseAIType();
        addMouseListener(this);
    }

    /**
     * Draws the game grid and draws the message at the bottom showing a string representing the game state.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        super.paint(g);
        gameGrid.paint(g);
        drawGameState(g);
    }

    /**
     * Resets the grid and returns the turn back to default.
     */
    public void restart() {
        gameGrid.reset();
        setGameState(GameState.BTurn);
    }


    /**
     * Handles the key input to have Escape exit the game,
     * R will restart the game, and A will swap the AI mode.
     *
     * @param keyCode The key that was pressed.
     */
    public void handleInput(int keyCode) {
        if(keyCode == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } else if(keyCode == KeyEvent.VK_R) {
            restart();
            repaint();
        } else if(keyCode == KeyEvent.VK_A) {
            chooseAIType();
        }
    }

    /**
     * Checks the grid position is valid, and then plays the move of the current player.
     * The game state is swapped upon a valid move being played.
     *
     * @param gridPosition Position to apply the move at.
     */
    private void playTurn(Position gridPosition) {
        if(!gameGrid.isValidMove(gridPosition)) {
            return;
        } else if(gameState == GameState.BTurn) {
            gameGrid.playMove(gridPosition, 1);
            setGameState(GameState.WTurn);
        } else if(gameState == GameState.WTurn) {
            gameGrid.playMove(gridPosition, 2);
            setGameState(GameState.BTurn);
        }
    }

    /**
     * Changes the state and modifies the message to display
     * at the bottom of the game to show the current state.
     *
     * @param newState The new state to set.
     */
    private void setGameState(GameState newState) {
        gameState = newState;
        switch (gameState) {
            case WTurn:
                // If there are moves for the White player
                if(gameGrid.getAllValidMoves().size() > 0) {
                    gameStateStr = "White Player Turn";
                } else {
                    // No moves for the white player. Check the black player
                    gameGrid.updateValidMoves(1);
                    if(gameGrid.getAllValidMoves().size() > 0) {
                        // The black player has moves, swap back to them
                        setGameState(GameState.BTurn);
                    } else {
                        // No moves for either player found, end the game.
                        testForEndGame(false);
                    }
                }
                break;
            case BTurn:
                // If there are moves for the Black player
                if(gameGrid.getAllValidMoves().size() > 0) {
                    gameStateStr = "Black Player Turn";
                } else {
                    // No moves for the black player. Check the white player
                    gameGrid.updateValidMoves(2);
                    if(gameGrid.getAllValidMoves().size() > 0) {
                        // The white player has moves, swap back to them
                        setGameState(GameState.WTurn);
                    } else {
                        // No moves for either player found, end the game.
                        testForEndGame(false);
                    }
                }
                 break;
            case WWins: gameStateStr = "White Player Wins! Press R."; break;
            case BWins: gameStateStr = "Black Player Wins! Press R."; break;
            case Draw: gameStateStr = "Draw! Press R."; break;
        }
    }

    /**
     * Tests for draws, and either player winning, then changes
     * the game state appropriately.
     */
    private void testForEndGame(boolean stillValidMoves) {
        int gameResult = gameGrid.getWinner(stillValidMoves);
        if(gameResult == 1) {
            setGameState(GameState.BWins);
        } else if(gameResult == 2) {
            setGameState(GameState.WWins);
        } else if(gameResult == 3) {
            setGameState(GameState.Draw);
        }
    }

    /**
     * Only does something if state is WTurn or BTurn.
     * Attempts to place the Black or White piece and then checks for change in
     * game state. If the AI is enabled and it is their turn
     * after a valid move they are told to take a turn with
     * the game state again evaluated after their turn.
     *
     * @param e Information about the mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if(gameState == GameState.WTurn || gameState == GameState.BTurn) {
            Position gridPosition = gameGrid.convertMouseToGridPosition(new Position(e.getX(), e.getY()));
            playTurn(gridPosition);
            testForEndGame(true);

            while(gameState == GameState.WTurn && aiBehaviour != null) {
                playTurn(aiBehaviour.chooseMove());
                testForEndGame(true);
            }
        }

        repaint();
    }

    /**
     * Draws the text showing the current game state centered at the bottom
     *  of the window.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    private void drawGameState(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 35));
        int strWidth = g.getFontMetrics().stringWidth(gameStateStr);
        g.drawString(gameStateStr, PANEL_WIDTH/2-strWidth/2, PANEL_HEIGHT-40);
    }

    /**
     * Shows a dialog box with options to select PvP or PvAI with Random.
     * Choosing PvP leaves the AI behaviour unset, and otherwise creates
     * an instance of the appropriate AI.
     */
    private void chooseAIType() {
        String[] options = new String[] {"Player vs Player", "Player vs Random AI"};
        String message = "Select the game mode you would like to use.";
        int difficultyChoice = JOptionPane.showOptionDialog(null, message,
                "Choose how to play.",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        switch(difficultyChoice) {
            case 0: // Remove the AI so it becomes PvP
                aiBehaviour = null;
                break;
            case 1:
                aiBehaviour = new SimpleAI(gameGrid);
                break;
        }
    }

    /**
     * Not set.
     *
     * @param e Not set.
     */
    @Override
    public void mouseClicked(MouseEvent e) {}
    /**
     * Not set.
     *
     * @param e Not set.
     */
    @Override
    public void mouseReleased(MouseEvent e) {}
    /**
     * Not set.
     *
     * @param e Not set.
     */
    @Override
    public void mouseEntered(MouseEvent e) {}
    /**
     * Not set.
     *
     * @param e Not set.
     */
    @Override
    public void mouseExited(MouseEvent e) {}
}

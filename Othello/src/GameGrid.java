import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Othello
 * Author: Peter Mitchell (2021)
 *
 * GameGrid class:
 * Manages a grid of GridCells providing methods to
 * draw and check the state of them.
 */
public class GameGrid extends Rectangle {
    /**
     * The grid of cells tracking current game state.
     */
    private GridCell[][] grid;

    /**
     * Move number used to track if the game is within the first 4 turns.
     */
    private int moveNumber;
    /**
     * A list of valid moves used for testing of who can play a move and showing valid moves.
     */
    private List<Position> validMoves;

    /**
     * Creates a grid of GridCells with the specified offset and sizing.
     *
     * @param position Top left corner offset of the grid.
     * @param width Width of the grid.
     * @param height Height of the grid.
     * @param gridWidth Number of grid cells horizontally.
     * @param gridHeight Number of grid cells vertically.
     */
    public GameGrid(Position position, int width, int height, int gridWidth, int gridHeight) {
        super(position, width, height);
        grid = new GridCell[gridWidth][gridHeight];
        int cellWidth = (width-position.x)/gridWidth;
        int cellHeight = (height-position.y)/gridHeight;
        for(int x = 0; x < gridWidth; x++) {
            for(int y = 0; y < gridHeight; y++) {
                grid[x][y] = new GridCell(new Position(position.x+cellWidth*x, position.y+cellHeight*y),
                        cellWidth, cellHeight);
            }
        }
        moveNumber = 0;
        validMoves = new ArrayList<>();
        updateValidMoves(1);
    }

    /**
     * Forces all the GridCells to reset back to their default state.
     */
    public void reset() {
        for(int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                grid[x][y].reset();
            }
        }
        moveNumber = 0;
        updateValidMoves(1);
    }

    /**
     * Gets the grid cell data.
     *
     * @return The array of all grid cells.
     */
    public GridCell[][] getGrid() {
        return grid;
    }

    /**
     * Searches the grid to find all currently valid moves.
     *
     * @return A list containing all valid moves.
     */
    public List<Position> getAllValidMoves() {
        return validMoves;
    }

    /**
     * Plays the move and swaps all adjacent cells that should be swapped.
     * Then updates the valid move list to match the opposite player.
     *
     * @param position Grid position to play the move at.
     * @param player The player ID of the player who is placing their piece.
     */
    public void playMove(Position position, int player) {
        moveNumber++;
        grid[position.x][position.y].setCellState(player);
        List<Position> changeCellPositions = getChangedPositionsForMove(position, player);
        for(Position swapPosition : changeCellPositions) {
            grid[swapPosition.x][swapPosition.y].setCellState(player);
        }
        updateValidMoves(player == 1 ? 2 : 1);
    }

    /**
     * Converts the mouse position to a valid position inside the grid.
     *
     * @param mousePosition Position of the mouse in the panel.
     * @return The grid position where the mouse has clicked or a position with -1,-1 if it was invalid,
     */
    public Position convertMouseToGridPosition(Position mousePosition) {
        int gridX = (mousePosition.x- position.x)/grid[0][0].width;
        int gridY = (mousePosition.y- position.y)/grid[0][0].height;
        if(gridX >= grid.length || gridX < 0 || gridY >= grid[0].length || gridY < 0) {
            return new Position(-1,-1);
        }
        return new Position(gridX,gridY);
    }

    /**
     * Checks if the position specified is in the valid move list.
     *
     * @param position Grid position to verify if it is in the valid move list.
     * @return True if the position can be played as a valid move by the current player.
     */
    public boolean isValidMove(Position position) {
        return getAllValidMoves().contains(position);
    }

    /**
     * Counts all the 0s,1s, and 2s on the grid and reports a winner if there is one.
     *
     * @param stillValidMoves If false, ignore 0s and choose a winner.
     * @return 0 for no winner, 3 for draw, 1 for player 1, and 2 for player 2.
     */
    public int getWinner(boolean stillValidMoves) {
        int[] counts = new int[3];
        for(int y = 0; y < grid[0].length; y++) {
            for(int x = 0; x < grid.length; x++) {
                counts[grid[x][y].getCellState()]++;
            }
        }

        if(stillValidMoves && counts[0] > 0) return 0;
        else if(counts[1] == counts[2]) return 3;
        else return counts[1] > counts[2] ? 1 : 2;
    }

    /**
     * Draws grid lines to box in the cells, and then draws the content of all GridCells.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        drawGridLines(g);
        for(int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                grid[x][y].paint(g);
            }
        }
    }

    /**
     * Draws lines to create boxes around all the positions where GridCells are located.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    private void drawGridLines(Graphics g) {
        g.setColor(Color.BLACK);
        // Draw vertical lines
        int y2 = position.y+height;
        int y1 = position.y;
        for(int x = 0; x < grid.length+1; x++)
            g.drawLine(position.x+x * grid[0][0].width, y1, position.x+x * grid[0][0].width, y2);

        // Draw horizontal lines
        int x2 = position.x+width;
        int x1 = position.x;
        for(int y = 0; y < grid[0].length+1; y++)
            g.drawLine(x1, position.y+y * grid[0][0].height, x2, position.y+y * grid[0][0].height);
    }

    /**
     * Updates the list of valid moves based on the current game state.
     * Leaves only the current moves highlighted.
     *
     * @param playerID The current player to update the move list with.
     */
    public void updateValidMoves(int playerID) {
        // Remove all highlighted elements so they are not valid moves visually
        for(Position validMove : validMoves) {
            grid[validMove.x][validMove.y].setHighlight(false);
        }
        validMoves.clear();
        // When in the first 4 turns only the middle 4 cells can be played.
        if(moveNumber < 4) {
            int midX = grid.length/2-1;
            int midY = grid[0].length/2-1;
            for (int x = midX; x < midX+2; x++) {
                for (int y = midY; y < midY+2; y++) {
                    if (grid[x][y].getCellState() == 0) {
                        validMoves.add(new Position(x, y));
                    }
                }
            }
        } else {
            // Otherwise find valid moves for the current player that change adjacent pieces
            for (int x = 0; x < grid.length; x++) {
                for (int y = 0; y < grid[0].length; y++) {
                    if (grid[x][y].getCellState() == 0 && getChangedPositionsForMove(new Position(x,y),playerID).size()>0) {
                        validMoves.add(new Position(x, y));
                    }
                }
            }
        }
        // Visually update all valid move positions to show with a highlight
        for(Position validMove : validMoves) {
            grid[validMove.x][validMove.y].setHighlight(true);
        }
    }

    /**
     * Checks for all changed cell positions based on playing the specified move.
     * Will be an empty list if move is invalid.
     *
     * @param position Grid position to check outward from.
     * @param playerID Current player to test with.
     * @return A list of all positions that were changed from playing at move at the specified position.
     */
    public List<Position> getChangedPositionsForMove(Position position, int playerID) {
        List<Position> result = new ArrayList<>();
        result.addAll(getChangedPositionsForMoveInDirection(position, playerID, Position.DOWN));
        result.addAll(getChangedPositionsForMoveInDirection(position, playerID, Position.LEFT));
        result.addAll(getChangedPositionsForMoveInDirection(position, playerID, Position.UP));
        result.addAll(getChangedPositionsForMoveInDirection(position, playerID, Position.RIGHT));
        return result;
    }

    /**
     * Tests for a single given direction finding all cells that would be changed from the given position.
     *
     * @param position Grid position to check outward from.
     * @param playerID Current player to test with.
     * @param direction Direction of motion to test in.
     * @return A list of all positions that were changed in the given direction if the position was played as a move.
     */
    private List<Position> getChangedPositionsForMoveInDirection(Position position, int playerID, Position direction) {
        List<Position> result = new ArrayList<>();
        Position movingPos = new Position(position);
        int otherPlayer = playerID == 1 ? 2 : 1;
        movingPos.add(direction);
        // Keep moving while there are positions that would be changed.
        while(inBounds(movingPos) && grid[movingPos.x][movingPos.y].getCellState() == otherPlayer) {
            result.add(new Position(movingPos));
            movingPos.add(direction);
        }
        // If the end position is off the board, or the end playerID does not match the player, that
        // means that the move would not give any valid swaps in this direction.
        if(!inBounds(movingPos) || grid[movingPos.x][movingPos.y].getCellState() != playerID) {
            result.clear();
        }
        return result;
    }

    /**
     * Test a given grid position if it is valid.
     *
     * @param position Grid position to test if valid.
     * @return True if the grid position is on the grid.
     */
    private boolean inBounds(Position position) {
        return !(position.x < 0 || position.y < 0 || position.x >= grid.length || position.y >= grid[0].length);
    }
}

import java.awt.*;


/**
 * Othello
 * Author: Peter Mitchell (2021)
 *
 * GridCell class:
 * Defines a grid cell that can be empty, Black, or White.
 * The cell can also be highlighted to show it is a valid move.
 */
public class GridCell extends Rectangle {
    /**
     * The state of the grid cell. 0=empty, 2=White, 1=Black
     */
    private int cellState;

    /**
     * When true it will give a highlight background on the cell to show it is a valid move.
     */
    private boolean highlight;

    /**
     * Initialises the GridCell and defaults to empty.
     *
     * @param position Position to draw at.
     * @param width Width of the cell.
     * @param height Height of the cell.
     */
    public GridCell(Position position, int width, int height) {
        super(position, width, height);
        reset();
    }

    /**
     * Resets to the default of empty.
     */
    public void reset() {
        cellState = 0;
        highlight = false;
    }

    /**
     * Changes the state to the specified value.
     *
     * @param newState The new state to change to.
     */
    public void setCellState(int newState) {
        this.cellState = newState;
    }

    /**
     * Gets the current cell state.
     *
     * @return The current cell state.
     */
    public int getCellState() {
        return cellState;
    }

    /**
     * Use to turn on or off the highlight for this cell.
     *
     * @param highlight True will make a highlight appear in the cell.
     */
    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    /**
     * Draws either a White or Black oval as necessary.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        if(highlight) {
            g.setColor(new Color(255, 187, 22, 203));
            g.fillRect(position.x, position.y, width, height);
        }

        if(cellState == 0) return;
        g.setColor(cellState == 1 ? Color.BLACK : Color.WHITE);
        g.fillOval(position.x, position.y, width, height);
    }
}

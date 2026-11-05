package com.pixel.editor;

import java.util.ArrayDeque;
import java.util.Objects;

import javafx.scene.paint.Color;

public class PixelGrid {
  private int ROWS = 32;
  private int COLUMNS = 32;
  final double CELLSIZE = 20;
  Color[][] grid = new Color[ROWS][COLUMNS];

  public PixelGrid() {
    for (int h = 0; h < ROWS; h++) {
      for (int w = 0; w < COLUMNS; w++) {
        grid[h][w] = new Color(0, 0, 0, 0);
      }
    }
  }

  public int getRows() {
    return ROWS;
  }

  public int getCols() {
    return COLUMNS;
  }

  public void changeSize(int rows, int cols) {
    // Create a new grid with the new size then
    // copy the existing pixel layout to the new grid
    // That new grid becomes the base grid for Canvas

    // Program should be able to handle grids with an
    // area of 1^2 to 256^2
    if (rows < 1 || cols < 1) {
      return;
    }
    if (rows > 256 || cols > 256) {
      return;
    }

    Color[][] newGrid = new Color[rows][cols];

    for (int h = 0; h < Math.min(rows, ROWS); h++) {
      for (int w = 0; w < Math.min(cols, COLUMNS); w++) {
        newGrid[h][w] = grid[h][w];
      }
    }

    for (int h = 0; h < rows; h++) {
      for (int w = 0; w < cols; w++) {
        if (newGrid[h][w] == null) {
          newGrid[h][w] = new Color(0, 0, 0, 0);
        }
      }
    }

    ROWS = rows;
    COLUMNS = cols;
    grid = newGrid;
  }

  public void drawPixel(int row, int col, Color c) {
    if (row >= 0 && row < ROWS && col >= 0 && col < COLUMNS) {
      grid[row][col] = c;
    }
  }

  public void fillContiguousArea(int row, int col, Color color) {
    if (row < 0 || row >= ROWS || col < 0 || col >= COLUMNS) {
      return;
    }

    Color originalColor = grid[row][col];
    if (Objects.equals(originalColor, color)) {
      return;
    }

    ArrayDeque<Integer> cellsToFill = new ArrayDeque<>();
    cellsToFill.add(row * COLUMNS + col);
    grid[row][col] = color;

    while (!cellsToFill.isEmpty()) {
      int cell = cellsToFill.remove();
      int currentRow = cell / COLUMNS;
      int currentCol = cell % COLUMNS;

      addMatchingNeighbor(cellsToFill, currentRow - 1, currentCol, originalColor, color);
      addMatchingNeighbor(cellsToFill, currentRow + 1, currentCol, originalColor, color);
      addMatchingNeighbor(cellsToFill, currentRow, currentCol - 1, originalColor, color);
      addMatchingNeighbor(cellsToFill, currentRow, currentCol + 1, originalColor, color);
    }
  }

  private void addMatchingNeighbor(ArrayDeque<Integer> cellsToFill, int row, int col,
      Color originalColor, Color replacementColor) {
    if (row >= 0 && row < ROWS && col >= 0 && col < COLUMNS
        && Objects.equals(grid[row][col], originalColor)) {
      grid[row][col] = replacementColor;
      cellsToFill.add(row * COLUMNS + col);
    }
  }

  public void clear() {
    for (int h = 0; h < ROWS; h++) {
      for (int w = 0; w < COLUMNS; w++) {
        grid[h][w] = new Color(0, 0, 0, 0);
      }
    }
  }
}

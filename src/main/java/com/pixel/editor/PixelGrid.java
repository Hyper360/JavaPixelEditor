package com.pixel.editor;

import javafx.scene.paint.Color;

// Foundational data structure for the grid

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

  void drawPixel(int row, int col, Color c) {
    grid[row][col] = c;
  }
}

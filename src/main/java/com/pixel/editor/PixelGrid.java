package com.pixel.editor;

import javafx.scene.paint.Color;

// Foundational data structure for the grid

public class PixelGrid {
  final int ROWS = 32;
  final int COLUMNS = 32;
  final double CELLSIZE = 20;
  Color[][] grid = new Color[ROWS][COLUMNS];

  public PixelGrid() {
    for (int h = 0; h < ROWS; h++) {
      for (int w = 0; w < COLUMNS; w++) {
        grid[h][w] = new Color(0, 0, 0, 0);
      }
    }
  }

  void drawPixel(int row, int col, Color c) {
    grid[row][col] = c;
  }
}

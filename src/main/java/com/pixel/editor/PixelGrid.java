package com.pixel.editor;

import javafx.scene.paint.Color;

public class PixelGrid {
  public final int ROWS = 32;
  public final int COLUMNS = 32;
  public final double CELLSIZE = 20;
  public Color[][] grid = new Color[ROWS][COLUMNS];

  public PixelGrid() {
    for (int h = 0; h < ROWS; h++) {
      for (int w = 0; w < COLUMNS; w++) {
        grid[h][w] = new Color(0, 0, 0, 0);
      }
    }
  }

  public void drawPixel(int row, int col, Color c) {
    if (row >= 0 && row < ROWS && col >= 0 && col < COLUMNS) {
      grid[row][col] = c;
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

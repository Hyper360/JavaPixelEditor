package com.pixel.editor;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PixelGridTest {

  @Test
  void fillContiguousAreaChangesOnlyTheSelectedRegion() {
    PixelGrid grid = new PixelGrid();
    grid.changeSize(4, 4);
    grid.drawPixel(0, 0, Color.RED);
    grid.drawPixel(0, 1, Color.RED);
    grid.drawPixel(1, 1, Color.RED);
    grid.drawPixel(2, 1, Color.RED);
    grid.drawPixel(2, 2, Color.RED);
    grid.drawPixel(0, 2, Color.BLUE);
    grid.drawPixel(3, 3, Color.RED);
    grid.drawPixel(1, 2, Color.BLUE);
    grid.drawPixel(2, 0, Color.BLUE);

    grid.fillContiguousArea(0, 0, Color.GREEN);

    assertEquals(Color.GREEN, grid.grid[0][0]);
    assertEquals(Color.GREEN, grid.grid[0][1]);
    assertEquals(Color.GREEN, grid.grid[1][1]);
    assertEquals(Color.GREEN, grid.grid[2][1]);
    assertEquals(Color.GREEN, grid.grid[2][2]);
    assertEquals(Color.BLUE, grid.grid[0][2]);
    assertEquals(Color.BLUE, grid.grid[1][2]);
    assertEquals(Color.RED, grid.grid[3][3]);
    assertEquals(Color.TRANSPARENT, grid.grid[3][0]);
  }
}

package com.pixel.editor;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Test
  void lightenPixelMakesPaintedColorBrighter() {
    PixelGrid grid = new PixelGrid();
    grid.drawPixel(0, 0, new Color(0.4, 0.5, 0.6, 1.0));

    grid.lightenPixel(0, 0);

    assertTrue(grid.grid[0][0].getRed() > 0.4);
    assertTrue(grid.grid[0][0].getGreen() > 0.5);
    assertTrue(grid.grid[0][0].getBlue() > 0.6);
  }

  @Test
  void darkenPixelMakesPaintedColorDarker() {
    PixelGrid grid = new PixelGrid();
    grid.drawPixel(0, 0, new Color(0.4, 0.5, 0.6, 1.0));

    grid.darkenPixel(0, 0);

    assertTrue(grid.grid[0][0].getRed() < 0.4);
    assertTrue(grid.grid[0][0].getGreen() < 0.5);
    assertTrue(grid.grid[0][0].getBlue() < 0.6);
  }

  @Test
  void brightnessCannotLeaveValidRgbRange() {
    PixelGrid grid = new PixelGrid();
    grid.drawPixel(0, 0, Color.WHITE);
    grid.drawPixel(0, 1, Color.BLACK);

    for (int i = 0; i < 20; i++) {
      grid.lightenPixel(0, 0);
      grid.darkenPixel(0, 1);
    }

    assertEquals(Color.WHITE, grid.grid[0][0]);
    assertEquals(Color.BLACK, grid.grid[0][1]);
  }
}

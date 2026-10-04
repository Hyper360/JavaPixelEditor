package com.pixel.editor;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

class PixelCanvas extends Canvas {
  // Rows and columns should be passed in here to in the future
  PixelGrid sheet = new PixelGrid();
  private final GraphicsContext graphics;

  public PixelCanvas() {
    super(640, 640);

    graphics = getGraphicsContext2D(); // Needed to draw stuff on the canvas

    setOnMousePressed(this::handleMouseEvent);
    setOnMouseDragged(this::handleMouseEvent);
    render();
  }

  private void handleMouseEvent(MouseEvent event) {
    if (event.isPrimaryButtonDown()) {
      draw(event.getX(), event.getY(), false);
    } else if (event.isSecondaryButtonDown()) {
      draw(event.getX(), event.getY(), true);
    }
  }

  private void draw(double x, double y, boolean erase) {
    int col = (int) (x / sheet.CELLSIZE);
    int row = (int) (y / sheet.CELLSIZE);

    System.out.println(row + " " + col);

    if (erase) {
      sheet.drawPixel(row, col, new Color(0, 0, 0, 0));
    } else {
      sheet.drawPixel(row, col, Color.BLACK);
    }
    render();
  }

  private void render() {
    graphics.clearRect(0, 0, getWidth(), getHeight());
    graphics.setFill(Color.WHITE);
    graphics.fillRect(0, 0, getWidth(), getHeight());

    // Drawing the grid lines
    graphics.setFill(Color.BLACK);
    for (int row = 0; row <= sheet.ROWS; row++) {
      graphics.fillRect(0, row * sheet.CELLSIZE, getWidth(), 2);
    }
    for (int col = 0; col <= sheet.ROWS; col++) {
      graphics.fillRect(col * sheet.CELLSIZE, 0, 1, getHeight());
    }

    // Drawing the grid cells
    for (int row = 0; row < sheet.ROWS; row++) {
      for (int col = 0; col < sheet.ROWS; col++) {
        graphics.setFill(sheet.grid[row][col]);
        graphics.fillRect(col * sheet.CELLSIZE, row * sheet.CELLSIZE,
            sheet.CELLSIZE, sheet.CELLSIZE);
      }
    }
  }
}

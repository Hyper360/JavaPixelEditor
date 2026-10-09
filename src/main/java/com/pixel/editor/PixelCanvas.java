package com.pixel.editor;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

class PixelCanvas extends Canvas {
  // Rows and columns should be passed in here to in the future
  public PixelGrid sheet = new PixelGrid();
  private final GraphicsContext graphics;
  private boolean gridVisible = true;

  public PixelCanvas() {
    super(640, 640);

    graphics = getGraphicsContext2D(); // Needed to draw stuff on the canvas

    setOnMousePressed(this::handleMouseEvent);
    setOnMouseDragged(this::handleMouseEvent);
    setFocusTraversable(true);
    setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.G) {
        toggleGrid();
      }
      if (event.getCode() == KeyCode.EQUALS) {
        growGrid();
      }
      if (event.getCode() == KeyCode.MINUS) {
        shrinkGrid();
      }
    });

    render();
  }

  public boolean isGridVisible() {
    return gridVisible;
  }

  public void toggleGrid() {
    gridVisible = !gridVisible;
    render();
  }

  // ===== SAVE/LOAD METHODS =====

  /**
   * Save current grid to binary file
   */
  public void saveGridAsBinary(String filepath) {
    try {
      PixelGridSerializer.saveBinary(sheet, filepath);
      System.out.println("✓ Grid saved to: " + filepath);
    } catch (Exception e) {
      System.err.println("✗ Error saving grid: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * Load grid from binary file
   */
  public void loadGridFromBinary(String filepath) {
    try {
      sheet = PixelGridSerializer.loadBinary(filepath);
      render();
      System.out.println("✓ Grid loaded from: " + filepath);
    } catch (Exception e) {
      System.err.println("✗ Error loading grid: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * Export grid as PNG image
   */
  public void exportGridAsPNG(String filepath) {
    try {
      ImageExporter.exportAsPNG(sheet, filepath);
      System.out.println("✓ Grid exported as PNG: " + filepath);
    } catch (Exception e) {
      System.err.println("✗ Error exporting PNG: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * Import PNG image into grid
   */
  public void importPNG(String filepath) {
    try {
      sheet = ImageExporter.importFromPNG(filepath);
      render();
      System.out.println("✓ PNG imported: " + filepath);
    } catch (Exception e) {
      System.err.println("✗ Error importing PNG: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * Clear the grid
   */
  public void newGrid() {
    sheet.clear();
    render();
  }

  public void growGrid() {
    sheet.changeSize(sheet.getRows() + 1, sheet.getRows() + 1);
    render();
  }

  public void shrinkGrid() {
    sheet.changeSize(sheet.getRows() - 1, sheet.getRows() - 1);
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

    // Draw grid lines
    if (gridVisible) {
      graphics.setStroke(Color.LIGHTGRAY);
      graphics.setLineWidth(1);
      for (int row = 0; row <= sheet.getRows(); row++) {
        graphics.strokeLine(0, row * sheet.CELLSIZE, getWidth(), row * sheet.CELLSIZE);
      }
      for (int col = 0; col <= sheet.getCols(); col++) {
        graphics.strokeLine(col * sheet.CELLSIZE, 0, col * sheet.CELLSIZE, getHeight());
      }
    }

    // Draw pixels
    for (int row = 0; row < sheet.getRows(); row++) {
      for (int col = 0; col < sheet.getCols(); col++) {
        graphics.setFill(sheet.grid[row][col]);
        graphics.fillRect(col * sheet.CELLSIZE, row * sheet.CELLSIZE,
            sheet.CELLSIZE, sheet.CELLSIZE);
      }
    }
  }
}

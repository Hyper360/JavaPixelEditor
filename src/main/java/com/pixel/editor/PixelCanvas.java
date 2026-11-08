package com.pixel.editor;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

enum Tool {
  PENCIL,
  RECTANGLE,
  CIRCLE
}

class PixelCanvas extends Canvas {
  // Rows and columns should be passed in here to in the future
  public PixelGrid sheet = new PixelGrid();
  private final GraphicsContext graphics;
  private boolean gridVisible = true;

  private double viewportX = 0;
  private double viewportY = 0;
  private double lastMouseX = 0;
  private double lastMouseY = 0;
  private double zoom = 1.0;

  private Color paintColor = Color.BLACK;
  private Tool currentTool = Tool.PENCIL;

  private int startCol = -1;
  private int startRow = -1;
  private int previewCol = -1;
  private int previewRow = -1;

  public PixelCanvas() {
    super(640, 640);

    graphics = getGraphicsContext2D(); // Needed to draw stuff on the canvas

    setOnMousePressed(this::handleMousePressed);
    setOnMouseDragged(this::handleMouseDragged);
    setOnMouseReleased(this::handleMouseReleased);
    setOnScroll(this::handleScroll);
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

  public boolean isGridVisible() { return gridVisible; }

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

  private void handleScroll(ScrollEvent event) {
    double scrollAmount = event.getDeltaY();
    // Using a zoom factor instead of a constant for smoother "zooming"
    double zoomFactor = 1.1;

    if (scrollAmount == 0) {
      return;
    }

    if (scrollAmount > 0) {
      zoom *= zoomFactor;
      if (zoom > 3) {
        zoom = 3;
      }
    } else {
      zoom /= zoomFactor;
      if (zoom < 0.2) {
        zoom = 0.2;
      }
    }
    render();
  }

  private void handleMousePressed(MouseEvent event) {
    requestFocus();
    lastMouseX = event.getX();
    lastMouseY = event.getY();

    if (event.isPrimaryButtonDown()) {
      if (currentTool == Tool.PENCIL) {
        draw(event.getX(), event.getY(), false);
      } else if (currentTool == Tool.RECTANGLE || currentTool == Tool.CIRCLE) {
        startCol = toGridCol(event.getX());
        startRow = toGridRow(event.getY());
        previewCol = startCol;
        previewRow = startRow;
        render();
      }
    } else if (event.isSecondaryButtonDown()) {
      draw(event.getX(), event.getY(), true);
    }
  }

  private void handleMouseDragged(MouseEvent event) {
    if (event.isMiddleButtonDown()) {
      double deltaX = event.getX() - lastMouseX;
      double deltaY = event.getY() - lastMouseY;

      viewportX -= deltaX / zoom;
      viewportY -= deltaY / zoom;

      lastMouseX = event.getX();
      lastMouseY = event.getY();
      render();
      return;
    }

    if (event.isPrimaryButtonDown()) {
      if (currentTool == Tool.PENCIL) {
        draw(event.getX(), event.getY(), false);
      } else if (currentTool == Tool.RECTANGLE || currentTool == Tool.CIRCLE) {
        previewCol = toGridCol(event.getX());
        previewRow = toGridRow(event.getY());
        render();
      }
    } else if (event.isSecondaryButtonDown()) {
      draw(event.getX(), event.getY(), true);
    }
  }

  private void handleMouseReleased(MouseEvent event) {
    if (currentTool != Tool.RECTANGLE && currentTool != Tool.CIRCLE) {
      return;
    }

    if (startCol < 0 || startRow < 0 || previewCol < 0 || previewRow < 0) {
      return;
    }

    commitShape();
    startCol = -1;
    startRow = -1;
    previewCol = -1;
    previewRow = -1;
    render();
  }

  private int toGridCol(double x) {
    return (int) (((x / zoom) + viewportX) / sheet.CELLSIZE);
  }

  private int toGridRow(double y) {
    return (int) (((y / zoom) + viewportY) / sheet.CELLSIZE);
  }

  private void commitShape() {
    int minCol = Math.min(startCol, previewCol);
    int maxCol = Math.max(startCol, previewCol);
    int minRow = Math.min(startRow, previewRow);
    int maxRow = Math.max(startRow, previewRow);

    if (currentTool == Tool.RECTANGLE) {
      for (int row = minRow; row <= maxRow; row++) {
        for (int col = minCol; col <= maxCol; col++) {
          sheet.drawPixel(row, col, paintColor);
        }
      }
    } else if (currentTool == Tool.CIRCLE) {
      double width = maxCol - minCol + 1.0;
      double height = maxRow - minRow + 1.0;
      double radius = Math.max(1.0, Math.min(width, height) / 2.0);

      double centerX = minCol + (width / 2.0);
      double centerY = minRow + (height / 2.0);

      for (int row = minRow; row <= maxRow; row++) {
        for (int col = minCol; col <= maxCol; col++) {
          double dx = (col + 0.5) - centerX;
          double dy = (row + 0.5) - centerY;

          if (dx * dx + dy * dy <= radius * radius) {
            sheet.drawPixel(row, col, paintColor);
          }
        }
      }
    }
  }

  private void draw(double x, double y, boolean erase) {
    int col = toGridCol(x);
    int row = toGridRow(y);

    if (erase) {
      sheet.drawPixel(row, col, new Color(0, 0, 0, 0));
    } else {
      sheet.drawPixel(row, col, paintColor);
    }
    render();
  }

  private void drawGridCells() {
    for (int row = 0; row < sheet.getRows(); row++) {
      for (int col = 0; col < sheet.getCols(); col++) {
        graphics.setFill(sheet.grid[row][col]);
        double screenX = ((col * sheet.CELLSIZE) - viewportX) * zoom;
        double screenY = ((row * sheet.CELLSIZE) - viewportY) * zoom;
        double visibleCellsize = sheet.CELLSIZE * zoom;
        graphics.fillRect(screenX, screenY, visibleCellsize, visibleCellsize);
      }
    }
  }

  private void drawGridLines() {
    graphics.setStroke(Color.LIGHTGRAY);
    graphics.setLineWidth(1);
    double gridWidth = (sheet.getCols() * sheet.CELLSIZE) * zoom;
    double gridHeight = (sheet.getRows() * sheet.CELLSIZE) * zoom;
    for (int row = 0; row <= sheet.getRows(); row++) {
      double drawRowStart = ((row * sheet.CELLSIZE) - viewportY) * zoom;
      graphics.strokeLine(-viewportX * zoom, drawRowStart,
          gridWidth - (viewportX * zoom), drawRowStart);
    }
    for (int col = 0; col <= sheet.getCols(); col++) {
      double drawColStart = ((col * sheet.CELLSIZE) - viewportX) * zoom;
      graphics.strokeLine(drawColStart, -viewportY * zoom,
          drawColStart, gridHeight - (viewportY * zoom));
    }
  }

  private void drawShapePreview(GraphicsContext gc) {
    if (currentTool != Tool.RECTANGLE && currentTool != Tool.CIRCLE) {
      return;
    }

    if (startCol < 0 || startRow < 0 || previewCol < 0 || previewRow < 0) {
      return;
    }

    int minCol = Math.min(startCol, previewCol);
    int maxCol = Math.max(startCol, previewCol);
    int minRow = Math.min(startRow, previewRow);
    int maxRow = Math.max(startRow, previewRow);

    gc.setStroke(paintColor);
    gc.setLineWidth(1.5);
    gc.setGlobalAlpha(0.75);

    if (currentTool == Tool.RECTANGLE) {
      gc.strokeRect(minCol * sheet.CELLSIZE, minRow * sheet.CELLSIZE,
          (maxCol - minCol + 1) * sheet.CELLSIZE,
          (maxRow - minRow + 1) * sheet.CELLSIZE);
    } else if (currentTool == Tool.CIRCLE) {
      double width = maxCol - minCol + 1.0;
      double height = maxRow - minRow + 1.0;
      double radius = Math.max(1.0, Math.min(width, height) / 2.0);

      double centerX = minCol + (width / 2.0);
      double centerY = minRow + (height / 2.0);

      gc.strokeOval(
          (centerX - radius) * sheet.CELLSIZE,
          (centerY - radius) * sheet.CELLSIZE,
          radius * 2 * sheet.CELLSIZE,
          radius * 2 * sheet.CELLSIZE
      );
    }

    gc.setGlobalAlpha(1.0);
  }

  private void render() {
    graphics.clearRect(0, 0, getWidth(), getHeight());
    graphics.setFill(Color.WHITE);
    graphics.fillRect(0, 0, getWidth(), getHeight());

    // Drawing the grid cells
    drawGridCells();

    // Drawing the grid lines
    if (gridVisible) {
      drawGridLines();
    }

    drawShapePreview(graphics);
  }

  public void setPaintColor(Color color) {
    paintColor = color;
  }

  public void setTool(Tool tool) {
    currentTool = tool;
  }
}

package com.pixel.editor;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

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
  private Integer lineStartRow;
  private Integer lineStartCol;
  private Integer lineEndRow;
  private Integer lineEndCol;
  private boolean lineInProgress;
  private boolean lineErasing;
  private EditingTool editingTool = EditingTool.PENCIL;
  public enum EditingTool {
    PENCIL,
    BUCKET_FILL,
    LINE,
  }


  private Color paintColor = Color.BLACK;

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

  public void setEditingTool(EditingTool editingTool) {
    if (this.editingTool == EditingTool.LINE
        && editingTool != EditingTool.LINE
        && lineInProgress) {
      lineInProgress = false;
      lineStartRow = null;
      lineStartCol = null;
      lineEndRow = null;
      lineEndCol = null;
      render();
    }
    this.editingTool = editingTool;
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
      if (editingTool == EditingTool.LINE) {
        startLine(event.getX(), event.getY(), false);
      } else if (editingTool == EditingTool.BUCKET_FILL) {
        fill(event.getX(), event.getY());
      } else {
        draw(event.getX(), event.getY(), false);
      }
    } else if (event.isSecondaryButtonDown()) {
      if (editingTool == EditingTool.LINE) {
        startLine(event.getX(), event.getY(), true);
      } else {
        draw(event.getX(), event.getY(), true);
      }
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
    } else if (editingTool == EditingTool.LINE && event.isPrimaryButtonDown()) {
      updateLinePreview(event.getX(), event.getY());
    } else if (editingTool == EditingTool.LINE && event.isSecondaryButtonDown()) {
      updateLinePreview(event.getX(), event.getY());
    } else if (event.isPrimaryButtonDown() && editingTool == EditingTool.PENCIL) {
      draw(event.getX(), event.getY(), false);
    } else if (event.isSecondaryButtonDown()) {
      draw(event.getX(), event.getY(), true);
    }
  }

  private void handleMouseReleased(MouseEvent event) {
    if (editingTool != EditingTool.LINE || !lineInProgress) {
      return;
    }

    updateLinePreview(event.getX(), event.getY());
    sheet.drawLine(lineStartRow, lineStartCol, lineEndRow, lineEndCol,
        lineErasing ? new Color(0, 0, 0, 0) : paintColor);

    lineInProgress = false;
    lineStartRow = null;
    lineStartCol = null;
    lineEndRow = null;
    lineEndCol = null;
    render();
  }

  private void startLine(double x, double y, boolean erase) {
    int[] cell = getGridCell(x, y);
    lineStartRow = cell[0];
    lineStartCol = cell[1];
    lineEndRow = cell[0];
    lineEndCol = cell[1];
    lineErasing = erase;
    lineInProgress = true;
    render();
  }

  private void updateLinePreview(double x, double y) {
    int[] cell = getGridCell(x, y);
    lineEndRow = cell[0];
    lineEndCol = cell[1];
    render();
  }

  private int[] getGridCell(double x, double y) {
    int col = (int) (((x / zoom) + viewportX) / sheet.CELLSIZE);
    int row = (int) (((y / zoom) + viewportY) / sheet.CELLSIZE);
    return new int[] {row, col};
  }

  private void drawLinePreview() {
    if (!lineInProgress) {
      return;
    }

    double startX = ((lineStartCol * sheet.CELLSIZE) - viewportX
        + sheet.CELLSIZE / 2) * zoom;
    double startY = ((lineStartRow * sheet.CELLSIZE) - viewportY
        + sheet.CELLSIZE / 2) * zoom;
    double endX = ((lineEndCol * sheet.CELLSIZE) - viewportX
        + sheet.CELLSIZE / 2) * zoom;
    double endY = ((lineEndRow * sheet.CELLSIZE) - viewportY
        + sheet.CELLSIZE / 2) * zoom;

    graphics.setGlobalAlpha(0.45);
    graphics.setStroke(lineErasing ? Color.WHITE : paintColor);
    graphics.setLineWidth(sheet.CELLSIZE * zoom);
    graphics.strokeLine(startX, startY, endX, endY);
    graphics.setGlobalAlpha(1.0);
  }

  private void draw(double x, double y, boolean erase) {
    int col = (int)(((x / zoom) + viewportX) / sheet.CELLSIZE);
    int row = (int)(((y / zoom) + viewportY) / sheet.CELLSIZE);

    if (erase) {
      sheet.drawPixel(row, col, new Color(0, 0, 0, 0));
    } else {
      sheet.drawPixel(row, col, paintColor);
    }
    render();
  }

  private void fill(double x, double y) {
    int col = (int)(((x / zoom) + viewportX) / sheet.CELLSIZE);
    int row = (int)(((y / zoom) + viewportY) / sheet.CELLSIZE);

    sheet.fillContiguousArea(row, col, paintColor);
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

    drawLinePreview();
  }

  public void setPaintColor(Color color) {
    paintColor = color;
  }
}
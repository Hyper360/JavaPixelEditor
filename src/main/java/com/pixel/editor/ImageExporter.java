package com.pixel.editor;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageExporter {

  /**
   * Export PixelGrid as PNG image
   * Each pixel is scaled up for visibility
   */
  public static void exportAsPNG(PixelGrid grid, String filepath, int scale) throws Exception {
    int width = grid.COLUMNS * scale;
    int height = grid.ROWS * scale;
    
    WritableImage image = new WritableImage(width, height);
    PixelWriter writer = image.getPixelWriter();
    
    // Write each grid pixel to image (scaled up)
    for (int row = 0; row < grid.ROWS; row++) {
      for (int col = 0; col < grid.COLUMNS; col++) {
        Color pixelColor = grid.grid[row][col];
        
        // Fill scaled pixels
        for (int i = 0; i < scale; i++) {
          for (int j = 0; j < scale; j++) {
            writer.setColor(col * scale + j, row * scale + i, pixelColor);
          }
        }
      }
    }
    
    // Save as PNG
    var bufferedImage = SwingFXUtils.fromFXImage(image, null);
    ImageIO.write(bufferedImage, "png", new File(filepath));
  }

  /**
   * Export with default 20px scale
   */
  public static void exportAsPNG(PixelGrid grid, String filepath) throws Exception {
    exportAsPNG(grid, filepath, 20);
  }

  /**
   * Import PNG image into PixelGrid
   * Scales down image to fit grid (32x32)
   */
  public static PixelGrid importFromPNG(String filepath) throws Exception {
    var image = ImageIO.read(new File(filepath));
    if (image == null) {
      throw new IOException("Could not read image file");
    }
    
    PixelGrid grid = new PixelGrid();
    int imgWidth = image.getWidth();
    int imgHeight = image.getHeight();
    
    // Sample image pixels and fit to grid
    for (int row = 0; row < grid.ROWS; row++) {
      for (int col = 0; col < grid.COLUMNS; col++) {
        int imgX = (col * imgWidth) / grid.COLUMNS;
        int imgY = (row * imgHeight) / grid.ROWS;
        
        // Clamp to image bounds
        imgX = Math.min(imgX, imgWidth - 1);
        imgY = Math.min(imgY, imgHeight - 1);
        
        // Get RGB from image
        int rgb = image.getRGB(imgX, imgY);
        
        // Convert to JavaFX Color
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int a = (rgb >> 24) & 0xFF;
        
        Color color = new Color(r / 255.0, g / 255.0, b / 255.0, a / 255.0);
        grid.grid[row][col] = color;
      }
    }
    
    return grid;
  }
}

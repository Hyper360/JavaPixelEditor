package com.pixel.editor;

import javafx.scene.paint.Color;
import java.io.*;

public class PixelGridSerializer {
  /**
   * Save PixelGrid to binary file (.pgrid)
   * Format: [rows(4)] [columns(4)] [grid data]
   */
  public static void saveBinary(PixelGrid grid, String filepath) throws IOException {
    try (DataOutputStream dos = new DataOutputStream(
        new FileOutputStream(filepath))) {
      
      // Write dimensions
      dos.writeInt(grid.ROWS);
      dos.writeInt(grid.COLUMNS);
      
      // Write pixel data
      for (int row = 0; row < grid.ROWS; row++) {
        for (int col = 0; col < grid.COLUMNS; col++) {
          Color color = grid.grid[row][col];
          // Store RGBA as doubles (8 bytes each = 32 bytes per pixel)
          dos.writeDouble(color.getRed());
          dos.writeDouble(color.getGreen());
          dos.writeDouble(color.getBlue());
          dos.writeDouble(color.getOpacity());
        }
      }
    }
  }

  /**
   * Load PixelGrid from binary file (.pgrid)
   */
  public static PixelGrid loadBinary(String filepath) throws IOException {
    try (DataInputStream dis = new DataInputStream(
        new FileInputStream(filepath))) {
      
      // Read dimensions
      int rows = dis.readInt();
      int cols = dis.readInt();
      
      // Validate dimensions
      if (rows <= 0 || cols <= 0) {
        throw new IOException("Invalid grid dimensions: " + rows + "x" + cols);
      }
      
      // Create new grid and read pixel data
      PixelGrid grid = new PixelGrid();
      
      for (int row = 0; row < rows && row < grid.ROWS; row++) {
        for (int col = 0; col < cols && col < grid.COLUMNS; col++) {
          double r = dis.readDouble();
          double g = dis.readDouble();
          double b = dis.readDouble();
          double a = dis.readDouble();
          grid.grid[row][col] = new Color(r, g, b, a);
        }
      }
      return grid;
    }
  }

  /**
   * Check if file is valid binary grid format
   */
  public static boolean isValidBinaryFile(String filepath) {
    try (DataInputStream dis = new DataInputStream(
        new FileInputStream(filepath))) {
      int rows = dis.readInt();
      int cols = dis.readInt();
      return rows > 0 && cols > 0;
    } catch (Exception e) {
      return false;
    }
  }
}

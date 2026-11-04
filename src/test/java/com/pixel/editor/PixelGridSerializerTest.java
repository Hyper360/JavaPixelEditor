package com.pixel.editor;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PixelGridSerializerTest {

  private static final String TEST_FILE = "test_grid.pgrid";
  private static final String TEST_PNG = "test_image.png";

  @BeforeEach
  void setup() {
    new File(TEST_FILE).delete();
    new File(TEST_PNG).delete();
  }

  @AfterEach
  void cleanup() {
    new File(TEST_FILE).delete();
    new File(TEST_PNG).delete();
  }

  @Test
  void testSaveAndLoadBinary() throws Exception {
    PixelGrid original = new PixelGrid();
    original.drawPixel(5, 5, Color.RED);
    original.drawPixel(10, 10, Color.BLUE);
    original.drawPixel(15, 15, Color.GREEN);

    PixelGridSerializer.saveBinary(original, TEST_FILE);
    assertTrue(new File(TEST_FILE).exists(), "Binary file should exist");

    PixelGrid loaded = PixelGridSerializer.loadBinary(TEST_FILE);

    assertEquals(Color.RED, loaded.grid[5][5], "Red pixel should match");
    assertEquals(Color.BLUE, loaded.grid[10][10], "Blue pixel should match");
    assertEquals(Color.GREEN, loaded.grid[15][15], "Green pixel should match");
  }

  @Test
  void testValidBinaryFile() throws Exception {
    PixelGrid grid = new PixelGrid();
    PixelGridSerializer.saveBinary(grid, TEST_FILE);

    assertTrue(PixelGridSerializer.isValidBinaryFile(TEST_FILE),
        "Should recognize valid binary file");
  }

  @Test
  void testExportAndImportPNG() throws Exception {
    PixelGrid original = new PixelGrid();
    original.drawPixel(0, 0, Color.RED);
    original.drawPixel(1, 1, Color.WHITE);
    original.drawPixel(2, 2, Color.BLUE);
    original.drawPixel(3, 3, Color.color(0, 1, 0, 0.5));

    ImageExporter.exportAsPNG(original, TEST_PNG, 10);
    assertTrue(new File(TEST_PNG).exists(), "PNG file should exist");

    BufferedImage exported = ImageIO.read(new File(TEST_PNG));
    assertEquals(0xFFFF0000, exported.getRGB(0, 0));
    assertEquals(0xFFFFFFFF, exported.getRGB(10, 10));
    assertEquals(0xFF0000FF, exported.getRGB(20, 20));
    assertEquals(0x8000FF00, exported.getRGB(30, 30));

    PixelGrid imported = ImageExporter.importFromPNG(TEST_PNG);
    assertNotNull(imported, "Imported grid should not be null");
    assertEquals(original.getRows(), imported.getRows(), "Row count should match");
    assertEquals(original.getCols(), imported.getCols(), "Column count should match");
    assertEquals(Color.RED, imported.grid[0][0]);
    assertEquals(Color.WHITE, imported.grid[1][1]);
    assertEquals(Color.BLUE, imported.grid[2][2]);
  }

  @Test
  void testClearGrid() {
    PixelGrid grid = new PixelGrid();
    grid.drawPixel(5, 5, Color.BLACK);
    grid.clear();

    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getCols(); j++) {
        assertEquals(new Color(0, 0, 0, 0), grid.grid[i][j],
            "All pixels should be transparent after clear");
      }
    }
  }
}

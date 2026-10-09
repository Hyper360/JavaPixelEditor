package com.pixel.editor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PixelCanvasTest {

  @Test
  void gridToggleCanBeToggled() {
    PixelCanvas canvas = new PixelCanvas();

    assertTrue(canvas.isGridVisible());
    canvas.toggleGrid();
    assertFalse(canvas.isGridVisible());
    canvas.toggleGrid();
    assertTrue(canvas.isGridVisible());
  }
}

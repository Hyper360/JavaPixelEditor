package com.pixel.editor;

import javafx.scene.paint.Color;

public interface Tool {
    void apply(PixelGrid grid, int row, int col, Color color);
}

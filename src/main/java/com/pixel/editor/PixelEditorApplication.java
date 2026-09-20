package com.pixel.editor;

import com.pixel.editor.PixelGrid;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PixelEditorApplication extends Application {

  @Override
  public void start(Stage stage) {
    PixelCanvas canvas = new PixelCanvas();
    Scene scene = new Scene(new StackPane(canvas), 640, 480);

    stage.setTitle("Java Pixel Editor");
    stage.setScene(scene);
    stage.show();
  }

  // The actual application should use the mouse down event like brushstrokes

  public static void main(String[] args) {
    launch(args);
  }
}

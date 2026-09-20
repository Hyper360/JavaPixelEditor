package com.pixel.editor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PixelEditorApplication extends Application {

  @Override
  public void start(Stage stage) {
    Label message = new Label("Java Pixel Editor Message");
    StackPane root = new StackPane(message);
    Scene scene = new Scene(root, 640, 480);

    stage.setTitle("Java Pixel Editor");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) { launch(args); }
}

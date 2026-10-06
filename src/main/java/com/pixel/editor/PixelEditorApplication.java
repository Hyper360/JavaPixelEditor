package com.pixel.editor;

import com.pixel.editor.PixelGrid;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PixelEditorApplication extends Application {

  @Override
  public void start(Stage stage) {
    PixelCanvas canvas = new PixelCanvas();
    StackPane root = new StackPane(canvas);
    Label hint = new Label("Press G to toggle grid");
    hint.setStyle("-fx-background-color: rgba(0,0,0,0.55); -fx-text-fill: white; -fx-padding: 6 10 6 10; -fx-background-radius: 8; ");
    StackPane.setMargin(hint, new Insets(12, 12, 12, 12));
    StackPane.setAlignment(hint, javafx.geometry.Pos.BOTTOM_RIGHT);
    root.getChildren().add(hint);

    Scene scene = new Scene(root, 640, 480);

    stage.setTitle("Java Pixel Editor");
    stage.setScene(scene);
    stage.setOnShown(event -> canvas.requestFocus());
    stage.show();
  }

  // The actual application should use the mouse down event like brushstrokes

  public static void main(String[] args) {
    launch(args);
  }
}

package com.pixel.editor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class ColorPicker {

     private final javafx.scene.control.ColorPicker color =
          new javafx.scene.control.ColorPicker(Color.BLACK);
     private final Stage stage = new Stage();

    public ColorPicker(Window owner) {
          stage.initOwner(owner);

          FlowPane root = new FlowPane();
          root.getChildren().add(color);

          stage.setTitle("Color Picker");
          stage.setScene(new Scene(root, 200, 200));
     }


     public void pickColor() {
          stage.show();
          stage.toFront();
     }

     public Color getColor() {
          return color.getValue();
     }

     public void setOnColorChanged(EventHandler<ActionEvent> handler) {
          color.setOnAction(handler);
     }

     public void setOnClosed(EventHandler<WindowEvent> handler) {
          stage.setOnHidden(handler);
     }

}

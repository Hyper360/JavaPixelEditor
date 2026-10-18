package com.pixel.editor;

import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PixelEditorApplication extends Application {

  private PixelCanvas pixelCanvas;
  private ColorPicker colorPicker;
  private Stage primaryStage;


  @Override
  public void start(Stage stage) {
    this.primaryStage = stage;
    pixelCanvas = new PixelCanvas();

    colorPicker = new ColorPicker(stage);
    colorPicker.setOnColorChanged(this::handleColorChanged);
    colorPicker.setOnClosed(this::handleColorPickerClosed);

    MenuBar menuBar = createMenuBar();

    BorderPane root = new BorderPane();
    root.setTop(menuBar);
    root.setCenter(pixelCanvas);

    Scene scene = new Scene(root, 800, 700);
    stage.setTitle("Java Pixel Editor");
    stage.setScene(scene);
    stage.show();

    pixelCanvas.requestFocus();
  }

  private MenuBar createMenuBar() {
    MenuBar menuBar = new MenuBar();

    // ===== FILE MENU =====
    Menu fileMenu = new Menu("File");

    MenuItem newItem = new MenuItem("New");
    newItem.setOnAction(e -> handleNew());

    MenuItem openBinaryItem = new MenuItem("Open Pixel File (.pgrid)");
    openBinaryItem.setOnAction(e -> handleOpenBinary());

    MenuItem importPNGItem = new MenuItem("Import from PNG");
    importPNGItem.setOnAction(e -> handleImportPNG());

    MenuItem saveBinaryItem = new MenuItem("Save as Binary (.pgrid)");
    saveBinaryItem.setOnAction(e -> handleSaveBinary());

    MenuItem exportPNGItem = new MenuItem("Export as PNG");
    exportPNGItem.setOnAction(e -> handleExportPNG());

    MenuItem exitItem = new MenuItem("Exit");
    exitItem.setOnAction(e -> primaryStage.close());

    fileMenu.getItems().addAll(
        newItem,
        new SeparatorMenuItem(),
        openBinaryItem,
        importPNGItem,
        new SeparatorMenuItem(),
        saveBinaryItem,
        exportPNGItem,
        new SeparatorMenuItem(),
        exitItem
    );

    // ===== EDIT MENU =====
    Menu editMenu = new Menu("Edit");

    MenuItem toggleGridItem = new MenuItem("Toggle Grid (G)");
    toggleGridItem.setOnAction(e -> pixelCanvas.toggleGrid());

    editMenu.getItems().addAll(toggleGridItem);

    // ===== COLOR PICKER =====
    Menu colorMenu = new Menu("Color");

    MenuItem colorItem = new MenuItem("Color");
    colorItem.setOnAction(this::handlePickColor);

    colorMenu.getItems().addAll(colorItem);

    menuBar.getMenus().addAll(fileMenu, editMenu, colorMenu);
    return menuBar;

  }

  private void handlePickColor(ActionEvent event) {
    colorPicker.pickColor();
  }

  private void handleColorChanged(ActionEvent event) {
    pixelCanvas.setPaintColor(colorPicker.getColor());
  }

  private void handleColorPickerClosed(WindowEvent event) {
    pixelCanvas.requestFocus();
  }

  // ===== FILE HANDLERS =====

  private void handleNew() {
    pixelCanvas.newGrid();
    System.out.println("New grid created");
  }

  private void handleOpenBinary() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Pixel Grid File");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Pixel Grid Files (*.pgrid)", "*.pgrid"),
        new FileChooser.ExtensionFilter("All Files (*.*)", "*.*")
    );

    File file = fileChooser.showOpenDialog(primaryStage);
    if (file != null) {
      try {
        if (!PixelGridSerializer.isValidBinaryFile(file.getAbsolutePath())) {
          System.err.println("Invalid grid file format");
          return;
        }
        pixelCanvas.loadGridFromBinary(file.getAbsolutePath());
      } catch (Exception e) {
        System.err.println("Failed to open file: " + e.getMessage());
      }
    }
  }

  private void handleImportPNG() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Import PNG Image");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("PNG Images (*.png)", "*.png"),
        new FileChooser.ExtensionFilter("All Images (*.jpg, *.jpeg, *.gif)", "*.jpg", "*.jpeg", "*.gif"),
        new FileChooser.ExtensionFilter("All Files (*.*)", "*.*")
    );

    File file = fileChooser.showOpenDialog(primaryStage);
    if (file != null) {
      try {
        pixelCanvas.importPNG(file.getAbsolutePath());
      } catch (Exception e) {
        System.err.println("Failed to import image: " + e.getMessage());
      }
    }
  }

  private void handleSaveBinary() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Pixel Grid");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Pixel Grid Files (*.pgrid)", "*.pgrid")
    );
    fileChooser.setInitialFileName("untitled.pgrid");

    File file = fileChooser.showSaveDialog(primaryStage);
    if (file != null) {
      try {
        pixelCanvas.saveGridAsBinary(file.getAbsolutePath());
      } catch (Exception e) {
        System.err.println("Failed to save file: " + e.getMessage());
      }
    }
  }

  private void handleExportPNG() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Export as PNG");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("PNG Images (*.png)", "*.png")
    );
    fileChooser.setInitialFileName("pixelart.png");

    File file = fileChooser.showSaveDialog(primaryStage);
    if (file != null) {
      try {
        pixelCanvas.exportGridAsPNG(file.getAbsolutePath());
      } catch (Exception e) {
        System.err.println("Failed to export PNG: " + e.getMessage());
      }
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}

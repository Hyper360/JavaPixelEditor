package com.pixel.editor;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PixelEditorApplicationTest {

  @Test
  void applicationExtendsJavaFxApplication() {
    assertTrue(Application.class.isAssignableFrom(PixelEditorApplication.class));
  }

  @Test
  void applicationHasStartMethodForPrimaryStage() {
    assertDoesNotThrow(() -> {
      Method startMethod = PixelEditorApplication.class.getMethod("start", Stage.class);

      assertTrue(startMethod.getReturnType().equals(Void.TYPE));
    });
  }
}

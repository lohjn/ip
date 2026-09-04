package kibo.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import kibo.Kibo;

/**
 * Tests the main JavaFX window and its command-handling controls.
 */
public class MainWindowTest {
    private static final int JAVAFX_TIMEOUT_SECONDS = 5;

    @BeforeAll
    static void startJavaFx() throws InterruptedException {
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {
            });
        }
    }

    @AfterAll
    static void stopJavaFx() {
        Platform.exit();
    }

    @Test
    void handleUserInput_listAndBye_displaysDialogsAndDisablesInput() throws Exception {
        FutureTask<Void> testActions = new FutureTask<>(() -> {
            URL mainWindowResource = MainWindowTest.class.getResource("/view/MainWindow.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(mainWindowResource);
            fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setKibo(new Kibo());

            VBox dialogContainer = (VBox) fxmlLoader.getNamespace().get("dialogContainer");
            TextField userInput = (TextField) fxmlLoader.getNamespace().get("userInput");
            Button sendButton = (Button) fxmlLoader.getNamespace().get("sendButton");

            assertEquals(1, dialogContainer.getChildren().size());

            userInput.setText("list");
            userInput.fireEvent(new ActionEvent());
            assertEquals(3, dialogContainer.getChildren().size());

            userInput.setText("bye");
            userInput.fireEvent(new ActionEvent());
            assertEquals(5, dialogContainer.getChildren().size());
            assertTrue(userInput.isDisabled());
            assertTrue(sendButton.isDisabled());
            return null;
        });

        Platform.runLater(testActions);
        testActions.get(JAVAFX_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}

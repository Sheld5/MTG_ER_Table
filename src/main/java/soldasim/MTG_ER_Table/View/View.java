package soldasim.MTG_ER_Table.View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import soldasim.MTG_ER_Table.Controller.Controller;
import soldasim.MTG_ER_Table.Controller.WorkData;

import java.awt.image.BufferedImage;

/**
 * View according to the MVC application model.
 * Handles user interaction and displaying the application.
 */
public class View extends Application implements Runnable {

    private static final String WINDOW_TITLE = "MTG ER Table";
    private static final Insets PADDING = new Insets(12, 12, 12, 12);
    private static final int SPACING = 12;

    private static final String UPDATE_FW_BUTTON_START_TEXT = "start selecting";
    private static final String UPDATE_FW_BUTTON_STOP_TEXT = "stop selecting";

    public static Controller controller;

    private Stage mainStage;
    private Scene testScene;
    private ImageView cardImageView;
    private Label foregroundWindowLabel;
    private Button updateFWButton;
    private boolean updatingFW = false;

    /**
     * Call Application.launch() to launch the view.
     */
    @Override
    public void run() {
        launch();
    }

    /**
     * Is called from the launch() call.
     * Initialize the view and give the controller a reference to itself.
     * @param stage the main stage
     * @see Controller
     */
    @Override
    public void start(Stage stage) {
        this.mainStage = stage;
        initialize();
        controller.setView(this);
        mainStage.show();
    }

    /**
     * Notify the controller that the view is terminating.
     * @see Controller
     */
    @Override
    public void stop() {
        controller.work.notifyViewTerminated();
    }

    public void displayImage(BufferedImage image) {
        if (image == null) {
            Platform.runLater(() -> cardImageView.setImage(null));
        } else {
            Platform.runLater(() -> cardImageView.setImage(ViewUtils.getImage(ViewUtils.rescaleImage(image, 256, 256))));
        }
    }

    public void giveForegroundWindowTitle(String foregroundWindowTitle) {
        Platform.runLater(() -> foregroundWindowLabel.setText(foregroundWindowTitle));
    }

    public static String getWindowTitle() {
        return WINDOW_TITLE;
    }

    private void initialize() {
        initTestScene();
        initMainStage(testScene);
    }

    private void initMainStage(Scene scene) {
        mainStage.setTitle(WINDOW_TITLE);
        mainStage.setResizable(false);
        mainStage.setScene(scene);
    }

    private void initTestScene() {
                    cardImageView = new ImageView();

                Pane imagePane = new Pane(cardImageView);
                imagePane.setPrefSize(256, 256);

                    TextArea deckList = new TextArea();

                    Button loadButton = new Button("load");
                    loadButton.setOnAction(event -> loadButtonPressed(deckList));

                VBox deckArea = new VBox(deckList, loadButton);
                deckArea.setAlignment(Pos.CENTER);
                deckArea.setSpacing(SPACING);
                deckArea.setPrefSize(128, 256);

                    foregroundWindowLabel = new Label();

                    updateFWButton = new Button(UPDATE_FW_BUTTON_START_TEXT);
                    updateFWButton.setOnAction(event -> updateFWButtonPressed());

                VBox windowsArea = new VBox(foregroundWindowLabel, updateFWButton);
                windowsArea.setAlignment(Pos.CENTER);
                windowsArea.setSpacing(SPACING);
                windowsArea.setPrefSize(128, 256);

            HBox content = new HBox(imagePane, deckArea, windowsArea);
            content.setAlignment(Pos.CENTER);
            content.setSpacing(SPACING);
            content.setPadding(PADDING);

        testScene = new Scene(content);
    }

    private void loadButtonPressed(TextArea deckList) {
        controller.work.giveDeckList(deckList.getText());
    }

    private void updateFWButtonPressed() {
        if (updatingFW) {
            updateFWButton.setText(UPDATE_FW_BUTTON_START_TEXT);
            controller.work.requestUpdateFW(WorkData.Update.STOP);
            updatingFW = false;
        } else {
            updateFWButton.setText(UPDATE_FW_BUTTON_STOP_TEXT);
            controller.work.requestUpdateFW(WorkData.Update.START);
            updatingFW = true;
        }
    }

}

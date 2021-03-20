package soldasim.MTG_ER_Table.View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import soldasim.MTG_ER_Table.Controller.Controller;
import soldasim.MTG_ER_Table.Controller.WorkRequest;

import java.awt.image.BufferedImage;

/**
 * View according to the MVC application model.
 * Handles user interaction and displaying the application.
 */
public class View extends Application implements Runnable {

    private static final String WINDOW_TITLE = "MTG ER Table";
    private static final Insets PADDING = new Insets(12, 12, 12, 12);
    private static final int SPACING = 12;

    private static final String SELECT_WINDOW_BUTTON_START_TEXT = "start selecting";
    private static final String SELECT_WINDOW_BUTTON_STOP_TEXT = "stop selecting";

    public static Controller controller;
    private boolean selectingWindow = false;
    private boolean updateCardImage = false;
    private Image cardImage;

    private Stage mainStage;
    private Scene testScene;
    private ImageView cardImageView;
    private ImageView windowCaptureView;
    private Label selectedWindowLabel;
    private Button selectWindowButton;

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
        controller.work.giveRequest(new WorkRequest.ViewTerminated());
    }

    public void displayCardImage(BufferedImage image) {
        Image newCardImage;
        if (image == null) {
            newCardImage = null;
        } else {
            newCardImage = ViewUtils.getImage(ViewUtils.scaleImageToFit(image, 256, 256));
        }

        cardImage = newCardImage;
        if (!updateCardImage) {
            updateCardImage = true;
            Platform.runLater(() -> {
                updateCardImage = false;
                cardImageView.setImage(cardImage);
            });
        }
    }

    public void displayWindowCapture(BufferedImage image) {
        if (image == null) {
            Platform.runLater(() -> windowCaptureView.setImage(null));
        } else {
            Image img = ViewUtils.getImage(ViewUtils.scaleImageToFit(image, 256, 144));
            Platform.runLater(() -> windowCaptureView.setImage(img));
        }
    }

    public void giveSelectedWindowTitle(String foregroundWindowTitle) {
        Platform.runLater(() -> selectedWindowLabel.setText(foregroundWindowTitle));
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

                Pane cardImagePane = new Pane(cardImageView);
                cardImagePane.setPrefSize(256, 256);

                    TextArea deckList = new TextArea();

                    Button loadButton = new Button("load");
                    loadButton.setOnAction(event -> loadButtonPressed(deckList));

                VBox deckArea = new VBox(deckList, loadButton);
                deckArea.setAlignment(Pos.CENTER);
                deckArea.setSpacing(SPACING);
                deckArea.setPrefSize(128, 256);

                    windowCaptureView = new ImageView();

                    selectedWindowLabel = new Label();

                    selectWindowButton = new Button(SELECT_WINDOW_BUTTON_START_TEXT);
                    selectWindowButton.setOnAction(event -> selectWindowButtonPressed());

                VBox windowsArea = new VBox(windowCaptureView, selectedWindowLabel, selectWindowButton);
                windowsArea.setAlignment(Pos.CENTER);
                windowsArea.setSpacing(SPACING);
                windowsArea.setPrefSize(144, 256);

            HBox content = new HBox(cardImagePane, deckArea, windowsArea);
            content.setAlignment(Pos.CENTER);
            content.setSpacing(SPACING);
            content.setPadding(PADDING);

        testScene = new Scene(content);
    }

    private void loadButtonPressed(TextArea deckList) {
        controller.work.giveRequest(new WorkRequest.DeckListUpdate(deckList.getText()));
    }

    private void selectWindowButtonPressed() {
        if (selectingWindow) {
            selectWindowButton.setText(SELECT_WINDOW_BUTTON_START_TEXT);
            controller.work.giveRequest(new WorkRequest.WindowSelecting(WorkRequest.WindowSelecting.Selecting.STOP));
            selectingWindow = false;
        } else {
            selectWindowButton.setText(SELECT_WINDOW_BUTTON_STOP_TEXT);
            controller.work.giveRequest(new WorkRequest.WindowSelecting(WorkRequest.WindowSelecting.Selecting.START));
            selectingWindow = true;
        }
    }

}

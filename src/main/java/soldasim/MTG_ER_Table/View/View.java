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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import soldasim.MTG_ER_Table.Controller.Controller;
import soldasim.MTG_ER_Table.Controller.WorkRequest;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * View according to the MVC application model.
 * Handles user interaction and displaying the application.
 */
public class View extends Application implements Runnable {

    private static final String MAIN_WINDOW_TITLE = "MTG ER Table";
    private static final String SELECT_WINDOW_WINDOW_TITLE = "Select Window to Capture";
    private static final String LOAD_DECKLIST_WINDOW_TITLE = "Load Decklist";
    private static final Insets PADDING = new Insets(12, 12, 12, 12);
    private static final int SPACING = 12;
    private static final int CARD_VIEW_SIZE = 256;
    private static final int WINDOW_VIEW_SIZE = 256;
    private static final String OPEN_SELECT_WINDOW_STAGE_BUTTON_TEXT = "Select Window";
    private static final String OPEN_LOAD_DECKLIST_STAGE_BUTTON_TEXT = "Load Decklist";
    private static final String SELECT_WINDOW_BUTTON_START_TEXT = "start selecting";
    private static final String SELECT_WINDOW_BUTTON_STOP_TEXT = "stop selecting";
    private static final String SELECT_WINDOW_DONE_BUTTON_TEXT = "Done";
    private static final String LOAD_DECKLIST_BUTTON_TEXT = "Load";

    public static Controller controller;
    private boolean selectingWindow = false;
    private boolean updateWindowView = false;
    private Image windowImage;

    private Stage mainStage;
    private Stage selectWindowStage;
    private Stage loadDecklistStage;
    private ImageView windowView;
    private ImageView cardImageView;
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

    /**
     * Returns a list of titles of all windows of this application.
     * @return an ArrayList containing window titles
     */
    public static ArrayList<String> getWindowTitles() {
        return new ArrayList<>(Arrays.asList(MAIN_WINDOW_TITLE, SELECT_WINDOW_WINDOW_TITLE, LOAD_DECKLIST_WINDOW_TITLE));
    }

    /**
     * Display the given image in the card image view.
     * @param image a BufferedImage containing a picture of a card
     */
    public void displayCardImage(BufferedImage image) {
        // TODO: display more than one card
        Image newCardImage;
        if (image == null) {
            newCardImage = null;
        } else {
            newCardImage = ViewUtils.getImage(ViewUtils.scaleImageToFit(image, CARD_VIEW_SIZE, CARD_VIEW_SIZE));
        }
        Platform.runLater(() -> cardImageView.setImage(newCardImage));
    }

    /**
     * Update selected window label.
     * @param foregroundWindowTitle a String containing the new window title
     */
    public void giveSelectedWindowTitle(String foregroundWindowTitle) {
        Platform.runLater(() -> selectedWindowLabel.setText(foregroundWindowTitle));
    }

    /**
     * Display window capture in the window view.
     * @param capture a BufferedImage containing a screenshot of an application window
     */
    public void displayWindowCapture(BufferedImage capture) {
        Image newWindowImage;
        if (capture == null) {
            newWindowImage = null;
        } else {
            newWindowImage = ViewUtils.getImage(ViewUtils.scaleImageToFit(capture, WINDOW_VIEW_SIZE, WINDOW_VIEW_SIZE));
        }

        windowImage = newWindowImage;
        if (!updateWindowView) {
            updateWindowView = true;
            Platform.runLater(() -> {
                updateWindowView = false;
                windowView.setImage(windowImage);
            });
        }
    }

    /**
     * Initialize the view.
     */
    private void initialize() {
        initSelectWindowStage(initSelectWindowScene());
        initLoadDecklistStage(initLoadDecklistScene());
        initMainStage(initMainScene());
    }

    private void initMainStage(Scene scene) {
        mainStage.setTitle(MAIN_WINDOW_TITLE);
        mainStage.setResizable(false);
        mainStage.setScene(scene);
    }

    private void initSelectWindowStage(Scene scene) {
        selectWindowStage = new Stage();
        selectWindowStage.setTitle(SELECT_WINDOW_WINDOW_TITLE);
        selectWindowStage.initModality(Modality.APPLICATION_MODAL);
        selectWindowStage.setResizable(false);
        selectWindowStage.setScene(scene);
        selectWindowStage.setOnCloseRequest(event -> controller.work.giveRequest(new WorkRequest.WindowStreaming(WorkRequest.Updating.STOP)));
    }

    private void initLoadDecklistStage(Scene scene) {
        loadDecklistStage = new Stage();
        loadDecklistStage.setTitle(LOAD_DECKLIST_WINDOW_TITLE);
        loadDecklistStage.initModality(Modality.APPLICATION_MODAL);
        loadDecklistStage.setResizable(false);
        loadDecklistStage.setScene(scene);
    }

    private Scene initMainScene() {

                cardImageView = new ImageView();

            StackPane cardArea = new StackPane(cardImageView);
            cardArea.setAlignment(Pos.CENTER);
            cardArea.setPrefSize(CARD_VIEW_SIZE, CARD_VIEW_SIZE);

                Button openSelectWindowStageButton = new Button(OPEN_SELECT_WINDOW_STAGE_BUTTON_TEXT);
                openSelectWindowStageButton.setOnAction(event -> showSelectWindowStage());

                Button openLoadDecklistStageButton = new Button(OPEN_LOAD_DECKLIST_STAGE_BUTTON_TEXT);
                openLoadDecklistStageButton.setOnAction(event -> showLoadDecklistStage());

            HBox controls = new HBox(openSelectWindowStageButton, openLoadDecklistStageButton);
            controls.setAlignment(Pos.CENTER);
            controls.setSpacing(SPACING);
            controls.setPadding(PADDING);

        VBox content = new VBox(cardArea, controls);
        content.setAlignment(Pos.CENTER);
        content.setSpacing(SPACING);
        content.setPadding(PADDING);

        return new Scene(content);
    }

    private Scene initSelectWindowScene() {

                windowView = new ImageView();

            Pane windowViewPane = new Pane(windowView);
            windowViewPane.setPrefSize(WINDOW_VIEW_SIZE, WINDOW_VIEW_SIZE);

            selectedWindowLabel = new Label();

                selectWindowButton = new Button(SELECT_WINDOW_BUTTON_START_TEXT);
                selectWindowButton.setOnAction(event -> selectWindowButtonPressed());

                Button selectDoneButton = new Button(SELECT_WINDOW_DONE_BUTTON_TEXT);
                selectDoneButton.setOnAction(event -> closeSelectWindowStage());

            HBox controls = new HBox(selectWindowButton, selectDoneButton);
            controls.setAlignment(Pos.CENTER);
            controls.setSpacing(SPACING);
            controls.setPadding(PADDING);

        VBox content = new VBox(windowViewPane, selectedWindowLabel, controls);
        content.setAlignment(Pos.CENTER);
        content.setSpacing(SPACING);
        content.setPadding(PADDING);

        return new Scene(content);
    }

    private Scene initLoadDecklistScene() {

            TextArea decklistArea = new TextArea();

            Button loadDecklistButton = new Button(LOAD_DECKLIST_BUTTON_TEXT);
            loadDecklistButton.setOnAction(event -> loadDecklistButtonPressed(decklistArea.getText()));

        VBox content = new VBox(decklistArea, loadDecklistButton);
        content.setAlignment(Pos.CENTER);
        content.setSpacing(SPACING);
        content.setPadding(PADDING);

        return new Scene(content);
    }

    /**
     * Show the window selection window.
     * Request the controller to start streaming the selected window to the view.
     */
    private void showSelectWindowStage() {
        selectWindowStage.show();
        controller.work.giveRequest(new WorkRequest.WindowStreaming(WorkRequest.Updating.START));
    }

    /**
     * Close the window selection window.
     * Request the controller to stop updating the selected window tile and streaming it to the view.
     */
    private void closeSelectWindowStage() {
        selectWindowStage.close();
        if (selectingWindow) selectWindowButtonPressed();
        controller.work.giveRequest(new WorkRequest.WindowStreaming(WorkRequest.Updating.STOP));
    }

    /**
     * Show the decklist loading window.
     */
    private void showLoadDecklistStage() {
        loadDecklistStage.show();
    }

    /**
     * Request the controller to start or stop updating the selected window.
     * Update the text of the window selection button accordingly.
     */
    private void selectWindowButtonPressed() {
        if (selectingWindow) {
            selectWindowButton.setText(SELECT_WINDOW_BUTTON_START_TEXT);
            controller.work.giveRequest(new WorkRequest.WindowSelecting(WorkRequest.Updating.STOP));
            selectingWindow = false;
        } else {
            selectWindowButton.setText(SELECT_WINDOW_BUTTON_STOP_TEXT);
            controller.work.giveRequest(new WorkRequest.WindowSelecting(WorkRequest.Updating.START));
            selectingWindow = true;
        }
    }

    /**
     * Request the controller to load a new decklist.
     * @param decklist a String containing user input from the text area
     */
    private void loadDecklistButtonPressed(String decklist) {
        controller.work.giveRequest(new WorkRequest.DeckListUpdate(decklist));
        loadDecklistStage.close();
    }

}

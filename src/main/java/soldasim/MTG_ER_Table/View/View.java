package soldasim.MTG_ER_Table.View;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import soldasim.MTG_ER_Table.Controller.Controller;
import soldasim.MTG_ER_Table.Controller.ScreenCapture;
import soldasim.MTG_ER_Table.Controller.WebcamController;

import static soldasim.MTG_ER_Table.View.ViewUtils.getImage;
import static soldasim.MTG_ER_Table.View.ViewUtils.rescaleImage;

/**
 * View according to the MVC application model.
 * Handles user interaction and displaying the application.
 */
public class View extends Application implements Runnable {

    private static final String WINDOW_TITLE = "MTG ER Table";

    private Controller controller;

    private Stage mainStage;
    private Scene testScene;

    /**
     * Save a reference to the application controller.
     * @param controller the controller
     * @see Controller
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Call launch() to launch the view.
     */
    @Override
    public void run() {
        launch();
    }

    /**
     * Is called from the launch() call. Initialize the view.
     * @param stage the main stage
     */
    @Override
    public void start(Stage stage) {
        this.mainStage = stage;
        initTestScene();
        initMainStage(testScene);
        mainStage.show();
    }

    private void initMainStage(Scene scene) {
        mainStage.setTitle(WINDOW_TITLE);
        mainStage.setResizable(false);
        mainStage.setScene(scene);
    }

    private void initTestScene() {
        ImageView imageView = new ImageView(getImage(rescaleImage(ScreenCapture.getScreen(), 853, 480)));
        HBox contentPane = new HBox(imageView);
        testScene = new Scene(contentPane);

        testScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case SPACE:
                    imageView.setImage(getImage(rescaleImage(ScreenCapture.getScreen(), 853, 480)));
            }
        });
    }

}

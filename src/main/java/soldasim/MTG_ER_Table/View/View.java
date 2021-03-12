package soldasim.MTG_ER_Table.View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import soldasim.MTG_ER_Table.Controller.Controller;

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
        testScene = new Scene(new Pane());
        //TODO
    }

}

package soldasim.MTG_ER_Table.View;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import soldasim.MTG_ER_Table.Controller.Controller;
import soldasim.MTG_ER_Table.Controller.WebcamController;

public class View extends Application implements Runnable {

    private static final String WINDOW_TITLE = "MTG ER Table";

    private Controller controller;

    private Stage mainStage;
    private Scene testScene;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        launch();
    }

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
        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(WebcamController.getImage(), null));
        Pane contentPane = new Pane(imageView);
        testScene = new Scene(contentPane);

        testScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case SPACE:
                    imageView.setImage(SwingFXUtils.toFXImage(WebcamController.getImage(), null));
            }
        });
    }

}

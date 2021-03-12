package soldasim.MTG_ER_Table.Controller;

import soldasim.MTG_ER_Table.View.View;

import java.awt.*;

/**
 * Controller according to the MVC application model.
 * Handles application flow and logic.
 */
public class Controller {

    private View view;

    /**
     * Save a reference to the application view class. Initialize.
     * @param view the application view class
     * @see View
     */
    public Controller(View view) {
        this.view = view;
        view.setController(this);

        try {
            ScreenCapture.init();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        initViewThread();
    }

    private void initViewThread() {
        Thread viewThread = new Thread(view);
        viewThread.start();
    }

}

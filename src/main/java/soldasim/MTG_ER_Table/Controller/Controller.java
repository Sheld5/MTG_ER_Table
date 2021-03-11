package soldasim.MTG_ER_Table.Controller;

import soldasim.MTG_ER_Table.View.View;

public class Controller {

    private View view;

    public Controller(View view) {
        this.view = view;
        view.setController(this);

        WebcamController.init();
        initViewThread();
    }

    private void initViewThread() {
        Thread viewThread = new Thread(view);
        viewThread.start();
    }

}

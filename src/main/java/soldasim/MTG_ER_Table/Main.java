package soldasim.MTG_ER_Table;

import soldasim.MTG_ER_Table.Controller.Controller;
import soldasim.MTG_ER_Table.View.View;

public class Main {

    public static void main(String[] args) {

        Controller controller = new Controller();
        View.controller = controller;
        View view = new View();
        Thread viewThread = new Thread(view);
        viewThread.start();
        controller.start();

    }

}

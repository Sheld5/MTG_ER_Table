package soldasim.MTG_ER_Table;

import soldasim.MTG_ER_Table.Controller.Controller;
import soldasim.MTG_ER_Table.View.View;

public class Main {

    public static void main(String[] args) {

        View view = new View();
        Controller controller = new Controller(view);

    }

}

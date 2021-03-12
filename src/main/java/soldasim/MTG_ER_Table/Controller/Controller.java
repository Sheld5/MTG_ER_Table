package soldasim.MTG_ER_Table.Controller;

import soldasim.MTG_ER_Table.View.View;

/**
 * Controller according to the MVC application model.
 * Handles application flow and logic.
 */
public class Controller {

    private View view;

    private final Object deckListLock = new Object();
    private String deckList;

    /**
     * Initialize.
     */
    public Controller() {
        deckList = "";
    }

    /**
     * Start the program.
     */
    public void start() {
        waitForView();
        startWorkLoop();
    }

    /**
     * Wait until view gives reference for itself and notifies.
     * @see View
     */
    private void waitForView() {
        synchronized (this) {
            while (view == null) {
                try {
                    this.wait();
                } catch (InterruptedException ignored) {}
            }
        }
    }

    /**
     * Give the controller a reference to the view and notify it.
     * @param view the application view instance
     * @see View
     */
    public void setView(View view) {
        synchronized (this) {
            this.view = view;
            this.notify();
        }
    }

    private void startWorkLoop() {
        while (true) {

            synchronized (deckListLock) {
                while (deckList.isEmpty()) {
                    try {
                        deckListLock.wait();
                    } catch (InterruptedException ignored) {}
                }
                view.giveCardList(TextParser.parseDeckList(deckList));
                deckList = "";
            }

        }
    }

    public void requestParseDeckList(String deckList) {
        synchronized (deckListLock) {
            this.deckList = deckList;
            deckListLock.notify();
        }
    }

}

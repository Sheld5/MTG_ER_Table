package soldasim.MTG_ER_Table.Controller;

public class Request {

    public interface Interface {
        void give(WorkData work);
    }

    /**
     * Notify the controller that the view has terminated.
     */
    public static class ViewTerminated implements Interface {
        @Override
        public void give(WorkData work) {
            work.viewTerminated = true;
        }
    }

    /**
     * Give the controller new deck list.
     */
    public static class DeckListUpdate implements Interface {
        private final String deckList;
        public DeckListUpdate(String deckList) {this.deckList = deckList;}
        @Override
        public void give(WorkData work) {
            work.deckList = deckList;
        }
    }

    /**
     * Request the controller to start or stop updating which window is selected.
     */
    public static class WindowSelecting implements Interface {
        public enum Selecting {
            START,
            STOP,
            NOTHING
        }
        private final Selecting selecting;
        public WindowSelecting(Selecting selecting) {
            this.selecting = selecting;
        }
        @Override
        public void give(WorkData work) {
            work.windowSelecting = selecting;
        }
    }

    public static class WebcamImage implements Interface {
        @Override
        public void give(WorkData work) {
            work.webcamImage = true;
        }
    }

}

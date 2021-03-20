package soldasim.MTG_ER_Table.Controller;

/**
 * This class includes different work request that can be given to the controller.
 */
public class WorkRequest {

    public enum Updating {
        START,
        STOP,
        NOTHING
    }

    /**
     * Each work request implements this interface.
     */
    public interface Request {
        void receive(WorkData work);
    }

    /**
     * Notify the controller that the view has terminated.
     */
    public static class ViewTerminated implements Request {
        @Override
        public void receive(WorkData work) {
            work.viewTerminated = true;
        }
    }

    /**
     * Give the controller new deck list.
     */
    public static class DeckListUpdate implements Request {
        private final String deckList;
        public DeckListUpdate(String deckList) {this.deckList = deckList;}
        @Override
        public void receive(WorkData work) {
            work.deckList = deckList;
        }
    }

    /**
     * Request the controller to start or stop updating which window is selected.
     */
    public static class WindowSelecting implements Request {
        private final Updating u;
        public WindowSelecting(Updating u) {
            this.u = u;
        }
        @Override
        public void receive(WorkData work) {
            work.windowSelecting = u;
        }
    }

    /**
     * Request the controller to start or stop streaming the selected window to the view.
     */
    public static class WindowStreaming implements Request {
        private final Updating u;
        public WindowStreaming(Updating u) {
            this.u = u;
        }
        @Override
        public void receive(WorkData work) {
            work.windowStreaming = u;
        }
    }

}

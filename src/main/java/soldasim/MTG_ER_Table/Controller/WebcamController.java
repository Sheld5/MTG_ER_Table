package soldasim.MTG_ER_Table.Controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import soldasim.MTG_ER_Table.View.View;

import java.awt.image.BufferedImage;

/**
 * Static class that handles the webcam.
 * Currently unused.
 */
public class WebcamController {

    private static final int WEBCAM_STREAMER_REFRESH_RATE = 60;

    private static Webcam webcam;
    private static WebcamStreamer webcamStreamer;

    // stores whether the webcam should be open when WebcamStreamer is not running
    // not whether the webcam is actually open
    private static boolean open;

    public static void init() {
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        open = false;
    }

    public static void openWebcam() {
        open = true;
        if (!webcam.isOpen()) webcam.open();
    }

    public static void closeWebcam() {
        open = false;
        if (webcam.isOpen() && webcamStreamer == null) webcam.close();
    }

    /**
     * Get an image from the webcam.
     * @return an image from the webcam
     */
    public static BufferedImage getImage() {
        if (!webcam.isOpen()) webcam.open();
        BufferedImage image = webcam.getImage();
        if (!open && webcamStreamer == null) webcam.close();
        return image;
    }

    /**
     * Start a new thread with WebcamStreamer that continuously updates the webcam view in the view.
     * Only the thread created from the last call of this function will be running.
     * @param view the view to be continuously updated
     */
    static void startStreamingWebcam(View view) {
        if (webcam == null) return;
        if (webcamStreamer != null) {
            webcamStreamer.run = false;
        }
        if (!webcam.isOpen()) webcam.open();
        webcamStreamer = new WebcamStreamer(view);
        Thread streamerThread = new Thread(webcamStreamer);
        streamerThread.start();
    }

    /**
     * Stop the WebcamStreamer.
     */
    static void stopStreamingWebcam() {
        if (webcamStreamer == null) return;
        webcamStreamer.run = false;
        webcamStreamer = null;
        if (!open) webcam.close();
    }

    /**
     * Runnable class which continuously updates the webcam view in the view with images from the webcam.
     */
    private static class WebcamStreamer implements Runnable {

        private final View view;
        private boolean run;

        private WebcamStreamer(View view) {
            this.view = view;
            run = true;
        }

        @Override
        public void run() {
            while (run) {
                long startTime = System.currentTimeMillis();

                // TODO: send webcam picture

                long waitTime = (long)(1000 / WEBCAM_STREAMER_REFRESH_RATE) - (System.currentTimeMillis() - startTime);
                if (waitTime > 0) {
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ignored) {}
                }
            }
        }

    }

}

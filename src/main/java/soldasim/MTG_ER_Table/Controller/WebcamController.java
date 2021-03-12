package soldasim.MTG_ER_Table.Controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import java.awt.image.BufferedImage;

/**
 * Static class that handles the webcam.
 */
public class WebcamController {

    private static Webcam webcam;

    /**
     * Initialize the webcam and turn it on.
     */
    public static void init() {
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();
    }

    /**
     * Get an image from the webcam.
     * @return an image from the webcam
     */
    public static BufferedImage getImage() {
        return webcam.getImage();
    }

}

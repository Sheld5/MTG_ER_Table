package soldasim.MTG_ER_Table.Controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import java.awt.image.BufferedImage;

public class WebcamController {

    private static Webcam webcam;

    public static void init() {
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();
    }

    public static BufferedImage getImage() {
        return webcam.getImage();
    }

}

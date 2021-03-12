package soldasim.MTG_ER_Table.View;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class ViewUtils {

    public static Image getImage(BufferedImage img) {
        return SwingFXUtils.toFXImage(img, null);
    }

}

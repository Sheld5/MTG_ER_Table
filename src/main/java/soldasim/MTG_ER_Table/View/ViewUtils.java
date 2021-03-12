package soldasim.MTG_ER_Table.View;

import javafx.embed.swing.SwingFXUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Static class with some utilities for view and work with images.
 */
public class ViewUtils {

    /**
     * Convert java.awt.image.BufferedImage to javafx.scene.image.Image
     * @param img instance of BufferedImage
     * @return the given BufferedImage converted to Image
     */
    public static javafx.scene.image.Image getImage(BufferedImage img) {
        return SwingFXUtils.toFXImage(img, null);
    }

    /**
     * Rescale a BufferedImage.
     * @param img instance of BufferedImage
     * @param width new width
     * @param height new height
     * @return the given BufferedImage rescaled to a different size
     */
    public static BufferedImage rescaleImage(BufferedImage img, int width, int height) {
        Image tmp = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        BufferedImage nimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = nimg.createGraphics();
        g.drawImage(tmp, 0, 0, null);
        g.dispose();
        return nimg;
    }

}

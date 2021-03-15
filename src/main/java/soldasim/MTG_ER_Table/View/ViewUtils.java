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
     * Rescale an image to given dimensions.
     * @param image the image to be rescaled as a BufferedImage
     * @param width new width
     * @param height new height
     * @return the rescaled image as a BufferedImage
     */
    public static BufferedImage scaleImage(BufferedImage image, int width, int height) {
        Image tmp = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(tmp, 0, 0, null);
        g.dispose();
        return newImage;
    }

    /**
     * Rescale an image to fit given dimensions while maintaining the original aspect ratio.
     * @param image the image to be rescaled as a BufferedImage
     * @param width maximum width of the rescaled image
     * @param height maximum height of the rescaled image
     * @return the rescaled image as a BufferedImage
     */
    public static BufferedImage scaleImageToFit(BufferedImage image, int width, int height) {
        int ogWidth = image.getWidth();
        int ogHeight = image.getHeight();

        if (ogHeight == height && ogWidth <= width) return image;
        if (ogWidth == width && ogHeight <= height) return image;

        double heightRatio = (double)height / ogHeight;
        double widthRatio = (double)width / ogWidth;

        if (fitByHeight(heightRatio, widthRatio)) {
            return scaleImage(image, (int)(ogWidth * heightRatio), (int)(ogHeight * heightRatio));
        } else {
            return scaleImage(image, (int)(ogWidth * widthRatio), (int)(ogHeight * widthRatio));
        }
    }

    private static boolean fitByHeight(double heightRatio, double widthRatio) {
        if (heightRatio > 1 && widthRatio <= 1) return true;
        if (widthRatio > 1 && heightRatio <= 1) return false;
        return heightRatio < widthRatio;
    }

}

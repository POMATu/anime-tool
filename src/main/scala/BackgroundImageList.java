import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class BackgroundImageList<T> extends JList<T> {
    private BufferedImage background;

    private ListModel<T> model;

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public BackgroundImageList(ListModel<T> model, Image background) {
        super(model);
        //try {
            this.background = toBufferedImage(background);//ImageIO.read(new File("Z:\\video.png"));
        this.model = model;

        //} catch (IOException ex) {
        //    ex.printStackTrace();
        //}
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        setForeground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (background != null && this.model.getSize() <=0) {
            Graphics2D g2d = (Graphics2D) g.create();
            int x = getWidth()/3; //getWidth() - background.getWidth();
            int y = getHeight()/3; // getHeight() - background.getHeight();
            float ratio = ((float)background.getHeight())/((float)background.getWidth());
            //System.out.println(ratio);
            g2d.drawImage(background, x, y, getWidth()/3,  Math.round((getWidth()/3)*ratio), this);
            g2d.dispose();
        }
        super.paintComponent(g);
    }

}
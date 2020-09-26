package buffers;

import transforms.Col;

import java.awt.image.BufferedImage;

/**
 * Created by Intel on 25.02.2018.
 */
public class ImageBuffer extends BufferedImage implements Buffer<Col> {

    public ImageBuffer(int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void setPixel(int x, int y, Col pixel) {
        setRGB(x, y, pixel.getRGB());
    }

    @Override
    public Col getPixel(int x, int y) {
        return new Col(getRGB(x, y));
    }

    @Override
    public void clear(Col pixel) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                setRGB(x, y, pixel.getRGB());
            }
        }
    }
}

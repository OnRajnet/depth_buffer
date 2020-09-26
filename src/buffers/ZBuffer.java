package buffers;

import transforms.Col;

/**
 * Created by Intel on 25.02.2018.
 */
public class ZBuffer {

    private ImageBuffer imageBuffer;
    private DepthBuffer depthBuffer;

    public ZBuffer(int width, int height) {
        imageBuffer = new ImageBuffer(width, height);
        depthBuffer = new DepthBuffer(width, height);
    }

    public void drawPixel(int x, int y, double z, Col pixelCol) {
        if (z < 1 && z > 0 && x < getWidth() && x > 0 && y < getHeight() && y > 0) {
            if (depthBuffer.getPixel(x, y) > z) {
                depthBuffer.setPixel(x, y, z);
                imageBuffer.setPixel(x, y, pixelCol);
            }
        }
    }

    public void clear(Col bg) {
        depthBuffer.clear(1.0);
        imageBuffer.clear(bg);
    }

    public ImageBuffer getImage() {
        return imageBuffer;
    }

    public int getWidth() {
        return imageBuffer.getWidth();
    }

    public int getHeight() {
        return imageBuffer.getHeight();
    }

}

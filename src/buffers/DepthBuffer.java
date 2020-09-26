package buffers;

/**
 * Created by Intel on 25.02.2018.
 */
public class DepthBuffer implements Buffer<Double> {

    private double[][] depth;

    public DepthBuffer(int width, int height) {
        depth = new double[width][height];
    }

    @Override
    public void setPixel(int x, int y, Double pixel) {
        depth[x][y] = pixel;
    }

    @Override
    public Double getPixel(int x, int y) {
        return depth[x][y];
    }

    @Override
    public void clear(Double pixel) {
        for (int x = 0; x < depth.length; x++) {
            for (int y = 0; y < depth[x].length; y++) {
                setPixel(x, y, pixel);
            }
        }
    }

    @Override
    public int getWidth() {
        return depth.length;
    }

    @Override
    public int getHeight() {
        return depth[0].length;
    }
}

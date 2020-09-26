package buffers;

/**
 * Created by Intel on 25.02.2018.
 */
public interface Buffer<T> {

    void setPixel(int x, int y, T pixel);
    T getPixel(int x, int y);
    void clear(T pixel);
    int getWidth();
    int getHeight();

}

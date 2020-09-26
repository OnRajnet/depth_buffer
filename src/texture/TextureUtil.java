package texture;

import transforms.Col;
import transforms.Vec2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Intel on 22.03.2018.
 */
public class TextureUtil {

    private static Map<String, BufferedImage> textures = new HashMap<>();

    public static Col getPixel(String textureFileName, Vec2D coordinations) {
        if (textures.containsKey(textureFileName)) {
            BufferedImage texture = textures.get(textureFileName);
            return getPixel(texture, coordinations);
        } else {
            URL textureFileUrl = Thread.currentThread().getContextClassLoader().getResource(textureFileName);
            if (textureFileUrl != null) {
                BufferedImage texture;
                try {
                    texture = ImageIO.read(textureFileUrl);
                } catch (IOException e) {
                    throw new RuntimeException("There was an error loading " + textureFileName + " texture.");
                }
                textures.put(textureFileName, texture);
                return getPixel(texture, coordinations);
            } else {
                throw new RuntimeException("Texture " + textureFileName + " was not found in resources.");
            }
        }
    }

    private static Col getPixel(BufferedImage texture, Vec2D coordiantions) {
        try {
            return new Col(texture.getRGB((int)(texture.getWidth() * coordiantions.getX()), (int)(texture.getHeight() * coordiantions.getY())));
        } catch (Exception e) {
            return new Col(0,0,0);
        }
    }
}

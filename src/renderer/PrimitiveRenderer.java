package renderer;

import buffers.ZBuffer;
import texture.TextureUtil;
import topology.Vertex;
import transforms.Col;
import transforms.Point3D;

/**
 * Created by Intel on 25.02.2018.
 */
@SuppressWarnings("Duplicates")
public class PrimitiveRenderer {

    private ZBuffer zBuffer;

    public PrimitiveRenderer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    public void renderPoint(Vertex v) {
        Vertex dehomogV = v.dehomog();

        if ((dehomogV.getX() > 1.0) ||
                (dehomogV.getX() < -1.0) ||
                (dehomogV.getY() > 1.0) ||
                (dehomogV.getY() < -1.0) ||
                (dehomogV.getZ() > 1.0) ||
                (dehomogV.getZ() < 0.0)) {
            return;
        }

        int x = (int) ((zBuffer.getWidth() - 1) * (dehomogV.getX() + 1)) / 2;
        int y = (int) ((zBuffer.getHeight() - 1) * (1 - dehomogV.getY()) / 2);

        zBuffer.drawPixel(x, y, dehomogV.getZ(), dehomogV.getCol());

    }

    public void renderLine(Vertex v1, Vertex v2) {
        Vertex dehomogV1 = v1.dehomog();
        Vertex dehomogV2 = v2.dehomog();

        if ((Math.min(dehomogV1.getX(), dehomogV2.getX()) > 1.0) ||
                (Math.max(dehomogV1.getX(), dehomogV2.getX()) < -1.0) ||
                (Math.min(dehomogV1.getY(), dehomogV2.getY()) > 1.0) ||
                (Math.max(dehomogV1.getY(), dehomogV2.getY()) < -1.0) ||
                (Math.min(dehomogV1.getZ(), dehomogV2.getZ()) > 1.0) ||
                (Math.max(dehomogV1.getZ(), dehomogV2.getZ()) < 0.0)) {
            return;
        }

        double x1 = (int) ((zBuffer.getWidth() - 1) * (dehomogV1.getX() + 1)) / 2;
        double y1 = (int) ((zBuffer.getHeight() - 1) * (1 - dehomogV1.getY()) / 2);
        double x2 = (int) ((zBuffer.getWidth() - 1) * (dehomogV2.getX() + 1)) / 2;
        double y2 = (int) ((zBuffer.getHeight() - 1) * (1 - dehomogV2.getY()) / 2);

        double temp1, temp2;
        Vertex temp3;

        double dX = Math.abs(x1 - x2);
        double dY = Math.abs(y1 - y2);

        if (dX > dY) {
            if (x1 > x2) {
                temp1 = x1;
                temp2 = y1;
                temp3 = dehomogV1;
                x1 = x2;
                y1 = y2;
                dehomogV1 = dehomogV2;
                x2 = temp1;
                y2 = temp2;
                dehomogV2 = temp3;
            }
            for (int x = (int) x1; x <= x2; x++) {
                double t = (x - x1) / (x2 - x1);
                double y = ((1 - t) * y1) + (t * y2);
                double z = ((1 - t) * dehomogV1.getZ()) + (t * dehomogV2.getZ());
                Col col = dehomogV1.getCol().mul(1 - t).add(dehomogV2.getCol().mul(t));
                zBuffer.drawPixel(x, (int) y, z, col);
            }
        } else {
            if (y1 > y2) {
                temp1 = x1;
                temp2 = y1;
                temp3 = dehomogV1;
                x1 = x2;
                y1 = y2;
                dehomogV1 = dehomogV2;
                x2 = temp1;
                y2 = temp2;
                dehomogV2 = temp3;
            }
            for (int y = (int) y1; y <= y2; y++) {
                double t = (y - y1) / (y2 - y1);
                double x = ((1 - t) * x1) + (t * x2);
                double z = ((1 - t) * dehomogV1.getZ()) + (t * dehomogV2.getZ());
                Col col = dehomogV1.getCol().mul(1 - t).add(dehomogV2.getCol().mul(t));
                zBuffer.drawPixel((int) x, y, z, col);
            }
        }
    }

    public void renderTriangle(Vertex v1, Vertex v2, Vertex v3) {
        Vertex dehomogV1 = v1.dehomog();
        Vertex dehomogV2 = v2.dehomog();
        Vertex dehomogV3 = v3.dehomog();

        if ((Math.min(Math.min(dehomogV1.getX(), dehomogV2.getX()), dehomogV3.getX()) > 1.0) ||
                (Math.max(Math.max(dehomogV1.getX(), dehomogV2.getX()), dehomogV3.getX()) < -1.0) ||
                (Math.min(Math.min(dehomogV1.getY(), dehomogV2.getY()), dehomogV3.getY()) > 1.0) ||
                (Math.max(Math.max(dehomogV1.getY(), dehomogV2.getY()), dehomogV3.getY()) < -1.0) ||
                (Math.min(Math.min(dehomogV1.getZ(), dehomogV2.getZ()), dehomogV3.getZ()) > 1.0) ||
                (Math.max(Math.max(dehomogV1.getZ(), dehomogV2.getZ()), dehomogV3.getZ()) < 0.0)) {
            return;
        }
        
        Vertex windowV1, windowV2, windowV3;
        boolean useTexture;
        
        if (v1.getCol() != null && v2.getCol() != null && v3.getCol() != null) {
            useTexture = false;
            windowV1 = new Vertex(
                    new Point3D((int) ((zBuffer.getWidth() - 1) * (dehomogV1.getX() + 1)) / 2, (int) ((zBuffer.getHeight() - 1) * (1 - dehomogV1.getY()) / 2), dehomogV1.getZ()),
                    dehomogV1.getCol()
            );
            windowV2 = new Vertex(
                    new Point3D((int) ((zBuffer.getWidth() - 1) * (dehomogV2.getX() + 1)) / 2, (int) ((zBuffer.getHeight() - 1) * (1 - dehomogV2.getY()) / 2), dehomogV2.getZ()),
                    dehomogV2.getCol()
            );
            windowV3 = new Vertex(
                    new Point3D((int) ((zBuffer.getWidth() - 1) * (dehomogV3.getX() + 1)) / 2, (int) ((zBuffer.getHeight() - 1) * (1 - dehomogV3.getY()) / 2), dehomogV3.getZ()),
                    dehomogV3.getCol()
            );
        } else if (v1.getTexCoords() != null && v2.getTexCoords() != null && v3.getTexCoords() != null) {
            useTexture = true;
            windowV1 = new Vertex(
                    new Point3D((int) ((zBuffer.getWidth() - 1) * (dehomogV1.getX() + 1)) / 2, (int) ((zBuffer.getHeight() - 1) * (1 - dehomogV1.getY()) / 2), dehomogV1.getZ()),
                    dehomogV1.getTexCoords()
            );
            windowV2 = new Vertex(
                    new Point3D((int) ((zBuffer.getWidth() - 1) * (dehomogV2.getX() + 1)) / 2, (int) ((zBuffer.getHeight() - 1) * (1 - dehomogV2.getY()) / 2), dehomogV2.getZ()),
                    dehomogV2.getTexCoords()
            );
            windowV3 = new Vertex(
                    new Point3D((int) ((zBuffer.getWidth() - 1) * (dehomogV3.getX() + 1)) / 2, (int) ((zBuffer.getHeight() - 1) * (1 - dehomogV3.getY()) / 2), dehomogV3.getZ()),
                    dehomogV3.getTexCoords()
            );
        } else {
            throw new RuntimeException("Inconsistent color/texture");
        }
        

        Vertex temp, v1I, v2I, v3I;
        double t;

        if (windowV1.getY() > windowV2.getY()) {
            temp = windowV1;
            windowV1 = windowV2;
            windowV2 = temp;
        }
        if (windowV2.getY() > windowV3.getY()) {
            temp = windowV2;
            windowV2 = windowV3;
            windowV3 = temp;
        }
        if (windowV1.getY() > windowV2.getY()) {
            temp = windowV1;
            windowV1 = windowV2;
            windowV2 = temp;
        }

        for (int y = (int)windowV1.getY() + 1; y <= windowV2.getY(); y++) {
            t = (y - windowV1.getY()) / (windowV2.getY() - windowV1.getY());
            v1I = windowV1.mul(1 - t).add(windowV2.mul(t));
            t = (y - windowV1.getY()) / (windowV3.getY() - windowV1.getY());
            v2I = windowV1.mul(1 - t).add(windowV3.mul(t));
            if (v1I.getX() > v2I.getX()) {
                temp = v1I;
                v1I = v2I;
                v2I = temp;
            }
            for (int x = (int) v1I.getX() + 1; x <= v2I.getX(); x++) {
                t = (x - v1I.getX()) / (v2I.getX() - v1I.getX());
                v3I = v1I.mul(1 - t).add(v2I.mul(t));
                if (!useTexture) {
                    zBuffer.drawPixel(x, y, v3I.getZ(), v3I.getCol());
                } else {
                    zBuffer.drawPixel(x, y, v3I.getZ(), TextureUtil.getPixel("squares.jpg", v3I.getTexCoords()));
                }
            }
        }
        for (int y = (int) windowV2.getY() + 1; y <= windowV3.getY(); y++) {
            t = (y - windowV2.getY()) / (windowV3.getY() - windowV2.getY());
            v1I = windowV2.mul(1 - t).add(windowV3.mul(t));
            t = (y - windowV1.getY()) / (windowV3.getY() - windowV1.getY());
            v2I = windowV1.mul(1 - t).add(windowV3.mul(t));
            if (v1I.getX() > v2I.getX()) {
                temp = v1I;
                v1I = v2I;
                v2I = temp;
            }
            for (int x = (int) v1I.getX() + 1; x <= v2I.getX(); x++) {
                t = (x - v1I.getX()) / (v2I.getX() - v1I.getX());
                v3I = v1I.mul(1 - t).add(v2I.mul(t));
                if (!useTexture) {
                    zBuffer.drawPixel(x, y, v3I.getZ(), v3I.getCol());
                } else {
                    zBuffer.drawPixel(x, y, v3I.getZ(), TextureUtil.getPixel("squares.jpg", v3I.getTexCoords()));
                }
            }
        }
    }
}

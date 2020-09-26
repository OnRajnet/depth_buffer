package topology;

import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec2D;

/**
 * Created by Intel on 25.02.2018.
 */
public class Vertex {

    private final Point3D pos;
    private Col col;
    private Vec2D texCoords;

    public Vertex(Point3D pos, Col col) {
        this.pos = pos;
        this.col = col;
    }
    
    public Vertex(Point3D pos, Vec2D texCoords) {
        this.pos = pos;
        this.texCoords = texCoords;
    }

    public Vertex transform(Mat4 matrix) {
        Point3D newPos = pos.mul(matrix);
        if (col != null) {
            return new Vertex(newPos, col);
        }
        if (texCoords != null) {
            return new Vertex(newPos, texCoords);
        }
        // should not happen
        return null;
    }

    public Vertex dehomog() {
        // w minimal value is 0.1, ignore zero division
        Point3D newPos = pos.mul(1 / getW());
        if (col != null) {
            return new Vertex(newPos, col);
        }
        if (texCoords != null) {
            return new Vertex(newPos, texCoords);
        }
        // should not happen
        return null;
    }

    public Vertex mul(double t) {
        Point3D newPos = pos.mul(t);
        if (col != null) {
            Col newCol = col.mul(t);
            return new Vertex(newPos, newCol);
        }
        if (texCoords != null) {
            Vec2D newCoords = texCoords.mul(t);
            return new Vertex(newPos, newCoords);
        }
        // should not happen
        return null;
    }

    public Vertex add(Vertex v) {
        Point3D newPos = pos.add(v.pos);
        if (col != null) {
            Col newCol = col.add(v.col);
            return new Vertex(newPos, newCol);
        }
        if (texCoords != null) {
            Vec2D newCoords = texCoords.add(v.texCoords);
            return new Vertex(newPos, newCoords);
        }
        // should not happen
        return null;
    }

    public double getX() {
        return pos.getX();
    }

    public double getY() {
        return pos.getY();
    }

    public double getZ() {
        return pos.getZ();
    }

    public double getW() {
        return pos.getW();
    }

    public Col getCol() {
        return col;
    }

    public Vec2D getTexCoords() {
        return texCoords;
    }
}

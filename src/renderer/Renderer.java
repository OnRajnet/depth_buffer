package renderer;

import buffers.ZBuffer;
import input.Input;
import topology.Vertex;
import transforms.Mat4;

/**
 * Created by Intel on 25.02.2018.
 */
public class Renderer {

    private Input input;
    private Mat4 model, view, projection;
    private PrimitiveRenderer primitiveRenderer;
    private double zNear;
    private boolean renderWireFrame = false;

    public Renderer(Input input, Mat4 model, Mat4 view, Mat4 projection, ZBuffer zBuffer, float zNear) {
        this.input = input;
        this.model = model;
        this.view = view;
        this.projection = projection;
        this.primitiveRenderer = new PrimitiveRenderer(zBuffer);
        this.zNear = zNear;
    }

    public void render() {

        input.getParts().forEach( part -> {

            Mat4 finalM = part.isTransfomable() ? model.mul(view.mul(projection)) : view.mul(projection);

            switch (part.getType()) {
                case POINT:
                    for (int i = part.getStartIndex(); i < part.getStartIndex() + part.getCount(); i++) {
                        clipPoint(input.getVertexBuffer().get(input.getIndexBuffer().get(i)).transform(finalM));
                    }
                    break;
                case LINE:
                    for (int i = part.getStartIndex(); i < part.getStartIndex() + part.getCount() * 2; i+=2) {
                        clipLine(
                                input.getVertexBuffer().get(input.getIndexBuffer().get(i)).transform(finalM),
                                input.getVertexBuffer().get(input.getIndexBuffer().get(i + 1)).transform(finalM)
                        );
                    }
                    break;
                case TRIANGLE:
                    for (int i = part.getStartIndex(); i < part.getStartIndex() + part.getCount() * 3; i+=3) {
                        clipTriangle(
                                input.getVertexBuffer().get(input.getIndexBuffer().get(i)).transform(finalM),
                                input.getVertexBuffer().get(input.getIndexBuffer().get(i + 1)).transform(finalM),
                                input.getVertexBuffer().get(input.getIndexBuffer().get(i + 2)).transform(finalM)
                        );
                    }
                    break;
            }
        });
    }

    private void clipPoint(Vertex v) {
        if (v.getW() > zNear) {
            primitiveRenderer.renderPoint(v);
        }
    }

    private void clipLine(Vertex v1, Vertex v2) {
        Vertex temp;
        if (v1.getW() < v2.getW()) {
            temp = v1;
            v1 = v2;
            v2 = temp;
        }
        if (v1.getW() < zNear) {
            return;
        }
        if (v2.getW() < zNear) {
            double t = (zNear - v1.getW()) / (v2.getW() - v1.getW());
            v2 = v1.mul(1 - t).add(v2.mul(t));
        }
        primitiveRenderer.renderLine(v1, v2);
    }

    private void clipTriangle(Vertex v1, Vertex v2, Vertex v3) {
        Vertex temp;
        double t;
        if (v1.getW() < v2.getW()) {
            temp = v1;
            v1 = v2;
            v2 = temp;
        }
        if (v2.getW() < v3.getW()) {
            temp = v2;
            v2 = v3;
            v3 = temp;
        }
        if (v1.getW() < v2.getW()) {
            temp = v1;
            v1 = v2;
            v2 = temp;
        }
        if (v1.getW() < zNear) {
            return;
        }
        if (v2.getW() < zNear) {
            t = (zNear - v1.getW()) / (v2.getW() - v1.getW());
            v2 = v1.mul(1 - t).add(v2.mul(t));
            t = (zNear - v1.getW()) / (v3.getW() - v1.getW());
            v3 = v1.mul(1 - t).add(v3.mul(t));
        }
        if (v3.getW() < zNear) {
            t = (zNear - v2.getW()) / (v3.getW() - v2.getW());
            Vertex v23 = v2.mul(1 - t).add(v3.mul(t));
            t = (zNear - v1.getW()) / (v3.getW() - v1.getW());
            v3 = v1.mul(1 - t).add(v3.mul(t));
            if (renderWireFrame) {
                primitiveRenderer.renderLine(v1, v2);
                primitiveRenderer.renderLine(v1, v23);
                primitiveRenderer.renderLine(v2, v23);

                primitiveRenderer.renderLine(v1, v23);
                primitiveRenderer.renderLine(v1, v3);
                primitiveRenderer.renderLine(v23, v3);
            } else {
                primitiveRenderer.renderTriangle(v1, v2, v23);
                primitiveRenderer.renderTriangle(v1, v23, v3);
            }
            return;
        }
        if (renderWireFrame) {
            primitiveRenderer.renderLine(v1, v2);
            primitiveRenderer.renderLine(v1, v3);
            primitiveRenderer.renderLine(v2, v3);
        } else {
            primitiveRenderer.renderTriangle(v1, v2, v3);
        }
    }

    public void switchWireFrameDrawing() {
        renderWireFrame = !renderWireFrame;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }
}

package input;

import topology.Part;
import topology.PartType;
import topology.Vertex;
import transforms.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Intel on 25.02.2018.
 */
public class Input {

    private List<Vertex> vertexBuffer = new ArrayList<>();
    private List<Integer> indexBuffer = new ArrayList<>();
    private List<Part> parts = new ArrayList<>();
    private int startVertex = 0;
    private int startIndex = 0;

    public Input() {
        populateInput();
    }

    public List<Vertex> getVertexBuffer() {
        return vertexBuffer;
    }

    public List<Integer> getIndexBuffer() {
        return indexBuffer;
    }

    public List<Part> getParts() {
        return parts;
    }

    private void populateInput() {
        //axis
//        addPart(PartType.LINE, false,
//                new Vertex(new Point3D(0, 0, 0), new Col(0xff0000)),
//                new Vertex(new Point3D(1, 0, 0), new Col(0xff0000)),
//                new Vertex(new Point3D(0, 0, 0), new Col(0x00ff00)),
//                new Vertex(new Point3D(0, 1, 0), new Col(0x00ff00)),
//                new Vertex(new Point3D(0, 0, 0), new Col(0x0000ff)),
//                new Vertex(new Point3D(0, 0, 1), new Col(0x0000ff))
//        );

       addPart(PartType.TRIANGLE_STRIP, true,
               new Vertex(new Point3D(0,0,0), new Vec2D(0,0)),
               new Vertex(new Point3D(0,1,0), new Vec2D(0,1)),
               new Vertex(new Point3D(1,0,0), new Vec2D(1,0)),
               new Vertex(new Point3D(1,1,0), new Vec2D(1,1))

       );

       addPart(PartType.TRIANGLE_STRIP, true,
               new Vertex(new Point3D(9,4,3), new Col(0xff0000)),
               new Vertex(new Point3D(9,6,3), new Col(0xff0000)),
               new Vertex(new Point3D(9,4,1), new Col(0x00ff00)),
               new Vertex(new Point3D(9,6,1), new Col(0x00ff00)),
               new Vertex(new Point3D(11,6,1), new Col(0x0000ff)),
               new Vertex(new Point3D(9,6,3), new Col(0x0000ff)),
               new Vertex(new Point3D(11,6,3), new Col(0xff0000)),
               new Vertex(new Point3D(9,4,3), new Col(0xff0000)),
               new Vertex(new Point3D(11,4,3), new Col(0x00ff00)),
               new Vertex(new Point3D(9,4,1), new Col(0x00ff00)),
               new Vertex(new Point3D(11,4,1), new Col(0x0000ff)),
               new Vertex(new Point3D(11,6,1), new Col(0x0000ff)),
               new Vertex(new Point3D(11,4,3), new Col(0xff0000)),
               new Vertex(new Point3D(11,6,3), new Col(0xff0000))

       );

       addCurve(true, new Col(0x00ff00), new Col(0xff00ff),
               new Point3D(4,21,1),
               new Point3D(5,3,0),
               new Point3D(3,16,2),
               new Point3D(6,12,1)

       );

      addCurvedSurface(true, new Col(0xff00ff), new Col(0x00ff00),
              new Point3D(6,8,1),
              new Point3D(7,8,2),
              new Point3D(8,8,2),
              new Point3D(10,8,2),
              new Point3D(6,9,2),
              new Point3D(7,9,4),
              new Point3D(9,9,2),
              new Point3D(10,9,2),
              new Point3D(6,13,2),
              new Point3D(7,13,2),
              new Point3D(8,13,2),
              new Point3D(10,13,2),
              new Point3D(6,14,2),
              new Point3D(7,14,3),
              new Point3D(8,14,4),
              new Point3D(10,14,2)
      );


        // add more parts
    }

    private void addPart(PartType partType, boolean transformable, Vertex... partVertices) {
        int count = 0;
        switch (partType) {
            case POINT:
                count = partVertices.length;
                for (int i = startVertex; i < startVertex + partVertices.length; i++) {
                    indexBuffer.add(i);
                }
                break;
            case LINE:
                if (partVertices.length % 2 != 0) throw new RuntimeException("Spatny pocet vrcholu pro vytvoreni usecek");
                count = partVertices.length / 2;
                for (int i = startVertex; i < startVertex + partVertices.length; i += 2) {
                    indexBuffer.add(i);
                    indexBuffer.add(i + 1);
                }
                break;
            case LINE_STRIP:
                if (partVertices.length < 2) throw new RuntimeException("Spatny pocet vrcholu pro vytvoreni useckoveho pasu");
                count = partVertices.length - 1;
                for (int i = startVertex; i < startVertex + partVertices.length - 1; i++) {
                    indexBuffer.add(i);
                    indexBuffer.add(i + 1);
                }
                partType = PartType.LINE;
                break;
            case TRIANGLE:
                if (partVertices.length % 3 != 0) throw new RuntimeException("Spatny pocet vrcholu pro vytvoreni trojuhelniku");
                count = partVertices.length / 3;
                for (int i = startVertex; i < startVertex + partVertices.length; i += 3) {
                    indexBuffer.add(i);
                    indexBuffer.add(i + 1);
                    indexBuffer.add(i + 2);
                }
                break;
            case TRIANGLE_STRIP:
                if (partVertices.length < 3) throw new RuntimeException("Spatny pocet vrcholu pro vytvoreni trojuhelnikoveho pasu");
                count = partVertices.length - 2;
                for (int i = startVertex; i < startVertex + partVertices.length - 1; i++) {
                    indexBuffer.add(i);
                    indexBuffer.add(i + 1);
                    indexBuffer.add(i + 2);
                }
                partType = PartType.TRIANGLE;
                break;
        }
        parts.add(new Part(partType, startIndex, count, transformable));
        vertexBuffer.addAll(Arrays.asList(partVertices));
        startVertex = startVertex + partVertices.length;
        startIndex = indexBuffer.size();
    }

    private void addCurve(boolean transformable, Col firstC, Col secondC, Point3D... points) {
        if (points.length == 4) {
            Col interpol;
            Cubic cubic = new Cubic(Cubic.BEZIER, points);
            List<Vertex> curveVertices = new ArrayList<>();
            for (double i = 0; i <= 1; i += 0.01) {
                interpol = firstC.mul(1 - i).add(secondC.mul(i));
                curveVertices.add(new Vertex(cubic.compute(i), interpol));
            }
            addPart(PartType.LINE_STRIP, transformable, curveVertices.toArray(new Vertex[curveVertices.size()]));
        } else {
            throw new RuntimeException("Zadany pocet bodu krivky se nerovna ctyrem.");
        }
    }

    private void addCurvedSurface(boolean transformable, Col firstC, Col secondC, Point3D... points) {
        if (points.length == 16) {
            Col interpol;
            Bicubic bicubic = new Bicubic(Cubic.BEZIER, points);
            List<Vertex> surfaceVertices = new ArrayList<>();
            for (double i = 0; i <= 1; i += 0.05) {
                for (double j = 0; j <= 1; j += 0.05) {
                    interpol = firstC.mul(1 - j).add(secondC.mul(j));

                    surfaceVertices.add(new Vertex(bicubic.compute(i, j), interpol));
                    surfaceVertices.add(new Vertex(bicubic.compute(i, j+0.05), interpol));
                    surfaceVertices.add(new Vertex(bicubic.compute(i+0.05, j+0.05), interpol));

                    surfaceVertices.add(new Vertex(bicubic.compute(i, j), interpol));
                    surfaceVertices.add(new Vertex(bicubic.compute(i+0.05, j+0.05), interpol));
                    surfaceVertices.add(new Vertex(bicubic.compute(i+0.05, j), interpol));
                }
            }
            addPart(PartType.TRIANGLE, transformable, surfaceVertices.toArray(new Vertex[surfaceVertices.size()]));
        } else {
            throw new RuntimeException("Zadany pocet bodu plochy se nerovna sestnacti.");
        }
    }
}

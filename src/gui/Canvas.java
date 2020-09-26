package gui;

import buffers.ZBuffer;
import input.Input;
import renderer.Renderer;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Intel on 05.03.2018.
 */
public class Canvas extends JFrame {

    private int w, h;
    private int x1, x2, y1, y2;
    private float zNear = 0.1f;
    private float zFar = 500f;
    private ZBuffer zBuffer;
    private JPanel panel;
    private Mat4 model, projPersp, orthProj;
    private Camera camera;
    private Renderer renderer;
    private boolean isPerspActive = true;

    private    int speed = 1;
    private double mouseSpeed = 0.5;

    private Col bg = new Col(0x000000);

    public Canvas(int w, int h) {
        this.w = w;
        this.h = h;
        setupRenderer();
        setupKeys();
        setupGui();
        SwingUtilities.invokeLater(() -> {
            SwingUtilities.invokeLater(() -> {
                SwingUtilities.invokeLater(() -> {
                    render();
                });
            });
        });
    }

    private void render() {
        zBuffer.clear(bg);
        renderer.render();
        redraw();
    }

    private void setupRenderer() {
        zBuffer = new ZBuffer(w, h);
        Input input = new Input();
        projPersp = new Mat4PerspRH(Math.PI / 4, (float) zBuffer.getHeight() / (float) zBuffer.getWidth(), zNear, zFar);
        orthProj = new Mat4OrthoRH(zBuffer.getWidth() / 35, zBuffer.getHeight() / 35, zNear, zFar);
        camera = new Camera(new Vec3D(40, 15, 8), Math.PI, 0, 30, true);
        model = new Mat4Scale(1,1,1);

        renderer = new Renderer(input, model, camera.getViewMatrix(), projPersp, zBuffer, zNear);
    }

    private void setupGui() {
        setSize(w, h);
        setTitle("Rajnet PGRF3 - ZBuffer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        panel = new JPanel();
        panel.setSize(w, h);
        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void setupKeys() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                int key = keyEvent.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_W:
                        camera = camera.forward(speed);
                        renderer.setView(camera.getViewMatrix());
                        render();
                        break;
                    case KeyEvent.VK_A:
                        camera = camera.left(speed);
                        renderer.setView(camera.getViewMatrix());
                        render();
                        break;
                    case KeyEvent.VK_S:
                        camera = camera.backward(speed);
                        renderer.setView(camera.getViewMatrix());
                        render();
                        break;
                    case KeyEvent.VK_D:
                        camera = camera.right(speed);
                        renderer.setView(camera.getViewMatrix());
                        render();
                        break;
                    case KeyEvent.VK_SHIFT:
                        camera = camera.up(speed);
                        renderer.setView(camera.getViewMatrix());
                        render();
                        break;
                    case KeyEvent.VK_CONTROL:
                        camera = camera.down(speed);
                        renderer.setView(camera.getViewMatrix());
                        render();
                        break;
                    case KeyEvent.VK_F:
                        if (isPerspActive) {
                            isPerspActive = false;
                            renderer.setProjection(orthProj);
                        } else {
                            isPerspActive = true;
                            renderer.setProjection(projPersp);
                        }
                        render();
                        break;
                    case KeyEvent.VK_K:
                        renderer.switchWireFrameDrawing();
                        render();
                        break;

                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                x1 = mouseEvent.getX();
                y1 = mouseEvent.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                x2 = mouseEvent.getX();
                y2 = mouseEvent.getY();

                camera = camera.addAzimuth(-((Math.PI * (x2 - x1)) / zBuffer.getWidth()) * mouseSpeed);
                camera = camera.addZenith(-((Math.PI * (y2 - y1)) / zBuffer.getHeight()) * mouseSpeed);

                renderer.setView(camera.getViewMatrix());
                render();

                x1 = x2;
                y1 = y2;
            }
        });
    }

    private void redraw() {
        if (panel.getGraphics() != null) {
            panel.getGraphics().drawImage(zBuffer.getImage(), 0, 0, null);
        }
    }

}

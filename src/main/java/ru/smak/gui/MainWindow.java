package ru.smak.gui;

import kotlin.Pair;
import ru.smak.dynamic.MaxIterations;
import ru.smak.graphics.*;
import ru.smak.math.fractals.Mandelbrot;
import ru.smak.math.fractals.MandelbrotX2;
import ru.smak.menu.InstrumentPanel;
import ru.smak.menu.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow extends JFrame {
    private final GraphicsPanel mainPanel = new GraphicsPanel();
    private InstrumentPanel tool;
    private final Plane plane;
    private static final int GROW = GroupLayout.DEFAULT_SIZE;
    private static final int SHRINK = GroupLayout.PREFERRED_SIZE;
    private final Dimension minSz = new Dimension(600, 500);

    private Point firstScalePoint = null;
    private Point lastScalePoint = null;
    private Point firstDragPoint = null;
    private Point lastDragPoint = null;
    private int LastButtonPressed;
    private int LastButtonReleased;

    public Plane getPlane() {
        return plane;
    }

    public GraphicsPanel getMainPanel()
    {
        return mainPanel;
    }
    public InstrumentPanel getInstrumentPanel(){return tool;}

    private Color test(float x) { return Color.GREEN;}

    public MainWindow(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(minSz);

        Mandelbrot m = new MandelbrotX2();

        plane = new Plane(-2.0, 1.0, -1.0, 1.0, 0, 0);
        var colorFunc = new ColorFunctionDark();
        FractalPainter fp = new FractalPainter(plane, m, colorFunc);

        mainPanel.setBackground(Color.WHITE);

        JMenuBar menuBar = new JMenuBar();
        MainMenu menu = new MainMenu(menuBar);
        setJMenuBar(menuBar);
        JToolBar toolBar = new JToolBar();
        tool = new InstrumentPanel(toolBar, this);

        //mainPanel.addPainter(fp);

        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                plane.setWidth(mainPanel.getWidth());
                plane.setHeight(mainPanel.getHeight());
            }
        });
        //region Расположение
        GroupLayout gl = new GroupLayout(getContentPane());

        gl.setHorizontalGroup(
                gl.createSequentialGroup()
                        .addGap(4)
                        .addGroup(gl.createParallelGroup()
                                .addComponent(toolBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                                .addGap(4)
                                .addComponent(mainPanel,GROW, GROW, GROW)
                        )
                        .addGap(4)
        );

        gl.setVerticalGroup(
                gl.createSequentialGroup()
                        .addGap(4)
                        .addComponent(toolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(4)
                        .addComponent(mainPanel, GROW, GROW, GROW)
                        .addGap(4)
        );
        setLayout(gl);
        //endregion
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                LastButtonPressed = e.getButton();
                if(LastButtonPressed == 1)
                {
                    firstScalePoint = e.getPoint();
                }
                else if(LastButtonPressed == 2)
                {
                    firstDragPoint = e.getPoint();
                }
            }

            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                LastButtonReleased = e.getButton();
                if(LastButtonReleased == 1)
                {
                    if (lastScalePoint !=null) {
                        var g = mainPanel.getGraphics();
                        g.setXORMode(Color.WHITE);
                        g.drawRect(Math.min(firstScalePoint.x, lastScalePoint.x), Math.min(firstScalePoint.y, lastScalePoint.y), Math.abs(lastScalePoint.x- firstScalePoint.x), Math.abs(lastScalePoint.y- firstScalePoint.y));
                        g.setPaintMode();
                    }
                    var xMin = Converter.INSTANCE.xScrToCrt(Math.min(firstScalePoint.x, lastScalePoint.x), plane);
                    var xMax = Converter.INSTANCE.xScrToCrt(Math.max(firstScalePoint.x, lastScalePoint.x), plane);
                    var yMin = Converter.INSTANCE.yScrToCrt(Math.min(firstScalePoint.y, lastScalePoint.y), plane);
                    var yMax = Converter.INSTANCE.yScrToCrt(Math.max(firstScalePoint.y, lastScalePoint.y), plane);
                    plane.setXEdges(new Pair<>(xMin, xMax));
                    plane.setYEdges(new Pair<>(yMin, yMax));
                    lastScalePoint = firstScalePoint = null;
                    MaxIterations maxIterations = new MaxIterations(MainWindow.this);
                    mainPanel.repaint();
                }
            }
        });

        mainPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if(LastButtonPressed == 1)
                {
                    var g = mainPanel.getGraphics();
                    g.setXORMode(Color.WHITE);
                    g.setColor(Color.BLACK);
                    if (lastScalePoint !=null) {
                        g.drawRect(Math.min(firstScalePoint.x, lastScalePoint.x), Math.min(firstScalePoint.y, lastScalePoint.y), Math.abs(lastScalePoint.x- firstScalePoint.x), Math.abs(lastScalePoint.y- firstScalePoint.y));
                    }
                    g.drawRect(Math.min(firstScalePoint.x, e.getX()), Math.min(firstScalePoint.y, e.getY()), Math.abs(e.getX()- firstScalePoint.x), Math.abs(e.getY()- firstScalePoint.y));
                    g.setPaintMode();
                    lastScalePoint = e.getPoint();
                }
                if(LastButtonPressed == 2)
                {

                }
            }
        });

    }

    @Override
    public void setVisible(boolean v){
        super.setVisible(v);
        plane.setWidth(mainPanel.getWidth());
        plane.setHeight(mainPanel.getHeight());
        var g = mainPanel.getGraphics();
        //костыль
        g.setXORMode(Color.WHITE);
        g.drawRect(-1000, -1000, 1, 1);
        g.setPaintMode();
    }
}

package io.github.captivecow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScreenView implements Runnable {

    private final String DEFAULT_SCREEN_WIDTH = "800";
    private final String DEFAULT_SCREEN_HEIGHT = "600";
    private final String DEFAULT_MAP = "demo-map.xml";
    private final String DEFAULT_TILE_WIDTH = "25";

    private final Logger logger = LoggerFactory.getLogger(ScreenView.class);

    private final Properties properties;
    private final JFrame frame;
    private final Canvas canvas;
    private final GridBagLayout layout;
    private final GridBagConstraints constraints;
    private BufferStrategy bufferStrategy;

    private final ScheduledExecutorService fpsScheduler;
    private final Runnable renderRunnable;
    private long lastTime;

    int heightTileAmount;
    int widthDrawSize;
    int heightDrawSize;
    TileMap map;
    private double accumulation;
    private int screenFps;
    private InputController inputController;

    public ScreenView() {
        properties = new Properties();
        frame = new JFrame("j2dMapRenderer");
        canvas = new Canvas();
        layout = new GridBagLayout();
        constraints = new GridBagConstraints();
        fpsScheduler = Executors.newScheduledThreadPool(1);
        inputController = new InputController();
        renderRunnable = this::render;
        accumulation = 0.0;
        screenFps = 0;
    }

    public static BufferedImage loadImage(String fileName) {
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
        InputStream rawImageFile = Objects.requireNonNull(ScreenView.class.getResourceAsStream("/" + fileName));

        BufferedImage rawImage;
        try {
            rawImage = ImageIO.read(rawImageFile);
            rawImageFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedImage compatibleImage = graphicsConfiguration.createCompatibleImage(rawImage.getWidth(),
                rawImage.getHeight(), Transparency.BITMASK);

        Graphics2D graphics2D = (Graphics2D) compatibleImage.getGraphics();
        graphics2D.drawImage(rawImage, 0, 0, null);
        graphics2D.dispose();

        return compatibleImage;
    }

    public void createAndShowGui() {

        try {
            InputStream configPropertiesStream = Objects
                    .requireNonNull(J2dMapRenderer.class.getResourceAsStream("/config.properties"));
            properties.load(configPropertiesStream);
            configPropertiesStream.close();

        } catch (IOException | NullPointerException ex) {
            logger.warn("Missing config file, using default width/height for screen.");
        }

        int screenWidth = Integer.parseInt(properties.getProperty("screen.width", DEFAULT_SCREEN_WIDTH));
        int screenHeight = Integer.parseInt(properties.getProperty("screen.height", DEFAULT_SCREEN_HEIGHT));
        int widthTileAmount = Integer.parseInt(properties.getProperty("map.tileWidth", DEFAULT_TILE_WIDTH));
        String mapFileName = properties.getProperty("map.name", DEFAULT_MAP);

        map = new TileMap(mapFileName);

        heightTileAmount = (int) (widthTileAmount * ((float) screenHeight) / (float) screenWidth);
        widthDrawSize = Math.ceilDiv(screenWidth, widthTileAmount);
        heightDrawSize = Math.ceilDiv(screenHeight, heightTileAmount);

        canvas.setPreferredSize(new Dimension(screenWidth, screenHeight));
        canvas.setIgnoreRepaint(true);
        canvas.addKeyListener(inputController);

        frame.setLayout(layout);

        constraints.gridx = 0;
        constraints.gridy = 0;
        frame.add(canvas, constraints);

        frame.setIgnoreRepaint(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setBackground(Color.BLACK);

        frame.pack();
        frame.setVisible(true);

        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();

        lastTime = System.nanoTime();
        fpsScheduler.scheduleAtFixedRate(renderRunnable, 0, 16, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        createAndShowGui();
    }

    public void render() {

        long currentTime = System.nanoTime();
        double currentDelta = (currentTime - lastTime) / 1000000000.0;
        lastTime = currentTime;
        accumulation += currentDelta;

        if (accumulation >= 1.0) {
            accumulation = 0.0;
            screenFps = (int) Math.round(1 / currentDelta);
        }

        do {
            do {
                Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();

                for (int i = 0; i < map.getMap().size(); i++) {
                    int x = i % map.getWidth();
                    int y = i / map.getWidth();
                    int mapStartX = x * widthDrawSize;
                    int mapStartY = y * heightDrawSize;
                    int mapEndX = x * widthDrawSize + widthDrawSize;
                    int mapEndY = y * heightDrawSize + heightDrawSize;

                    int tileNum = map.getMap().get(i);

                    Tile tile = map.getTiles().get(tileNum);

                    g2d.drawImage(map.getMapTileImage(), mapStartX, mapStartY, mapEndX, mapEndY, tile.beginX(),
                            tile.beginY(), tile.endX(), tile.endY(), null);
                }

                g2d.setColor(Color.YELLOW);
                g2d.fillRect(0, 0, 50, 15);
                g2d.setColor(Color.BLACK);
                g2d.drawString("FPS: " + screenFps, 4, 11);

                g2d.dispose();
            } while (bufferStrategy.contentsRestored());
            bufferStrategy.show();
        } while (bufferStrategy.contentsLost());
    }
}

package io.github.captivecow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

public class ScreenView implements Runnable {

    private final String DEFAULT_SCREEN_WIDTH = "800";
    private final String DEFAULT_SCREEN_HEIGHT = "600";
    private final String DEFAULT_MAP = "demo-map.xml";
    private final String DEFAULT_TILE_WIDTH="25";

    private final Logger logger = LoggerFactory.getLogger(ScreenView.class);

    private final Properties properties;
    private final JFrame frame;
    private final Canvas canvas;
    private final GridBagLayout layout;
    private final GridBagConstraints constraints;
    private BufferStrategy bufferStrategy;

    public ScreenView() {
        properties = new Properties();
        frame = new JFrame("j2dMapRenderer");
        canvas = new Canvas();
        layout = new GridBagLayout();
        constraints = new GridBagConstraints();
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

        TileMap map = new TileMap(mapFileName);

        canvas.setPreferredSize(new Dimension(screenWidth, screenHeight));
        canvas.setIgnoreRepaint(true);

        frame.setLayout(layout);

        constraints.gridx = 0;
        constraints.gridy = 0;
        frame.add(canvas, constraints);

        frame.setIgnoreRepaint(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);

        frame.pack();
        frame.setVisible(true);

        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();

        int heightTileAmount = (int) (widthTileAmount * ( (float) screenHeight) / (float) screenWidth);
        int widthDrawSize = Math.ceilDiv(screenWidth, widthTileAmount);
        int heightDrawSize = Math.ceilDiv(screenHeight, heightTileAmount);

        ArrayList<Integer> numberMap = map.getMap();

        do {
            do {
                Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();

                for(int i = 0; i<numberMap.size(); i++){
                    int x = i % map.getWidth();
                    int y = i / map.getWidth();
                    int mapStartX = x * widthDrawSize;
                    int mapStartY = y * heightDrawSize;
                    int mapEndX = x * widthDrawSize + widthDrawSize;
                    int mapEndY = y * heightDrawSize + heightDrawSize;

                    int tileNum = numberMap.get(i);

                    Tile tile = map.getTiles().get(tileNum);

                    g2d.drawImage(map.getMapTileImage(),
                            mapStartX,
                            mapStartY,
                            mapEndX,
                            mapEndY,
                            tile.beginX(),
                            tile.beginY(),
                            tile.endX(),
                            tile.endY(),
                            null);
                }
                g2d.dispose();
            } while (bufferStrategy.contentsRestored());
            bufferStrategy.show();
        } while (bufferStrategy.contentsLost());

    }

    @Override
    public void run() {
        createAndShowGui();
    }
}

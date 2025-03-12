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
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

public class ScreenView implements Runnable {

    private final String DEFAULT_SCREEN_WIDTH = "800";
    private final String DEFAULT_SCREEN_HEIGHT = "600";

    private final Logger logger = LoggerFactory.getLogger(ScreenView.class);

    private final Properties properties;
    private final JFrame frame;
    private final Canvas canvas;
    private final GridBagLayout layout;
    private final GridBagConstraints constraints;
    private BufferStrategy bufferStrategy;
    private final BufferedImage tileSetImage;

    public ScreenView() {
        properties = new Properties();
        frame = new JFrame("j2dMapRenderer");
        canvas = new Canvas();
        layout = new GridBagLayout();
        constraints = new GridBagConstraints();
        tileSetImage = loadImage("tileset.png");
    }

    public BufferedImage loadImage(String fileName) {
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

        TileMap map = new TileMap();
        map.createMap("demo-map.xml");

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

        // i.e. rows = 1 for tiles, columns = 4
        int columns = map.getImageWidth() / map.getTileWidth();
        int rows = map.getImageHeight() / map.getTileHeight();

        HashMap<Integer, Tile> tiles = new HashMap<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                int startHeight = i * map.getTileHeight();
                int startWidth = j * map.getTileWidth();
                int endHeight = startHeight + map.getTileHeight();
                int endWidth = startWidth + map.getTileWidth();

                Tile tile = new Tile(j + 1, startWidth, startHeight, endWidth, endHeight);
                System.out.println(tile);
                tiles.put(tile.getId(), tile);
            }
        }

        do {
            do {
                Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();

                Tile tile = tiles.get(3);

                g2d.drawImage(tileSetImage, 16, 16, 32, 32, tile.getBeginX(), tile.getBeginY(), tile.getEndX(),
                        tile.getEndY(), null);

                g2d.dispose();
                System.out.println("running?");
            } while (bufferStrategy.contentsRestored());
            bufferStrategy.show();
        } while (bufferStrategy.contentsLost());

    }

    @Override
    public void run() {
        createAndShowGui();
    }
}

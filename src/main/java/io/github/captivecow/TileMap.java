package io.github.captivecow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TileMap {

    private final Logger logger = LoggerFactory.getLogger(TileMap.class);
    private final ArrayList<Integer> map;
    private final HashMap<Integer, Tile> tiles;
    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;
    private String imageName;
    private BufferedImage mapTileImage;
    private int imageWidth;
    private int imageHeight;

    public TileMap(String mapFileName) {
        map = new ArrayList<>();
        tiles = new HashMap<>();
        createMap(mapFileName);
    }

    public void createMap(String mapFileName) {

        try {
            InputStream demoMapStream = Objects
                    .requireNonNull(J2dMapRenderer.class.getResourceAsStream("/" + mapFileName));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(demoMapStream);
            demoMapStream.close();

            Element mapTag = document.getDocumentElement();

            width = Integer.parseInt(mapTag.getAttribute("width"));
            height = Integer.parseInt(mapTag.getAttribute("height"));
            tileWidth = Integer.parseInt(mapTag.getAttribute("tilewidth"));
            tileHeight = Integer.parseInt(mapTag.getAttribute("tileheight"));

            NodeList nodes = mapTag.getElementsByTagName("*");

            for (int i = 0; i < nodes.getLength(); i++) {

                Node nestedNode = nodes.item(i);

                if (nestedNode.getNodeName().equals("data")) {

                    String mapLayout = nestedNode.getTextContent();

                    for (int j = 0; j < mapLayout.length(); j++) {
                        if (Character.isDigit(mapLayout.charAt(j))) {
                            map.add(Character.getNumericValue(mapLayout.charAt(j)));
                        }
                    }

                } else if (nestedNode.getNodeName().equals("image")) {
                    imageName = nestedNode.getAttributes().getNamedItem("source").getNodeValue();
                    imageWidth = Integer.parseInt(nestedNode.getAttributes().getNamedItem("width").getNodeValue());
                    imageHeight = Integer.parseInt(nestedNode.getAttributes().getNamedItem("height").getNodeValue());
                }
            }
            mapTileImage = ScreenView.loadImage(imageName);

            int columns = imageWidth / tileWidth;
            int rows = imageHeight / tileHeight;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {

                    int startHeight = i * tileHeight;
                    int startWidth = j * tileWidth;
                    int endHeight = startHeight + tileHeight;
                    int endWidth = startWidth + tileWidth;

                    Tile tile = new Tile(j + 1, startWidth, startHeight, endWidth, endHeight);
                    tiles.put(tile.id(), tile);
                }
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }

    }

    public ArrayList<Integer> getMap() {
        return map;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getImageName() {
        return imageName;
    }

    public BufferedImage getMapTileImage() {
        return mapTileImage;
    }

    public HashMap<Integer, Tile> getTiles() {
        return tiles;
    }
}

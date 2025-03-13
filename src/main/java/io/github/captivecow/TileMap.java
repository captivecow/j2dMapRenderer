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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class TileMap {

    private final Logger logger = LoggerFactory.getLogger(TileMap.class);
    private final ArrayList<Integer> map;
    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;
    private String imageName;
    private int imageWidth;
    private int imageHeight;

    public TileMap() {
        map = new ArrayList<>();
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

            String mapLayout = "";

            for (int i = 0; i < nodes.getLength(); i++) {

                Node nestedNode = nodes.item(i);

                if (nestedNode.getNodeName().equals("data")) {
                    mapLayout = nestedNode.getTextContent();
                } else if (nestedNode.getNodeName().equals("image")) {
                    imageName = nestedNode.getAttributes().getNamedItem("source").getNodeValue();
                    imageWidth = Integer.parseInt(nestedNode.getAttributes().getNamedItem("width").getNodeValue());
                    imageHeight = Integer.parseInt(nestedNode.getAttributes().getNamedItem("height").getNodeValue());
                }
            }

            for (int i = 0; i < mapLayout.length(); i++) {
                if (Character.isDigit(mapLayout.charAt(i))) {
                    map.add(Character.getNumericValue(mapLayout.charAt(i)));
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
}

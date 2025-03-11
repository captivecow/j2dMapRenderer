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
import java.util.Objects;

public class TileMap {

    private final Logger logger = LoggerFactory.getLogger(TileMap.class);

    public TileMap() {
    }

    public void createMap(String mapFileName) {

        try {
            InputStream demoMapStream = Objects
                    .requireNonNull(J2dMapRenderer.class.getResourceAsStream("/" + mapFileName));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(demoMapStream);
            demoMapStream.close();

            Element map = document.getDocumentElement();
            NodeList nodes = map.getElementsByTagName("*");

            for (int i = 0; i < nodes.getLength(); i++) {
                Node nestedNode = nodes.item(i);
                logger.info(nestedNode.getNodeName());
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }

    }
}

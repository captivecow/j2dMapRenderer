package io.github.captivecow;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TileMap {

    public TileMap(){}


    public void createMap(String mapFileName){
        InputStream demoMapStream = Objects.requireNonNull(J2dMapRenderer.class.getResourceAsStream("/" + mapFileName));

        try {
            String demoMapStr = new String(demoMapStream.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject map = new JSONObject(demoMapStr);
            System.out.println(map.get("height"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

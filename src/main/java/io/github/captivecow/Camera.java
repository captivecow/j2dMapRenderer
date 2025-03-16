package io.github.captivecow;

import java.awt.Graphics2D;

public class Camera {

    private TileMap map;
    private final int screenWidth;
    private final int screenHeight;
    private final int widthTileAmount;
    private final int heightTileAmount;
    private final int widthDrawSize;
    private final int heightDrawSize;
    private final int maxWidth;
    private final int maxHeight;

    private int x;
    private int y;


    public Camera(TileMap map, int screenWidth, int screenHeight, int widthTileAmount){
        x = 0;
        y = 0;
        this.map = map;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.widthTileAmount = widthTileAmount;
        heightTileAmount = (int) (widthTileAmount * ((float) screenHeight) / (float) screenWidth);
        widthDrawSize = Math.ceilDiv(screenWidth, widthTileAmount);
        heightDrawSize = Math.ceilDiv(screenHeight, heightTileAmount);
        maxWidth = map.getWidth() * widthDrawSize;
        maxHeight = map.getHeight() * heightDrawSize;
    }

    public void render(Graphics2D g2d){

        for (int i = 0; i < map.getMap().size(); i++) {
            int x = i % map.getWidth();
            int y = i / map.getWidth();
            int mapStartX = x * widthDrawSize;
            int mapStartY = y * heightDrawSize;
            int mapEndX = x * widthDrawSize + widthDrawSize;
            int mapEndY = y * heightDrawSize + heightDrawSize;

            int tileNum = map.getMap().get(i);

            Tile tile = map.getTiles().get(tileNum);

            if(intersects(mapStartX, mapStartY, mapEndX, mapEndY)){
                g2d.drawImage(map.getMapTileImage(), mapStartX-this.x, mapStartY-this.y, mapEndX, mapEndY, tile.beginX(),
                        tile.beginY(), tile.endX(), tile.endY(), null);
            }
//            else {
//                System.out.println("Doesn't intersect - x:" + mapStartX + " to " + mapEndX + " y: " + mapStartY + " to " + mapEndY);
//                System.out.println("Camera is - x:" + this.x + " to " + (this.x+screenWidth) + " y: " + this.y + " to " + (this.y+screenHeight));
//            }
        }
    }

    public boolean intersects(int mapStartX, int mapStartY, int mapEndX, int mapEndY){
        return !(mapStartX > (x+screenWidth) || mapEndX < x || mapStartY > (y+screenHeight) || mapEndY < y);
    }

    public void setX(int x) {
        this.x = x;
        if(this.x < 0){
            this.x = 0;
        }
        if(this.x+screenWidth > maxWidth){
            this.x = maxWidth-screenWidth;
        }
    }

    public void setY(int y) {
        this.y = y;
        if(this.y < 0){
            this.y = 0;
        }
        if(this.y+screenHeight > maxHeight){
            this.y = maxHeight-screenHeight;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

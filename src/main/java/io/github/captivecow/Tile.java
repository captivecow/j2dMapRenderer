package io.github.captivecow;

public class Tile {

    private final int endX;
    private final int endY;
    private final int beginX;
    private final int beginY;
    private final int id;

    public Tile(int id, int beginX, int beginY, int endX, int endY) {
        this.id = id;
        this.beginX = beginX;
        this.beginY = beginY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public String toString() {
        return "Tile = id: " + id + ", x area: " + beginX + "to " + endX + ", y area: " + beginY + " to " + endY;
    }

    public int getId() {
        return id;
    }

    public int getBeginX() {
        return beginX;
    }

    public int getBeginY() {
        return beginY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }
}

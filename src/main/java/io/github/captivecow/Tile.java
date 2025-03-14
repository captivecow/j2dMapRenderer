package io.github.captivecow;

public record Tile(int id, int beginX, int beginY, int endX, int endY) {

    @Override
    public String toString() {
        return "Tile = id: " + id + ", x area: " + beginX + "to " + endX + ", y area: " + beginY + " to " + endY;
    }
}

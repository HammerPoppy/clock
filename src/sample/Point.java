package sample;

class Point {
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    Point(double x, double y) {

        this.x = x;
        this.y = y;
    }

    private final double x;
    private final double y;
}

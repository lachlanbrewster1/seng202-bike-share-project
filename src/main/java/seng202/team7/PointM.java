package seng202.team7;

/**
 * @author Morgan English
 * Stores information about a point
 */
public class PointM {
    private int x;
    private int y;

    /**
     * Creates a new point object with the co-ordinates given
     * @param x int x position
     * @param y int y position
     */
    public PointM(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new point object with the co-ordinates given
     * @param x double x position
     * @param y double y position
     */
    public PointM(double x, double y)
    {
        this.x = (int) Math.round(x);
        this.y = (int) Math.round(y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void print()
    {
        System.out.println("X: " + x + " | Y: " + y);
    }

    /**
     * returns the latitude from the point
     * @return latitude to use on map
     */
    public double getLat()
    {
        return (double) x / StaticVariables.pointMultiplier;
    }

    /**
     * returns the longitude from the point
     * @return longitude of the point
     */
    public double getLon()
    {
        return (double) y/StaticVariables.pointMultiplier;
    }
}

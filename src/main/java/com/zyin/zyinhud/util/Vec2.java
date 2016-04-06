package com.zyin.zyinhud.util;

/**
 * The type Vec 2.
 */
public class Vec2
{
    /**
     * The X.
     */
    double x;
    /**
     * The Y.
     */
    double y;

    /**
     * Instantiates a new Vec 2.
     *
     * @param x the x
     * @param y the y
     */
    public Vec2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Dot double.
     *
     * @param v the v
     * @return the double
     */
/*public double signedAngle(Vec2 v2)
    {
          double perpDot = perpDot(this, v2);
          return Math.atan2(perpDot, this.dot(v2));
    }
    public double perpDot(Vec2 v1, Vec2 v2)
    {
    	return v1.x * v2.y - v1.y * v2.x;
    }*/
    public double dot(Vec2 v)
    {
        return this.x * v.x + this.y * v.y;
    }

    /**
     * Length double.
     *
     * @return the double
     */
    public double length()
    {
        return Math.sqrt(x * x + y * y);
    }
    /*public double cross(Vec2 v)
    {
    	return this.x*v.y - this.y*v.x;
    }*/

}

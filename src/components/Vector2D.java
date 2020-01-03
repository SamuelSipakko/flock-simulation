package components;

import java.util.Random;


/** A simple 2D vector class */
public class Vector2D {
    public double x;
    public double y;

    public Vector2D() { x = 0; y = 0; }
    public Vector2D(Vector2D v) { x = v.x; y = v.y; }
    public Vector2D(double x, double y) { this.x = x; this.y = y; }

    public void setTo(Vector2D vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public double length() {
        return Math.sqrt(x*x + y*y);
    }

    public Vector2D multiply(double d) {
        x *= d;
        y *= d;
        return this;
    }

    public Vector2D divide(double d) {
        x /= d;
        y /= d;
        return this;
    }

    public Vector2D limit(double len) {
        if (length() > len)
            setMagnitude(len);
        return this;
    }

    public Vector2D ensureDirection() {
        if (x != 0 || y != 0) return this;
        this.x = new Random().nextBoolean() ? 0.0001 + new Random().nextDouble() % 0.0009 : 0.0001 - new Random().nextDouble() % 0.0009;
        this.y = new Random().nextBoolean() ? 0.0001 + new Random().nextDouble() % 0.0009 : 0.0001 - new Random().nextDouble() % 0.0009;
        return this;
    }

    public Vector2D add(Vector2D vector) {
        this.x += vector.x;
        this.y += vector.y;
        return this;
    }

    public Vector2D add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2D subtract(Vector2D vector) {
        this.x -= vector.x;
        this.y -= vector.y;
        return this;
    }

    public Vector2D subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }


    /** Sets the magnitude of this vector by
     *  normalizing it and multiplying by given
     *  length. If this vector is zero vector,
     *  does nothing.
     *
     * @param length the new length of the vector
     * @return this vector after the modification
     */
    public Vector2D setMagnitude(double length) {
        double len = length();
        if (len == 0) return this;
        x = x * length / len;
        y = y * length / len;
        return this;
    }

    /** Returns a new Vector2D which is a
     *  normalized copy of this one. If this
     *  vector is zero vector, returns a new
     *  zero vector.
     *
     * @return a new Vector2D that is normalized
     */
    public Vector2D getNormalized() {
        double len = length();
        if (len == 0) return new Vector2D(x,y);
        return new Vector2D(x/len, y/len);
    }

    /** Returns the dot product of this and the given vector
     *
     * @param another another vector
     * @return the dot product of the vectors
     */
    public double dotProduct(Vector2D another) {
        return this.x*another.x + this.y*another.y;
    }

    public Vector2D normalize() {
        double len = length();
        if (len == 0) return this;
        x /= len;
        y /= len;
        return this;
    }


}

package components;

import javafx.scene.shape.Circle;

import java.util.Random;

/** Entities represent Boids in the simulation */
public class Entity {
    private Vector2D position = null;
    private Vector2D velocity = null;
    private Vector2D acceleration = null;
    private double speed = 0;
    private Circle shape = null;

    /** Creates a new entity with random parameters
     *
     * @param x x coordinate of the entity
     * @param y y coordinate of the entity
     */
    public Entity(double x, double y) {
        Random randGen = new Random();
        this.position = new Vector2D(x, y);
        this.speed = randGen.nextDouble() * 3 + 1;
        this.velocity = new Vector2D(randGen.nextDouble()*5 - 2.5, randGen.nextDouble()*5 - 2.5).setMagnitude(speed);
        this.acceleration = new Vector2D(this.velocity);
    }

    /** Calculates the distance between this and the given entity
     *
     * @param e another entity
     * @return the distance between this and the other entity
     */
    public double distanceFrom(Entity e) {
        return Math.sqrt(Math.pow(e.position.x - this.position.x, 2) + Math.pow(e.position.y - this.position.y, 2));
    }

    /** Calculates the distance between this entity and the given location
     *
     * @param x the x of the location
     * @param y the y of the location
     * @return the distance between this entity and the location
     */
    public double distanceFrom(double x, double y) {
        return Math.sqrt(Math.pow(x - this.position.x, 2) + Math.pow(y - this.position.y, 2));
    }

    /** Moves the entity and the javafx shape representing this
     *  entity
     *
     * @param xLimit x limit of the world
     * @param yLimit y limit of the world
     */
    public void move(double xLimit, double yLimit) {
        this.position.add(acceleration);
        this.velocity.setTo(acceleration);
        if (position.x < 0) this.position.x = 1;
        if (position.y < 0) this.position.y = 1;
        if (position.x > xLimit) this.position.x = xLimit-1;
        if (position.y > yLimit) this.position.y = yLimit-1;
        if (shape == null) return;
        shape.setCenterX(this.position.x);
        shape.setCenterY(this.position.y);
    }

    /** Calculates and returns the angle between the velocity
     *  of this entity and the direction from this entity
     *  to the given entity. Returned angles are in degrees
     *
     * @param another another entity
     * @return angle between the heading of this entity and
     * direction to given entity in degrees
     */
    public double angleToEntity(Entity another) {
        Vector2D normalizedVecToAnother = new Vector2D(another.position)
                .subtract(this.position)
                .getNormalized();
        Vector2D normalizedVelocity = this.velocity.getNormalized();

        // 57.2957795131 = 180/pi (conversion multiplier)
        return Math.acos(normalizedVelocity.dotProduct(normalizedVecToAnother)) * 57.2957795131;
    }

    public void setShape(Circle shape) { this.shape = shape; }

    public Vector2D getVelocity() { return velocity; }
    public void setVelocity(Vector2D velocity) { this.velocity = velocity; }

    public Vector2D getAcceleration() { return acceleration; }

    public Vector2D getPosition() { return position; }
    public void setPosition(Vector2D position) { this.position = position; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public double getX() { return position.x; }
    public double getY() { return position.y; }
}

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.*;

import  components.*;

public class Main extends Application {

    private static ArrayList<Entity> entities = new ArrayList<>();

    private static boolean awaitsDrawing = false;

    // Boxes are used to pass them as modifiable parameters to functions
    private static Box<Integer> entityCountBox = new Box<>(100);
    private static Box<Double> alignmentMultiplierBox = new Box<>(0.8);
    private static Box<Double> cohesionMultiplierBox = new Box<>(0.5);
    private static Box<Double> separationMultiplierBox = new Box<>(1.0);
    private static Box<Integer> alignmentDistanceBox = new Box<>(50);
    private static Box<Integer> cohesionDistanceBox = new Box<>(30);
    private static Box<Integer> separationDistanceBox = new Box<>(50);
    private static Box<Double> maxForceBox = new Box<>(0.1);
    private static Box<Double> maxSpeedBox = new Box<>(3.0);
    private static Box<Double> detectionAngleBox = new Box<>(225.0);


    /* Private methods */

    /** A simple wrapper class for any type */
    private static class Box<T> {
        T val;
        Box(T value) { val = value; }
    }


    /** Filters extra dots and other non-decimal characters
     * from given string.
     *
     * @param string string to filter
     * @return filtered string
     */
    private String filterDecimalString(String string) {
        return string
                .replaceAll("[^0-9.]", "")
                .replaceFirst("\\.", "@")
                .replaceAll("\\.", "")
                .replaceFirst("@", ".");
    }


    /** Creates a new Boid and adds it to 'entities'.
     *  Creates also a javafx shape for the Boid and
     *  adds it to children of given pane.
     *
     * @param pane pane used as boid drawing area
     * @return created Boid
     */
    private Entity createBoid(Pane pane) {
        Random randGen = new Random();
        double randX = randGen.nextDouble() * pane.getWidth();
        double randY = randGen.nextDouble() * pane.getHeight();
        Entity e = new Entity(randX, randY);
        entities.add(e);

        Circle cir = new Circle();
        e.setShape(cir);
        cir.setCenterX(randX);
        cir.setCenterY(randY);
        cir.setRadius(2);
        cir.setFill(Color.rgb(255,255,255));
        pane.getChildren().add(cir);
        return e;
    }


    /** Updates changed entity (i.e. Boid) count by either creating new
     *  entities or removing them. If the count inside entityCountBox
     *  equals 'entities' size, does nothing.
     *
     * @param pane pane used as boid drawing area
     */
    private void updateEntityCount(Pane pane) {
        int diff = entityCountBox.val - entities.size();
        if (diff > 0) {
            for (int i = 0; i < diff; i++) { createBoid(pane); }
        }
        else if (diff < 0) {
            ListIterator<Entity> it = entities.listIterator(entities.size()+diff);
            for (int i = diff; i < 0; i++) { it.next(); it.remove(); }
            pane.getChildren().remove(pane.getChildren().size()+diff, pane.getChildren().size());
        }
    }


    /** Sets string property of given property unless the string hasn't changed.
     *
     * @param property property to change
     * @param newValue new string to set to the property
     */
    private void setStringPropertyUnlessEqual(StringProperty property, String newValue) {
        if (!property.getValue().equals(newValue)) property.setValue(newValue);
    }
    /** Sets string property of given property unless the string hasn't changed.
     *  The int passed as argument is converted to the string that is used to set
     *  the value of the given property.
     *
     * @param property property to change
     * @param newValue int to use to set the property string
     */
    private void setStringPropertyUnlessEqual(StringProperty property, int newValue) {
        String valueAsString = Integer.toString(newValue);
        if (!property.getValue().equals(valueAsString)) property.setValue(valueAsString);
    }

    /** Changes given double modifier and its controls to given new value.
     *  newValue is assumed to contain a double as string and the function
     *  will throw NumberFormatException if that's not the case.
     *
     * @param newValue the new double value as string
     * @param max limit of the new value
     * @param valueToChange box containing the modifier to change
     * @param slider javafx slider that controls corresponding property
     * @param textField javafx textField that controls corresponding property
     */
    private void changeDoubleModifier(String newValue, double max, Box<Double> valueToChange, Slider slider, TextField textField) {
        String filtered = filterDecimalString(newValue);
        double parsed = filtered.isEmpty() ? 0 : Double.parseDouble(filtered.replaceFirst("\\.$",""));
        double value = Math.min(Math.max(parsed, 0), max);
        valueToChange.val = value;
        slider.setValue(value);
        textField.setText(filtered.length() > 4 ? filtered.substring(0, 5) : filtered);
        if (parsed > max) textField.setStyle("-fx-text-fill: red;");
        else textField.setStyle("-fx-text-fill: black;");
    }

    /** Changes given int modifier and its controls to given new value.
     *  newValue is assumed to contain an integer as string and the function
     *  will throw NumberFormatException if that's not the case.
     *
     * @param newValue the new integer value as string
     * @param max limit of the new value
     * @param valueToChange box containing the modifier to change
     * @param slider javafx slider that controls corresponding property
     * @param textField javafx textField that controls corresponding property
     */
    private void changeIntModifier(String newValue, int max, Box<Integer> valueToChange, Slider slider, TextField textField, int stringLength) {
        String filtered = newValue.replaceAll("[^0-9]", "");
        int parsed = filtered.isEmpty() ? 0 : Integer.parseInt(filtered);
        int value = Math.min(Math.max(parsed, 0), max);
        valueToChange.val = value;
        slider.setValue(value);
        textField.setText(filtered.length() > stringLength ? filtered.substring(0, stringLength) : filtered);
        if (parsed > max) textField.setStyle("-fx-text-fill: red;");
        else textField.setStyle("-fx-text-fill: black;");
    }

    /* Public methods */

    /** Starts the javafx GUI */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("GUI/layout.fxml"));
        primaryStage.setTitle("Flock simulation");
        primaryStage.setScene(new Scene(root, 1000, 750));
        primaryStage.show();

        Pane pane = (Pane) root.lookup("#draw_pane");

        /* UI controls */

        Slider alignmentSlider  = (Slider) root.lookup("#alignment_slider");
        Slider cohesionSlider   = (Slider) root.lookup("#cohesion_slider");
        Slider separationSlider = (Slider) root.lookup("#separation_slider");
        Slider maxForceSlider   = (Slider) root.lookup("#maxforce_slider");
        Slider maxSpeedSlider   = (Slider) root.lookup("#maxspeed_slider");
        Slider boidCountSlider  = (Slider) root.lookup("#count_slider");
        Slider alignmentDistanceSlider  = (Slider) root.lookup("#alignment_distance_slider");
        Slider cohesionDistanceSlider  = (Slider) root.lookup("#cohesion_distance_slider");
        Slider separationDistanceSlider  = (Slider) root.lookup("#separation_distance_slider");
        Slider detectionAngleSlider  = (Slider) root.lookup("#detection_angle_slider");

        TextField alignmentTextfield  = (TextField) root.lookup("#alignment_field");
        TextField cohesionTextfield   = (TextField) root.lookup("#cohesion_field");
        TextField separationTextfield = (TextField) root.lookup("#separation_field");
        TextField maxForceTextfield   = (TextField) root.lookup("#maxforce_field");
        TextField maxSpeedTextfield   = (TextField) root.lookup("#maxspeed_field");
        TextField boidCountTextfield  = (TextField) root.lookup("#count_field");
        TextField alignmentDistanceTextfield  = (TextField) root.lookup("#alignment_distance_field");
        TextField cohesionDistanceTextfield  = (TextField) root.lookup("#cohesion_distance_field");
        TextField separationDistanceTextfield  = (TextField) root.lookup("#separation_distance_field");
        TextField detectionAngleTextfield  = (TextField) root.lookup("#detection_angle_field");

        StringProperty alignmentProperty = new SimpleStringProperty(alignmentTextfield.getText());
        StringProperty cohesionProperty = new SimpleStringProperty(cohesionTextfield.getText());
        StringProperty separationProperty = new SimpleStringProperty(separationTextfield.getText());
        StringProperty maxForceProperty = new SimpleStringProperty(maxForceTextfield.getText());
        StringProperty maxSpeedProperty = new SimpleStringProperty(maxSpeedTextfield.getText());
        StringProperty boidCountProperty = new SimpleStringProperty(boidCountTextfield.getText());
        StringProperty alignmentDistanceProperty = new SimpleStringProperty(alignmentDistanceTextfield.getText());
        StringProperty cohesionDistanceProperty = new SimpleStringProperty(cohesionDistanceTextfield.getText());
        StringProperty separationDistanceProperty = new SimpleStringProperty(separationDistanceTextfield.getText());
        StringProperty detectionAngleProperty = new SimpleStringProperty(detectionAngleTextfield.getText());

        /* Listeners for UI controls */

        alignmentProperty.addListener((__, ___, newValue) -> changeDoubleModifier(newValue, 1, alignmentMultiplierBox, alignmentSlider, alignmentTextfield));
        alignmentSlider.valueProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(alignmentProperty, newValue.toString()));
        alignmentTextfield.textProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(alignmentProperty, newValue));

        cohesionProperty.addListener((__, ___, newValue) -> changeDoubleModifier(newValue, 1, cohesionMultiplierBox, cohesionSlider, cohesionTextfield));
        cohesionSlider.valueProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(cohesionProperty, newValue.toString()));
        cohesionTextfield.textProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(cohesionProperty, newValue));

        separationProperty.addListener((__, ___, newValue) -> changeDoubleModifier(newValue, 1, separationMultiplierBox, separationSlider, separationTextfield));
        separationSlider.valueProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(separationProperty, newValue.toString()));
        separationTextfield.textProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(separationProperty, newValue));

        maxForceProperty.addListener((__, ___, newValue) -> changeDoubleModifier(newValue, 1, maxForceBox, maxForceSlider, maxForceTextfield));
        maxForceSlider.valueProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(maxForceProperty, newValue.toString()));
        maxForceTextfield.textProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(maxForceProperty, newValue));

        maxSpeedProperty.addListener((__, ___, newValue) -> changeDoubleModifier(newValue, 10, maxSpeedBox, maxSpeedSlider, maxSpeedTextfield));
        maxSpeedSlider.valueProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(maxSpeedProperty, newValue.toString()));
        maxSpeedTextfield.textProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(maxSpeedProperty, newValue));

        boidCountProperty.addListener((__, ___, newValue) -> changeIntModifier(newValue, 10000, entityCountBox, boidCountSlider, boidCountTextfield, 5));
        boidCountSlider.valueProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(boidCountProperty, newValue.intValue()));
        boidCountTextfield.textProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(boidCountProperty, newValue));

        alignmentDistanceProperty.addListener((__, ___, newValue) -> changeIntModifier(newValue, 1000, alignmentDistanceBox, alignmentDistanceSlider, alignmentDistanceTextfield, 4));
        alignmentDistanceSlider.valueProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(alignmentDistanceProperty, newValue.intValue()));
        alignmentDistanceTextfield.textProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(alignmentDistanceProperty, newValue));

        cohesionDistanceProperty.addListener((__, ___, newValue) -> changeIntModifier(newValue, 1000, cohesionDistanceBox, cohesionDistanceSlider, cohesionDistanceTextfield, 4));
        cohesionDistanceSlider.valueProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(cohesionDistanceProperty, newValue.intValue()));
        cohesionDistanceTextfield.textProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(cohesionDistanceProperty, newValue));

        separationDistanceProperty.addListener((__, ___, newValue) -> changeIntModifier(newValue, 1000, separationDistanceBox, separationDistanceSlider, separationDistanceTextfield, 4));
        separationDistanceSlider.valueProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(separationDistanceProperty, newValue.intValue()));
        separationDistanceTextfield.textProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(separationDistanceProperty, newValue));

        detectionAngleProperty.addListener((__, ___, newValue) -> changeDoubleModifier(newValue, 360, detectionAngleBox, detectionAngleSlider, detectionAngleTextfield));
        detectionAngleSlider.valueProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(detectionAngleProperty, newValue.toString()));
        detectionAngleTextfield.textProperty().addListener((__, ___, newValue) -> setStringPropertyUnlessEqual(detectionAngleProperty, newValue));

        // Initiate controls
        boidCountProperty.setValue(entityCountBox.val.toString());
        alignmentProperty.setValue(alignmentMultiplierBox.val.toString());
        cohesionProperty.setValue(cohesionMultiplierBox.val.toString());
        separationProperty.setValue(separationMultiplierBox.val.toString());
        alignmentDistanceProperty.setValue(alignmentDistanceBox.val.toString());
        cohesionDistanceProperty.setValue(cohesionDistanceBox.val.toString());
        separationDistanceProperty.setValue(separationDistanceBox.val.toString());
        maxForceProperty.setValue(maxForceBox.val.toString());
        maxSpeedProperty.setValue(maxSpeedBox.val.toString());
        detectionAngleProperty.setValue(detectionAngleBox.val.toString());

        /* Create Boids */

        for (int i = 0; i < entityCountBox.val; i++) { createBoid(pane); }

        /* Create simulation loop */

        Thread updateThread = new Thread(() -> {
            while (true) {
                while (awaitsDrawing) {
                    try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                updateAccelerations(pane.getWidth(), pane.getHeight());
                awaitsDrawing = true;
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!awaitsDrawing) return;
                updateEntityCount(pane);
                for (Entity e : entities) { e.move(pane.getWidth(), pane.getHeight()); }
                awaitsDrawing = false;
            }
        }.start();
    }


    /** Updates the accelerations of all entities (i.e. Boids)
     *  by using alignment, cohesion and separation forces in
     *  conjunction with border force.
     *
     * @param xLimit the xLimit of the world, used to determinate borders
     * @param yLimit the xLimit of the world, used to determinate borders
     */
    public void updateAccelerations(double xLimit, double yLimit) {
        // Loop all entities and update their accelerations in parallel
        entities.parallelStream().forEach(cur -> {
            int alignmentNeighbors = 0;
            int cohesionNeighbors = 0;
            int separationNeighbors = 0;
            Vector2D alignment = new Vector2D();
            Vector2D cohesion = new Vector2D();
            Vector2D separation = new Vector2D();

            // Use nearby entities to determinate forces
            for (Entity ee : entities) {
                if (ee == cur) continue;
                //if (cur.angleToEntity(ee) > detectionAngleBox.val/2) continue;
                double dist = Math.max(cur.distanceFrom(ee), 0.001);

                if (dist < alignmentDistanceBox.val) {
                    alignmentNeighbors++;
                    alignment.add(ee.getVelocity());
                }
                if (dist < cohesionDistanceBox.val) {
                    cohesionNeighbors++;
                    cohesion.add(ee.getPosition());
                }
                if (dist < separationDistanceBox.val) {
                    separationNeighbors++;
                    separation.add(new Vector2D(cur.getPosition()).subtract(ee.getPosition())
                            .ensureDirection()
                            .divide(dist*dist));
                }
            }

            if (alignmentNeighbors != 0)
                alignment
                        .divide(alignmentNeighbors)
                        .setMagnitude(maxSpeedBox.val)
                        .subtract(cur.getVelocity())
                        .limit(maxForceBox.val);
            if (cohesionNeighbors != 0)
                cohesion
                        .divide(cohesionNeighbors)
                        .subtract(cur.getPosition())
                        .setMagnitude(maxSpeedBox.val)
                        .subtract(cur.getVelocity())
                        .limit(maxForceBox.val);
            if (separationNeighbors != 0)
                separation
                        .divide(separationNeighbors)
                        .setMagnitude(maxSpeedBox.val)
                        .subtract(cur.getVelocity())
                        .limit(maxForceBox.val);

            cur.getAcceleration()
                    .add(alignment.multiply(alignmentMultiplierBox.val))
                    .add(cohesion.multiply(cohesionMultiplierBox.val))
                    .add(separation.multiply(separationMultiplierBox.val))
                    .limit(maxSpeedBox.val);

            // Border force
            double bfX = cur.getX() < xLimit/2 ? 100/Math.pow(cur.getX(),2) : -100/Math.pow(xLimit - cur.getX(),2);
            double bfY = cur.getY() < yLimit/2 ? 100/Math.pow(cur.getY(),2) : -100/Math.pow(yLimit - cur.getY(),2);
            cur.getAcceleration().add(new Vector2D(bfX, bfY)).limit(maxSpeedBox.val);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}

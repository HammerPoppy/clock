package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Calendar;

public class Main extends Application {

    ////////////////////////////////////////

    private final static int SECONDS_GAP = 20;

    private final static int MINUTES_GAP = 30;
    private final static int MINUTES_WIDTH = 4;

    private final static int HOURS_GAP = 40;
    private final static int HOURS_WIDTH = 7;

    private static final int MILLIS = 333;

    ////////////////////////////////////////

    private Task<Void> task;

    private Line secondsArrow;

    private Line minutesArrow0;
    private Line minutesArrow1;
    private Line minutesArrow2;
    private Line minutesArrow3;

    private Line hoursArrow0;
    private Line hoursArrow1;
    private Line hoursArrow2;
    private Line hoursArrow3;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = FXMLLoader.load(getClass().getResource("clock.fxml"));

        final double radius = 95;
        ArrayList<Point> degrees = new ArrayList<>();

        for (int i = 0; i < 360; i += 6) {

            degrees.add(new Point(Math.cos(Math.toRadians(i - 90)), Math.sin(Math.toRadians(i - 90))));

            makeDashes(i, radius, degrees, root);

        }

        primaryStage.setTitle("Big Tee");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setResizable(false);
        primaryStage.show();

        // Initializing arrows
        Calendar calendar = Calendar.getInstance();
        int seconds = calendar.get(Calendar.SECOND);
        secondsArrow = getSecondsArrow(radius, degrees, seconds);

        int minutes = calendar.get(Calendar.MINUTE);
        double minutesPointsX[] = getMinutesPointsX(minutes, radius, degrees);
        double minutesPointsY[] = getMinutesPointsY(radius, degrees, minutes);
        initializeMinutesArrow(minutesPointsX, minutesPointsY);

        double hours = (calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60) / (double) 60;
        double hoursPointsX[] = getHoursPointsX(hours, radius, degrees);
        double hoursPointsY[] = getHoursPointsY(hours, radius, degrees);

        initializeHoursArrow(hoursPointsX, hoursPointsY);

        root.getChildren().add(secondsArrow);
        addMinutesArrow(root);
        addHoursArrow(root);

        task = new Task<Void>() {
            @Override
            protected Void call() {
                while (!this.isCancelled()) {
                    try {
                        Thread.sleep(MILLIS);
                    } catch (Exception ignored) {

                    }

                    Calendar calendar = Calendar.getInstance();

                    // seconds arrow
                    int seconds = calendar.get(Calendar.SECOND);
                    setSecondsArrowXY(seconds, radius, degrees);

                    // minutes arrow
                    int minutes = calendar.get(Calendar.MINUTE);
                    double minutesPointsX[] = getMinutesPointsX(minutes, radius, degrees);
                    double minutesPointsY[] = getMinutesPointsY(minutes, radius, degrees);
                    setMinutesArrowXY(minutesPointsX, minutesPointsY);


                    // hours arrow
                    double hours = (calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60) / (double) 60;
                    double hoursPointsX[] = getHoursPointsX(hours, radius, degrees);
                    double hoursPointsY[] = getHoursPointsY(hours, radius, degrees);

                    setHoursArrowXY(hoursPointsX, hoursPointsY);

                }
                return null;
            }
        };

        new Thread(task).start();
    }

    private Line getSecondsArrow(double radius, ArrayList<Point> degrees, int seconds) {
        return new Line(((radius - 90) * degrees.get((seconds + 30) % 60).getX() + 150),
                (radius - 90) * degrees.get((seconds + 30) % 60).getY() + 150,
                ((radius - SECONDS_GAP) * degrees.get(seconds).getX() + 150),
                (radius - SECONDS_GAP) * degrees.get(seconds).getY() + 150);
    }

    private double[] getMinutesPointsY(double radius, ArrayList<Point> degrees, int minutes) {
        return new double[]{
                (radius - MINUTES_GAP) * degrees.get(minutes).getY() + 150,
                MINUTES_WIDTH * degrees.get((minutes + 15) % 60).getY() + 150,
                (MINUTES_WIDTH * 2) * degrees.get((minutes + 30) % 60).getY() + 150,
                MINUTES_WIDTH * degrees.get((minutes + 45) % 60).getY() + 150
        };
    }

    private void initializeMinutesArrow(double[] minutesPointsX, double[] minutesPointsY) {
        minutesArrow0 = new Line(minutesPointsX[0], minutesPointsY[0], minutesPointsX[1], minutesPointsY[1]);
        minutesArrow1 = new Line(minutesPointsX[1], minutesPointsY[1], minutesPointsX[2], minutesPointsY[2]);
        minutesArrow2 = new Line(minutesPointsX[2], minutesPointsY[2], minutesPointsX[3], minutesPointsY[3]);
        minutesArrow3 = new Line(minutesPointsX[3], minutesPointsY[3], minutesPointsX[0], minutesPointsY[0]);
    }

    private void initializeHoursArrow(double[] hoursPointsX, double[] hoursPointsY) {
        hoursArrow0 = new Line(hoursPointsX[0], hoursPointsY[0], hoursPointsX[1], hoursPointsY[1]);
        hoursArrow1 = new Line(hoursPointsX[1], hoursPointsY[1], hoursPointsX[2], hoursPointsY[2]);
        hoursArrow2 = new Line(hoursPointsX[2], hoursPointsY[2], hoursPointsX[3], hoursPointsY[3]);
        hoursArrow3 = new Line(hoursPointsX[3], hoursPointsY[3], hoursPointsX[0], hoursPointsY[0]);
    }

    private void addHoursArrow(Pane root) {
        root.getChildren().addAll(hoursArrow0, hoursArrow1, hoursArrow2, hoursArrow3);
    }

    private void addMinutesArrow(Pane root) {
        root.getChildren().addAll(minutesArrow0, minutesArrow1, minutesArrow2, minutesArrow3);
    }

    private void setSecondsArrowXY(int seconds, double radius, ArrayList<Point> degrees) {
        Platform.runLater(() -> secondsArrow.setEndX((radius - SECONDS_GAP) * degrees.get(seconds).getX() + 150));
        Platform.runLater(() -> secondsArrow.setEndY((radius - SECONDS_GAP) * degrees.get(seconds).getY() + 150));
        Platform.runLater(() -> secondsArrow.setStartX((radius - 90) * degrees.get((seconds + 30) % 60).getX() + 150));
        Platform.runLater(() -> secondsArrow.setStartY((radius - 90) * degrees.get((seconds + 30) % 60).getY() + 150));
    }

    private double[] getMinutesPointsY(int minutes, double radius, ArrayList<Point> degrees) {
        return new double[]{
                (radius - MINUTES_GAP) * degrees.get(minutes).getY() + 150,
                (MINUTES_WIDTH * degrees.get((minutes + 15) % 60).getY()) + 150,
                (MINUTES_WIDTH * 2) * degrees.get((minutes + 30) % 60).getY() + 150,
                (MINUTES_WIDTH * degrees.get((minutes + 45) % 60).getY()) + 150
        };
    }

    private double[] getMinutesPointsX(int minutes, double radius, ArrayList<Point> degrees) {
        return new double[]{
                (radius - MINUTES_GAP) * degrees.get(minutes).getX() + 150,
                MINUTES_WIDTH * degrees.get((minutes + 15) % 60).getX() + 150,
                (MINUTES_WIDTH * 2) * degrees.get((minutes + 30) % 60).getX() + 150,
                MINUTES_WIDTH * degrees.get((minutes + 45) % 60).getX() + 150
        };
    }

    private double[] getHoursPointsX(double hours, double radius, ArrayList<Point> degrees) {
        return new double[]{
                (radius - HOURS_GAP) * degrees.get(((int) (hours * 5) % 60)).getX() + 150,
                (HOURS_WIDTH * degrees.get((int) (((hours + 3) * 5) % 60)).getX()) + 150,
                (HOURS_WIDTH * 2) * degrees.get((int) (((hours + 6) * 5) % 60)).getX() + 150,
                (HOURS_WIDTH * degrees.get((int) (((hours + 9) * 5) % 60)).getX()) + 150
        };
    }

    private double[] getHoursPointsY(double hours, double radius, ArrayList<Point> degrees) {
        return new double[]{
                (radius - HOURS_GAP) * degrees.get((int) (hours * 5) % 60).getY() + 150,
                HOURS_WIDTH * degrees.get((int) ((hours + 3) * 5) % 60).getY() + 150,
                (HOURS_WIDTH * 2) * degrees.get((int) ((hours + 6) * 5) % 60).getY() + 150,
                HOURS_WIDTH * degrees.get(((int) ((hours + 9) * 5) % 60) % 60).getY() + 150
        };
    }

    private void setHoursArrowXY(double[] hoursPointsX, double[] hoursPointsY) {
        Platform.runLater(() -> hoursArrow0.setStartX(hoursPointsX[0]));
        Platform.runLater(() -> hoursArrow0.setStartY(hoursPointsY[0]));
        Platform.runLater(() -> hoursArrow0.setEndX(hoursPointsX[1]));
        Platform.runLater(() -> hoursArrow0.setEndY(hoursPointsY[1]));
        Platform.runLater(() -> hoursArrow1.setStartX(hoursPointsX[1]));
        Platform.runLater(() -> hoursArrow1.setStartY(hoursPointsY[1]));
        Platform.runLater(() -> hoursArrow1.setEndX(hoursPointsX[2]));
        Platform.runLater(() -> hoursArrow1.setEndY(hoursPointsY[2]));
        Platform.runLater(() -> hoursArrow2.setStartX(hoursPointsX[2]));
        Platform.runLater(() -> hoursArrow2.setStartY(hoursPointsY[2]));
        Platform.runLater(() -> hoursArrow2.setEndX(hoursPointsX[3]));
        Platform.runLater(() -> hoursArrow2.setEndY(hoursPointsY[3]));
        Platform.runLater(() -> hoursArrow3.setStartX(hoursPointsX[3]));
        Platform.runLater(() -> hoursArrow3.setStartY(hoursPointsY[3]));
        Platform.runLater(() -> hoursArrow3.setEndX(hoursPointsX[0]));
        Platform.runLater(() -> hoursArrow3.setEndY(hoursPointsY[0]));
    }

    private void setMinutesArrowXY(double[] minutesPointsX, double[] minutesPointsY) {
        Platform.runLater(() -> minutesArrow0.setStartX(minutesPointsX[0]));
        Platform.runLater(() -> minutesArrow0.setStartY(minutesPointsY[0]));
        Platform.runLater(() -> minutesArrow0.setEndX(minutesPointsX[1]));
        Platform.runLater(() -> minutesArrow0.setEndY(minutesPointsY[1]));
        Platform.runLater(() -> minutesArrow1.setStartX(minutesPointsX[1]));
        Platform.runLater(() -> minutesArrow1.setStartY(minutesPointsY[1]));
        Platform.runLater(() -> minutesArrow1.setEndX(minutesPointsX[2]));
        Platform.runLater(() -> minutesArrow1.setEndY(minutesPointsY[2]));
        Platform.runLater(() -> minutesArrow2.setStartX(minutesPointsX[2]));
        Platform.runLater(() -> minutesArrow2.setStartY(minutesPointsY[2]));
        Platform.runLater(() -> minutesArrow2.setEndX(minutesPointsX[3]));
        Platform.runLater(() -> minutesArrow2.setEndY(minutesPointsY[3]));
        Platform.runLater(() -> minutesArrow3.setStartX(minutesPointsX[3]));
        Platform.runLater(() -> minutesArrow3.setStartY(minutesPointsY[3]));
        Platform.runLater(() -> minutesArrow3.setEndX(minutesPointsX[0]));
        Platform.runLater(() -> minutesArrow3.setEndY(minutesPointsY[0]));
    }

    private void makeDashes(int iteration, double radius, ArrayList<Point> degrees, Pane root) {
        Line line;
        if ((iteration % 30) == 0) {
            if ((iteration % 90) == 0) {
                line = makeDash(10, radius, degrees, iteration);
            } else {
                line = makeDash(6, radius, degrees, iteration);
            }
        } else {
            line = makeDash(2, radius, degrees, iteration);
        }
        root.getChildren().addAll(line);
    }

    private Line makeDash(int size, double radius, ArrayList<Point> degrees, int iteration) {

        return new Line(radius * degrees.get(iteration / 6).getX() + 150,
                radius * degrees.get(iteration / 6).getY() + 150,
                (radius - size) * degrees.get(iteration / 6).getX() + 150,
                (radius - size) * degrees.get(iteration / 6).getY() + 150);
    }

    @Override
    public void stop() throws Exception {
        task.cancel();
        super.stop();
    }
}

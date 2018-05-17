package sample;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MainWindow implements Initializable{

    public static boolean init_array[][]; //начальное расположение живых клеток. Настраивается в SettingsMenu
    public static int columns; //начальное количество колонок.Настраивается в SettingsMenu
    public static int rows; //начальное количество строк. Настраивается в SettingsMenu

    @FXML
    BorderPane root;
    @FXML
    GridPane field; // поле, где "кипит жизнь"
    @FXML
    Label lab_round; //отображение номера раунда
    @FXML
    Label lab_population; //отображение количества "живых" клеток
    @FXML
    Slider slider_speed; //изменение интервала между раундами
    @FXML
    Label lab_speed; //отображение значения интервала между раундами

    long time_last = System.currentTimeMillis(); //хранится время прошлого шага
    int time_refresh = 200; //интервал между шагами

    int round = 0; // текущий раунд (шаг)

    double size_rectangle = 30; //размер клетки (прямоугольника)

    static Rectangle rectangles[][]; //хранятся ячейки (прямоугольники) с клетками (чёрный прямоугольник - клетка "жива")
    static boolean cells[][]; //массив для хранения адресов живых клеток на предыдущем шаге
    static boolean new_cells[][]; //согласно интервалу между шагами (time_refresh) поле перерисовывается

    AnimationTimer timer; //согласно интервалу между шагами (time_refresh) поле перерисовывается

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        setControl(i_play, i_arrow);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                boolean b = init_array[i][j];
                new_cells[i][j] = b;
                cells[i][j] = b;
            }
        }


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                rectangles[i][j] = new Rectangle(size_rectangle, size_rectangle);
                field.add(rectangles[i][j], j, i);
            }
        }

        redraw();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long time_current = System.currentTimeMillis();
                if (time_current - time_last >= time_refresh) {
                    step();
                    time_last = time_current;
                }
            }
        };

        slider_speed.valueProperty().addListener((ov, old_value, new_value) -> {
            time_refresh = (int) Math.ceil(new_value.doubleValue());
            lab_speed.setText(String.valueOf(time_refresh));
        }); // изменение интервала раундов (шагов) ползунком
    }

    private void step() {
        round++;
        simulate();
        redraw();

        lab_round.setText(String.valueOf(round));
        lab_population.setText(String.valueOf(population));
    }

    void redraw () {
        population = 0;
        for (int i = 0; i < rectangles.length; i++) {

            for (int j = 0; j < rectangles[0].length; j++) {

                Color color;
                if (new_cells[i][j]) {
                    population++;
                    color = Color.BLACK;
                } else {
                    color = Color.WHITE;
                }
                rectangles[i][j].setFill(color);
            }
        }
    }

    int population;

    void simulate () {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new_cells[i][j];
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {


                int neighbors = 0;
                for (int k = -1; k < 2; k++) {
                    for (int g = -1; g < 2; g++) {
                        if (g == 0 && k == 0) {
                            continue;
                        }
                        try {
                            if (cells[i+k][j+g]) {
                                neighbors++;
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            continue;
                        }
                    }
                }
                if (cells[i][j]) {
                    if (!(neighbors == 2 || neighbors == 3)) {
                        new_cells[i][j] = false;
                    }
                } else {
                    if (neighbors == 3) {
                        new_cells[i][j] = true;
                        population++;
                    }
                }
            }
        }

    }

    public void clickOnSettings () {
        Settings2.setValues(new_cells);
        Stage stage_settings  = new Stage();
        if (Main.stage_settings != null) {
            Main.stage_settings.close();
        }
        Main.stage_settings = stage_settings;

        Scene scene_settings = new Scene(new Settings2());
        scene_settings.getStylesheets().add(getClass().getResource("/Resource/Style/Style.css").toExternalForm());

        stage_settings.setScene(scene_settings);
        stage_settings.show();
        timer.stop();

    }

    public static void setInitValue (int new_rows, int new_columns, boolean color[][]) {
        rows = new_rows;
        columns = new_columns;

        rectangles = new Rectangle[rows][columns];
        cells = new boolean[rows][columns];
        new_cells = new boolean[rows][columns];

        if (color == null) {
            init_array = null;
        } else {
            init_array = new boolean[rows][columns];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    init_array[i][j] = color[i][j];
                }
            }
        }
    }

    @FXML
    ImageView iv_left;
    @FXML
    ImageView iv_center;
    @FXML
    ImageView iv_right;

    Image i_arrow = new Image(getClass().getResourceAsStream("/Resource/arrow.png"));
    Image i_arrow_disable = new Image(getClass().getResourceAsStream("/Resource/arrow_disable.png"));
    Image i_play = new Image(getClass().getResourceAsStream("/Resource/play.png"));
    Image i_pause = new Image(getClass().getResourceAsStream("/Resource/pause.png"));

    boolean play = false;//"Идёт ли анимация?"

    public void clickOnPlay () {
        if (play) {
            setControl(i_play, i_arrow);
            timer.stop();
        } else {
            setControl(i_pause, i_arrow_disable);
            timer.start();
        }
        play = !play;
    }

    private void setControl(Image center, Image side) {
        iv_center.setImage(center);
        iv_left.setImage(side);
        iv_right.setImage(side);
    }

    public void clickOnRight() {
        if (iv_right.getImage().equals(i_arrow)) {
            step();
        }
    }
}

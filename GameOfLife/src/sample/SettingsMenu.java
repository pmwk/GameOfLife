package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

/*
Меню, в котором выставляются настройки, такие как:
-количество строк и столбцов
-начальное расположение живых клеток:
    a) вручную
    b) случайно
    c) выбор стандартных фигур
    d) переданное через MainWindow
    e) Загруженное через файл

*/

public class SettingsMenu implements Initializable{

    private static boolean start_values[][] = null; // используется для задания некого начального положения клеток
    private static int columns = 10; //количество колонок
    private static int rows = 10; //количество строк

    public static void setValues (boolean values[][]) {
        start_values = values;
        rows = values.length;
        columns = values[0].length;
    }

    @FXML
    Label lab_columns; //показывает значение количества колонок
    @FXML
    Label lab_rows; //показывает значение количества строк
    @FXML
    GridPane table; //поле, где можно задать "живые" клетки
    @FXML
    TreeView tree_shapes;
    @FXML
    BorderPane root;

    double size_side = 20; //размер живой клетки

    /*
    Хранится динамичный двумерный массив прямоугольников - если прямоугольник чёрный, то клетка "жива"
    При изменении столбцов/строк - изменяется и rects
    При окончании настройки - полученное поле передаётся в MainWindow
    */
    ArrayList<ArrayList<Cell>> rects = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for (int i = 0; i < rows; i++) {
            rects.add(new ArrayList<Cell>());
            for (int j = 0; j < columns; j++) {
                Cell cell = new Cell();
                table.add (cell, j, i);
                rects.get(i).add(cell);
            }
        } //Создание поля

        if (start_values != null) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (start_values[i][j]) {
                        rects.get(i).get(j).liven();
                    }
                }
            }
        }

        table.setGridLinesVisible(true); //на table выводятся линии

        boolean example[][] = {{true, true}, {true, true}};
        TreeItem<Label> tree_root = new TreeItem<>(new Label ("Shapes"));
        tree_root.setExpanded(true);

        String array_stable_shapes[] = {"Block", "Beehive", "Loaf", "Boat", "Tub"};
        TreeItem<Label> stable_shapes = new TreeItem<>(new Label("Still lifes"));
        for (int i = 0; i < array_stable_shapes.length; i++) {
            stable_shapes.getChildren().add(new TreeItem<>(new Shape(array_stable_shapes[i], example)));
        }

        String array_oscillators_shapes[] = {"Blinker", "Toad", "Beacon", "Pulsar", "Pentadecathlon"};
        TreeItem<Label> oscillators_shapes = new TreeItem<>(new Label("Oscillators"));
        for (int i = 0; i < array_oscillators_shapes.length; i++) {
            oscillators_shapes.getChildren().add(new TreeItem<>(new Shape(array_oscillators_shapes[i], example)));
        }

        tree_root.getChildren().addAll(stable_shapes, oscillators_shapes);
        tree_shapes.setRoot(tree_root);


    }

    /*
    добавление колонки
     */
    public void addColumn() {
        table.setGridLinesVisible(false);
        for (int i = 0; i < rows; i++) {
            Cell cell = new Cell();
            table.add (cell, columns - 1, i);
            rects.get(i).add(cell);
        }
        table.setGridLinesVisible(true);
    }

    /*
    добавление строки
     */
    public void addRow () {
        table.setGridLinesVisible(false);
        ArrayList<Cell> row = new ArrayList<>();
        rects.add(row);
        for (int i = 0; i < columns; i++) {
            Cell cell = new Cell();
            table.add (cell, i, rows - 1);
            row.add(cell);
        }
        table.setGridLinesVisible(true);
    }

    /*
    удаление строки
     */
    public void deleteRow () {
        table.setGridLinesVisible(false);
        ArrayList<Cell> row = rects.get(rows);
        rects.remove(row);
        for (int i = 0; i < columns; i++) {
            table.getChildren().remove(row.get(i));
        }
        table.setGridLinesVisible(true);
    }

    /*
    удаление колонки
     */
    public void deleteColumn () {
        table.setGridLinesVisible(false);
        for (int i = 0; i < rows; i++) {
            Cell cell = rects.get(i).get(columns);
            rects.get(i).remove(cell);
            table.getChildren().remove(cell);
        }
        table.setGridLinesVisible(true);
    }

    /*
    действие, при нажатии на увелечение количества колонок
     */
    public void clickOnPlusColumns () {
        columns++;
        lab_columns.setText(String.valueOf(columns));
        addColumn();
    }

    /*
    действие, при нажатии на уменьшение количества колонок
     */
    public void clickOnMinusColumns () {
        if (columns > 3) {
            columns --;
            lab_columns.setText(String.valueOf(columns));
            deleteColumn();
        }

    }

    /*
    действие, при нажатии на увелечение количества строк
     */
    public void clickOnPlusRows () {
        rows++;
        lab_rows.setText(String.valueOf(rows));
        addRow();
    }

    /*
    действие, при нажатии на уменьшение количества строк
     */
    public void clickOnMinusRows () {
        if (rows > 3) {
            rows --;
            lab_rows.setText(String.valueOf(rows));
            deleteRow();
        }
    }

    /*
    действие, при нажатии на "Start" - окно настроек должно закрыться и открывается поле симуляции
     */
    public void clickOnStart () {
        boolean b[][] = new boolean[rows][columns]; //создаём массив с
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (rects.get(i).get(j).getFill() == Color.BLACK) {
                    b[i][j] = true;
                } else {
                    b[i][j] = false;
                }
            }
        }
        MainWindow.setInitValue(rows, columns, b);
        try {
            Main.stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("MainWindow.fxml"))));
            if (Main.stage_settings != null) {
                Main.stage_settings.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    действие, при нажатии на Random
    поле, случайным образом заполняется живыми клетками
    */
    public void clickOnRandom() {
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (random.nextBoolean()) {
                    rects.get(i).get(j).liven();
                } else {
                    rects.get(i).get(j).kill();
                }
            }
        }
    }

    /*
    действие, при нажатие на кнопку "Clear"
    очищает поле - все клетки "мёртвые"
     */
    public void clickOnClear () {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                rects.get(i).get(j).kill();
            }
        }
    }

    private boolean pressed = false;

    //клетка
    private class Cell extends Rectangle {

        public Cell() {
            super(size_side, size_side, Color.WHITE);

            setOnMouseClicked(event -> {
                Color current_color = (Color) getFill();
                if (current_color == Color.BLACK) {
                    kill();
                } else {
                    liven();
                }
            });

            setOnMouseEntered(event -> {
                setFill(Color.LIGHTGREEN);
                System.out.println(event.getSceneX());
            });

            setOnMouseExited(event -> {
                setFill(Color.WHITE);
            });
        }

        public void liven () {
            setFill(Color.BLACK);
        }

        public void kill () {
            setFill(Color.WHITE);
        }
    }

    private class Shape extends Label{

        Shape2 shape2;

        public Shape (String name, boolean values[][]) {
            super(name);

            shape2 = new Shape2(values);

            setOnDragDetected(event -> {
                pressed = true;
                root.getChildren().add(shape2);
                shape2.setTranslateX(event.getSceneX());
                shape2.setTranslateY(event.getSceneY());
            });

            setOnMouseDragged(event2 -> {
                shape2.setTranslateX(event2.getSceneX());
                shape2.setTranslateY(event2.getSceneY());

            });

            setOnMouseReleased(event2 -> {
                pressed = false;
                root.getChildren().remove(shape2);
            });

        }

    }

    private class Shape2 extends GridPane {
        public Shape2(boolean values[][]) {
            for (int i = 0; i < values.length; i++) {
                for (int j = 0; j < values[0].length; j++) {
                    Cell cell = new Cell();
                    add(cell, j, i);
                    if (values[i][j]) {
                        cell.liven();
                    }
                }
            }

        }
    }

}

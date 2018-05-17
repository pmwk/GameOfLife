package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Settings2 extends Pane {

    private final double offsetX_root_rows_columns  = -240; //отступ от правого края root_rows_columns

    private final double offsetY_title = -20; //отступ от вверха для подписи окна (lab_title)
    private final double offsetY_btn_start = -80; //отступ от низа btn_start

    private final double size_cell = 20;


    private final double height_tree = 300; //высота дерева со стандартными элементами
    private final double width_tree = 200; //высота дерева со стандартными элементами
    private static int rows = 10;
    private static int columns = 10;
    GridPane field = new GridPane();
    ArrayList<ArrayList<Cell>> cells = new ArrayList<>();

    private static boolean start_values[][] = null; // используется для задания некого начального положения клеток

    public Settings2() {
        super();

        if (Main.stage_settings != null) {
            Main.stage_settings.setHeight(height_tree + rows * size_cell + 120);
            Main.stage_settings.setWidth(width_tree - offsetX_root_rows_columns + columns * size_cell + 150);
        }

        Label lab_title = new Label("Settings");
        lab_title.getStyleClass().add("title0");
        lab_title.setTranslateY(10); //Label с подписью окна

        TreeView tv_shape = new TreeView();
        tv_shape.setPrefSize(width_tree, height_tree);
        tv_shape.setTranslateX(10); //Здесь хранится дерево со стандартными фигурами
        initTree(tv_shape);

        VBox root_rows_columns = new VBox();
        root_rows_columns.getStyleClass().add("title1");
        rowPlusMinus row_rows = new rowPlusMinus(rows, "rows") {
            @Override
            void plus() {
                addRow();
            }

            @Override
            void minus() {
                deleteRow();
            }
        };
        rowPlusMinus row_columns = new rowPlusMinus(columns, "columns") {
            @Override
            void plus() {
                addColumn();
            }

            @Override
            void minus() {
                deleteColumn();
            }
        };
        root_rows_columns.setAlignment(Pos.CENTER);
        root_rows_columns.getChildren().addAll(row_rows, row_columns); //здесь хранятся настройки с количеством столбцов и строк


        Button btn_start = new Button("Start");
        btn_start.getStyleClass().add("btn1"); //кнопка старт - закрывает окно настроек, и в окно "Симуляции"



        /*
        *Создание field - где можно задавать начальные значения колонии
         */

        VBox root_field = new VBox(); //панель, где хранится field, btn_random, btn_clear
        root_field.setAlignment(Pos.CENTER);
        root_field.setSpacing(5);
        // (само поле, кнопка рандомного заполнения, кнопка очистки поля)
        for (int i = 0; i < rows; i++) {
            cells.add(new ArrayList<Cell>());
            for (int j = 0; j < columns; j++) {
                Cell cell = new Cell();
                field.add (cell, j, i);
                cells.get(i).add(cell);
            }
        } //Создание поля

        if (start_values != null) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (start_values[i][j]) {
                        cells.get(i).get(j).liven();
                    }
                }
            }
        }

        field.setGridLinesVisible(true); //на table выводятся линии

        Button btn_random = new Button("Random");
        btn_random.getStyleClass().add("btn2"); //кнопка рандома - поле заполняется случайным образом

        Button btn_clear = new Button("Clear");
        btn_clear.getStyleClass().add("btn3"); //кнопка очистки - поле очищается (все клетки умирают)ё

        root_field.getChildren().addAll(field, btn_random, btn_clear);

        getChildren().addAll(lab_title, tv_shape, root_rows_columns, btn_start, root_field);

        widthProperty().addListener((ov, old_value, new_value) -> {
            lab_title.setTranslateX(new_value.doubleValue() / 2 + offsetY_title);

            root_rows_columns.setTranslateX(new_value.doubleValue() + offsetX_root_rows_columns);

            btn_start.setTranslateX(new_value.doubleValue() / 2);

            root_field.setTranslateX(new_value.doubleValue() / 2 - columns * size_cell/2);

            currentX_table = field.getParent().getTranslateX();
        }); //компоновка элементов, при измнении ширины окна

        heightProperty().addListener((ov, old_value, new_value) -> {
            tv_shape.setTranslateY(new_value.doubleValue() * 0.3);

            root_rows_columns.setTranslateY(tv_shape.getTranslateY());

            btn_start.setTranslateY(new_value.doubleValue() + offsetY_btn_start);

            root_field.setTranslateY(new_value.doubleValue() / 2 - rows * size_cell / 2);

            currentY_table = field.getParent().getTranslateY();
        }); //компоновка элементов, при при изменении высоты окна

        btn_random.setOnMouseClicked(event -> {
            Random random = new Random(System.currentTimeMillis());
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (random.nextBoolean()) {
                        cells.get(i).get(j).liven();
                    } else {
                        cells.get(i).get(j).kill();
                    }
                }
            }
        }); //случайное заполнение колонии

        btn_clear.setOnMouseClicked(event -> {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    cells.get(i).get(j).kill();
                }
            }
        }); //очистка поля

        btn_start.setOnMouseClicked(event -> {
            boolean b[][] = new boolean[rows][columns]; //создаём массив с
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (cells.get(i).get(j).getFill() == Color.BLACK) {
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
                if (!Main.stage.isShowing()) {
                    Main.stage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }); //передаём настройки на поле симуляции, закрываем окно

/*        bottom.addListener((ov, old_value, new_value) -> {
            System.out.println("NewValue - " + new_value);
            System.out.println("oldValue - " + old_value);
            int difference = (int) (new_value.doubleValue() - old_value.doubleValue());
            if (difference > 0) {
                addRows(difference);
                removeMaskWithoutMask();
            } else if (difference < 0) {
                deleteRow();
            }
        });*/
    }

    public void addRow() {
        rows++;

        field.setGridLinesVisible(false);
        ArrayList<Cell> row = new ArrayList<>();
        cells.add(row);
        for (int i = 0; i < columns; i++) {
            Cell cell = new Cell();
            field.add (cell, i, rows - 1);
            row.add(cell);

        }
        if (Main.stage_settings.getHeight() < height_tree + rows * size_cell + 120) {
            double new_height = Main.stage_settings.getHeight() + size_cell;
            Main.stage.setHeight(new_height);
        } //обработка, что при добавлении строки, окно расширялось
        field.setGridLinesVisible(true);
    }

    public void deleteRow() {
        rows--;

        field.setGridLinesVisible(false);
        ArrayList<Cell> row = cells.get(rows);
        cells.remove(row);
        for (int i = 0; i < columns; i++) {
            field.getChildren().remove(row.get(i));
        }
        field.setGridLinesVisible(true);
    }

    public void addColumn() {
        columns++;

        field.setGridLinesVisible(false);
        for (int i = 0; i < rows; i++) {
            Cell cell = new Cell();
            field.add (cell, columns - 1, i);
            cells.get(i).add(cell);
        }
        if (getWidth() < width_tree - offsetX_root_rows_columns + columns * size_cell + 150) {
            Main.stage.setWidth(Main.stage_settings.getWidth() + size_cell);
        }
        field.setGridLinesVisible(true);
    }

    public void deleteColumn() {
        columns--;

        field.setGridLinesVisible(false);
        for (int i = 0; i < rows; i++) {
            Cell cell = cells.get(i).get(columns);
            cells.get(i).remove(cell);
            field.getChildren().remove(cell);
        }
        field.setGridLinesVisible(true);
    }

    public static void setValues (boolean values[][]) {
        start_values = values;
        rows = values.length;
        columns = values[0].length;
    }

    private void initTree (TreeView tv_shape) {
        TreeItem<Label> tree_root = new TreeItem<>(new Label ("Shapes"));
        tree_root.setExpanded(true);


        boolean array_stable_shapes_b[][][] = {{{true, true}, {true, true}},
                {{false, true, true, false}, {true, false, false, true}, {false, true, true, false}},
                {{false, true, true, false}, {true, false, false, true}, {false, true, false, true}, {false, false, true, false}},
                {{true, true, false}, {true, false, true}, {false, true, false}},
                {{false, true, false}, {true, false, true}, {false, true, false}}};
        String array_stable_shapes[] = {"Block", "Beehive", "Loaf", "Boat", "Tub"};
        TreeItem<Label> stable_shapes = new TreeItem<>(new Label("Still lifes"));
        for (int i = 0; i < array_stable_shapes.length; i++) {
            stable_shapes.getChildren().add(new TreeItem<>(new LabelShape(array_stable_shapes[i], array_stable_shapes_b[i])));
        }

        boolean array_oscillators_shapes_b[][][] = {{{true, true, true}},
                {{false, true, true, true}, {true, true, true, false}},
                {{true, true, false, false}, {true, true, false, false}, {false, false, true, true}, {false, false, true, true}}};
        String array_oscillators_shapes[] = {"Blinker", "Toad", "Beacon"}; //ДОБАВИТЬ  "Pulsar", "Pentadecathlon"
        TreeItem<Label> oscillators_shapes = new TreeItem<>(new Label("Oscillators"));
        for (int i = 0; i < array_oscillators_shapes.length; i++) {
            oscillators_shapes.getChildren().add(new TreeItem<>(new LabelShape(array_oscillators_shapes[i], array_oscillators_shapes_b[i])));
        }

        boolean array_spaceships_shapes_b[][][] = {{{true, false, true}, {false, true, true}, {false, true, false}},
                {{false, false, true, true, false}, {true, true, false, true, true}, {true, true, true, true, false}, {false, true, true, false, false}}};
        String array_spaceships_shapes[] = {"Glider", "Lightweight spaceships"};
        TreeItem<Label> spaceships_shapes = new TreeItem<>(new Label("Spaceships"));
        for (int i = 0; i < array_spaceships_shapes.length; i++) {
            spaceships_shapes.getChildren().add(new TreeItem<>(new LabelShape(array_spaceships_shapes[i], array_spaceships_shapes_b[i])));
        }

        /*boolean array_gun_shapes_b[][][] = {{{}}};
        String array_gun_shapes[] = {"Gosper gilder gun"};
        TreeItem<Label> gun_shapes = new TreeItem<>(new Label("Guns"));
        for (int i = 0; i < array_gun_shapes.length; i++) {
            gun_shapes.getChildren().add(new TreeItem<>(new LabelShape(array_gun_shapes[i], array_gun_shapes_b[i])));
        }*/

        tree_root.getChildren().addAll(stable_shapes, oscillators_shapes, spaceships_shapes);
        tv_shape.setRoot(tree_root);

    }

    /*
    *Дополнительный элемент GUI - строка, где видно значение, подпись, и возможно изменять значение с помощью "+" и "-"
    */
    abstract private class rowPlusMinus extends HBox {

        private Label lab_value; //label со значением
        private VBox root_control = new VBox(); //панель для хранения "+" и "-"
        private Label lab_plus = new Label("+"); //увеличит значение на 1
        private Label lab_minus = new Label("-"); //уменьшит значение на 1, но не меньше 3
        private Label lab_name; // имя атрибута
        private int value;

        public rowPlusMinus(int startValue, String name) {
            value = startValue;

            lab_value = new Label(String.valueOf(value));
            lab_name = new Label(name);

            root_control.setAlignment(Pos.CENTER);
            root_control.getChildren().addAll(lab_plus, lab_minus);

            setAlignment(Pos.CENTER);
            getChildren().addAll(lab_value, root_control, lab_name);

            lab_plus.setOnMouseClicked(event -> {
                value++;
                lab_value.setText(String.valueOf(value));
                plus();
            });

            lab_minus.setOnMouseClicked(event -> {
                if (value > 3) {
                    value--;
                    lab_value.setText(String.valueOf(value));
                    minus();
                }
            });
        }

        abstract void plus();
        abstract void minus();
    }
    /*
    *Дополнительный элемент GUI - клетка: Rectangle принажатии оживает (Color.Black), при повторном умирает (Color.White)
     */
    private class Cell extends Rectangle {

        private Color currentColor = Color.WHITE;

        public Cell() {
            super(size_cell, size_cell, Color.WHITE);

            setOnMouseClicked(event -> {
                Color current_color = (Color) getFill();
                if (current_color == Color.BLACK) {
                    kill();
                } else {
                    liven();
                }
            });
        }

        public void liven () {
            setFill(Color.BLACK);
            currentColor = Color.BLACK;
        }

        public void kill () {
            setFill(Color.WHITE);
            currentColor = Color.WHITE;
        }

        public void setMaskDead() {
            setFill(Color.LIGHTGREEN);
        }

        public void setMaskAlive () {
            setFill(Color.RED);
        }

        public void removeMask() {
            setFill(currentColor);
        };

        public boolean isAlive() {
            if (currentColor == Color.WHITE || currentColor == Color.LIGHTGREEN) {
                return true;
            } else {
                return false;
            }
        }
    }

    /*
    * дополнительный элемент GUI - Label,
    * при зажатии которой образуется фигура, перемещающейся за курсором.
    * Если отпустить надо полем - то фигура отобразится, иначе пропадёт
    * */

    private double currentX_table;
    private double currentY_table;

    private boolean isInTable (double x, double y) {
        return x > currentX_table && x < currentX_table + columns*size_cell && y > currentY_table && y < currentY_table + rows * size_cell;
    } //определяет по входным координатам принадлежит ли точка таблице

    private int getColumnCell (double x) {
        return (int) Math.floor((x - currentX_table) / size_cell);
    } //определяет в каком столбце находится точка по её x-координате

    private int getRowCell (double y) {
        return (int) Math.floor((y - currentY_table) / size_cell);
    } //определяет в каком столбце находится точка по её x-координате

    private class LabelShape extends Label {

        int oldRow = -1;
        int oldColumn = -1;

        public LabelShape(String name, boolean shape[][]) {
            super(name);

            GridPane field_shape = new GridPane();
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    Cell cell = new Cell();
                    if (shape[i][j]) {
                        cell.liven();
                    } else {
                        cell.kill();
                    }
                    field_shape.add(cell, j, i);
                }
            }

            setOnMousePressed(event -> {
                Settings2.this.getChildren().add(field_shape);
                field_shape.setTranslateX(event.getSceneX());
                field_shape.setTranslateY(event.getSceneY());
            });

            setOnMouseDragged(event -> {
                double x = event.getSceneX();
                double y = event.getSceneY();
                field_shape.setTranslateX(x);
                field_shape.setTranslateY(y);

                if (isInTable(x, y)) {
                    int currentRow = getRowCell(y);
                    int currentColumn = getColumnCell(x);
                    if (oldRow == -1) {
                        field_shape.setOpacity(0.20);
                    }
                    if (oldRow != currentRow || oldColumn != currentColumn) {
                        oldRow = currentRow;
                        oldColumn = currentColumn;
                        setIllusionShapeOnField(shape, currentRow, currentColumn);
                    }
                } else {
                    if (oldRow != -1) {
                        field_shape.setOpacity(1);
                        oldRow = -1;
                        oldColumn = -1;
                        removeMask();
                    }
                }
            });

            setOnMouseReleased(event -> {
                Settings2.this.getChildren().remove(field_shape);
                oldRow = -1;
                oldColumn = -1;
                field_shape.setOpacity(1);


                double x = event.getSceneX();
                double y = event.getSceneY();
                if (x > currentX_table && x < currentX_table + columns*size_cell && y > currentY_table && y < currentY_table + rows * size_cell) {
                    int currentY = (int) Math.floor((x - currentX_table) / size_cell);
                    int currentX = (int) Math.floor((y - currentY_table) / size_cell);

                    setShapeOnField(shape, currentX, currentY);
                }
            });
        }
    }

    private void addRows (int size) {
        for (int i = 0; i < size; i++) {
            addRow();
        }
    } //добавляет определёное количество строк

    private void addColumns (int size) {
        for (int i = 0; i < size; i++) {
            addColumn();
        }
    } //добавляет определённое количеством столбцов


    private SimpleIntegerProperty bottom = new SimpleIntegerProperty(0);
    private int right = 0;

    public void setIllusionShapeOnField (boolean shape[][], int x, int y) {
        removeMaskWithoutMask();

        /*bottom.set((x + shape.length + 1) - rows);

        *//*if (x + shape.length + 1 > rows + bottom.get()) {
            addRows((x + shape.length + 1) - rows);
            bottom.set(bottom.getValue() + 1);
        }*//*

        if (y + shape[0].length + 1 > columns) {
            addColumns((y + shape[0].length + 1) - columns);
        }*/


        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                if (i+x >= cells.size()) {
                    continue;
                }
                if (j+y >= cells.get(0).size()) {
                    continue;
                }
                if (shape[i][j]) {
                    cells.get(i + x).get(j + y).setMaskAlive();
                } else {
                    cells.get(i + x).get(j + y).setMaskDead();
                }
            }
        }
    }

    private void removeMask() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells.get(i).get(j).removeMask();
            }
        }
    }

    private void removeMaskWithoutMask () {
        removeMask();

        for (int i = 0; i < bottom.doubleValue(); i++) {
            ArrayList<Cell> row = cells.get(rows - i - 1);
            for (Cell cell : row) {
                cell.setMaskDead();
            }
        }
    }

    private void setShapeOnField (boolean shape[][], int x, int y) {
        removeMask();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                if (i+x >= cells.size()) {
                    continue;
                }
                if (j+y >= cells.get(0).size()) {
                    continue;
                }
                if (shape[i][j]) {
                    cells.get(i + x).get(j + y).liven();
                } else {
                    cells.get(i + x).get(j + y).kill();
                }
            }
        }
    }

}

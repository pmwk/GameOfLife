package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage stage;
    public static Stage stage_settings = new Stage();

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        //Parent root = FXMLLoader.load(getClass().getResource("Settings.fxml")); //при запуске открывается окно настроек, где можно задать начальные параметры
        Pane root = new Settings2();
        stage_settings.setTitle("Conway's Game of Life"); //title окна
        stage_settings.setScene(new Scene(root, 800, 600));
        stage_settings.getScene().getStylesheets().add(getClass().getResource("/Resource/Style/Style.css").toExternalForm());
        stage_settings.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}

package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main extends Application {

    private Parent root;
    private Scene scene;
    @FXML private TextField filePath;
    @FXML private Button fileChooser;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("sample/NastiaMemo.fxml"));
        loader.setController(this);
        root = loader.load();
        scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {

        launch(args);
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

    }

    public void onOpenChooser(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Memo text files", "*.txt"));
        chooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        try {
            File file = chooser.showOpenDialog(scene.getWindow());
            Scanner scanner = new Scanner(file);
            System.out.println("Wybrano: " + file.getAbsolutePath());
            filePath.setText(file.getAbsolutePath());
        }
        catch(NullPointerException e) {
            e.printStackTrace();
            return;
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            return;
        }



    }
}

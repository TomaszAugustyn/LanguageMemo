package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main extends Application {

    private Parent root;
    private Scene scene;
    private WordEntryList wordEntryList;
    private ObservableList<WordEntry> data;
    @FXML private TextField filePath;
    @FXML private Button fileChooser;
    @FXML private TableView<WordEntry> table = new TableView<WordEntry>();
    @FXML private TableColumn wordColumn;
    @FXML private TableColumn translationColumn;


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("sample/NastiaMemo.fxml"));
        loader.setController(this);
        root = loader.load();
        scene = new Scene(root);
        primaryStage.setTitle("NastiaMemo");
        primaryStage.setScene(scene);
        primaryStage.show();
        initTable();
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
            String line = "";
            wordEntryList = new WordEntryList();

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if(line.contains(";")){
                    List<String> colonList = new ArrayList<String>(Arrays.asList(line.split(";")));
                    for (String s : colonList) {
                        if(s.contains(",")){
                            List<String> comaList = new ArrayList<String>(Arrays.asList(s.split(",")));
                            WordEntry wordEntry = new WordEntry(comaList.get(0), comaList.get(1));
                            wordEntryList.addWord(wordEntry);
                        }
                       else{
                            continue;
                        }
                    }
                }
                else{
                    continue;
                }

            }
            System.out.println("Chosen: " + file.getAbsolutePath());
            filePath.setText(file.getAbsolutePath());
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            System.out.println("File not found.");
        }

        fillTable(wordEntryList);
        System.out.println(wordEntryList);
        return;
    }

    private void fillTable(WordEntryList wordEntryList){
        data = FXCollections.observableArrayList(wordEntryList.getWordsList());
        table.setItems(data);
        table.getColumns().addAll(wordColumn, translationColumn);

    }

    private void initTable() {
        wordColumn = new TableColumn("Word");
        translationColumn = new TableColumn("Translation");
        wordColumn.setCellValueFactory(
                new PropertyValueFactory<WordEntry,String>("word")
        );
        translationColumn.setCellValueFactory(
                new PropertyValueFactory<WordEntry,String>("translation")
        );


    }

}



package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {

    private Parent root;
    private Scene scene;
    private WordEntryList wordEntryList;
    private ObservableList<WordEntry> data;
    private File loadedFile;
    @FXML private TextField filePath;
    @FXML private TextField wordField;
    @FXML private TextField translationField;
    @FXML private Button fileChooser;
    @FXML private TableView<WordEntry> table = new TableView<WordEntry>();
    @FXML private TableColumn<WordEntry, String> wordColumn;
    @FXML private TableColumn<WordEntry, String> translationColumn;


    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("sample/NastiaMemo.fxml"));
        loader.setController(this);
        root = loader.load();
        scene = new Scene(root);
        stage.setTitle("NastiaMemo");
        stage.setScene(scene);
        stage.show();
        initTable();
        loadedFile = new File(System.getProperty("user.dir") + "\\LanguageMemo.txt");
        if (!loadedFile.exists()) {
            loadedFile.createNewFile();
        }
        getWordEntryListFromFile(loadedFile);
        fillTable(wordEntryList);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                String currentFilePath = filePath.getText();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save changes to \"" + currentFilePath +"\"?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();

                if(alert.getResult() == ButtonType.YES) {
                    WriteFileFromWordEntryList(loadedFile);
                }
            }
        });

    }

    public static void main(String[] args) {

        launch(args);
        System.out.println("Working Directory = " +
                System.getProperty("user.dir") );

    }

    private void WriteFileFromWordEntryList(File loadedFile) {

        String streamedString = wordEntryList.getWordsList()
                                            .stream()
                                            .map(wordEntry -> wordEntry.getWord() + "," + wordEntry.getTranslation() + ";")
                                            .collect(Collectors.joining());
        try {
            FileWriter fileWriter = new FileWriter(loadedFile, false);
            fileWriter.write(streamedString);
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot write to \"" + loadedFile.getPath() + "\" file.");
        }
    }


    public void onOpenChooser(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Memo text files", "*.txt"));
        chooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        try {
            loadedFile = chooser.showOpenDialog(scene.getWindow());
            getWordEntryListFromFile(loadedFile);
            fillTable(wordEntryList);
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            System.out.println("File not found.");
        }

    }

    public void onAddWordBtnClicked(ActionEvent actionEvent) {
        String word = wordField.getText();
        String translation = translationField.getText();

        if(word.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Word field must not be empty.", ButtonType.OK);
            alert.showAndWait();

        }
        else if(translation.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Translation field must not be empty.", ButtonType.OK);
            alert.showAndWait();
        }
        else{
            wordEntryList.addWord(new WordEntry(word, translation));
            fillTable(wordEntryList);
            wordField.clear();
            translationField.clear();
        }
    }


    private void getWordEntryListFromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(file));
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
                }
            }

        }
        System.out.println("Chosen: " + file.getAbsolutePath());
        filePath.setText(file.getAbsolutePath());

    }

    private void fillTable(WordEntryList wordEntryList){

        table.getColumns().clear();
        data = FXCollections.observableArrayList(wordEntryList.getWordsList());
        table.setItems(data);
        table.getColumns().addAll(wordColumn, translationColumn);

        /*data.removeAll();
        data.addAll(wordEntryList.getWordsList());*/

    }

    private void initTable() {
        wordColumn = new TableColumn("Word");
        translationColumn = new TableColumn("Translation");
        wordColumn.impl_setWidth(125);
        translationColumn.impl_setWidth(122);

        wordColumn.setCellValueFactory(
                new PropertyValueFactory<WordEntry,String>("Word")
        );
        translationColumn.setCellValueFactory(
                new PropertyValueFactory<WordEntry,String>("Translation")
        );

    }

}



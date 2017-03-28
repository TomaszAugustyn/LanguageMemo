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
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * App for creating language memos supporting learining new words (JavaFX).
 * @author Tomasz Augustyn
 */
public class Main extends Application {

    private Parent root;
    private Scene scene;
    private WordEntryList wordEntryList;
    private WordEntryList initialWordEntryList;
    private ObservableList<WordEntry> data;
    private File loadedFile;

    @FXML private TabLearningController tabLearningController;
    @FXML private TextField filePath;
    @FXML private AutoCompleteTextField wordField;
    @FXML private AutoCompleteTextField translationField;
    @FXML private Button fileChooser;
    @FXML private Button deleteWordBtn;
    @FXML private Button addWordBtn;
    @FXML private TableView<WordEntry> table = new TableView<WordEntry>();
    @FXML private TableColumn<WordEntry, String> wordColumn;
    @FXML private TableColumn<WordEntry, String> translationColumn;
    @FXML private Region addAndDeleteRegion;
    @FXML private ToggleSwitch toggle;
    @FXML private Label label;



    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("sample/resources/NastiaMemo.fxml"));
        loader.setController(this);
        root = loader.load();
        scene = new Scene(root);
        stage.setTitle("NastiaMemo");
        stage.setScene(scene);
        stage.show();
        scene.getStylesheets().add("sample/resources/mySwitch.css");
        tabLearningController.init(this);

        initTable();
        loadedFile = new File(System.getProperty("user.dir") + "\\LanguageMemo.txt");
        if (!loadedFile.exists()) {
            loadedFile.createNewFile();
        }
        wordEntryList = getWordEntryListFromFile(loadedFile);
        initialWordEntryList = getWordEntryListFromFile(loadedFile);
        afterWordsListChanged();
        setToggleSwitchOnMouseClicked();
        wordField.setPopupEnabled(false);   //program start in AddWord mode, so we disable the popup
        translationField.setPopupEnabled(false);    //program start in AddWord mode, so we disable the popup
        //addAndDeleteRegion.setStyle("-fx-background-color: #ff4855");

        table.setOnMouseClicked(mc -> {
            if(!toggle.isSelected()){
                WordEntry wordEntry = table.getSelectionModel().getSelectedItem();
                wordField.setText(wordEntry.getWord());
                wordField.hidePopup();
                translationField.setText(wordEntry.getTranslation());
                translationField.hidePopup();

            }
        });

        stage.setOnCloseRequest(we -> {
            if(!wordEntryList.equals(initialWordEntryList))
            {
                String currentFilePath = filePath.getText();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save changes to \"" + currentFilePath +"\"?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();

                if(alert.getResult() == ButtonType.YES) {
                    WriteFileFromWordEntryList(loadedFile);
                }
            }
        });

        class WordAutoCompleteListener implements AutoCompleteSelectedEventListener {

            //implement the required method(s) of the interface
            @Override
            public void handleAutoCompleteSelected(EventObject e, String selectedText){

                String translation = wordEntryList.getEntryEquivalent(selectedText, WordEntryList.eEntries.WORD);
                translationField.setText(translation);
                translationField.hidePopup();
            }
        }

        class TranslationAutoCompleteListener implements AutoCompleteSelectedEventListener {

            //implement the required method(s) of the interface
            @Override
            public void handleAutoCompleteSelected(EventObject e, String selectedText){

                String word = wordEntryList.getEntryEquivalent(selectedText, WordEntryList.eEntries.TRANSLATION);
                wordField.setText(word);
                wordField.hidePopup();
            }
        }

        wordField.addEventListener(new WordAutoCompleteListener());
        translationField.addEventListener(new TranslationAutoCompleteListener());

    }

    private void afterWordsListChanged() {
        fillTable(wordEntryList);
        recalculateWordsCounter();
        wordField.clearEntries();
        translationField.clearEntries();
        wordField.getEntries().addAll(wordEntryList.getWordsAsList());
        translationField.getEntries().addAll(wordEntryList.getTranslationsAsList());

    }

    public static void main(String[] args) {

        launch(args);
    }

    private void setToggleSwitchOnMouseClicked() {
        toggle.setOnMouseClicked(t -> {
            if (toggle.isSelected()){
                toggle.setText("Add mode");
                toggle.setStyle("-fx-base: limegreen");
                addWordBtn.setDisable(false);
                deleteWordBtn.setDisable(true);
                wordField.setPopupEnabled(false);
                translationField.setPopupEnabled(false);

            }
            else {
                toggle.setText("Delete mode");
                toggle.setStyle("-fx-base:  #ff4855");
                addWordBtn.setDisable(true);
                deleteWordBtn.setDisable(false);
                wordField.setPopupEnabled(true);
                translationField.setPopupEnabled(true);

            }
            wordField.clear();
            translationField.clear();
            t.consume();
        });
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
            wordEntryList = getWordEntryListFromFile(loadedFile);
            afterWordsListChanged();
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
            afterWordsListChanged();
            wordField.clear();
            translationField.clear();
        }
    }

    public void onDeleteWordBtnClicked(ActionEvent actionEvent){
        String word = wordField.getText();
        String translation = translationField.getText();
        WordEntry wordEntry = new WordEntry(word, translation);

        if(word.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Word field must not be empty. Click on the table to load a word entry.", ButtonType.OK);
            alert.showAndWait();

        }
        else if(translation.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Translation field must not be empty. Click on the table to load a word entry.", ButtonType.OK);
            alert.showAndWait();
        }
        else if(wordEntryList.isWordEntryOnList(wordEntry)){
            wordEntryList.deleteWord(wordEntry);
            afterWordsListChanged();
            wordField.clear();
            translationField.clear();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The word entry you want to delete doesn't exist in your dictionary.", ButtonType.OK);
            alert.showAndWait();
            wordField.clear();
            translationField.clear();
        }
    }

    private void recalculateWordsCounter(){
        long lCounter = wordEntryList.getCount();
        label.setText("  Your dictionary has:\n" + Long.toString(lCounter) + " entries");

    }

    private WordEntryList getWordEntryListFromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(file));
        String line = "";
        WordEntryList wordEntryListLocal = new WordEntryList();

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if(line.contains(";")){
                List<String> colonList = new ArrayList<String>(Arrays.asList(line.split(";")));
                for (String s : colonList) {
                    if(s.contains(",")){
                        List<String> comaList = new ArrayList<String>(Arrays.asList(s.split(",")));
                        WordEntry wordEntry = new WordEntry(comaList.get(0), comaList.get(1));
                        wordEntryListLocal.addWord(wordEntry);
                    }
                }
            }

        }
        System.out.println("Chosen: " + file.getAbsolutePath());
        filePath.setText(file.getAbsolutePath());
        fileChooser.requestFocus();

        return wordEntryListLocal;

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

    public WordEntryList getWordEntryList(){
        return this.wordEntryList;
    }


}


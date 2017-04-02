package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import org.controlsfx.control.ToggleSwitch;

import java.io.File;
import java.util.EventObject;

/**
 * Created by Tomek on 28.03.2017.
 */
public class TabWordsController {

    private Main main;
    private File loadedFile;
    private WordEntryList wordEntryList;

    private ObservableList<WordEntry> data;

    @FXML private AutoCompleteTextField wordField;
    @FXML private AutoCompleteTextField translationField;
    @FXML private Button deleteWordBtn;
    @FXML private Button addWordBtn;
    @FXML private TableView<WordEntry> table = new TableView<WordEntry>();
    @FXML private TableColumn<WordEntry, String> wordColumn;
    @FXML private TableColumn<WordEntry, String> translationColumn;
    @FXML private Region addAndDeleteRegion;
    @FXML private ToggleSwitch toggle;
    @FXML private Label label;

    public void init(Main mainController){
        this.main = mainController;
        this.loadedFile = main.getLoadedFile();
        this.wordEntryList = main.getWordEntryList();

        initTable();
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

    public void afterWordsListChanged() {
        this.loadedFile = main.getLoadedFile();
        this.wordEntryList = main.getWordEntryList();
        fillTable(wordEntryList);
        recalculateWordsCounter();
        wordField.clearEntries();
        translationField.clearEntries();
        wordField.getEntries().addAll(wordEntryList.getWordsAsList());
        translationField.getEntries().addAll(wordEntryList.getTranslationsAsList());
        main.enableOrDisableStartLearningButton();

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

package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import org.controlsfx.control.ToggleSwitch;
import java.io.File;
import java.util.EventObject;


/**
 * This is a class for controlling "Words" tab responsible for
 * managing words/translations dictionary
 * @author Tomasz Augustyn
 */
public class TabWordsController {

    private Main main;
    private File loadedFile;
    private WordEntryList wordEntryList;
    private ObservableList<WordEntry> data;
    private final int WORD_COLUMN_DEFAULT_WIDTH = 125;
    private final int TRANSLATION_COLUMN_DEFAULT_WIDTH = 122;
    public static double TOGGLE_SWITCH_BTN_POS_OFFSET_X = 0.75;
    public static double TOGGLE_SWITCH_BTN_POS_OFFSET_Y = 0.50;

    @FXML public AutoCompleteTextField wordField;
    @FXML public AutoCompleteTextField translationField;
    @FXML public Button deleteWordBtn;
    @FXML public Button addWordBtn;
    @FXML public TableView<WordEntry> table = new TableView<WordEntry>();
    @FXML public TableColumn<WordEntry, String> wordColumn;
    @FXML public TableColumn<WordEntry, String> translationColumn;
    @FXML public ToggleSwitch toggle;
    @FXML public Label label;
    @FXML public Region addAndDeleteRegion;
    @FXML public SplitPane splitPane;

    public void init(Main mainController){
        this.main = mainController;
        this.loadedFile = main.getLoadedFile();
        this.wordEntryList = main.getWordEntryList();

        initTable();
        afterWordsListChanged();
        setToggleSwitchOnMouseClicked();
        wordField.setPopupEnabled(false);   //program start in AddWord mode, so we disable the popup
        translationField.setPopupEnabled(false);    //program start in AddWord mode, so we disable the popup

        table.setOnMouseClicked(mc -> {
            tableSelectionChanged();
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null ) {
                tableSelectionChanged();
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
            toggleSwitchBtn();
            t.consume();
        });
    }

    public void toggleSwitchBtn() {
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
    }

    public void onAddWordBtnClicked() {
        String word = wordField.getText();
        String translation = translationField.getText();
        WordEntry wordEntry = new WordEntry(word, translation);

        if(word.isEmpty() || translation.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Word and Translation fields must not be empty.", ButtonType.OK);
            alert.showAndWait();
            if (word.isEmpty())
                Tools.vanishGlowEffect(this.wordField);
            if (translation.isEmpty())
                Tools.vanishGlowEffect(this.translationField);
            return;
        }
        if(wordEntryList.isWordEntryOnList(wordEntry)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "This word entry [word: " + word + ", translation: " + translation + "] " + "is already in your dictionary.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        wordEntryList.addWord(new WordEntry(word.trim(), translation.trim()));
        afterWordsListChanged();
        wordField.clear();
        translationField.clear();

    }

    public void onDeleteWordBtnClicked(){
        String word = wordField.getText();
        String translation = translationField.getText();
        WordEntry wordEntry = new WordEntry(word, translation);

        if(word.isEmpty() || translation.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Word and Translation fields must not be empty. Click on the table to load a word entry.", ButtonType.OK);
            alert.showAndWait();
            if (word.isEmpty())
                Tools.vanishGlowEffect(this.wordField);
            if (translation.isEmpty())
                Tools.vanishGlowEffect(this.translationField);
            return;
        }
        if(!wordEntryList.isWordEntryOnList(wordEntry)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The word entry you want to delete doesn't exist in your dictionary.", ButtonType.OK);
            alert.showAndWait();
            wordField.clear();
            translationField.clear();
            return;
        }

        wordEntryList.deleteWord(wordEntry);
        afterWordsListChanged();
        wordField.clear();
        translationField.clear();
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

    }

    private void initTable() {
        wordColumn = new TableColumn<>("Word");
        translationColumn = new TableColumn<>("Translation");
        wordColumn.impl_setWidth(WORD_COLUMN_DEFAULT_WIDTH);
        translationColumn.impl_setWidth(TRANSLATION_COLUMN_DEFAULT_WIDTH);

        wordColumn.setCellValueFactory(
                new PropertyValueFactory<WordEntry,String>("Word")
        );
        translationColumn.setCellValueFactory(
                new PropertyValueFactory<WordEntry,String>("Translation")
        );

    }

    private void tableSelectionChanged(){
        if (!toggle.isSelected()) {
            WordEntry wordEntry = table.getSelectionModel().getSelectedItem();
            wordField.setText(wordEntry.getWord());
            wordField.hidePopup();
            translationField.setText(wordEntry.getTranslation());
            translationField.hidePopup();
        }
    }

}

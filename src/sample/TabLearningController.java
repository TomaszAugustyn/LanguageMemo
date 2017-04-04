package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

import static java.lang.Math.min;

/**
 * Created by Tomek on 26.03.2017.
 */
public class TabLearningController {

    @FXML public Button startBtn;
    @FXML private RadioButton p2eRadio;
    @FXML private RadioButton e2pRadio;
    @FXML private TextField wordsPerSessionField;
    @FXML private TextField enterTranslationField;
    @FXML private Label englishWordLabel;
    @FXML private Label polishWordLabel;
    @FXML private Button enterBtn;

    private ToggleGroup toggleGroup = new ToggleGroup();
    private WordEntryList wordEntryList;
    private boolean sessionStarted = false;

    private Main main;

    public void init(Main mainController) {
        this.main = mainController;
        wordEntryList = main.getWordEntryList();

        p2eRadio.setToggleGroup(toggleGroup);
        e2pRadio.setToggleGroup(toggleGroup);
        p2eRadio.setSelected(true);
        enterBtn.setDisable(true);

        wordsPerSessionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 5 && newValue.matches("\\d*")) {
                return;
            }
            wordsPerSessionField.setText(oldValue);
            wordsPerSessionField.positionCaret(wordsPerSessionField.getLength());
        });

    }

    @FXML private void onStartBtnClicked(ActionEvent actionEvent){
        if(sessionStarted){
            startBtn.setText("Start learning");
            startBtn.setStyle("-fx-background-color: mediumpurple; -fx-border-color: black;");
            enterBtn.setDisable(true);
            sessionStarted = false;
        }else{
            startBtn.setText("Stop learning");
            startBtn.setStyle("-fx-background-color: #ff4855; -fx-border-color: black;");
            enterBtn.setDisable(false);
            sessionStarted = true;
        }

        if(sessionStarted){
            wordEntryList = main.getWordEntryList();
            int nrOfWordsPerSession = getNrOfWordsPerSession();
            List<WordEntry> wordsSubList =  wordEntryList.getNRandomUniqueWordEntries(nrOfWordsPerSession);

            //System.out.println(wordsSubList);
        }

    }

    private int getNrOfWordsPerSession() {
        String stringFromField = wordsPerSessionField.getText();
        int nrOfWordsPerSession = (int)wordEntryList.getCount();

        if(!stringFromField.isEmpty()){
            int nrFromField = Integer.parseInt(stringFromField);

            if(nrFromField > 0){
                nrOfWordsPerSession = min(nrFromField, nrOfWordsPerSession);
            }
        }
        return nrOfWordsPerSession;
    }
}

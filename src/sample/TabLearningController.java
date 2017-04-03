package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

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
    private ToggleGroup toggleGroup = new ToggleGroup();
    private WordEntryList wordEntryList;

    private Main main;

    public void init(Main mainController) {
        this.main = mainController;
        wordEntryList = main.getWordEntryList();

        p2eRadio.setToggleGroup(toggleGroup);
        e2pRadio.setToggleGroup(toggleGroup);
        p2eRadio.setSelected(true);

        wordsPerSessionField.textProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length() < 5 && newValue.matches("\\d*")) {
                    int value = Integer.parseInt(newValue);
                } else {
                    wordsPerSessionField.setText(oldValue);
                    wordsPerSessionField.positionCaret(wordsPerSessionField.getLength());
                }
            }
        });

    }

    @FXML private void onStartBtnClicked(ActionEvent actionEvent){
        wordEntryList = main.getWordEntryList();
        int nrOfWordsPerSession = getNrOfWordsPerSession();
        List<WordEntry> wordsSubList =  wordEntryList.getNRandomUniqueWordEntries(nrOfWordsPerSession);

        System.out.println(wordsSubList);

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

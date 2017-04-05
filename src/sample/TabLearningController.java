package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;
import static javafx.scene.paint.CycleMethod.NO_CYCLE;

/**
 * Created by Tomek on 26.03.2017.
 */
public class TabLearningController {

    private static class EnglishLblGradientStyle{
        private static Stop[] stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.TRANSPARENT)};
        private static LinearGradient gradient = new LinearGradient(1.0, 0, 1.0, 0.8428571428571429, true, CycleMethod.NO_CYCLE, stops);
    }

    private static class PolishLblGradientStyle{
        private static Stop[] stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.WHITE)};
        private static LinearGradient gradient = new LinearGradient(1.0, 0.9476190476190476, 1.0, 1.0, true, CycleMethod.REFLECT, stops);
    }

    private static class SessionContainer{
        private static boolean sessionStarted = false;
        private static int nrOfWordsPerSession = 0;
        private static List<WordEntry> randomizedWordList = new ArrayList<>();
        private static int translationMode = 0;
    }

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
        adjustControlsToSessionState();
        toggleSessionState();

        if(SessionContainer.sessionStarted){
            wordEntryList = main.getWordEntryList();
            SessionContainer.nrOfWordsPerSession = getNrOfWordsPerSession();
            SessionContainer.randomizedWordList =  wordEntryList.getNRandomUniqueWordEntries(SessionContainer.nrOfWordsPerSession);
            SessionContainer.translationMode = e2pRadio.isSelected()? WordEntry.ENG_2_POL : WordEntry.POL_2_ENG;

            System.out.println(SessionContainer.randomizedWordList);
        }

    }

    private void adjustControlsToSessionState() {
        if(SessionContainer.sessionStarted){
            startBtn.setText("Start learning");
            startBtn.setStyle("-fx-background-color: mediumpurple; -fx-border-color: black;");
            enterBtn.setDisable(true);
            englishWordLabel.setText("Press \"Start learning\"");
            polishWordLabel.setText("Press \"Start learning\"");
            englishWordLabel.setTextFill(EnglishLblGradientStyle.gradient);
            polishWordLabel.setTextFill(PolishLblGradientStyle.gradient);

        }else{
            startBtn.setText("Stop learning");
            startBtn.setStyle("-fx-background-color: #ff4855; -fx-border-color: black;");
            enterBtn.setDisable(false);
            englishWordLabel.setTextFill(Color.BLACK);
            polishWordLabel.setTextFill(Color.BLACK);
        }
    }

    private void toggleSessionState() {
        SessionContainer.sessionStarted = !SessionContainer.sessionStarted;
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

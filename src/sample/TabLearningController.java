package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.fxmisc.richtext.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;


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
        private static int correctAnswers = 0;
        private static int wrongAnswers = 0;
        private static int currentPositionInList = 0;
        private static String correctAnswerForCurrPos = "";
        private static boolean lastAnswerWrong = false;

        private static void resetVariables(){
            nrOfWordsPerSession = 0;
            randomizedWordList = new ArrayList<>();
            translationMode = 0;
            correctAnswers = 0;
            wrongAnswers = 0;
            currentPositionInList = 0;
            correctAnswerForCurrPos = "";
            lastAnswerWrong = false;
        }

    }

    @FXML public Button startBtn;
    @FXML private RadioButton p2eRadio;
    @FXML private RadioButton e2pRadio;
    @FXML private TextField wordsPerSessionField;
    @FXML private TextField answerField;
    @FXML private StyleClassedTextArea englishWordLabel;
    @FXML private StyleClassedTextArea polishWordLabel;
    @FXML private Label correctCounter;
    @FXML private Label wrongCounter;
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
        enterBtn.setDefaultButton(true);
        englishWordLabel.replaceText("Press \"Start learning\"");
        polishWordLabel.replaceText("Press \"Start learning\"");
        englishWordLabel.setStyleClass(0, englishWordLabel.getText().length(), "welcometext1");
        polishWordLabel.setStyleClass(0, polishWordLabel.getText().length(),"welcometext2");
        polishWordLabel.setStyle("-fx-background-color: transparent;");
        englishWordLabel.setStyle("-fx-background-color: transparent;");

        wordsPerSessionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 5 && newValue.matches("\\d*")) {
                return;
            }
            wordsPerSessionField.setText(oldValue);
            wordsPerSessionField.positionCaret(wordsPerSessionField.getLength());
        });

    }

    @FXML private void onStartBtnClicked(){
        adjustControlsToSessionState();
        toggleSessionState();

        if(SessionContainer.sessionStarted){
            wordEntryList = main.getWordEntryList();
            SessionContainer.nrOfWordsPerSession = getNrOfWordsPerSession();
            SessionContainer.randomizedWordList =  wordEntryList.getNRandomUniqueWordEntries(SessionContainer.nrOfWordsPerSession);
            SessionContainer.translationMode = e2pRadio.isSelected()? WordEntry.ENG_2_POL : WordEntry.POL_2_ENG;

            displayFirstPosition();

        }
    }

    private void adjustControlsToSessionState() {
        if(SessionContainer.sessionStarted){
            startBtn.setText("Start learning");
            startBtn.setStyle("-fx-background-color: mediumpurple; -fx-border-color: black;");
            enterBtn.setDisable(true);
            englishWordLabel.replaceText("Press \"Start learning\"");
            polishWordLabel.replaceText("Press \"Start learning\"");
            englishWordLabel.setStyleClass(0, englishWordLabel.getText().length(), "welcometext1");
            polishWordLabel.setStyleClass(0, polishWordLabel.getText().length(),"welcometext2");
            wordsPerSessionField.setDisable(false);
            e2pRadio.setDisable(false);
            p2eRadio.setDisable(false);
            correctCounter.setText("0");
            wrongCounter.setText("0");
            SessionContainer.resetVariables();

        }else{
            startBtn.setText("Stop learning");
            startBtn.setStyle("-fx-background-color: #ff4855; -fx-border-color: black;");
            enterBtn.setDisable(false);
            englishWordLabel.setStyleClass(0, englishWordLabel.getText().length(),"normaltext");
            polishWordLabel.setStyleClass(0, polishWordLabel.getText().length(),"normaltext");
            wordsPerSessionField.setDisable(true);
            e2pRadio.setDisable(true);
            p2eRadio.setDisable(true);
            answerField.requestFocus();
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

    private void displayFirstPosition(){
        if(SessionContainer.randomizedWordList.size() > 0){
            WordEntry wordEntry = SessionContainer.randomizedWordList.get(0);
            SessionContainer.correctAnswerForCurrPos = SessionContainer.translationMode == WordEntry.ENG_2_POL ?  wordEntry.getTranslation() : wordEntry.getWord();
            WordEntry underscoredEntry = wordEntry.convertWordEntryToUnderscores(SessionContainer.translationMode);
            englishWordLabel.replaceText(underscoredEntry.getWord());
            polishWordLabel.replaceText(underscoredEntry.getTranslation());
            SessionContainer.currentPositionInList = 1;
        }
    }
    @FXML private void onEnterBtnClicked(){
        if(SessionContainer.lastAnswerWrong){

            answerField.clear();
            enterBtn.setText("Enter");
            SessionContainer.lastAnswerWrong = false;
            displayNextPosition();
            return;
        }
        if(!answerField.getText().isEmpty()) {
            String enteredWord = answerField.getText().toUpperCase();
            String correctAnswer = SessionContainer.correctAnswerForCurrPos;
            if (enteredWord.equals(correctAnswer.toUpperCase())){
                SessionContainer.correctAnswers++;
                correctCounter.setText(String.valueOf(SessionContainer.correctAnswers));
                System.out.println("Correct Answer!");
                answerField.clear();
                displayNextPosition();
                return;
            }
            SessionContainer.wrongAnswers++;
            wrongCounter.setText(String.valueOf(SessionContainer.wrongAnswers));
            System.out.println("wrong!!!");
            SessionContainer.lastAnswerWrong = true;


            if(SessionContainer.translationMode == WordEntry.ENG_2_POL) {
                int lengthWrongAns = answerField.getText().length();
                polishWordLabel.replaceText(answerField.getText() + "  " + correctAnswer);
                polishWordLabel.setStyleClass(0, lengthWrongAns,"textStrikeThrough");
                polishWordLabel.setStyleClass(lengthWrongAns + 2, polishWordLabel.getLength(),"redtext");
            }
             else {
                int lengthWrongAns = answerField.getText().length();
                englishWordLabel.replaceText(answerField.getText() + "  " + correctAnswer);
                englishWordLabel.setStyleClass(0, lengthWrongAns,"textStrikeThrough");
                englishWordLabel.setStyleClass(lengthWrongAns + 2, englishWordLabel.getLength(),"redtext");
            }

            enterBtn.setText("Next");
            enterBtn.setStyle("-fx-background-color: #ff4855; -fx-border-color: black;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The word you wrote is empty. Please give your answer.", ButtonType.OK);
        alert.showAndWait();

    }

    private void displayNextPosition() {
        if(SessionContainer.currentPositionInList < SessionContainer.randomizedWordList.size()){
            WordEntry wordEntry = SessionContainer.randomizedWordList.get(SessionContainer.currentPositionInList);
            SessionContainer.correctAnswerForCurrPos = SessionContainer.translationMode == WordEntry.ENG_2_POL ?  wordEntry.getTranslation() : wordEntry.getWord();
            WordEntry underscoredEntry = wordEntry.convertWordEntryToUnderscores(SessionContainer.translationMode);
            englishWordLabel.replaceText(underscoredEntry.getWord());
            polishWordLabel.replaceText(underscoredEntry.getTranslation());
            SessionContainer.currentPositionInList++;
            enterBtn.setStyle("-fx-background-color: mediumpurple; -fx-border-color: black;");
            englishWordLabel.setStyleClass(0, englishWordLabel.getText().length(),"normaltext");
            polishWordLabel.setStyleClass(0, polishWordLabel.getText().length(),"normaltext");
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Session finished. your score: \n" + "Correct Answers: "
                    + SessionContainer.correctAnswers + "\n Wrong Answers: " + SessionContainer.wrongAnswers , ButtonType.OK);
            alert.showAndWait();
            onStartBtnClicked();
            startBtn.requestFocus();
            correctCounter.setText("0");
            wrongCounter.setText("0");
        }
    }
}

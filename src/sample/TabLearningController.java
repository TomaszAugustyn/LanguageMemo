package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import org.fxmisc.richtext.*;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;


/**
 * This is a class for controlling "Learning" tab responsible for displaying
 * words from dictionary during learning session
 * @author Tomasz Augustyn
 */
public class TabLearningController {

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
    @FXML private ImageView thumbUp;
    @FXML private ImageView thumbDown;

    private static final int MAX_NR_OF_DIGITS_IN_WORDS_PER_SESSION_FIELD = 4;
    private ToggleGroup toggleGroup = new ToggleGroup();
    private WordEntryList wordEntryList;
    private Main main;

    public void init(Main mainController) {
        this.main = mainController;
        wordEntryList = main.getWordEntryList();

        p2eRadio.setToggleGroup(toggleGroup);
        e2pRadio.setToggleGroup(toggleGroup);
        thumbUp.setImage(new Image("sample/resources/logo/thumbUp.png"));
        thumbDown.setImage(new Image("sample/resources/logo/thumbDown.png"));
        polishWordLabel.setStyle("-fx-background-color: transparent;");
        englishWordLabel.setStyle("-fx-background-color: transparent;");
        enterBtn.setDefaultButton(true);
        p2eRadio.setSelected(true);
        setControlsStyleToStopped();

        wordsPerSessionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() <= MAX_NR_OF_DIGITS_IN_WORDS_PER_SESSION_FIELD && newValue.matches("\\d*")) {
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
            setControlsStyleToStopped();
            SessionContainer.resetVariables();
            return;
        }

        setControlsStyleToStarted();
    }

    private void setControlsStyleToStarted() {
        startBtn.setText("Stop learning");
        startBtn.setStyle("-fx-background-color: #ff4855; -fx-border-color: black;");
        enterBtn.setDisable(false);
        englishWordLabel.setStyleClass(0, englishWordLabel.getText().length(),"normaltext");
        polishWordLabel.setStyleClass(0, polishWordLabel.getText().length(),"normaltext");
        wordsPerSessionField.setDisable(true);
        e2pRadio.setDisable(true);
        p2eRadio.setDisable(true);
        answerField.setDisable(false);
        answerField.requestFocus();
    }

    private void setControlsStyleToStopped() {
        startBtn.setText("Start learning");
        startBtn.setStyle("-fx-background-color: mediumpurple; -fx-border-color: black;");
        enterBtn.setText("Enter");
        enterBtn.setStyle("-fx-background-color: mediumpurple; -fx-border-color: black;");
        answerField.clear();
        answerField.setDisable(true);
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
            answerField.requestFocus();
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
                answerField.requestFocus();
                displayNextPosition();
                return;
            }
            handleWrongAnswer(correctAnswer);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The word you wrote is empty. Please give your answer.", ButtonType.OK);
        alert.showAndWait();
        answerField.requestFocus();

    }

    private void handleWrongAnswer(String correctAnswer) {
        SessionContainer.wrongAnswers++;
        wrongCounter.setText(String.valueOf(SessionContainer.wrongAnswers));
        System.out.println("Wrong Answer!");
        SessionContainer.lastAnswerWrong = true;
        enterBtn.setText("Next");
        enterBtn.setStyle("-fx-background-color: #ff4855; -fx-border-color: black;");

        if(SessionContainer.translationMode == WordEntry.ENG_2_POL) {
            int lengthWrongAns = answerField.getText().length();
            polishWordLabel.replaceText(answerField.getText() + "  " + correctAnswer);
            polishWordLabel.setStyleClass(0, lengthWrongAns,"textStrikeThrough");
            polishWordLabel.setStyleClass(lengthWrongAns + 2, polishWordLabel.getLength(),"redtext");
            return;
        }

        int lengthWrongAns = answerField.getText().length();
        englishWordLabel.replaceText(answerField.getText() + "  " + correctAnswer);
        englishWordLabel.setStyleClass(0, lengthWrongAns,"textStrikeThrough");
        englishWordLabel.setStyleClass(lengthWrongAns + 2, englishWordLabel.getLength(),"redtext");

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
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Session finished. your score: \n" + "Correct Answers: "
                + SessionContainer.correctAnswers + "\nWrong Answers: " + SessionContainer.wrongAnswers , ButtonType.OK);
        alert.showAndWait();
        onStartBtnClicked();
        startBtn.requestFocus();
        correctCounter.setText("0");
        wrongCounter.setText("0");

    }
}

package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.*;
import java.util.List;
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
    private File loadedFile;

    @FXML private TabLearningController tabLearningController;
    @FXML private TabWordsController tabWordsController;
    @FXML private TextField filePath;
    @FXML private Button fileChooser;
    @FXML private ImageView image;
    @FXML private Label pathLabel;
    @FXML private Pane filePathPane;
    @FXML private TabPane tabPane;

    private static final double MIN_STAGE_WIDTH = 730.0;
    private static final double MIN_STAGE_HEIGHT = 515.0;
    private static final double DEFAULT_DIVIDER_POSITION = 0.3785;
    private static final int GAP_BETWEEN_LABEL_AND_FILEPATH = 20;

    private static final double DROP_SHADOW_OFFSET_X = 0f;
    private static final double DROP_SHADOW_OFFSET_Y = 0f;
    private static final double DROP_SHADOW_RADIUS = 16.0;

    public static DropShadow borderGlow;

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("sample/resources/view/NastiaMemo.fxml"));
        loader.setController(this);
        root = loader.load();
        scene = new Scene(root);
        stage.setTitle("LanguageMemo");
        stage.setMinWidth(MIN_STAGE_WIDTH);
        stage.setMinHeight(MIN_STAGE_HEIGHT);
        stage.getIcons().add(new Image("sample/resources/logo/LM.png"));
        image.setImage(new Image("sample/resources/logo/LanguageMemo.png"));
        stage.setScene(scene);
        stage.show();
        scene.getStylesheets().add("sample/resources/mySwitch.css");
        scene.getStylesheets().add("sample/resources/styles.css");
        initializeBorderGlow();

        loadedFile = new File(System.getProperty("user.dir") + "\\LanguageMemo.txt");
        if (!loadedFile.exists()) {
            loadedFile.createNewFile();
        }
        wordEntryList = getWordEntryListFromFile(loadedFile);
        initialWordEntryList = getWordEntryListFromFile(loadedFile);

        stage.setOnCloseRequest(we -> {
            checkConditionAndWriteFileFromList();
        });

        addHeightAndWidthListeners();
        tabLearningController.init(this);
        tabWordsController.init(this);

    }

    private void initializeBorderGlow() {
        borderGlow = new DropShadow();
        //borderGlow.setColor(Color.valueOf("#FF4855"));
        borderGlow.setColor(Color.RED);
        borderGlow.setOffsetX(DROP_SHADOW_OFFSET_X);
        borderGlow.setOffsetY(DROP_SHADOW_OFFSET_Y);
        borderGlow.setRadius(DROP_SHADOW_RADIUS);
    }

    public static void vanishGlowEffect(Node node)
    {
        DropShadow vanishingShadow = (DropShadow)borderGlow.impl_copy();
        node.setEffect(vanishingShadow);
        node.setEffect(vanishingShadow);
        final Timeline timeline = new Timeline();
        //timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);
        final KeyValue kv = new KeyValue(vanishingShadow.radiusProperty(), 0.0);
        final KeyFrame kf = new KeyFrame(Duration.millis(1000), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    private void addHeightAndWidthListeners() {
        scene.widthProperty().addListener(
                (observable, oldValue, newValue) -> {
                    Double dxWidth = (Double)newValue - (Double)oldValue;
                    filePath.setPrefWidth(filePath.getWidth() + dxWidth);
                    fileChooser.setLayoutX(fileChooser.getLayoutX() + dxWidth);
                    tabWordsController.splitPane.setPrefWidth(tabWordsController.splitPane.getWidth() + dxWidth);
                    tabWordsController.addAndDeleteRegion.setPrefWidth(tabWordsController.addAndDeleteRegion.getWidth() + dxWidth);
                    tabWordsController.table.setPrefWidth((Double)newValue*DEFAULT_DIVIDER_POSITION);
                    tabWordsController.splitPane.setDividerPositions(tabWordsController.table.getWidth());
                    tabLearningController.englishWordLabel.setPrefWidth(tabLearningController.englishWordLabel.getWidth() + dxWidth);
                    tabLearningController.polishWordLabel.setPrefWidth(tabLearningController.polishWordLabel.getWidth() + dxWidth);
                });

        scene.heightProperty().addListener(
                (observable, oldValue, newValue) -> {
                    Double dxHeight = (Double)newValue - (Double)oldValue;
                    if(dxHeight > 0){
                        tabPane.setPrefHeight(tabPane.getHeight() + dxHeight);
                        filePathPane.setPrefHeight(filePathPane.getHeight() + dxHeight);
                    }
                    tabWordsController.splitPane.setPrefHeight(tabWordsController.splitPane.getHeight() + dxHeight);
                    tabWordsController.addAndDeleteRegion.setPrefHeight(tabWordsController.addAndDeleteRegion.getHeight() + dxHeight);
                    tabWordsController.table.setPrefHeight(tabWordsController.table.getHeight() + dxHeight);

                });

        // binding controls to filePathPane
        filePath.layoutYProperty().bind((filePathPane.heightProperty().divide(2)).subtract(filePath.heightProperty().divide(2)) );
        fileChooser.layoutYProperty().bind((filePathPane.heightProperty().divide(2)).subtract(filePath.heightProperty().divide(2)) );
        pathLabel.layoutYProperty().bind((filePathPane.heightProperty().divide(2)).subtract(filePath.heightProperty().divide(2)).subtract(GAP_BETWEEN_LABEL_AND_FILEPATH) );
        image.layoutYProperty().bind((filePathPane.heightProperty().divide(2)).subtract(image.fitHeightProperty().divide(2)) );

    }

    public static void main(String[] args) {

        launch(args);
    }


    private void checkConditionAndWriteFileFromList() {
        if(!wordEntryList.equals(initialWordEntryList))
        {
            String currentFilePath = filePath.getText();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save changes to \"" + currentFilePath +"\"?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if(alert.getResult() == ButtonType.YES) {
                WriteFileFromWordEntryList(loadedFile);
            }
        }
    }

    private void WriteFileFromWordEntryList(File loadedFile) {

        String streamedString = wordEntryList.getWordsList()
                .stream()
                .map(wordEntry -> wordEntry.getWord() + WordEntry.WORD_TRANSLATION_SEPARATOR + wordEntry.getTranslation() + WordEntry.NEW_LINE_SEPARATOR)
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

    @FXML public void onOpenChooser(){

        checkConditionAndWriteFileFromList();
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Memo text files", "*.txt"));
        chooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        try {
            File tmpFile = chooser.showOpenDialog(scene.getWindow());
            loadedFile = tmpFile == null ? loadedFile : tmpFile;
            wordEntryList = getWordEntryListFromFile(loadedFile);
            initialWordEntryList = getWordEntryListFromFile(loadedFile);
            tabWordsController.afterWordsListChanged();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            System.out.println("File not found.");
        }

    }



    private WordEntryList getWordEntryListFromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(file));
        String line = "";
        WordEntryList wordEntryListLocal = new WordEntryList();

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if(line.contains(WordEntry.NEW_LINE_SEPARATOR)){
                List<String> colonList = new ArrayList<String>(Arrays.asList(line.split(WordEntry.NEW_LINE_SEPARATOR)));
                for (String s : colonList) {
                    if(s.contains(WordEntry.WORD_TRANSLATION_SEPARATOR)){
                        List<String> comaList = new ArrayList<String>(Arrays.asList(s.split(WordEntry.WORD_TRANSLATION_SEPARATOR)));
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

    public void enableOrDisableStartLearningButton(){
        if(wordEntryList.getCount() > 0) {
            tabLearningController.startBtn.setDisable(false);
            return;
        }
        tabLearningController.startBtn.setDisable(true);
    }

    public WordEntryList getWordEntryList(){
        return this.wordEntryList;
    }

    public File getLoadedFile(){
        return this.loadedFile;
    }

}


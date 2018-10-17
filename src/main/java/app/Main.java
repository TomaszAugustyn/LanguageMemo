package app;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
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


    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("NastiaMemo.fxml"));
        loader.setController(this);
        root = loader.load();
        scene = new Scene(root);
        stage.setTitle("LanguageMemo");
        stage.setMinWidth(MIN_STAGE_WIDTH);
        stage.setMinHeight(MIN_STAGE_HEIGHT);
        stage.getIcons().add(new Image("LM.png"));
        image.setImage(new Image("LanguageMemo.png"));
        stage.setScene(scene);
        stage.show();
        scene.getStylesheets().add("mySwitch.css");
        scene.getStylesheets().add("styles.css");

        loadedFile = new File(System.getProperty("user.dir") + "\\LanguageMemo.txt");
        if (!loadedFile.exists()) {
            loadedFile.createNewFile();
        }
        wordEntryList = getWordEntryListFromFile(loadedFile);
        initialWordEntryList = getWordEntryListFromFile(loadedFile);

        stage.setOnCloseRequest(windowEvent -> {
            if (!checkConditionAndWriteFileFromList()){
                windowEvent.consume();
            }
        });

        addHeightAndWidthListeners();
        tabLearningController.init(this);
        tabWordsController.init(this);

    }

    private void addHeightAndWidthListeners() {
        scene.widthProperty().addListener(
                (observable, oldValue, newValue) -> {
                    double dxWidth = (double)newValue - (double)oldValue;
                    filePath.setPrefWidth(filePath.getWidth() + dxWidth);
                    fileChooser.setLayoutX(fileChooser.getLayoutX() + dxWidth);
                    tabWordsController.splitPane.setPrefWidth(tabWordsController.splitPane.getWidth() + dxWidth);
                    tabWordsController.addAndDeleteRegion.setPrefWidth(tabWordsController.addAndDeleteRegion.getWidth() + dxWidth);
                    tabWordsController.table.setPrefWidth((double)newValue*DEFAULT_DIVIDER_POSITION);
                    tabWordsController.splitPane.setDividerPositions(tabWordsController.table.getWidth());
                    tabLearningController.englishWordLabel.setPrefWidth(tabLearningController.englishWordLabel.getWidth() + dxWidth);
                    tabLearningController.polishWordLabel.setPrefWidth(tabLearningController.polishWordLabel.getWidth() + dxWidth);
                });

        scene.heightProperty().addListener(
                (observable, oldValue, newValue) -> {
                    double dxHeight = (double)newValue - (double)oldValue;
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


    private boolean checkConditionAndWriteFileFromList() {
        if(!wordEntryList.equals(initialWordEntryList))
        {
            String currentFilePath = filePath.getText();
            ButtonType fakeNoButton = new ButtonType("No", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save changes to \"" + currentFilePath +"\"?", ButtonType.YES, fakeNoButton, cancelButton);
            Optional<ButtonType> result = alert.showAndWait();

            if(result.isPresent() && result.get() == ButtonType.YES) {
                WriteFileFromWordEntryList(loadedFile);
                initialWordEntryList = wordEntryList;
                return true;
            }
            else return result.isPresent() && result.get() == fakeNoButton;

        }
        return true;
    }

    private void WriteFileFromWordEntryList(File loadedFile) {

        String streamedString = wordEntryList.getWordsList()
                .stream()
                .map(wordEntry -> wordEntry.getWord() + WordEntry.WORD_TRANSLATION_SEPARATOR + wordEntry.getTranslation() + WordEntry.NEW_LINE_SEPARATOR)
                .collect(Collectors.joining());
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(loadedFile), StandardCharsets.UTF_8);
            writer.write(streamedString);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot write to \"" + loadedFile.getPath() + "\" file.");
        }
    }

    @FXML public void onOpenChooser(){

        if (!checkConditionAndWriteFileFromList()){
            return;
        }
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

    @FXML public void onMenuItemChooseFile(){
        onOpenChooser();
    }

    @FXML public void onMenuItemSave(){
        checkConditionAndWriteFileFromList();
    }

    @FXML public void onMenuItemQuit(){
        if (checkConditionAndWriteFileFromList()){
            Platform.exit();
        }
    }

    @FXML public void onMenuItemAddWord(){
        tabWordsController.onAddWordBtnClicked();
    }

    @FXML public void onMenuItemDeleteWord(){
        tabWordsController.onDeleteWordBtnClicked();
    }

    @FXML public void onMenuItemChangeMode(){
        double offsetX = tabWordsController.toggle.getLayoutBounds().getWidth() * TabWordsController.TOGGLE_SWITCH_BTN_POS_OFFSET_X;
        double offsetY = tabWordsController.toggle.getLayoutBounds().getHeight()* TabWordsController.TOGGLE_SWITCH_BTN_POS_OFFSET_Y;
        Tools.simulateClick(tabWordsController.toggle, offsetX, offsetY);
    }


    private WordEntryList getWordEntryListFromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(file), "utf-8");
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


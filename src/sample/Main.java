package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("sample/resources/view/NastiaMemo.fxml"));
        loader.setController(this);
        root = loader.load();
        scene = new Scene(root);
        stage.setTitle("NastiaMemo");
        stage.getIcons().add(new Image("sample/resources/logo/LM.png"));
        image.setImage(new Image("sample/resources/logo/NastiaMemo.png"));
        stage.setScene(scene);
        stage.show();
        scene.getStylesheets().add("sample/resources/mySwitch.css");
        scene.getStylesheets().add("sample/resources/styles.css");

        loadedFile = new File(System.getProperty("user.dir") + "\\LanguageMemo.txt");
        if (!loadedFile.exists()) {
            loadedFile.createNewFile();
        }
        wordEntryList = getWordEntryListFromFile(loadedFile);
        initialWordEntryList = getWordEntryListFromFile(loadedFile);

        stage.setOnCloseRequest(we -> {
            checkConditionAndWriteFileFromList();
        });

        tabLearningController.init(this);
        tabWordsController.init(this);

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

    @FXML public void onOpenChooser(){

        checkConditionAndWriteFileFromList();

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Memo text files", "*.txt"));
        chooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        try {
            loadedFile = chooser.showOpenDialog(scene.getWindow());
            wordEntryList = getWordEntryListFromFile(loadedFile);
            initialWordEntryList = getWordEntryListFromFile(loadedFile);
            tabWordsController.afterWordsListChanged();
        }
        catch(NullPointerException e) {
            e.printStackTrace();
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


package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomek on 11.02.2017.
 */
public class WordEntryList {

    private List<WordEntry> wordsList = new ArrayList<>();

    public void addWord(WordEntry wordEntry){
        if(!wordEntry.getWord().isEmpty() &&
                !wordEntry.getTranslation().isEmpty())
            this.wordsList.add(wordEntry);
        else {
            System.out.println("Word or translation cannot be empty");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Information");
            alert.setContentText("Nastia! Word or translation cannot be empty!");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
    }

    public WordEntry getWordEntry(String word){
        for (WordEntry w : wordsList) {
            if (w.getWord().equals(word))
                return w;
        }
        return new WordEntry("", "");
    }

    public WordEntryList(List<WordEntry> wordsList) {
        this.wordsList = wordsList;
    }

    public WordEntryList() {

    }

    public List<WordEntry> getWordsList() {
        return wordsList;
    }

    public void setWordsList(List<WordEntry> wordsList) {
        this.wordsList = wordsList;
    }

    @Override
    public String toString() {
        return "WordEntryList{" +
                "wordsList=" + wordsList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WordEntryList)) return false;

        WordEntryList that = (WordEntryList) o;

        return getWordsList().equals(that.getWordsList());
    }

    @Override
    public int hashCode() {
        return getWordsList().hashCode();
    }
}

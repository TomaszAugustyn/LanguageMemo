package app;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is WordEntryList class that manages word and
 * translation entries used in the application
 * @author Tomasz Augustyn
 */
public class WordEntryList {

    private List<WordEntry> wordsList = new ArrayList<>();

    public enum eEntries{
        WORD, TRANSLATION
    }

    public void addWord(WordEntry wordEntry){
        if(!wordEntry.getWord().isEmpty() &&
                !wordEntry.getTranslation().isEmpty()) {
            this.wordsList.add(wordEntry);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Information");
        alert.setContentText("Word or translation cannot be empty!");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });

    }

    public void deleteWord(WordEntry wordEntry){
        wordsList.removeIf(wordEntry::equals);
    }

    public boolean isWordEntryOnList(WordEntry wordEntry){
        return wordsList.stream().anyMatch(wordEntry::equals);
    }

    public long getCount(){
        return (long)wordsList.size();
    }

    public List<String> getWordsAsList(){
        return getWordsList().stream()
                            .map( wordEntry -> wordEntry.getWord())
                            .collect(Collectors.toList());
    }

    public List<String> getTranslationsAsList(){
        return getWordsList().stream()
                .map( wordEntry -> wordEntry.getTranslation())
                .collect(Collectors.toList());
    }

    public String getEntryEquivalent(String wordOrTranslation, eEntries entryType){
        switch(entryType){
            case WORD:
                for (WordEntry wordEntry : this.wordsList) {
                    if(wordOrTranslation.equals(wordEntry.getWord()))
                    {
                        return wordEntry.getTranslation();
                    }
                }
                break;
            case TRANSLATION:
                for (WordEntry wordEntry : this.wordsList) {
                    if(wordOrTranslation.equals(wordEntry.getTranslation()))
                    {
                        return wordEntry.getWord();
                    }
                }
                break;
            default:
                return "";
        }

        return "";
    }

    public List<WordEntry> getNRandomUniqueWordEntries(int n) throws IllegalArgumentException{
        if(n > getCount())
            throw new IllegalArgumentException("Method has been used with argument \"n\" exceeding the size of word entry list.");

        List<WordEntry> wordsListCopy = new ArrayList<>(wordsList);
        Collections.shuffle(wordsListCopy);
        return wordsListCopy.subList(0, n);

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

package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Tomek on 11.02.2017.
 */
public class WordEntry {

    private StringProperty word;
    private StringProperty  translation;

    public WordEntry(String word, String translation) {
        this.word = new SimpleStringProperty(word);
        this.translation = new SimpleStringProperty(translation);
    }


    public String getWord() {
        return word.get();
    }

    public void setWord(String word) {
        this.word.set(word);
    }

    public String getTranslation() {
        return translation.get();
    }

    public void setTranslation(String translation) {
        this.translation.set(translation);
    }

    @Override
    public String toString() {
        return "WordEntry{" +
                "word=" + word +
                ", translation=" + translation +
                '}';
    }
}

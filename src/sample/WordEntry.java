package sample;

/**
 * Created by Tomek on 11.02.2017.
 */
public class WordEntry {

    private String word = "";
    private String translation = "";

    public WordEntry(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "WordEntry{" +
                "word='" + word + '\'' +
                ", translation='" + translation + '\'' +
                '}';
    }
}

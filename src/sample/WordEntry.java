package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.Arrays;
import java.util.List;

/**
 * This is WordEntry class that manages word and translation strings
 * @author Tomasz Augustyn
 */
public class WordEntry {

    private StringProperty word;
    private StringProperty  translation;
    public static final int ENG_2_POL = 0;
    public static final int POL_2_ENG = 1;

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


    @SuppressWarnings("Duplicates")
    public WordEntry convertWordEntryToUnderscores(int conversionType) throws IllegalArgumentException{
        String word = this.getWord();
        String translation = this.getTranslation();

        if(conversionType == ENG_2_POL && !this.isWordOrTranslationEmpty()){

            String modifiedTranslation = "";
            List<String> items = Arrays.asList(translation.split("\\s+"));
            for (String s : items) {
                String underscoredStr = s.replaceAll("[\\S]", "_ ");
                String modifiedStr = String.valueOf(s.charAt(0))
                        + underscoredStr.substring(1, underscoredStr.length());
                modifiedTranslation = new StringBuilder().append(modifiedTranslation).append(modifiedStr).append("  ").toString();
            }

            return new WordEntry(word, modifiedTranslation);
        }

       if(conversionType == POL_2_ENG && !this.isWordOrTranslationEmpty()){

           String modifiedWord = "";
           List<String> items = Arrays.asList(word.split("\\s+"));
           for (String s : items) {
               String underscoredStr = s.replaceAll("[\\S]", "_ ");
               String modifiedStr = String.valueOf(s.charAt(0))
                       + underscoredStr.substring(1, underscoredStr.length());
               modifiedWord = new StringBuilder().append(modifiedWord).append(modifiedStr).append("  ").toString();
           }

            return new WordEntry(modifiedWord, translation);
        }

        throw new IllegalArgumentException();
    }

    public boolean isWordOrTranslationEmpty(){
        return this.getWord().isEmpty() || this.getTranslation().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WordEntry)) return false;

        WordEntry wordEntry = (WordEntry) o;

        if (!getWord().equals(wordEntry.getWord())) return false;
        return getTranslation().equals(wordEntry.getTranslation());
    }

    @Override
    public int hashCode() {
        int result = getWord().hashCode();
        result = 31 * result + getTranslation().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "WordEntry{" +
                "word=" + word +
                ", translation=" + translation +
                '}';

    }
}

package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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


    public WordEntry convertWordEntryToUnderscores(int conversionType) throws IllegalArgumentException{
        String word = this.getWord();
        String translation = this.getTranslation();

        if(conversionType == ENG_2_POL && !this.isWordOrTranslationEmpty()){

            String UnderscoredTranslationStr = translation.replaceAll("[\\w]", "_ ");
            String modifiedTranslation = String.valueOf(translation.charAt(0))
                                        + UnderscoredTranslationStr.substring(1, UnderscoredTranslationStr.length());
            return new WordEntry(word, modifiedTranslation);

        }
       if(conversionType == POL_2_ENG && !this.isWordOrTranslationEmpty()){

            String underscoredWordStr = word.replaceAll("[\\w]", "_ ");
            String modifiedWord = String.valueOf(word.charAt(0))
                                + underscoredWordStr.substring(1, underscoredWordStr.length());
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

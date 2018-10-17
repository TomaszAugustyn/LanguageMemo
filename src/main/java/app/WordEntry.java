package app;

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
    public static final String WORD_TRANSLATION_SEPARATOR = ",";
    public static final String NEW_LINE_SEPARATOR = ";";


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


    public WordEntry convertWordEntryToUnderscores(int conversionType) {
        String word = this.getWord();
        String translation = this.getTranslation();

        String wordOrTranslation = conversionType == ENG_2_POL ? translation : word;
        StringBuilder modified = new StringBuilder();
        String[] items = wordOrTranslation.split("\\s+");
        for (String s : items) {
            String underscoredStr = s.replaceAll("[\\S&&[^-]&&[^']&&[^/]&&[^)]&&[^(]&&[^!]&&[^,]&&[^.]&&[^?]&&[^\"]&&[^&]&&[^:]&&[^;]]", "_ ");
            String modifiedSingle = String.valueOf(s.charAt(0))
                    + underscoredStr.substring(1);
            modified.append(modifiedSingle).append("  ");
        }

        return conversionType == ENG_2_POL ? new WordEntry(word, modified.toString()) : new WordEntry(modified.toString(), translation);

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

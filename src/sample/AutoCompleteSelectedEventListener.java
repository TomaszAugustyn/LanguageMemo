package sample;

import java.util.EventObject;

/**
 * Created by Tomek on 25.03.2017.
 */
public interface AutoCompleteSelectedEventListener {

    public void handleAutoCompleteSelected(EventObject e, String selectedText);
}

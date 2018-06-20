package app;

import java.util.EventObject;

/**
 * Listener for an event when a position from search results in AutoCompleteTextField has been selected.
 * @author Tomasz Augustyn
 */
public interface AutoCompleteSelectedEventListener {

    public void handleAutoCompleteSelected(EventObject e, String selectedText);
}

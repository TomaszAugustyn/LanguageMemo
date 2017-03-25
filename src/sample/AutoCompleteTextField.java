package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is a TextField which implements an "autocomplete" functionality, based on a supplied list of entries.
 * @author Caleb Brinkman
 */
public class AutoCompleteTextField extends TextField
{
    /** The existing autocomplete entries. */
    private final SortedSet<String> entries;
    /** The popup used to select an entry. */
    private ContextMenu entriesPopup;


    /**
     * Event Listener to notify that specified entry has been selected from search list.
     * added by: Tomasz Augustyn
     */
    private List _listeners = new ArrayList();
    public synchronized void addEventListener(AutoCompleteSelectedEventListener listener) {
        _listeners.add(listener);
    }
    public synchronized void removeEventListener(AutoCompleteSelectedEventListener listener)   {
        _listeners.remove(listener);
    }

    /**
     * call this method whenever you want to notify the event listeners of the particular event
     * added by: Tomasz Augustyn
     */
    private synchronized void fireEvent(String selectedText) {
        AutoCompleteSelectedEvent event = new AutoCompleteSelectedEvent(this);
        Iterator i = _listeners.iterator();
        while(i.hasNext())  {
            ((AutoCompleteSelectedEventListener) i.next()).handleAutoCompleteSelected(event, selectedText);
        }
    }

    /** Construct a new AutoCompleteTextField. */
    public AutoCompleteTextField() {
        super();
        entries = new TreeSet<>();
        entriesPopup = new ContextMenu();
        textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                if (getText() != null)
                {
                    if (getText().length() == 0)
                    {
                        entriesPopup.hide();
                    } else
                    {
                        LinkedList<String> searchResult = new LinkedList<>();
                        final List<String> filteredEntries = entries.stream().filter(e -> e.toLowerCase().contains(getText().toLowerCase())).collect(Collectors.toList());
                        searchResult.addAll(filteredEntries);
                        if (entries.size() > 0)
                        {
                            populatePopup(searchResult);
                            if (!entriesPopup.isShowing())
                            {
                                entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                            }
                        } else
                        {
                            entriesPopup.hide();
                        }
                    }
                }

            }
        });

        focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                entriesPopup.hide();
            }
        });

    }

    /**
     * Get the existing set of autocomplete entries.
     * @return The existing autocomplete entries.
     */
    public SortedSet<String> getEntries() { return entries; }

    /**
     * Remove the existing set of autocomplete entries.
     * added by: Tomasz Augustyn
     */
    public void clearEntries(){
        this.entries.clear();
    }

    /**
     * Hide entries popup
     * added by: Tomasz Augustyn
     */
    public void hidePopup(){
        entriesPopup.hide();
    }

    /**
     * Populate the entry set with the given search results.  Display is limited to 10 entries, for performance.
     * @param searchResult The set of matching strings.
     */
    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        // If you'd like more entries, modify this line.
        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);
        for (int i = 0; i < count; i++)
        {
            final String result = searchResult.get(i);
            Label entryLabel = new Label(result);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent actionEvent) {
                    setText(result);
                    fireEvent(result);
                    entriesPopup.hide();
                }
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);

    }
}
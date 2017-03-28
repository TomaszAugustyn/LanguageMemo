package sample;


import java.util.EventObject;

/**
 * New event for listening when a position from search results in AutoCompleteTextField has been selected.
 * @author Tomasz Augustyn
 */
public class AutoCompleteSelectedEvent extends EventObject {

    public AutoCompleteSelectedEvent(Object source){
        super(source);
    }
}

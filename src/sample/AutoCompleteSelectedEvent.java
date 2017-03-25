package sample;


import java.util.EventObject;

/**
 * Created by Tomek on 25.03.2017.
 */
public class AutoCompleteSelectedEvent extends EventObject {

    public AutoCompleteSelectedEvent(Object source){
        super(source);
    }
}

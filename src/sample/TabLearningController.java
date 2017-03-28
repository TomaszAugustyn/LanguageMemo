package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;

/**
 * Created by Tomek on 26.03.2017.
 */
public class TabLearningController {
    @FXML Button startBtn;
    @FXML RadioButton p2eRadio;
    @FXML RadioButton e2pRadio;

    private Main main;

    public void init(Main mainController){
        this.main = mainController;
    }

    @FXML private void onStartBtnClicked(ActionEvent actionEvent){

    }
}

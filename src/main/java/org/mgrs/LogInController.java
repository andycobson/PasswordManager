package org.mgrs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInController {

    // Start Up Scene elements
    @FXML
    private Label startLoginMessageLabel;
    @FXML
    private TextField startScreeUsrNTextfield;
    @FXML
    private TextField startScreenPassTextfield;

    SecurityService ss = new SecurityService();
    private Stage stage;
    private Scene currentScene;

    public void switchToSceneLoggedIn(ActionEvent event, String username) throws IOException{
        String sceneName ="LoggedInUserScene.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName));
        Parent root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        currentScene = new Scene(root);
        stage.setScene(currentScene);
        stage.show();
    }

    public void switchToSceneSignUp(ActionEvent event) throws IOException {
        String sceneName ="signUpScene.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName));
        Parent root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        currentScene = new Scene(root);
        stage.setScene(currentScene);
        stage.show();
    }

    @FXML
    public void startLoginButtonPress(ActionEvent event){
        int maxUsernameLength = 30;
        int maxPasswordLength = 20;
        String usernameInput = startScreeUsrNTextfield.getText();
        String passwordInput = startScreenPassTextfield.getText();
        if(usernameInput.length() > maxUsernameLength || usernameInput.length() < 8 || passwordInput.length() > maxPasswordLength || passwordInput.length() < 8){
            startLoginMessageLabel.setText("Incorrect Username/Password please try again. Or Sign-Up");
            return;
        }
        int authStatus = ss.authenticateLogin(usernameInput, passwordInput);
        if(authStatus != 1){
            startLoginMessageLabel.setText("Incorrect Username/Password please try again. Or Sign-Up");
            return;
        } else{
            startLoginMessageLabel.setText("Sign-In Successful!");
        }
        try {
            switchToSceneLoggedIn(event, usernameInput);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

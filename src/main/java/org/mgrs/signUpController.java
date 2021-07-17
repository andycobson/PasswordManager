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


public class signUpController {

    @FXML
    private Label passwordSignUpMessage2;
    @FXML
    private Label usernameSignUpMessage;
    @FXML
    private TextField signUpPassTextfield;
    @FXML
    private TextField signUpConfPasswordField;
    @FXML
    private Label signUpMessageLabel;
    @FXML
    private TextField signUpUsrNTextfield;
    @FXML
    private Label passwordSignUpMessage1;

    private Stage stage;
    private Scene currentScene;

    SecurityService ss = new SecurityService();

    public void switchToSceneStartUp(ActionEvent event) throws IOException {
        String sceneName ="LogInScene.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName));
        Parent root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        currentScene = new Scene(root);
        stage.setScene(currentScene);
        stage.show();
    }

    @FXML
    private void initSignUpService(){
        String username;
        String password;

        // Check Username
        if(checkUserNameRequirements() == 0){
            boolean taken = checkUsernameAvailable();
            if(!taken){
                username = signUpUsrNTextfield.getText();
                usernameSignUpMessage.setText("");
            } else{
                usernameSignUpMessage.setText("Username is already taken.");
                return;
            }
        } else{
            return;
        }

        // Check password
        if(checkPasswordRequirement() != -1) {
            passwordSignUpMessage1.setText("");
            if (passwordMatch(signUpPassTextfield.getText(), signUpConfPasswordField.getText()) != 0) {
                passwordSignUpMessage2.setText("Passwords do not match.");
                return;
            } else{
                password = signUpPassTextfield.getText();
                passwordSignUpMessage1.setText("");
                passwordSignUpMessage2.setText("");
            }
        } else{
            return;
        }
        ss.setLoginInfo(username, password);
        signUpUsrNTextfield.setText("");
        signUpPassTextfield.setText("");
        signUpConfPasswordField.setText("");
        signUpMessageLabel.setText("Sign Up Successful!");
    }

    private int passwordMatch(String password, String confirmPass){
        return password.compareTo(confirmPass);
    }

    private int checkUserNameRequirements(){
        String userName = signUpUsrNTextfield.getText();
        if(userName.length() < 8 || userName.length() > 30) {
            usernameSignUpMessage.setText("Invalid username length.");
            return -1;
        }
        return 0;
    }

    private int checkPasswordRequirement(){
        String password = signUpPassTextfield.getText();
        boolean caseFound = false;
        boolean specCharFound = false;
        boolean numFound = false;

        if(password.length() < 8 || password.length() > 20){
            passwordSignUpMessage1.setText("Invalid Length.");
            return -1;
        }

        for(int i = 0; i < password.length(); i++){
            int c = password.charAt(i);
            if(c > 64 && c < 91){
                caseFound = true;
            }
            if((c > 32 && c < 47) || (c > 57 && c < 65)){
                specCharFound = true;
            }
            if(c > 47 && c < 58){
                numFound = true;
            }
        }

        if(!caseFound){
            passwordSignUpMessage1.setText("Upper-case missing.");
            return -1;
        }
        if(!specCharFound){
            passwordSignUpMessage1.setText("Special Character missing.");
            return -1;
        }
        if(!numFound){
            passwordSignUpMessage1.setText("Number missing.");
            return -1;
        }

        return 0;
    }

    private boolean checkUsernameAvailable(){
        String userName = signUpUsrNTextfield.getText();
        return ss.getFileManager().checkUserNameInFile(userName);
    }



}

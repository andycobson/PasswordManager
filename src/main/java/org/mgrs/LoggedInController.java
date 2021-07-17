package org.mgrs;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Vector;

public class LoggedInController {

    @FXML
    private Label loggedInWelLabel;
    @FXML
    private VBox loggedInVbox;

    TextField serviceField;
    TextField usernameField;
    TextField passwordField;
    TextField userKeyField;
    TextField userUKF;

    SecurityService ss = new SecurityService();
    fileService fs = new fileService(0, ss.getLoggedInUser());
    Vector<Vector<String>> info;
    Vector<HBox> boxes;

    public void initialize(){
        loggedInWelLabel.setText("Welcome, " + ss.getLoggedInUser() + "!");
        initVboxItems();
    }

    public void logOutNow(ActionEvent event){
        ss.setLoggedInUser();
        Stage stage;
        Scene currentScene;
        String sceneName = "LogInScene.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName));
        try {
            Parent root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentScene = new Scene(root);
            stage.setScene(currentScene);
            stage.show();
        }catch(IOException e){
            System.out.println("NULL SCENE POINTER");
        }
    }

    public void initVboxItems() {
        this.info = fs.getAllUserInfo();
        this.boxes = new Vector<>();

        for (int i = 0; i < info.size(); i++) {
            HBox item = new HBox();
            item.setPrefHeight(50);
            item.setPrefWidth(750);
            item.setSpacing(15);
            item.setAlignment(Pos.CENTER_LEFT);

            userUKF = new TextField();
            userUKF.setId("userKeyField" + String.valueOf(i));
            userUKF.setPromptText("Pin");
            userUKF.setPrefWidth(100);
            Button viewButton = new Button();
            viewButton.setId("viewButton" + String.valueOf(i));
            viewButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new comButtonHandler());
            viewButton.setText("View");

            Button editButton = new Button();
            editButton.setId("editButton" + String.valueOf(i));
            editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new comButtonHandler());
            editButton.setText("Edit");

            Label label1 = new Label();
            label1.setPrefWidth(100);
            label1.setText(info.elementAt(i).elementAt(2));
            Label label2 = new Label();
            label2.setPrefWidth(100);
            label2.setText(info.elementAt(i).elementAt(0));
            Label label3 = new Label();
            label3.setPrefWidth(100);
            label3.setText("*******");

            item.getChildren().addAll(label1, label2, label3, userUKF, viewButton, editButton);
            boxes.add(item);
        }
        HBox insertBox = new HBox();
        insertBox.setPrefHeight(50);
        insertBox.setPrefWidth(750);
        insertBox.setSpacing(15);
        insertBox.setAlignment(Pos.CENTER_LEFT);

        serviceField = new TextField();
        serviceField.setPrefWidth(100);
        usernameField = new TextField();
        usernameField.setPrefWidth(100);
        passwordField = new TextField();
        passwordField.setPrefWidth(100);
        userKeyField = new TextField();
        userKeyField.setPrefWidth(100);

        serviceField.setPromptText("Service/Website");
        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");
        userKeyField.setPromptText("Pin");
        Button saveButton = new Button();
        saveButton.setText("Save");
        saveButton.setId("saveButton" + String.valueOf(info.size()));
        saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new comButtonHandler());

        insertBox.getChildren().addAll(serviceField, usernameField, passwordField, userKeyField, saveButton);
        boxes.add(insertBox);
        if(loggedInVbox.getChildren().size() > 0){
            loggedInVbox.getChildren().clear();
        }
        for(int j = 0; j < boxes.size(); j++){
            loggedInVbox.getChildren().add(boxes.elementAt(j));
        }

    }

    private class comButtonHandler implements EventHandler<Event> {
        @Override
        public void handle(Event evt) {
            String btnid = ((Control)evt.getSource()).getId();
            int index = Integer.parseInt(btnid.substring(btnid.length() - 1));
            String btnType = btnid.substring(0,3);

            // When a save button is pressed then this block of code will be executed.
            if(btnType.equals("sav") || btnType.equals("siv")){
                int bindex = 0;
                if(btnType.equals("siv")){
                    bindex = index;
                } else{
                    bindex = boxes.size() - 1;
                }

                serviceField = (TextField)boxes.elementAt(bindex).getChildren().get(0);
                usernameField = (TextField)boxes.elementAt(bindex).getChildren().get(1);
                passwordField = (TextField)boxes.elementAt(bindex).getChildren().get(2);
                userKeyField = (TextField)boxes.elementAt(bindex).getChildren().get(3);
                // Check if any of the text fields are empty
                if(passwordField.getText().trim().isEmpty()){
                    passwordField.setPromptText("Enter Password");
                    return;
                }
                if(usernameField.getText().trim().isEmpty()){
                    usernameField.setPromptText("Enter Username");
                    return;
                }
                if(serviceField.getText().trim().isEmpty()){
                    serviceField.setPromptText("Enter Service");
                    return;
                }
                if(userKeyField.getText().trim().isEmpty()){
                    userKeyField.setPromptText("Enter Pin");
                    return;
                }

                if(btnType.equals("sav")) {
                    try {
                        String encpt = ss.encrypt(passwordField.getText(), userKeyField.getText());
                        fs.addLoginData(usernameField.getText(), encpt, serviceField.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else{
                    String encpt = null;
                    try {
                        encpt = ss.encrypt(passwordField.getText(), userKeyField.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    info = fs.getAllUserInfo();
                    info.elementAt(index).set(0, usernameField.getText());
                    info.elementAt(index).set(1, encpt);
                    info.elementAt(index).set(2, serviceField.getText());
                    fs.clearFile();
                    for(int i = 0; i < info.size(); i++){
                        fs.addLoginData(info.elementAt(i).elementAt(0), info.elementAt(i).elementAt(1), info.elementAt(i).elementAt(2));
                    }
                }
                initVboxItems();
                return;
            } else if(btnType.equals("del")){
                info.removeElementAt(index);
                fs.clearFile();
                for(int i = 0; i < info.size(); i++){
                    fs.addLoginData(info.elementAt(i).elementAt(0), info.elementAt(i).elementAt(1), info.elementAt(i).elementAt(2));
                }
                initVboxItems();
                return;
            }
            // Here we looking to view or edit.
            //userUKF = new TextField();

            TextField userUKF = (TextField)boxes.elementAt(index).getChildren().get(3);
            String key = userUKF.getText();
            if(key.length() == 0){
                userUKF.setPromptText("Enter Key");
                return;
            }

            String password = info.elementAt(index).elementAt(1);

            Label passLabel = new Label();
            passLabel.setText("*******");
            try {
                password = ss.decrypt(password, key);
                if(password.equals("NULL")){
                    userUKF.setText("");
                    userUKF.setPromptText("Wrong Key");
                    return;
                }
                passLabel.setText(password);
            } catch (Exception e) {
                e.printStackTrace();
            }

            HBox item = new HBox();
            item.setPrefHeight(50);
            item.setPrefWidth(750);
            item.setSpacing(15);
            item.setAlignment(Pos.CENTER_LEFT);

            Button button1 = new Button();
            button1.addEventHandler(MouseEvent.MOUSE_CLICKED, new comButtonHandler());
            Button button2 = new Button();
            button2.addEventHandler(MouseEvent.MOUSE_CLICKED, new comButtonHandler());

            if(btnType.equals("vie")) {
                button1.setId("viewButton" + String.valueOf(index));
                button1.setText("View");

                button2.setId("editButton" + String.valueOf(index));
                button2.setText("Edit");

                userUKF.setPromptText("Pin");
                userUKF.setText("");

                Label label1 = new Label();
                label1.setPrefWidth(100);
                label1.setText(info.elementAt(index).elementAt(2));
                Label label2 = new Label();
                label2.setPrefWidth(100);
                label2.setText(info.elementAt(index).elementAt(0));
                passLabel.setPrefWidth(100);

                item.getChildren().addAll(label1, label2, passLabel, userUKF, button1, button2);
                boxes.set(index,item);
            } else if(btnType.equals("edi")){
                serviceField = new TextField();
                serviceField.setPrefWidth(100);
                usernameField = new TextField();
                usernameField.setPrefWidth(100);
                passwordField = new TextField();
                passwordField.setPrefWidth(100);
                userKeyField = new TextField();
                userKeyField.setPrefWidth(100);

                serviceField.setText(info.elementAt(index).elementAt(2));
                usernameField.setText(info.elementAt(index).elementAt(0));
                passwordField.setText(password);
                userKeyField.setText("");
                userKeyField.setPromptText("Pin");

                button1.setId("deleteButton" + index);
                button1.setText("Delete");

                button2.setId("siveButton" + index);
                button2.setText("Save");
                item.getChildren().addAll(serviceField, usernameField, passwordField, userKeyField, button1, button2);
                boxes.set(index,item);
            }

            if(loggedInVbox.getChildren().size() > 0){
                loggedInVbox.getChildren().clear();
            }
            for(int j = 0; j < boxes.size(); j++){
                loggedInVbox.getChildren().add(boxes.elementAt(j));
            }
        }
    }



}

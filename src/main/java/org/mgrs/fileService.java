package org.mgrs;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Vector;

/*
    This class is used for creating text files used for holding login information,
        and then reading and writing on the file.
 */

public class fileService {
    private String loginFilePath;
    private File loginFile;
    private Path currentRelativePath = Paths.get("");
    private String spath = currentRelativePath.toAbsolutePath().toString();

    private int mode;

    // The mode chooses which file to work in when an instance is created.
    // Mode 1, sets the txt file to one that holds all the users login information.
    // Mode 2, sets the txt file to a users personal file that holds their service/website info.

    fileService(int mode){
        this.mode = mode;
        if(mode == 1){
            initLoginFile();
        }
    }

    fileService(int mode, String username){
        this.mode = mode;
        if(mode == 0){
            initUserFile(username);
        }
    }

    public void initLoginFile(){
        this.loginFilePath = spath + "/temp/usifs.txt";
        this.loginFile = new File(loginFilePath);
        getLoginFile();
    }

    public void initUserFile(String username){
        this.loginFilePath = spath + "/temp/" + username + "_reach.txt";
        getLoginFile();
    }

    /*
        Function: getLoginFile
        Create Director based on the current directory location that the program is being ran from.
        In this directory is where the files will be created and maintained.
     */
    private void getLoginFile() {
        File dir = new File(spath + "/temp");
        if(!dir.exists()){
            dir.mkdirs();
        }
        this.loginFile = new File(loginFilePath);
        try {
            if (!loginFile.isFile()) {
                loginFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
        Function: addLoginData
        This function is used for both adding to the application login file and to the
            individual user data.
        We will expect String service to be empty when adding to the login file.
        Each row in the string will be for a different user in the login file, while each
            row in the individual user data will represent a new password entry.
        The data for each row is delimited with a semicolon surround by a single space.
        The standard java library for is used for file writing.
     */
    public void addLoginData(String username, String hePassword, String service) {
        String s = "";

        if(service.equals("")){
            s = username + " ; " + hePassword;
        }else{
            s = username + " ; " + hePassword + " ; " + service;
        }
            try {
                FileWriter fileWriter = new FileWriter(loginFilePath, true);
                BufferedWriter bw = new BufferedWriter(fileWriter);
                if (loginFile.length() != 0){
                    bw.newLine();
                }
                bw.write(s);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    /*
        Function: clearFile
        This function is called when we want to edit a line in a individual user data file and reset the
            data to the file.
     */
    public void clearFile(){
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        try {
            fileWriter = new FileWriter(loginFilePath, false);
            printWriter = new PrintWriter(fileWriter, false);
            printWriter.flush();
            printWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
        Function: checkUserNameInFile
        This function checks to see if the input username is already in the file. Later
            needed to not allow multiple users with the same username. Returns a boolean
            based if the username is found or not.
     */
    public boolean checkUserNameInFile(String username) {
        if (loginFile.length() == 0) {
            return false;
        }
        try {
            Scanner fileReader = new Scanner(loginFile);
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                if (username.compareTo(data.split(" ; ")[0]) == 0) {
                    return true;
                }
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
        Function: checkHashedPasswordsFromFile
        This function checks to see if the input hashed password is in the file. Later
            needed to compare the stored hashed password and compare with hashed user input
            password.
     */
    public int checkHashedPasswordsFromFile(String password) {
        if (loginFile.length() == 0) {
            return 0;
        }
        try {
            Scanner fileReader = new Scanner(loginFile);
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                if (password.compareTo(data.split(" ; ")[1]) == 0) {
                    return 1;
                }
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /*
        Function: getAllUserInfo
        This creates and returns a 2d vector of the file it reads.
     */
    public Vector<Vector<String>> getAllUserInfo() {
        Vector<Vector<String>> info = new Vector<>();
        Vector<String> pts = new Vector<>();
        if (loginFile.length() == 0) {
            return info;
        }
        try {
            Scanner fileReader = new Scanner(loginFile);
            while (fileReader.hasNextLine()) {
                pts = new Vector<>();
                String data = fileReader.nextLine();
                String parts[] = data.split(" ; ");
                for(String part:parts){
                    pts.add(part);
                }
                info.add(pts);
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }


}

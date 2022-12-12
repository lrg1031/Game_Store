package com.mycompany.lukegreen_finalproject_csc311;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setTitle("CSC 311 - Final Project - Luke Green");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * insert data into the database
     * parameters are just input fields for game data
     * 
     * @param title
     * @param esrbRating
     * @param userRating
     * @param price 
     */
    public static void insertDBData(String title, String esrbRating, int userRating, double price) {
        String databaseURL = "";
        Connection conn = null;
        try {
            databaseURL = "jdbc:ucanaccess://.//Games.accdb";
            conn = DriverManager.getConnection(databaseURL);

        } catch (SQLException ex) {
            //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            String sql = "INSERT INTO GameTable (Title, EsrbRating, UserRating, Price) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, esrbRating);
            preparedStatement.setInt(3, userRating);
            preparedStatement.setDouble(4, price);
            int row = preparedStatement.executeUpdate();
            if (row > 0) {
                //System.out.println("Row inserted");
            }
        } catch (SQLException e) {
            System.out.println("Error: File not Found insert");
        }
    }
    
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
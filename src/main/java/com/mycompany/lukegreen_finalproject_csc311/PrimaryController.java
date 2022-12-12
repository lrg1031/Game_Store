package com.mycompany.lukegreen_finalproject_csc311;

import java.util.ArrayList;
import javafx.fxml.FXML;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Controller class for my app
 * 
 * @author Luke Green
 */
public class PrimaryController {
    
    //counter to act as an iterator, checks which game we're looking at
    int currentGame = 0;
    
    //count up total cost of purchased games
    double currentCost = 0;
    
    //collection class to represent games inported from DB, form JSON file
    @FXML
    private ArrayList<Game> games = new ArrayList();
    
    //colleciton class to represent games you've purchased
    @FXML
    private ArrayList<Game> purchasedGames = new ArrayList();
    
    @FXML
    private TextField titleText;
    
    @FXML
    private TextField esrbText;
    
    @FXML
    private TextField userRatingText;
    
    @FXML
    private TextField priceText;
    
    @FXML
    private Text purchasedText;
    
    @FXML
    private Button purchaseBtn;
    
    @FXML
    private Button loadDb;
    
    @FXML
    private ListView purchasedView;
    
    @FXML
    private TextField costText;
    
    /**
     * will empty the database, and set total price to 0
     */
    @FXML
    public void initialize() {
        try {
                String databaseURL = "";
                Connection conn = null;
                databaseURL = "jdbc:ucanaccess://.//Games.accdb";
                conn = DriverManager.getConnection(databaseURL);
                String sql = "DELETE FROM GameTable";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error: File not Found MSA");
            }
        costText.setText("$" + Double.toString(0.0));
    }
    
    /**
     * thread used here to clean up purchase animation behavior
     * will control the animation/button disabling for each purchase
     * 
     * btw if you're reading this THEADS ARE AWESOME
     */
    public class PurchasedTextThread extends Thread {

        @Override
        public void run() {

            Platform.runLater(() -> {
                purchaseBtn.setDisable(true);
            });

            purchasedText.setOpacity(1);

            final Timeline timeline1 = new Timeline();
            timeline1.getKeyFrames().add(new KeyFrame(Duration.seconds(3), new KeyValue(purchasedText.layoutYProperty(), 295)));

            final Timeline timeline2 = new Timeline();
            timeline1.getKeyFrames().add(new KeyFrame(Duration.seconds(0), new KeyValue(purchasedText.layoutYProperty(), 315)));

            FadeTransition ft = new FadeTransition(Duration.millis(500), purchasedText);
            ft.setFromValue(1);
            ft.setToValue(0);

            ParallelTransition pt = new ParallelTransition(purchasedText, timeline2, ft);
            SequentialTransition st = new SequentialTransition(purchasedText, timeline1, pt);
            st.play();

            try {
                Thread.sleep(3500);
            } catch (InterruptedException ex) {
                Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Platform.runLater(() -> {
                purchaseBtn.setDisable(false);
            });
        }
    }
    
    
    /**
     * thread to prevent GUI from hanging while copying to database
     * also disables button during hang time
     */
    public class LoadDBThread extends Thread {

        @Override
        public void run() {

            Platform.runLater(() -> {
                loadDb.setDisable(true);
            });

            try {
                String databaseURL = "";
                Connection conn = null;
                databaseURL = "jdbc:ucanaccess://.//Games.accdb";
                conn = DriverManager.getConnection(databaseURL);
                String sql = "DELETE FROM GameTable";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Error: File not Found MSA");
            }

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            try {
                FileReader fr = new FileReader("SampleGames.json");
                Game[] t = gson.fromJson(fr, Game[].class);
                for (int i = 0; i < t.length; i++) {
                    String title = t[i].getTitle();
                    String esrbRating = t[i].getEsrbRating();
                    int userRating = t[i].getUserRating();
                    double price = t[i].getPrice();
                    App.insertDBData(title, esrbRating, userRating, price);
                }
            } catch (FileNotFoundException ex) {
                System.out.println("Error: File not Found JSON");
            }

            Platform.runLater(() -> {
                loadDb.setDisable(false);
            });

        }

    }

    
    /**
     * will load the database from the JSON file
     * uses a thread to prevent GUI hang for long operation
     */
     @FXML
    private void loadDBFromJSONfile() {
        LoadDBThread dbThread = new LoadDBThread();
        dbThread.start();
    }
    
    /**
     * will load games from the database to the Game Store
     * will add JSON formatted game objects to the member java collection class
     */
    @FXML
    private void loadGamesFromDB() {
        
        games = new ArrayList();

        String databaseURL = "";
        Connection conn = null;
        try {
            databaseURL = "jdbc:ucanaccess://.//Games.accdb";
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException ex) {
            System.out.println("Error: File not Found");
        }

        try {
            String tableName = "GameTable";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("select * from " + tableName);
            while (result.next()) {
                int id = result.getInt("ID");
                String title = result.getString("Title");
                String esrbRating = result.getString("EsrbRating");
                int userRating = result.getInt("UserRating");
                double price = result.getDouble("Price");
                //add to games list
                Game g = new Game(title, esrbRating, userRating, price);
                games.add(g);
            }
        } catch (SQLException except) {
            except.printStackTrace();
        }
        if(games.size() > 0) {
        //set the store to first game after loading games form db
        titleText.setText(games.get(0).getTitle());
        esrbText.setText(games.get(0).getEsrbRating());
        userRatingText.setText(Integer.toString(games.get(0).getUserRating()) + "/10");
        priceText.setText("$" + Double.toString(games.get(0).getPrice()));
        }
    }
    
    /**
     * will move to the next viewable game in the collection/store
     * doesn't do anything if at the end of the collection already
     */
    @FXML
    private void handleRight() {
        if (games.size() > 0) {
            if (currentGame < games.size() - 1) {
                currentGame++;
                titleText.setText(games.get(currentGame).getTitle());
                esrbText.setText(games.get(currentGame).getEsrbRating());
                userRatingText.setText(Integer.toString(games.get(currentGame).getUserRating()) + "/10");
                priceText.setText("$" + Double.toString(games.get(currentGame).getPrice()));
            }
        }
    }

    /**
     * will move to the previous viewable game in the collection/store
     * doesn't do anything if at the start of the collection already
     */
    @FXML
    private void handleLeft() {
        if (games.size() > 0) {
            if (currentGame > 0) {
                currentGame--;
                titleText.setText(games.get(currentGame).getTitle());
                esrbText.setText(games.get(currentGame).getEsrbRating());
                userRatingText.setText(Integer.toString(games.get(currentGame).getUserRating()) + "/10");
                priceText.setText("$" + Double.toString(games.get(currentGame).getPrice()));
            }
        }
    }
    
    /**
     * ------------uses lambdas and streams to calculate total price------------
     * 
     * will purchase the selected game
     * adds game data to listview, and updates total price
     * plays an animation to let user know of purchase completion
     * 
     * also uses a thread
     * 
     * @throws InterruptedException 
     */
    @FXML
    private void handlePurchase() throws InterruptedException {
        if(games.size() > 0) {

        PurchasedTextThread purchaseThread = new PurchasedTextThread();
        purchaseThread.start();
        
        purchasedGames.add(games.get(currentGame));
        
        String newEntry = games.get(currentGame).toString();
        ObservableList<String> gameTitles = purchasedView.getItems();
        gameTitles.add(newEntry);
        
        //deep copy of purchased games to another collection to stream it
        ArrayList<Game> gamesCost = new ArrayList();
        for(int i = 0; i < purchasedGames.size(); i++) {
            gamesCost.add(purchasedGames.get(i).makeCopy());
        }
        
        //a stream is used to help calculate total price of the games combined
        //lambda is used in the below experssion to map games to their prices
        currentCost = gamesCost.stream().mapToDouble(g -> g.getPrice()).sum();
        costText.setText("$" + Double.toString(currentCost));
        }
    }
    
    /**
     * will close the GUI
     */
    @FXML
    private void closeGUI() {
        System.exit(0);
    }
    
    /**
     * will reset the purchases you've made
     * resets lsitview, total price, and purchased game collection
     */
    @FXML
    private void resetPurchases() {
        purchasedGames = new ArrayList();
        ObservableList<String> gameTitles = purchasedView.getItems();
        gameTitles.clear();
        currentCost = 0;
        costText.setText("$" + currentCost);
    }

    /**
     * Poor switchToSecondary no one ever uses you
     * 
     * @throws IOException 
     */
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}

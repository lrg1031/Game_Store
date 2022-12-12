package com.mycompany.lukegreen_finalproject_csc311;


import com.google.gson.annotations.SerializedName;

/**
 * Makes up game class
 * 
 * @author Luke Green
 */
public class Game {
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("esrbRating")
    private String esrbRating;
    
    @SerializedName("userRating")
    private int userRating;
    
    @SerializedName("price")
    private double price;
    
    /**
     * constructor
     */
    public Game() {
        title = "";
        esrbRating = "";
        userRating = 0;
        price = 0.0;
    }
    
    /**
     * constructor w parameters
     * 
     * @param title
     * @param esrb rating
     * @param user rating
     * @param price 
     */
    public Game(String title, String esrb, int user, double price) {
        this.title = title;
        this.esrbRating = esrb;
        this.userRating = user;
        this.price = price;
    }
    
    /**
     * copy constructor
     * 
     * @param other copied
     */
    public Game(Game other) {
        this.title = other.getTitle();
        this.esrbRating = other.getEsrbRating();
        this.userRating = other.getUserRating();
        this.price = other.getPrice();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEsrbRating() {
        return esrbRating;
    }

    public void setEsrbRating(String esrbRating) {
        this.esrbRating = esrbRating;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    
    /**
     * deep copy of this game
     * 
     * @return deep copy
     */
    public Game makeCopy() {
        String title = this.title;
        String esrb = this.esrbRating;
        int userRating = this.userRating;
        double price = this.price;
        Game newGame = new Game(title, esrb, userRating, price);
        return newGame;
    }
    
    /**
     * override based on member variables
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        Game other = (Game) obj;
        if(title.equals(other.getTitle()) && esrbRating.equals(other.getEsrbRating()) && userRating == other.getUserRating() && price == other.getPrice()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * return in the form: "title, esrb, rating/10, $price"
     * 
     * @return 
     */
    @Override
    public String toString() {
        String s =  title + ", " + esrbRating + ", " + userRating + "/10" + ", " + "$" + price;
        return s;
    }

    
}

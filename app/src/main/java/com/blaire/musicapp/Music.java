package com.blaire.musicapp;

public class Music {
    public String artist,image, title;

    public Music(){

    }
    public Music(String artist, String image, String title) {
        this.artist = artist;
        this.image = image;
        this.title = title;
    }

    public String getArtist() {

        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

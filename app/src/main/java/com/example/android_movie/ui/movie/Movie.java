package com.example.android_movie.ui.movie;

public class Movie {
    String title, description, release, producer, genre, id, imgpath, roles;

    public Movie(String title, String description, String release, String producer,  String genre, String imgpath, String id) {
        this.title = title;
        this.description = description;
        this.release = release;
        this.producer = producer;
        this.genre = genre;
        this.imgpath = imgpath;
        this.id = id;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", release='" + release + '\'' +
                ", producer='" + producer + '\'' +
                ", genre='" + genre + '\'' +
                ", imgpath='" + imgpath + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}

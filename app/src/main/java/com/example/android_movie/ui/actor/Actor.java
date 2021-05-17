package com.example.android_movie.ui.actor;

public class Actor {
    String firstname, lastname, note, id, imgpath, role;

    public Actor(String imgpath, String firstname, String lastname, String note, String id) {
        this.imgpath = imgpath.toString();
        this.firstname = firstname.toString();
        this.lastname = lastname.toString();
        this.note = note.toString();
        this.id = id.toString();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "imgpath='" + imgpath + '\'' +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", note='" + note + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}


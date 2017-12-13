/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.demos.kitchen;


import java.util.Date;

/**
 *
 * @author Shil
 */
public class Evaluation {
     private int id;
    private byte note; //max 127
    private Date myDate;//type date ?
    // private DATETIME date_evaluation ; //type date ?
    private String commentaire;
   

    public Evaluation(byte note, String commentaire) {
        Date date = new Date();
        //  Date date = Date.valueOf(LocalDate.now());
        this.myDate = date;
        this.note = note;
        this.commentaire = commentaire;
       
    }

    public Evaluation() {
    }

    // les constructeur à modifier aprés integration
    public Evaluation(int id, byte note, Date myDate, String commentaire) {
        this.id = id;
        this.note = note;
        this.myDate = myDate;
        this.commentaire = commentaire;
       
    }

    public Evaluation(byte note, Date myDate, String commentaire) {

        this.note = note;
        this.myDate = myDate;
        this.commentaire = commentaire;
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getNote() {
        return note;
    }

    public void setNote(byte note) {
        this.note = note;
    }

    public Date getMyDate() {
        return myDate;
    }

    public void setMyDate(Date myDate) {
        this.myDate = myDate;
    }

    /*public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }*/

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }


   

    /*public Trajet getTrajet() {
    return trajet;
    }
    public void setTrajet(Trajet trajet) {
    this.trajet = trajet;
    }*/
   

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Evaluation other = (Evaluation) obj;
        if (this.id != other.id) {
            return false;
        }
    
        return true;
    }

   
    @Override
    public String toString() {
        return "Evaluation{" + "id=" + id + ", note=" + note + ", myDate=" + myDate + ", commentaire=" + commentaire + '}';
    }

   
    

    
}

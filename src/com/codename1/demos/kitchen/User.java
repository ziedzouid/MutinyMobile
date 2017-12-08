/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.demos.kitchen;

import com.codename1.l10n.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ASUS
 */
public class User {

    protected int id;
    protected String nom;
    protected String prenom;
    protected String sexe;
    protected Date date_naissance;
    protected String email;
    protected int tel;
    protected String mdp;
    protected String avatar;
    protected String role;

    public User() {
    }

    public User(int id, String nom, String prenom, String sexe, Date date_naissance, int tel, String email, String mdp, String avatar, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.date_naissance = date_naissance;
        this.email = email;
        this.tel = tel;
        this.mdp = mdp;
        this.avatar = avatar;
        this.role = role;
    }

    public User(String nom, String prenom, String sexe, Date date_naissance, int tel, String email, String mdp, String avatar, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.date_naissance = date_naissance;
        this.email = email;
        this.tel = tel;
        this.mdp = mdp;
        this.avatar = avatar;
        this.role = role;

    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Date getDate_naissance() {
        return date_naissance;
    }

    public String getEmail() {
        return email;
    }

    public int getTel() {
        return tel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setDate_naissance(Date date_naissance) {
        this.date_naissance = date_naissance;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", sexe=" + sexe + ", date_naissance=" + new SimpleDateFormat("MM/dd/yyyy").format(date_naissance) + ", email=" + email + ", tel=" + tel + ", mdp=" + mdp + ", role=" + role + ", avatar=" + avatar + '}';
    }

    public String getSexe() {
        return sexe;
    }

    public String getMdp() {
        return mdp;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}

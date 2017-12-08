/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.codename1.demos.kitchen;
 package com.codename1.demos.kitchen;

import com.codename1.components.OnOffSwitch;
import com.codename1.io.CharArrayReader;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.Log;
import com.codename1.io.NetworkManager;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Dialog;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.spinner.Picker;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public class UserDAO {

    static User user = new User();
    int temp;
    Form acceuil;
    int attempt;

    public void loginUser() {

        String userlog = Input2.emaillog.getText();
        String passlog = Input2.passwordlog.getText();

        ConnectionRequest connectionRequest;
        connectionRequest = new ConnectionRequest() {
            @Override
            protected void readResponse(InputStream input) throws IOException {

                JSONParser json = new JSONParser();
                try {
                    Reader reader = new InputStreamReader(input, "UTF-8");
                    Map<String, Object> data = json.parseJSON(reader);
                    if (data.isEmpty()) {
                        Dialog.show("error", "Email not found ! please retry ", "cancel", "ok");
                    } else {
                        user.setId((int) Float.parseFloat(data.get("id").toString()));
                        user.setEmail(((String) data.get("email")));
                        user.setNom(((String) data.get("nom")));
                        user.setPrenom(((String) data.get("prenom")));
                        user.setTel((int) Float.parseFloat(data.get("telephone").toString()));
                        user.setMdp(((String) data.get("password")));
                        user.setSexe(((String) data.get("sexe")));
                        Map<String, Object> data2 = (Map<String, Object>) (data.get("dateNaissance"));
                        temp = (int) Float.parseFloat(data2.get("timestamp").toString());
                        Date myDate = new Date(temp * 1000L);
                        user.setDate_naissance(myDate);
                        List<String> content = new ArrayList<>();
                        content.addAll((Collection<? extends String>) (data.get("roles")));
                        user.setRole(content.get(0));
                    }
                } catch (IOException err) {
                    Log.e(err);

                }

            }

            @Override
            protected void postResponse() {
                System.out.println(user);
                if (passlog.equals("")) {
                    Dialog.show("error", "Please put your password ! ", "cancel", "ok");
                } else if (!(user.getMdp().equals(passlog))) {
                    System.out.println(user.getMdp());
                    System.out.println(passlog);

                    if (attempt >= 3) {
                        Dialog.show("error", "You have tried 3 wrong password !, We have send your password in mail ", "cancel", "ok");
                        attempt = 0;

                    } else {
                        Dialog.show("error", "Wrong password please retry! ", "cancel", "ok");
                        attempt++;
                    }

                } else {
                    /*acceuil = new Form(BorderLayout.CENTER);
                    Label label1 = new Label();
                    if (user.role.equals("ROLE_CONDUCTEUR")) {
                        label1.setText("Hello Pilot !!");
                    } else {
                        label1.setText("Hello Passenger !!");
                    }
                    acceuil.add(label1);
                    acceuil.show();*/

                    KitchenSink ks = new KitchenSink();
                    ks.showMainUI();

                }

            }

        };
        connectionRequest.setUrl("http://localhost/api-cool/web/app_dev.php/user/find/" + userlog);
        NetworkManager.getInstance().addToQueue(connectionRequest);

    }

    public void RegisterUser() {

        /* TextField firstname = (TextField) MyApplication.builder.findByName("firstname", MyApplication.ctn2);
        TextField lastname = (TextField) MyApplication.builder.findByName("lastname", MyApplication.ctn2);
        TextField email = (TextField) MyApplication.builder.findByName("emailsign", MyApplication.ctn2);
        TextField phonenumber = (TextField) MyApplication.builder.findByName("phonesign", MyApplication.ctn2);
        TextField password = (TextField) MyApplication.builder.findByName("passwordsign", MyApplication.ctn2);
        TextField passconfirm = (TextField) MyApplication.builder.findByName("passconfirm", MyApplication.ctn2);
        Picker p = (Picker) MyApplication.builder.findByName("birthdate", MyApplication.ctn2);
        OnOffSwitch offSwitch = (OnOffSwitch) MyApplication.builder.findByName("sexe", MyApplication.ctn2);
        ComboBox cmb = (ComboBox) MyApplication.builder.findByName("role", MyApplication.ctn2);*/
        // System.out.println(p.getText());
        String nom = Input.name.getText();
        String prenom = Input.lastname.getText();
        String mail = Input.email.getText();
        int tel = Integer.parseInt(Input.phonenumber.getText());
        String pass = Input.password.getText();
        String passconf = Input.passwordconfirm.getText();
        Date date = Input.birthday.getDate();
        String pickrole = Input.role.getSelectedString();

        String sexe;
        if (Input.sexe.isValue() == false) {
            sexe = "Masculin";
        } else {
            sexe = "Femenin";

        }
        String role;
        if (pickrole.equals("PILOT")) {
            role = "ROLE_CONDUCTEUR";
        } else {
            role = "ROLE_PASSAGER";
        }

        if (!pass.equals(passconf)) {
            Dialog.show("error", "please confirm your password ", "cancel", "ok");

        } else {
            user.setEmail(mail);
            user.setNom(nom);
            user.setPrenom(prenom);
            user.setTel(tel);
            user.setMdp(pass);
            user.setSexe(sexe);
            user.setDate_naissance(date);
            user.setRole(role);
            System.out.println(user);

        }
        ConnectionRequest connectionRequest;

        connectionRequest = new ConnectionRequest() {
            @Override
            protected void readResponse(InputStream input) throws IOException {
                System.out.println(input);

            }

            @Override
            protected void postResponse() {
                /* acceuil = new Form(BorderLayout.CENTER);
                Label label2 = new Label();
                if (role.equals("ROLE_CONDUCTEUR")) {
                    label2.setText("Hello Pilote " + nom);
                } else {
                    label2.setText("Hello Passenger " + nom);

                }
                acceuil.add(label2);
                acceuil.show();*/

                KitchenSink ks = new KitchenSink();
                ks.showMainUI();

            }

        };
        connectionRequest.setUrl("http://localhost/api-cool/web/app_dev.php/user/new?firstname=" + nom + "&lastname=" + prenom + "&mail=" + mail + "&phone=" + tel + "&password=" + pass + "&sexe=" + sexe + "&role=" + role + "&birthdate=" + Input.birthday.getDate());
        NetworkManager.getInstance().addToQueue(connectionRequest);

    }

}

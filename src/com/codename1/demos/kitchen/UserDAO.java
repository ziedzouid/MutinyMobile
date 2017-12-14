/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.codename1.demos.kitchen;
package com.codename1.demos.kitchen;

import com.codename1.components.OnOffSwitch;
import com.codename1.components.ToastBar;
import com.codename1.io.CharArrayReader;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.Log;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.ui.Dialog;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.events.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author ASUS
 */
public class UserDAO {

    static User user = new User();
    int temp;
    Form acceuil;
    int attempt;
    static ArrayList<User> users;
    String en;
    String decrypt1;
    String decrypt2;

    public void loginUser() {
        getListUsers();
        //System.out.println(users);
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
                        decrypt1 = (String) data.get("password");
                        decrypt2 = MD5.hash(passlog);
                        // user.setMdp(decrypt);                        
                        user.setSexe(((String) data.get("sexe")));
                        Map<String, Object> data2 = (Map<String, Object>) (data.get("dateNaissance"));
                        temp = (int) Float.parseFloat(data2.get("timestamp").toString());
                        Date myDate = new Date(temp * 1000L);
                        user.setDate_naissance(myDate);
                        List<String> content = new ArrayList<>();
                        content.addAll((Collection<? extends String>) (data.get("roles")));
                        user.setRole(content.get(0));
                        en = (String) data.get("enabled");
                    }
                } catch (IOException err) {
                    Log.e(err);

                }

            }

            @Override
            protected void postResponse() {

                System.out.println(en);

                if (passlog.equals("")) {
                    Dialog.show("error", "Please put your password ! ", "cancel", "ok");
                } else if (!(decrypt1.equals(decrypt2))) {

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
                    user.setMdp(passlog);
                    System.out.println(user);
                    KitchenSink ks = new KitchenSink();
                    ks.showMainUI();

                }

            }

        };
        connectionRequest.setUrl("http://localhost/api-cool/web/app_dev.php/user/find/" + userlog);
        NetworkManager.getInstance().addToQueue(connectionRequest);

    }

    public void RegisterUser() {

        boolean namevalvide = Validation.isEmpty(Input.name.getText());
        boolean prenonvalvide = Validation.isEmpty(Input.lastname.getText());
        boolean mailvalvide = Validation.isEmpty(Input.email.getText());
        boolean telvalvide = Validation.isEmpty(Input.phonenumber.getText());
        boolean passvalvide = Validation.isEmpty(Input.password.getText());
        boolean passconfvalvide = Validation.isEmpty(Input.passwordconfirm.getText());

        /* long datebirth = Input.birthday.getDate().getTime();
               System.out.println(datebirth);*/
        Date dateb = Input.birthday.getDate();
        Date d = new Date();
        long local = d.getTime();
        long birth = dateb.getTime();
        long agelong = local - birth;
        long age = agelong / (31536000 * 1000L);
        System.out.println(age);

        /* System.out.println("local="+local);
        System.out.println("birth="+birth);
        System.out.println(local-birth);*/
        //boolean mailvalstruct = Validation.test_email(mail);
        //boolean telvalstruct = Validation.isIntConvertible(Input.phonenumber.getText());
        boolean tempval = true;

        if (namevalvide) {
            Dialog.show("error", "please insert your firstname ", "cancel", "ok");
            tempval = false;
        } else if (prenonvalvide) {
            Dialog.show("error", "please insert your lastname ", "cancel", "ok");
            tempval = false;

        } else if (mailvalvide) {
            Dialog.show("error", "please insert your email ", "cancel", "ok");
            tempval = false;

        } else if (telvalvide) {
            Dialog.show("error", "please insert your phone number ", "cancel", "ok");
            tempval = false;

        } else if (passvalvide) {
            Dialog.show("error", "please insert your password ", "cancel", "ok");
            tempval = false;

        } else if (passconfvalvide) {
            Dialog.show("error", "please confirm your passsword ", "cancel", "ok");
            tempval = false;

        }/* else if (!mailvalstruct) {
            Dialog.show("error", "please enter a valide email ", "cancel", "ok");
            tempval = false;

        } else if (!telvalstruct) {
            Dialog.show("error", "please enter a valide phone number ", "cancel", "ok");
            tempval = false;

        }*/ else if (!Input.password.getText().equals(Input.passwordconfirm.getText())) {
            Dialog.show("error", "password not confirmed ", "cancel", "ok");
            tempval = false;

        } else if (age < 18) {
            Dialog.show("error", "Your Age is less then 18 ", "cancel", "ok");
            tempval = false;
        }

        if (tempval) {
            boolean userverif = false;
            getListUsers();
            System.out.println(users);
            for (User us : users) {
                if (us.email.equals(Input.email.getText())) {
                    userverif = true;
                }

            }
            if (userverif) {
                Dialog.show("error", "Mail used !!", "cancel", "ok");

            } else {

                String nom = Input.name.getText();
                String prenom = Input.lastname.getText();
                String mail = Input.email.getText();
                int tel = Integer.parseInt(Input.phonenumber.getText());
                String pass = Input.password.getText();
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

                user.setEmail(mail);
                user.setNom(nom);
                user.setPrenom(prenom);
                user.setTel(tel);
                user.setMdp(pass);
                user.setSexe(sexe);
                user.setDate_naissance(date);
                user.setRole(role);
                System.out.println(user);

                ConnectionRequest connectionRequest = null;

                connectionRequest = new ConnectionRequest() {
                    @Override

                    protected void readResponse(InputStream input) throws IOException {
                        System.out.println(input);

                    }

                    @Override
                    protected void postResponse() {
                        ToastBar.showMessage("Save pressed...", FontImage.MATERIAL_INFO);
                        KitchenSink ks = new KitchenSink();
                        ks.showMainUI();

                    }

                };

                connectionRequest.setUrl("http://localhost/api-cool/web/app_dev.php/user/new?firstname=" + nom + "&lastname=" + prenom + "&mail=" + mail + "&phone=" + tel + "&password=" + MD5.hash(pass) + "&sexe=" + sexe + "&role=" + role + "&birthdate=" + Input.birthday.getDate());
                NetworkManager.getInstance().addToQueue(connectionRequest);
            }
        }
    }

    public void updateUser() {

        boolean namevalvide = Validation.isEmpty(updateProfile.nameup.getText());
        boolean prenonvalvide = Validation.isEmpty(updateProfile.lastnameup.getText());
        boolean mailvalvide = Validation.isEmpty(updateProfile.emailup.getText());
        boolean telvalvide = Validation.isEmpty(updateProfile.phonenumberup.getText());

        Date dateb = updateProfile.birthdayup.getDate();
        Date d = new Date();
        long local = d.getTime();
        long birth = dateb.getTime();
        long agelong = local - birth;
        long age = agelong / (31536000 * 1000L);
        System.out.println(age);

        boolean tempval = true;

        if (namevalvide) {
            Dialog.show("error", "please insert your firstname ", "cancel", "ok");
            tempval = false;
        } else if (prenonvalvide) {
            Dialog.show("error", "please insert your lastname ", "cancel", "ok");
            tempval = false;

        } else if (mailvalvide) {
            Dialog.show("error", "please insert your email ", "cancel", "ok");
            tempval = false;

        } else if (telvalvide) {
            Dialog.show("error", "please insert your phone number ", "cancel", "ok");
            tempval = false;

        } /* else if (!mailvalstruct) {
            Dialog.show("error", "please enter a valide email ", "cancel", "ok");
            tempval = false;

        } else if (!telvalstruct) {
            Dialog.show("error", "please enter a valide phone number ", "cancel", "ok");
            tempval = false;

        }*/ else if (age < 18) {
            Dialog.show("error", "Your Age is less then 18 ", "cancel", "ok");
            tempval = false;
        }

        if (tempval) {
            boolean userverif = false;
            getListUsers();
            System.out.println(users);
            for (User us : users) {
                if (us.email.equals(updateProfile.emailup.getText())) {
                    userverif = true;
                }

            }
            if (userverif) {
                Dialog.show("error", "Mail used !!", "cancel", "ok");

            } else {

                String nom = updateProfile.nameup.getText();
                String prenom = updateProfile.lastnameup.getText();
                String mail = updateProfile.emailup.getText();
                int tel = Integer.parseInt(updateProfile.phonenumberup.getText());

                Date date = updateProfile.birthdayup.getDate();

                user.setEmail(mail);
                user.setNom(nom);
                user.setPrenom(prenom);
                user.setTel(tel);
                user.setDate_naissance(date);
                System.out.println(user);

                ConnectionRequest connectionRequest = null;

                connectionRequest = new ConnectionRequest() {
                    @Override

                    protected void readResponse(InputStream input) throws IOException {
                        System.out.println(input);

                    }

                    @Override
                    protected void postResponse() {
                        ToastBar.showMessage("Changes ...", FontImage.MATERIAL_INFO);
                        KitchenSink ks = new KitchenSink();
                        ks.showMainUI();

                    }

                };

                connectionRequest.setUrl("http://localhost/api-cool/web/app_dev.php/user/edit?id=" + user.id + "&firstname=" + nom + "&lastname=" + prenom + "&mail=" + mail + "&phone=" + tel + "&birthdate=" + updateProfile.birthdayup.getDate());
                NetworkManager.getInstance().addToQueue(connectionRequest);
            }
        }
    }

    public void updateUserMdp() {

        boolean namevalvide = Validation.isEmpty(updatepass.nameup.getText());
        boolean prenonvalvide = Validation.isEmpty(updatepass.lastnameup.getText());
        boolean mailvalvide = Validation.isEmpty(updatepass.emailup.getText());

        boolean tempval = true;

        if (namevalvide) {
            Dialog.show("error", "please insert your firstname ", "cancel", "ok");
            tempval = false;
        } else if (prenonvalvide) {
            Dialog.show("error", "please insert your lastname ", "cancel", "ok");
            tempval = false;

        } else if (mailvalvide) {
            Dialog.show("error", "please insert your email ", "cancel", "ok");
            tempval = false;

        }
        /* else if (!mailvalstruct) {
            Dialog.show("error", "please enter a valide email ", "cancel", "ok");
            tempval = false;

        } else if (!telvalstruct) {
            Dialog.show("error", "please enter a valide phone number ", "cancel", "ok");
            tempval = false;

        }*/

        if (tempval) {
            System.out.println(users);

            if (!user.mdp.equals(updatepass.nameup.getText())) {
                System.out.println(user.mdp); 
                System.out.println(updatepass.nameup); 
                Dialog.show("error", "OLD PASS WRONG !!", "cancel", "ok");
            } else if (user.mdp.equals(updatepass.lastnameup.getText())) {
                Dialog.show("error", "DO Not use  OLD ONE !!", "cancel", "ok");

            } else if (!updatepass.lastnameup.getText().equals(updatepass.emailup.getText())) {
                Dialog.show("error", "Not confirmed !!", "cancel", "ok");

            } else {

                String mdp = updatepass.lastnameup.getText();

                user.setMdp(mdp);

                System.out.println(user);

                ConnectionRequest connectionRequest = null;

                connectionRequest = new ConnectionRequest() {
                    @Override

                    protected void readResponse(InputStream input) throws IOException {
                        System.out.println(input);

                    }

                    @Override
                    protected void postResponse() {
                        ToastBar.showMessage("Changes ...", FontImage.MATERIAL_INFO);
                        KitchenSink ks = new KitchenSink();
                        ks.showMainUI();

                    }

                };

                connectionRequest.setUrl("http://localhost/api-cool/web/app_dev.php/user/editpass?id=" + user.id + "&mdp=" + MD5.hash(mdp));
                NetworkManager.getInstance().addToQueue(connectionRequest);
            }
        }
    }

    public void getListUsers() {
        ConnectionRequest con = new ConnectionRequest();
        con.setUrl("http://localhost/api-cool/web/app_dev.php/user/all"); //Pour la liste des étudiants 

        con.addResponseListener(new ActionListener<NetworkEvent>() {

            @Override
            public void actionPerformed(NetworkEvent evt) {
                String json = new String(con.getResponseData());
                users = new ArrayList<>();
                try {

                    JSONParser j = new JSONParser();

                    Map<String, Object> etudiants = j.parseJSON(new CharArrayReader(json.toCharArray()));

                    System.out.println();
                    List<Map<String, Object>> list = (List<Map<String, Object>>) etudiants.get("root");

                    users.clear();

                    for (Map<String, Object> obj : list) {
                        User e = new User();//id, json, status);
                        e.setId((int) Float.parseFloat(obj.get("id").toString()));
                        e.setEmail(((String) obj.get("email")));
                        e.setNom(((String) obj.get("nom")));
                        e.setPrenom(((String) obj.get("prenom")));
                        e.setTel((int) Float.parseFloat(obj.get("telephone").toString()));
                        e.setSexe(((String) obj.get("sexe")));
                        Map<String, Object> data2 = (Map<String, Object>) (obj.get("dateNaissance"));
                        List<String> content = new ArrayList<>();
                        content.addAll((Collection<? extends String>) (obj.get("roles")));
                        //e.setRole(content.get(0));
                        //users.add(e);
                        if (content.get(0).equals("ROLE_CONDUCTEUR")) {
                            e.setRole("Pilot");
                            users.add(e);
                        } else if (content.get(0).equals("ROLE_PASSAGER")) {
                            e.setRole("passenger");
                            users.add(e);
                        }

                    }
                    // System.out.println(users);
                } catch (IOException ex) {
                }

            }

        });

        NetworkManager.getInstance().addToQueue(con);

    }

}

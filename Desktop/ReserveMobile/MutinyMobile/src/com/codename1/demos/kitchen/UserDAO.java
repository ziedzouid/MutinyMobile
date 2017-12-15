/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.codename1.demos.kitchen;
package com.codename1.demos.kitchen;

import com.codename1.components.ToastBar;
import static com.codename1.demos.kitchen.KitchenSink.res;
import com.codename1.io.CharArrayReader;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.Log;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.lang.Math;
import java.util.Random;

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
    boolean tempvalog = true;
    static String KeySms;

    public void msgApi(int phone) {
        String ACCOUNT_SID = "AC6a7e8ae95710a4389b00efa448d00e74";
        String AUTH_TOKEN = "e2019ece4632c0f9519c58ec64e34a29";
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        String tophone = Integer.toString(phone);
       
        System.out.println(phone);
        System.out.println(tophone);

        Random rand = new Random();
        int rd = rand.nextInt(500);

        KeySms = Integer.toString(rd);
        Message message = Message.creator(new PhoneNumber("+216" + tophone),
                new PhoneNumber("+12015617496"),
                KeySms).create();

        System.out.println(message.getSid());
    }

    public void loginUser() {
        getListUsers();
        //System.out.println(users);
        String userlog = LoginForm.emaillog.getText();
        String passlog = LoginForm.passwordlog.getText();

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
                        int keyphoen = (int) Float.parseFloat(data.get("telephone").toString());
                        decrypt1 = (String) data.get("password");
                        decrypt2 = MD5.hash(passlog);
                        // user.setMdp(decrypt);
                        user.setMdp(passlog);
                        user.setSexe(((String) data.get("sexe")));
                        Map<String, Object> data2 = (Map<String, Object>) (data.get("dateNaissance"));
                        temp = (int) Float.parseFloat(data2.get("timestamp").toString());
                        Date myDate = new Date(temp * 1000L);
                        user.setDate_naissance(myDate);
                        List<String> content = new ArrayList<>();
                        content.addAll((Collection<? extends String>) (data.get("roles")));
                        user.setRole(content.get(0));
                        en = (String) data.get("enabled");
                        tempvalog = true;
                        user.setTel(user.getTel()-2);
                        if (passlog.equals("")) {
                            Dialog.show("error", "Please put your password ! ", "cancel", "ok");
                            tempvalog = false;
                        } else if (!(decrypt1.equals(decrypt2))) {

                            System.out.println(passlog);

                            if (attempt >= 2) {

                                boolean smsSend = Dialog.show("error", "You have tried 3 wrong password !, We have send your password in mail ", "cancel", "ok");
                                tempvalog = false;
                                attempt = 0;
                                if (!smsSend) {
                                    msgApi(keyphoen-2);
                                    Demo d = new UpdatepassSmsForm();
                                    d.init(res);
                                    Form f = new Form("UPDATE Pass with key", new BorderLayout());
                                    f.add(BorderLayout.CENTER, d.createDemo(f));
                                    Form previous = Display.getInstance().getCurrent();
                                    f.getToolbar().setBackCommand(" ", ee -> {
                                        if (d.onBack()) {
                                            previous.showBack();
                                        }
                                    });
                                    f.show();
                                }

                            } else {
                                Dialog.show("error", "Wrong password please retry! ", "cancel", "ok");
                                tempvalog = false;
                                attempt++;
                            }

                        } else if (en.equals("false")) {
                            Dialog.show("error", "Disabled Account !! ", "cancel", "ok");
                            tempvalog = false;

                        }
                    }
                } catch (IOException err) {
                    Log.e(err);

                }

            }

            @Override
            protected void postResponse() {

                System.out.println(en);
                if (tempvalog) {
                    // System.out.println(user);
                    KitchenSink ks = new KitchenSink();
                    ks.showMainUI();

                }

            }

        };
        connectionRequest.setUrl("http://localhost/api-cool/web/app_dev.php/user/find/" + userlog);
        NetworkManager.getInstance().addToQueue(connectionRequest);

    }

    public void RegisterUser() {

        boolean namevalvide = Validation.isEmpty(RegisterForm.name.getText());
        boolean prenonvalvide = Validation.isEmpty(RegisterForm.lastname.getText());
        boolean mailvalvide = Validation.isEmpty(RegisterForm.email.getText());
        boolean telvalvide = Validation.isEmpty(RegisterForm.phonenumber.getText());
        boolean passvalvide = Validation.isEmpty(RegisterForm.password.getText());
        boolean passconfvalvide = Validation.isEmpty(RegisterForm.passwordconfirm.getText());

        /* long datebirth = Input.birthday.getDate().getTime();
               System.out.println(datebirth);*/
        Date dateb = RegisterForm.birthday.getDate();
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

        }*/ else if (!RegisterForm.password.getText().equals(RegisterForm.passwordconfirm.getText())) {
            Dialog.show("error", "password not confirmed ", "cancel", "ok");
            tempval = false;

        } else if (age < 18) {
            Dialog.show("error", "Your Age is less then 18 ", "cancel", "ok");
            tempval = false;
        }

        if (tempval) {
            boolean userverif = false;
            getListUsers();
            //  System.out.println(users);
            for (User us : users) {
                if (us.email.equals(RegisterForm.email.getText())) {
                    userverif = true;
                }

            }
            if (userverif) {
                Dialog.show("error", "Mail used !!", "cancel", "ok");

            } else {

                String nom = RegisterForm.name.getText();
                String prenom = RegisterForm.lastname.getText();
                String mail = RegisterForm.email.getText();
                int tel = Integer.parseInt(RegisterForm.phonenumber.getText());
                String pass = RegisterForm.password.getText();
                Date date = RegisterForm.birthday.getDate();
                String pickrole = RegisterForm.role.getSelectedString();

                String sexe;
                if (RegisterForm.sexe.isValue() == false) {
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
                //System.out.println(user);

                ConnectionRequest connectionRequest = null;

                connectionRequest = new ConnectionRequest() {
                    @Override

                    protected void readResponse(InputStream input) throws IOException {
                        System.out.println(input);

                    }

                    @Override
                    protected void postResponse() {
                        getUserIdByMail(mail);
                        ToastBar.showMessage("Save pressed...", FontImage.MATERIAL_INFO);
                        KitchenSink ks = new KitchenSink();
                        ks.showMainUI();

                    }

                };

                connectionRequest.setUrl("http://localhost/api-cool/web/app_dev.php/user/new?firstname=" + nom + "&lastname=" + prenom + "&mail=" + mail + "&phone=" + tel + "&password=" + MD5.hash(pass) + "&sexe=" + sexe + "&role=" + role + "&birthdate=" + RegisterForm.birthday.getDate());
                NetworkManager.getInstance().addToQueue(connectionRequest);
            }
        }
    }

    public void updateUser() {

        boolean namevalvide = Validation.isEmpty(UpdateProfileForm.nameup.getText());
        boolean prenonvalvide = Validation.isEmpty(UpdateProfileForm.lastnameup.getText());
        boolean mailvalvide = Validation.isEmpty(UpdateProfileForm.emailup.getText());
        boolean telvalvide = Validation.isEmpty(UpdateProfileForm.phonenumberup.getText());

        Date dateb = UpdateProfileForm.birthdayup.getDate();
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

            String nom = UpdateProfileForm.nameup.getText();
            String prenom = UpdateProfileForm.lastnameup.getText();
            String mail = UpdateProfileForm.emailup.getText();
            int tel = Integer.parseInt(UpdateProfileForm.phonenumberup.getText());
            Date date = UpdateProfileForm.birthdayup.getDate();

            user.setEmail(mail);
            user.setNom(nom);
            user.setPrenom(prenom);
            user.setTel(tel);
            user.setDate_naissance(date);
            //  System.out.println(user);

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

            connectionRequest.setUrl("http://localhost/api-cool/web/app_dev.php/user/edit?id=" + user.id + "&firstname=" + nom + "&lastname=" + prenom + "&mail=" + mail + "&phone=" + tel + "&birthdate=" + UpdateProfileForm.birthdayup.getDate());
            NetworkManager.getInstance().addToQueue(connectionRequest);

        }
    }

    public void updateUserMdp() {

        boolean namevalvide = Validation.isEmpty(UpdatepassForm.nameup.getText());
        boolean prenonvalvide = Validation.isEmpty(UpdatepassForm.lastnameup.getText());
        boolean mailvalvide = Validation.isEmpty(UpdatepassForm.emailup.getText());

        boolean tempval = true;

        if (namevalvide) {
            Dialog.show("error", "please insert your OLD password ", "cancel", "ok");
            tempval = false;
        } else if (prenonvalvide) {
            Dialog.show("error", "please insert your new password ", "cancel", "ok");
            tempval = false;

        } else if (mailvalvide) {
            Dialog.show("error", "please confirm your new password ", "cancel", "ok");
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
            System.out.println("From update mdp");
            //  System.out.println(user);

            if (!user.mdp.equals(UpdatepassForm.nameup.getText())) {
                System.out.println(user.mdp);
                System.out.println(UpdatepassForm.nameup);
                Dialog.show("error", "OLD PASS WRONG !!", "cancel", "ok");
            } else if (user.mdp.equals(UpdatepassForm.lastnameup.getText())) {
                Dialog.show("error", "DO Not use  OLD ONE !!", "cancel", "ok");

            } else if (!UpdatepassForm.lastnameup.getText().equals(UpdatepassForm.emailup.getText())) {
                Dialog.show("error", "Not confirmed !!", "cancel", "ok");

            } else {

                String mdp = UpdatepassForm.lastnameup.getText();

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

    public void updateUserMdpSms() {

        boolean namevalvide = Validation.isEmpty(UpdatepassSmsForm.nameup.getText());
        boolean prenonvalvide = Validation.isEmpty(UpdatepassSmsForm.lastnameup.getText());
        boolean mailvalvide = Validation.isEmpty(UpdatepassSmsForm.emailup.getText());

        boolean tempval = true;

        if (namevalvide) {
            Dialog.show("error", "please insert The Sended key ", "cancel", "ok");
            tempval = false;
        } else if (prenonvalvide) {
            Dialog.show("error", "please insert your new password ", "cancel", "ok");
            tempval = false;

        } else if (mailvalvide) {
            Dialog.show("error", "please confirm your new password ", "cancel", "ok");
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
            //System.out.println("From update mdp");
            //  System.out.println(user);

            if (!UserDAO.KeySms.equals(UpdatepassSmsForm.nameup.getText())) {
                //System.out.println(user.mdp);
                System.out.println(UpdatepassSmsForm.nameup);
                Dialog.show("error", "Wrong key !!", "cancel", "ok");
            } else if (!UpdatepassSmsForm.lastnameup.getText().equals(UpdatepassSmsForm.emailup.getText())) {
                Dialog.show("error", "Not confirmed !!", "cancel", "ok");

            } else {
                System.out.println(UserDAO.KeySms);
                System.out.println(UpdatepassSmsForm.nameup);
                
                String mdp = UpdatepassSmsForm.lastnameup.getText();

                user.setMdp(mdp);

                //System.out.println(user);
                ConnectionRequest connectionRequest = null;

                connectionRequest = new ConnectionRequest() {
                    @Override

                    protected void readResponse(InputStream input) throws IOException {
                        System.out.println(input);

                    }

                    @Override
                    protected void postResponse() {
                        ToastBar.showMessage("Password changed ", FontImage.MATERIAL_INFO);
                        Demo d = new LoginForm();
                        d.init(res);
                        Form f = new Form("LOGIN", new BorderLayout());
                        f.add(BorderLayout.CENTER, d.createDemo(f));
                        f.show();

                    }

                };

                connectionRequest.setUrl("http://localhost/api-cool/web/app_dev.php/user/editpass?id=" + user.id + "&mdp=" + MD5.hash(mdp));
                NetworkManager.getInstance().addToQueue(connectionRequest);
            }
        }
    }

    public void DisableAccount() {

        ConnectionRequest connectionRequest = null;

        connectionRequest = new ConnectionRequest() {
            @Override

            protected void readResponse(InputStream input) throws IOException {
                System.out.println(input);

            }

            @Override
            protected void postResponse() {
                ToastBar.showMessage("Account Disabled See You", FontImage.MATERIAL_INFO);
                Demo d = new LoginForm();
                d.init(res);
                Form f = new Form("LOGIN", new BorderLayout());
                f.add(BorderLayout.CENTER, d.createDemo(f));
                f.show();
            }

        };

        connectionRequest.setUrl("http://localhost/api-cool/web/app_dev.php/user/disableAcc?id=" + user.id);
        NetworkManager.getInstance().addToQueue(connectionRequest);

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

                    // System.out.println();
                    List<Map<String, Object>> list = (List<Map<String, Object>>) etudiants.get("root");

                    users.clear();

                    System.out.println(user.email);
                    for (Map<String, Object> obj : list) {
                        String mailfr = ((String) obj.get("email"));
                        if (!mailfr.equals(user.email)) {
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

                    }
                    // System.out.println(users);
                } catch (IOException ex) {
                }

            }

        });

        NetworkManager.getInstance().addToQueue(con);

    }

    int idusbymail;

    public void getUserIdByMail(String mail) {

        ConnectionRequest con = new ConnectionRequest();
        con.setUrl("http://localhost/api-cool/web/app_dev.php/user/find/" + mail);

        con.addResponseListener(new ActionListener<NetworkEvent>() {

            @Override
            public void actionPerformed(NetworkEvent evt) {
                String json = new String(con.getResponseData());

                try {

                    JSONParser j = new JSONParser();

                    Map<String, Object> task = j.parseJSON(new CharArrayReader(json.toCharArray()));

                    //idusbymail = (int) Float.parseFloat(task.get("id").toString());
                    user.setId((int) Float.parseFloat(task.get("id").toString()));
                    //System.out.println(idusbymail);
                    System.out.println(user);
                } catch (IOException ex) {
                }

            }

        });

        NetworkManager.getInstance().addToQueue(con);

    }

}

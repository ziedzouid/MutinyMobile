/*
 * Copyright (c) 2012, Codename One and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Codename One designates this
 * particular file as subject to the "Classpath" exception as provided
 * in the LICENSE file that accompanied this code.
 *  
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Codename One through http://www.codenameone.com/ if you 
 * need additional information or have any questions.
 */
package com.codename1.demos.kitchen;

import com.codename1.capture.Capture;
import com.codename1.components.FloatingHint;
import com.codename1.components.OnOffSwitch;
import com.codename1.components.ToastBar;
import static com.codename1.demos.kitchen.RegisterForm.sexe;
import static com.codename1.demos.kitchen.KitchenSink.res;
import com.codename1.io.Log;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.Button;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.table.TableLayout;
import java.io.IOException;

/**
 * Demonstrates basic usage of input facilities, device orientation behavior as
 * well as adapting the UI to tablets. This demo shows off a typical form with
 * user information, different keyboard types, ability to capture an avatar
 * image and style it etc.
 *
 * @author Shai Almog
 */
public class ProfileForm extends Demo {

    public String getDisplayName() {
        return "My Account";
    }

    public Image getDemoIcon() {
        return getResources().getImage("input.png");
    }

    @Override
    public String getDescription() {
        return "Demonstrates basic usage of input facilities, device orientation behavior as well as adapting the UI to tablets."
                + "This demo shows off a typical form with user information, different keyboard types, ability to capture an "
                + "avatar image and style it etc.";
    }

    @Override
    public String getSourceCodeURL() {
        return "https://github.com/codenameone/KitchenSink/blob/master/src/com/codename1/demos/kitchen/Input.java";
    }

    private void addComps(Form parent, Container cnt, Component... cmps) {
        if (Display.getInstance().isTablet() || !Display.getInstance().isPortrait()) {
            TableLayout tl = new TableLayout(cmps.length / 2, 2);
            cnt.setLayout(tl);
            tl.setGrowHorizontally(true);
            for (Component c : cmps) {
                if (c instanceof Container) {
                    cnt.add(tl.createConstraint().horizontalSpan(2), c);
                } else {
                    cnt.add(c);
                }
            }
        } else {
            cnt.setLayout(BoxLayout.y());
            for (Component c : cmps) {
                cnt.add(c);
            }
        }
        if (cnt.getClientProperty("bound") == null) {
            cnt.putClientProperty("bound", "true");
            if (!Display.getInstance().isTablet()) {
                parent.addOrientationListener((e) -> {
                    Display.getInstance().callSerially(() -> {
                        cnt.removeAll();
                        addComps(parent, cnt, cmps);
                        cnt.animateLayout(800);
                    });
                });
            }
        }
    }

    @Override
    public Container createDemo(Form parent) {

        Label firstname = new Label("Name");
        FontImage.setMaterialIcon(firstname, FontImage.MATERIAL_PERSON);
        Label lastname = new Label("LastName");
        FontImage.setMaterialIcon(lastname, FontImage.MATERIAL_PERSON);
        Label birthdate = new Label("Birth Date");
        FontImage.setMaterialIcon(birthdate, FontImage.MATERIAL_DATE_RANGE);
        Label sexe = new Label("Sexe");
        FontImage.setMaterialIcon(sexe, FontImage.MATERIAL_PEOPLE);
        Label mail = new Label("Email");
        FontImage.setMaterialIcon(mail, FontImage.MATERIAL_EMAIL);
        Label phone = new Label("Phone Number");
        FontImage.setMaterialIcon(phone, FontImage.MATERIAL_SMARTPHONE);
        Label accountdis = new Label("Disable Account");
        FontImage.setMaterialIcon(accountdis, FontImage.MATERIAL_CLOSE);

        Container comps = new Container();
        String tel = Integer.toString(UserDAO.user.tel);
        String date = new SimpleDateFormat("MM/dd/yyyy").format(UserDAO.user.date_naissance);
        OnOffSwitch accountval = new OnOffSwitch();
        accountval.setName("Disable");
        /* Container swt = new Container(new BoxLayout(BoxLayout.X_AXIS));
        swt.add(new Label("MAN", "InputContainerLabel"));
        swt.add(sexe);
        swt.add(new Label("WOMAN", "InputContainerLabel"));*/
        addComps(parent, comps,
                BorderLayout.center(firstname).
                        add(BorderLayout.EAST, new Label(UserDAO.user.prenom, "InputContainerLabel")),
                BorderLayout.center(lastname).
                        add(BorderLayout.EAST, new Label(UserDAO.user.nom, "InputContainerLabel")),
                BorderLayout.center(birthdate).
                        add(BorderLayout.EAST, new Label(date, "InputContainerLabel")),
                BorderLayout.center(sexe).
                        add(BorderLayout.EAST, new Label(UserDAO.user.sexe, "InputContainerLabel")),
                BorderLayout.center(mail).
                        add(BorderLayout.EAST, new Label(UserDAO.user.email, "InputContainerLabel")),
                BorderLayout.center(phone).
                        add(BorderLayout.EAST, new Label(tel, "InputContainerLabel")),
                BorderLayout.center(accountdis).
                        add(BorderLayout.EAST, accountval)
        );

        accountval.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (accountval.isValue() == true) {
                    boolean conf = Dialog.show("Alerte !", "ARE YOU SURE TO DISABLE YUR ACCOUNT", "NO", "YES");
                    if (conf == false) {
                        UserDAO userDAO = new UserDAO();
                        userDAO.DisableAccount();                               
                        accountval.setValue(false);

                    } else {

                        accountval.setValue(false);

                    }
                }
            }
        });

        comps.setScrollableY(true);
        comps.setUIID("PaddedContainer");

        Container content = BorderLayout.center(comps);

        Button login = new Button("Modify profile");
        Button singup = new Button("Change Password");
        //login.setUIID("InputAvatarImage");
        Container ctnbt = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        ctnbt.add(login);
        ctnbt.add(singup);

        content.add(BorderLayout.SOUTH, ctnbt);

        // UserDAO userDAO = new UserDAO();
        login.addActionListener(e -> {
            //ToastBar.showMessage("Save pressed...", FontImage.MATERIAL_INFO);
            //userDAO.RegisterUser();
            Demo d = new UpdateProfileForm();
            d.init(res);
            Form f = new Form("UPDATE", new BorderLayout());
            f.add(BorderLayout.CENTER, d.createDemo(f));
            Form previous = Display.getInstance().getCurrent();
            f.getToolbar().setBackCommand(" ", ee -> {
                if (d.onBack()) {
                    previous.showBack();
                }
            });;

            f.show();

        });

        singup.addActionListener(e -> {
            //ToastBar.showMessage("Save pressed...", FontImage.MATERIAL_INFO);
            //userDAO.RegisterUser();
            Demo d = new UpdatepassForm();
            d.init(res);
            Form f = new Form("UPDATE PASS", new BorderLayout());
            f.add(BorderLayout.CENTER, d.createDemo(f));
            Form previous = Display.getInstance().getCurrent();
            f.getToolbar().setBackCommand(" ", ee -> {
                if (d.onBack()) {
                    previous.showBack();
                }
            });;

            f.show();

        });

        content.setUIID("InputContainerForeground");

        Button avatar = new Button("");
        avatar.setUIID("InputAvatar");
        Image defaultAvatar = FontImage.createMaterial(FontImage.MATERIAL_CAMERA, "InputAvatarImage", 8);
        Image circleMaskImage = getResources().getImage("circle.png");
        defaultAvatar = defaultAvatar.scaled(circleMaskImage.getWidth(), circleMaskImage.getHeight());
        defaultAvatar = ((FontImage) defaultAvatar).toEncodedImage();
        Object circleMask = circleMaskImage.createMask();
        defaultAvatar = defaultAvatar.applyMask(circleMask);
        avatar.setIcon(defaultAvatar);

        avatar.addActionListener(e -> {
            if (Dialog.show("Camera or Gallery", "Would you like to use the camera or the gallery for the picture?", "Camera", "Gallery")) {
                String pic = Capture.capturePhoto();
                if (pic != null) {
                    try {
                        Image img = Image.createImage(pic).fill(circleMaskImage.getWidth(), circleMaskImage.getHeight());
                        System.out.println(pic);
                        avatar.setIcon(img.applyMask(circleMask));
                    } catch (IOException err) {
                        ToastBar.showErrorMessage("An error occured while loading the image: " + err);
                        Log.e(err);
                    }
                }
            } else {
                Display.getInstance().openGallery(ee -> {
                    if (ee.getSource() != null) {
                        try {
                            Image img = Image.createImage((String) ee.getSource()).fill(circleMaskImage.getWidth(), circleMaskImage.getHeight());
                            System.out.println(ee.getSource());
                            avatar.setIcon(img.applyMask(circleMask));
                        } catch (IOException err) {
                            ToastBar.showErrorMessage("An error occured while loading the image: " + err);
                            Log.e(err);
                        }
                    }
                }, Display.GALLERY_IMAGE);
            }
        });

        Container actualContent = LayeredLayout.encloseIn(content,
                FlowLayout.encloseCenter(avatar));

        Container input;
        if (!Display.getInstance().isTablet()) {
            Label placeholder = new Label(" ");

            Component.setSameHeight(actualContent, placeholder);
            Component.setSameWidth(actualContent, placeholder);

            input = BorderLayout.center(placeholder);

            parent.addShowListener(e -> {
                if (placeholder.getParent() != null) {
                    input.replace(placeholder, actualContent, CommonTransitions.createFade(1500));
                }
            });
        } else {
            input = BorderLayout.center(actualContent);
        }
        input.setUIID("InputContainerBackground");

        return input;
    }

}

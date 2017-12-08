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
import static com.codename1.demos.kitchen.KitchenSink.res;
import com.codename1.io.Log;
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
public class Input2 extends Demo {

    static TextField emaillog, passwordlog;

    public String getDisplayName() {
        return "Input2";
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
        emaillog = new TextField("", "E-mail", 20, TextField.EMAILADDR);
        FontImage.setMaterialIcon(emaillog.getHintLabel(), FontImage.MATERIAL_EMAIL);
        passwordlog = new TextField("", "Password", 20, TextField.PASSWORD);
        FontImage.setMaterialIcon(passwordlog.getHintLabel(), FontImage.MATERIAL_LOCK);

        Container comps = new Container();
        addComps(parent, comps,
                new Label("E-Mail", "InputContainerLabel"),
                emaillog,
                new Label("Password", "InputContainerLabel"),
                passwordlog);

        comps.setScrollableY(true);
        comps.setUIID("PaddedContainer");

        Container content = BorderLayout.center(comps);

        Button login = new Button("login");
        Button singup = new Button("SignUp");
        login.setUIID("InputAvatarImage");
        Container ctnbt = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        ctnbt.add(login);
        ctnbt.add(singup);

        content.add(BorderLayout.SOUTH, ctnbt);

        UserDAO userDAO = new UserDAO();
        login.addActionListener(e -> {
            ToastBar.showMessage("Connecting...", FontImage.MATERIAL_ACCOUNT_BALANCE_WALLET);
            //userDAO.RegisterUser();
            userDAO.loginUser();
        });

        singup.addActionListener(e -> {
            Demo d = new Input();
            d.init(res);
            Form f = new Form("REGISTER", new BorderLayout());
            f.add(BorderLayout.CENTER, d.createDemo(f));
            Form previous = Display.getInstance().getCurrent();
            f.getToolbar().setBackCommand(" ", ee -> {
                if (d.onBack()) {
                    previous.showBack();
                }
            });
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
                            // Image img = Image.createImage("/temp6510064849086438710.jpg").fill(circleMaskImage.getWidth(), circleMaskImage.getHeight());
                            System.out.println((String) ee.getSource());
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

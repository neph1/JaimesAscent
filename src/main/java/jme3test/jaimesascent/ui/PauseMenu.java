/*
 * Copyright (c) 2024 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jme3test.jaimesascent.ui;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.style.ElementId;
import e.g.jme3hudl.ControlLayout;

/**
 * @author wil
 */
public class PauseMenu extends Window {
    
    public static final int OPTION_RESUME = 2;
    public static final int OPTION_EXIT   = 3;

    public PauseMenu(ControlLayout.RootPane pane, Application application) {
        super(pane, application);
        initComponents();
    }
    
    private void initComponents() {
        ControlLayout layout = new ControlLayout(pane);
        
        Container leftPanel = new Container(layout);        
        leftPanel.setBackground(new UIImage("Interface/UI/panel_01.png"));
        leftPanel.setPreferredSize(new Vector3f(500, 700, 0));
        getRootPane().addChild(leftPanel, ControlLayout.Alignment.LeftCenter, false);

        Label title = new Label("Jaimes Ascent");
        title.setPreferredSize(new Vector3f(leftPanel.getPreferredSize().x - 25, 50, 0));
        title.setFont(GuiGlobals.getInstance().loadFont("/Interface/Fonts/OrbitronBlack.fnt"));
        title.setColor(ColorRGBA.White.clone());
        title.setTextHAlignment(HAlignment.Left);
        title.setTextVAlignment(VAlignment.Center);
        
        leftPanel.addChild(title, ControlLayout.Alignment.LeftTop, false);
        layout.setAttribute(ControlLayout.POSITION, title, new Vector3f(25, 80, 1));
        layout.setAttribute(ControlLayout.FONT_SIZE, title, 35.0f);
        
        Label nameMenu = new Label("Pause Menu");
        nameMenu.setPreferredSize(new Vector3f(leftPanel.getPreferredSize().x - 25, 50, 0));
        nameMenu.setFont(GuiGlobals.getInstance().loadFont("/Interface/Fonts/OrbitronBlack.fnt"));
        nameMenu.setColor(new ColorRGBA(0.412f, 0.424f, 0.463f, 1.0f));
        nameMenu.setTextHAlignment(HAlignment.Left);
        nameMenu.setTextVAlignment(VAlignment.Center);
        
        leftPanel.addChild(nameMenu, ControlLayout.Alignment.LeftTop, false);
        layout.setAttribute(ControlLayout.POSITION, nameMenu, new Vector3f(25, 115, 1));
        layout.setAttribute(ControlLayout.FONT_SIZE, nameMenu, 25.0f);
        
        Button buttonResume = new Button("Resume", new ElementId("MyButton"));
        buttonResume.setBackground(new UIImage("Interface/UI/button_01.png"));
        buttonResume.setPreferredSize(new Vector3f(300, 45, 0));
        buttonResume.setFont(GuiGlobals.getInstance().loadFont("/Interface/Fonts/OrbitronSemiBold.fnt"));
        buttonResume.setColor(new ColorRGBA(0.522f, 0.537f, 0.584f, 1.0f));
        buttonResume.setTextHAlignment(HAlignment.Center);
        buttonResume.setTextVAlignment(VAlignment.Center);
        buttonResume.addClickCommands((source) -> {
            setVisible(false);
        });
        
        leftPanel.addChild(buttonResume, ControlLayout.Alignment.LeftCenter, false);
        layout.setAttribute(ControlLayout.POSITION, buttonResume, new Vector3f(50, 30, 1));
        layout.setAttribute(ControlLayout.FONT_SIZE, buttonResume, 20.0f);
        
        Button button2 = new Button("Exit", new ElementId("MyButton"));
        button2.setBackground(new UIImage("Interface/UI/button_01.png"));
        button2.setPreferredSize(new Vector3f(300, 45, 0));
        button2.setFont(GuiGlobals.getInstance().loadFont("/Interface/Fonts/OrbitronSemiBold.fnt"));
        button2.setColor(new ColorRGBA(0.522f, 0.537f, 0.584f, 1.0f));
        button2.setTextHAlignment(HAlignment.Center);
        button2.setTextVAlignment(VAlignment.Center);
        button2.addClickCommands((source) -> {
            application.stop();
            System.exit(0);
        });
        
        leftPanel.addChild(button2, ControlLayout.Alignment.LeftCenter, false);
        layout.setAttribute(ControlLayout.POSITION, button2, new Vector3f(50, -30, 1));
        layout.setAttribute(ControlLayout.FONT_SIZE, button2, 20.0f);        
        setAlpha(0);
    }
}

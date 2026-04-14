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
package jme3test.jaimesascent.screen;

import com.jme3.math.Vector3f;
import com.simsilica.lemur.Button;
import jme3test.jaimesascent.ui.MyLayout;
import jme3test.jaimesascent.ui.PauseMenu;
import jme3test.jaimesascent.ui.UIImage;
import jme3test.jaimesascent.ui.Window;

/**
 * @author wil
 */
public class GameGUIScreen extends AbstractScreen {

    private Window window;
    
    @Override
    protected void init() {
        MyLayout layout = (MyLayout) rootContainer.getLayout();
                
        window = new PauseMenu(layout.getRootPane(), getApplication());
        window.setPreferredSize(layout.getRootPane().getResolution().clone());
        rootContainer.addChild(window, MyLayout.Alignment.Center, false);        
        getApplication().getInputManager()
                        .setMouseCursor(createJmeCursorFromClassPath("Interface/UI/cursor_g.png", getApplication().getAssetManager()));
        
        Button menu = new Button("");
        menu.setBackground(new UIImage("Interface/UI/button_03.png"));
        menu.setPreferredSize(new Vector3f(50, 50, 0));
        menu.addClickCommands((source) -> {
            window.setVisible(true);
        });
        rootContainer.addChild(menu, MyLayout.Alignment.RightTop, true);
        layout.setAttribute(MyLayout.POSITION, menu, new Vector3f(20, 20, 0));
    }

    public Window getWindow() {
        return window;
    }
}

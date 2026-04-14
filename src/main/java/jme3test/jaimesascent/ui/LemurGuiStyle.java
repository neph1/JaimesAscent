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
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.Attributes;
import com.simsilica.lemur.style.Styles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class in charge of initializing a theme for the components to be used (Lemur)
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
public final class LemurGuiStyle {
    
    public static final String MY_STYLE = "JaimesStyle";
    
    public static void initialize(Application app) {
        GuiGlobals.initialize(app);
        GuiGlobals.getInstance()
                  .getStyles()
                  .setDefaultStyle(MY_STYLE);
        
        Styles styles = GuiGlobals.getInstance()
                                  .getStyles();
        Attributes attrs;
        attrs = styles.getSelector("MyButton", MY_STYLE);
        
        final Map<Button.ButtonAction, List<Command<? super Button>>> defAnimButton = new HashMap<>();
        defAnimButton.put(Button.ButtonAction.Up, new ArrayList<>() {{
            add((source) -> {                
                if (!source.isPressed()) {
                    source.setLocalScale(1.0f);
                }
            });
        }});        
        defAnimButton.put(Button.ButtonAction.Down, new ArrayList<>() {{
            add((source) -> {                
                if (source.isPressed()) {                    
                    source.setLocalScale(0.95f);
                }
            });
        }});        
        attrs.set("buttonCommands", defAnimButton);
    }
}

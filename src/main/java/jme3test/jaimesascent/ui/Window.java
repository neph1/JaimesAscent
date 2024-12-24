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
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.anim.Animation;
import com.simsilica.lemur.anim.PanelTweens;
import com.simsilica.lemur.anim.TweenAnimation;
import com.simsilica.lemur.anim.Tweens;
import com.simsilica.lemur.core.AbstractGuiControlListener;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.effect.AbstractEffect;
import com.simsilica.lemur.effect.Effect;
import com.simsilica.lemur.effect.EffectInfo;
import e.g.jme3hudl.ControlLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wil
 */
public class Window extends Panel {

    public static final int OPTION_SHOW = 0;
    public static final int OPTION_HIDE  = 1;
    
    private static class Fader extends Panel {
        public Fader() {
            initComponents();
        }        
        private void initComponents() {
            setBackground(new UIImage("Interface/UI/fill.png", new ColorRGBA(0, 0, 0, 1)));
            setAlpha(0.8f);
        }

        @Override
        public void setAlpha(float alpha, boolean recursive) {
            if (alpha <= 0.8f) {
                super.setAlpha(alpha, recursive);
            }
        }
    }
    
    protected class ResizeListener extends AbstractGuiControlListener {
        @Override
        public void reshape( GuiControl source, Vector3f pos, Vector3f size ) {
            resetStateView();
        }
    }
    
    private Fader fader;
    private boolean visible;
    private final List<WindowListener<Integer>> windosListeners;
    
    protected Container rootPane;
    protected ControlLayout.RootPane pane;
    protected Application application;
    
    public Window(ControlLayout.RootPane pane, Application application) {
        this.windosListeners = new ArrayList<>();
        this.application = application;
        this.pane = pane;
        initComponents();
    }
    
    protected final void fireWindosListener(int op) {
        for (final WindowListener<Integer> wl : this.windosListeners) {
            if (wl == null) {
                continue;
            }
            wl.doAction(op);
        }
    }
    
    public void addWindosListener(WindowListener<Integer> wl) {
        this.windosListeners.add(wl);
    }
    
    public void removeWindosListener(WindowListener<Integer> wl) {
        this.windosListeners.remove(wl);
    }
    
    protected void resetStateView() {
        Vector3f myPref = getPreferredSize();
        fader.setPreferredSize(myPref.clone());
        rootPane.setPreferredSize(myPref.clone());
    }
    
    private void initComponents() {
        setBackground(null);
        
        ControlLayout layout = new ControlLayout(pane);
        GuiControl control = getControl(GuiControl.class);
        
        fader = new Fader();
        rootPane = new Container(new ControlLayout(pane));
        
        control.addListener(new ResizeListener());
        control.setLayout(layout);
        
        layout.addChild(fader, ControlLayout.Alignment.Center, false);
        layout.addChild(rootPane, ControlLayout.Alignment.Center, false);        
        layout.setAttribute(ControlLayout.POSITION, rootPane, new Vector3f(0, 0, 1));
        
        // === ------------------------------------------------------------- ===
        Effect<Panel> show = new AbstractEffect<Panel>("open/close") {
            @Override
            public Animation create(Panel t, EffectInfo ei) {                
                return new TweenAnimation(Tweens.callMethod(t, "fireWindosListener", OPTION_SHOW), 
                                            Tweens.sequence(PanelTweens.fade(t, 0f, 1f, 1)));
            }};
        Effect<Panel> close = new AbstractEffect<Panel>("open/close") {
            @Override
            public Animation create(Panel t, EffectInfo ei) {                
                return new TweenAnimation(Tweens.sequence(PanelTweens.fade(t, 1f, 0f, 1), 
                                            Tweens.callMethod(t, "fireWindosListener", OPTION_HIDE)));
            }};
        addEffect("show", show);
        addEffect("close", close);
    }
    
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }
        runEffect(visible ? "show" : "close");
        this.visible = visible;
        this.application.getInputManager().setCursorVisible(visible);
    }

    public Container getRootPane() {
        return rootPane;
    }
}

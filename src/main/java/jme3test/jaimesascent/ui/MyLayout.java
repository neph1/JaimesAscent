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

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.AbstractGuiComponent;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.core.GuiLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author wil
 */
public class MyLayout extends AbstractGuiComponent implements GuiLayout {
    
    private static final Logger LOG = Logger.getLogger(MyLayout.class.getName());
    
    public static final String POSITION  = "Position";
    public static final String ALIGNMENT = "Alignment";
    public static final String FONT_SIZE = "FontSize";
    public static final String LOCK_SCALING   = "Lockscaling";
    public static final String DEPTH_POSITION = "DepthPosition";
    
    public static enum Alignment {
        Center,
        CenterTop,
        CenterBottom,
        RightCenter,
        RightTop,
        RightBottom,
        LeftCenter,
        LeftTop,
        LeftBottom;
    }
    
    static class Attributes {
        
        public Boolean lockscaling;
        public Vector3f originalPos;
        
        public Alignment alignment;
        public float fontsize;
        
        public Attributes(Object... constraints) {
            for (final Object element : constraints) {
                if (element == null)
                    continue;

                if ((element instanceof Boolean) 
                        && (this.lockscaling == null)) {
                    this.lockscaling = (Boolean) element;
                } else if ((element instanceof Alignment) 
                                && (this.alignment == null)) {
                    this.alignment = (Alignment) element;
                }

                if (this.alignment != null 
                        && this.lockscaling != null)
                    break;
            }

            if (this.lockscaling == null)
                this.lockscaling = Boolean.FALSE;

            if (this.alignment == null)
                this.alignment = Alignment.Center;

            this.originalPos = new Vector3f();
        }
        
        void setLockscaling(Boolean lockscaling) {
            this.lockscaling = lockscaling;
        }
        void setOriginalPos(Vector3f originalPos) {
            this.originalPos = originalPos;
        }
        void setAlignment(Alignment alignment) {
            this.alignment = alignment;
        }
        void setFontsize(float fontsize) {
            this.fontsize = fontsize;
        }
    }
    
    final class Control {
        
        private final Attributes attributes;
        private final GuiControl gc;
        
        public Control(GuiControl gc, Object... constraints) {
            this.attributes = new Attributes(constraints);
            this.gc = gc;
        }
        
        void setFontSize() {
            if ((gc.getNode()) instanceof Label) {
                ((Label) gc.getNode()).setFontSize(attributes.fontsize * rootPane.getScaleFactor().y);
            } else {
                LOG.log(Level.WARNING, "GuiControl({0}) :It is not a label control.", gc.getClass());
            }
        }
        
        void resize() {
            Vector3f mySize = new Vector3f();
            Vector3f prefSize = gc.getPreferredSize();
            
            final Vector3f fac = rootPane.getScaleFactor();
            mySize.x = attributes.lockscaling ? prefSize.x * fac.y : prefSize.x * fac.x;
            mySize.y = prefSize.y * fac.y;
            mySize.z = prefSize.z * fac.z;
            
            gc.setSize(mySize);

            final Node nodeControl    = gc.getNode();
            final Vector3f parentSize = getParentSize();

            nodeControl.setLocalTranslation(parentSize.x / 2.0F, -parentSize.y / 2.0F, parentSize.z / 2.0F);
            nodeControl.move(-mySize.x * 0.5F, mySize.y * 0.5F, -mySize.z * 0.5F);
            
            nodeControl.move(calculatePosition());
        }
        
        Vector3f calculatePosition() {
            float width  = gc.getSize().x,
                  height = gc.getSize().y;

            final Vector3f myPos = attributes.originalPos;
            final Vector3f fac = rootPane.getScaleFactor();
            
            float offsetX = myPos.getX();
            float offsetY = myPos.getY();

            float xPos, yPos, zPos = myPos.z * fac.z;
            switch (attributes.alignment) {
                case Center -> {
                    if ( hasParentAndLockscaling() ) {
                        xPos = (offsetX * fac.y);
                        yPos = (offsetY * fac.y);
                    } else {
                        xPos = (offsetX * fac.x);
                        yPos = (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                }
                case CenterBottom -> {
                    if ( hasParentAndLockscaling() ) {
                        xPos = offsetX * fac.y;
                        yPos = -(getParentSize().y * 0.5f) + (height * 0.5f) + (offsetY * fac.y);
                    } else {
                        xPos = offsetX * fac.x;
                        yPos = -(getParentSize().y * 0.5f) + (height * 0.5f) + (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                }
                case CenterTop -> {
                    if ( hasParentAndLockscaling() ) {
                        xPos = offsetX * fac.y;
                        yPos = (getParentSize().y * 0.5f) - (height * 0.5f) - (offsetY * fac.y);
                    } else {
                        xPos = offsetX * fac.x;
                        yPos = (getParentSize().y * 0.5f) - (height * 0.5f) - (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                }
                case LeftBottom -> {
                    if ( hasParentAndLockscaling() ) {
                        xPos = -(getParentSize().x * 0.5f) + (width * 0.5f) + offsetX * fac.y;
                        yPos = -(getParentSize().y * 0.5f) + (height * 0.5f) + (offsetY * fac.y);
                    } else {
                        xPos = -(getParentSize().x * 0.5f) + (width * 0.5f) + offsetX * fac.x;
                        yPos = -(getParentSize().y * 0.5f) + (height * 0.5f) + (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                }
                case LeftCenter -> {
                    if ( hasParentAndLockscaling() ) {
                        xPos = -(getParentSize().x * 0.5f) + (width * 0.5f) + offsetX * fac.y;
                        yPos = (offsetY * fac.y);
                    } else {
                        xPos = -(getParentSize().x * 0.5f) + (width * 0.5f) + offsetX * fac.x;
                        yPos = (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                }
                case LeftTop -> {
                    if ( hasParentAndLockscaling() ) {
                        xPos = -(getParentSize().x * 0.5f) + (width * 0.5f) + offsetX * fac.y;
                        yPos = (getParentSize().y * 0.5f) - (height * 0.5f) - (offsetY * fac.y);
                    } else {
                        xPos = -(getParentSize().x * 0.5f) + (width * 0.5f) + offsetX * fac.x;
                        yPos = (getParentSize().y * 0.5f) - (height * 0.5f) - (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                }
                case RightBottom -> {
                    if ( hasParentAndLockscaling() ) {
                        xPos = (getParentSize().x * 0.5f) - (width * 0.5f) - (offsetX * fac.y);
                        yPos = -(getParentSize().y * 0.5f) + (height * 0.5f) + (offsetY * fac.y);
                    } else {
                        xPos = (getParentSize().x * 0.5f) - (width * 0.5f) - (offsetX * fac.x);
                        yPos = -(getParentSize().y * 0.5f) + (height * 0.5f) + (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                }
                case RightCenter -> {
                    if ( hasParentAndLockscaling() ) {
                        xPos = (getParentSize().x * 0.5f) - (width * 0.5f) - (offsetX * fac.y);
                        yPos = (offsetY * fac.y);
                    } else {
                        xPos = (getParentSize().x * 0.5f) - (width * 0.5f) - (offsetX * fac.x);
                        yPos = (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                }
                case RightTop -> {
                    if ( hasParentAndLockscaling() ) {
                        xPos = (getParentSize().x * 0.5f) - (width * 0.5f) - (offsetX * fac.y);
                        yPos = (getParentSize().y * 0.5f) - (height * 0.5f) - (offsetY * fac.y);
                    } else {
                        xPos = (getParentSize().x * 0.5f) - (width * 0.5f) - (offsetX * fac.x);
                        yPos = (getParentSize().y* 0.5f) - (height * 0.5f) - (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                }
                default -> throw new AssertionError();
            }
        }
        
        private Vector3f getParentSize() {
            return getGuiControl().getSize();
        }
    }
    
    public static class RootPane {    
        public Vector3f window;
        public Vector3f resolution;
        public RootPane() {
        }
        
        public Vector3f getScaleFactor() {
            float rx = resolution.x == 0 ? 0 : window.x / resolution.x,
                  ry = resolution.y == 0 ? 0 : window.y / resolution.y,
                  rz = resolution.z == 0 ? 0 : resolution.z / window.z;
            return new Vector3f(rx, ry, rz);
        }
        public Vector3f getWindow() { return window; }
        public Vector3f getResolution() { return resolution; }
    }
    
    public static RootPane onCreateRootPane(Vector3f window, Vector3f resolution) {
        final RootPane layer = new RootPane();
        layer.window = window;
        layer.resolution = resolution;
        return layer;
    }
    
    private static boolean useParentControlLayout(GuiControl control) {
        if ( control == null) { return false; }
        GuiLayout layout = control.getLayout();
        return (layout instanceof MyLayout);
    }
    
    private final Map<Node, Control> children = new HashMap<>();
        
    private final RootPane rootPane;    
    public MyLayout(RootPane rootPane) {
        this.rootPane = rootPane;
    }
    
    public RootPane getRootPane() {
        return rootPane;
    }
    
    @Override
    public void calculatePreferredSize(Vector3f size) {
        float w = 0,
              h = 0, 
              z = 0;

        for (final Map.Entry<Node, Control> entry : children.entrySet()) {
            Control c = entry.getValue();
            Vector3f d = c.gc.getPreferredSize();
            if (d.x > w) {
                w = d.x;
            }
            if (d.y > h) {
                h = d.z;
            }
            if (d.z > z) {
                z = d.z;
            }
        }
        size.set(w, h, z);
    }
    
    private boolean hasParentAndLockscaling() {
        final Node node = getNode().getParent();
        if ( node == null ) {
            return false;
        }
        
        if ( useParentControlLayout(node.getControl(GuiControl.class)) ) {
            MyLayout layout = node.getControl(GuiControl.class).getLayout();
            return layout.isLockscalingChild(this.getNode());
        }
        return false;
    }
    
    private boolean isLockscalingChild(Node target) {
        final Control cl = children.get(target);
        if ( cl != null ) {
            return cl.attributes.lockscaling;
        }
        return false;
    }
    
    @Override
    public void reshape(Vector3f pos, Vector3f size) {
        for (final Map.Entry<Node, Control> entry : children.entrySet()) {
            if ( entry.getValue() == null ) 
                continue;
            
            Control control = entry.getValue();
            control.resize();
        }
    }
    
    @SuppressWarnings(value = {"unchecked"})
    public <T extends Object> T getAttribute(String ac, Node tar) {
        if (ac == null || tar == null) {
            throw new IllegalArgumentException();
        }
        final Control cl = children.get(tar);
        if ( cl == null) {
            return null;
        }
        switch (ac) {
            case ALIGNMENT -> {
                return (T) cl.attributes.alignment;
            }
            case DEPTH_POSITION -> {
                return (T) Float.valueOf(cl.attributes.originalPos.z);
            }
            case FONT_SIZE -> {
                return (T) Float.valueOf(cl.attributes.fontsize);
            }
            case LOCK_SCALING -> {
                return (T) cl.attributes.lockscaling;
            }
            case POSITION -> {
                return (T) cl.attributes.originalPos;
            }
            default -> throw new AssertionError();
        }
    }
    
    @SuppressWarnings(value = {"unchecked"})
    public <T extends Object> boolean setAttribute(String ac, Node tar, T value) {
        if (ac == null || tar == null) {
            throw new IllegalArgumentException();
        }
        final Control cl = children.get(tar);
        if ( cl == null) {
            return false;
        }
        switch (ac) {
            case ALIGNMENT -> {
                if (value instanceof Alignment alignment) {
                    cl.attributes.setAlignment(alignment);
                    cl.gc.invalidate();
                    return true;
                }
                return true;
            }
            case DEPTH_POSITION -> {
                if (value instanceof Number number) {
                    cl.attributes.originalPos.setZ(number.floatValue());
                    cl.gc.invalidate();
                    return true;
                }
                return false;
            }
            case FONT_SIZE -> {
                if (value instanceof Number number) {
                    cl.attributes.setFontsize(number.floatValue());
                    cl.setFontSize();
                    return true;
                }
                return false;
            }
            case LOCK_SCALING -> {
                if (value instanceof Boolean aBoolean) {
                    cl.attributes.setLockscaling(aBoolean);
                    cl.gc.invalidate();
                    return true;
                }
                return false;
            }
            case POSITION -> {
                if (value instanceof Vector3f vector3f) {
                    cl.attributes.setOriginalPos(vector3f);
                    cl.gc.invalidate();
                    return true;
                }
                return false;
            }
            default -> throw new AssertionError();
        }
    }
    
    @Override
    public <T extends Node> T addChild(T n, Object... constraints) {
        if( n != null && n.getControl(GuiControl.class) == null ) {
            throw new IllegalArgumentException( "Child is not GUI element." );
        }
        
        if ( n == null ) { return null; }
        if ( children.containsKey(n) ) {
            removeChild(n);
        }
        
        children.put(n, new Control(n.getControl(GuiControl.class), constraints));
        if ( isAttached() ) {
            getNode().attachChild(n);
        }
        invalidate();
        return n;
    }
    
    @Override
    public void removeChild(Node n) {
        Control c = children.remove(n);
        if ( c != null ) {
            c.gc.getNode().removeFromParent();
            invalidate();
        }
    }
    
    @Override
    public Collection<Node> getChildren() {
        return Collections.unmodifiableSet(children.keySet());
    }
    
    @Override
    public void clearChildren() {
        for (final Node n : getChildren()) {
            n.removeFromParent();
        }
        children.clear();
        invalidate();
    }
    
    @Override
    public void detach(GuiControl parent) {
        super.detach(parent);
        Collection<Node> copy = new ArrayList<>(getChildren());    
        for( Node n : copy ) {
            n.removeFromParent();
        }
    }
    
    @Override
    public void attach(GuiControl parent) {
        super.attach(parent);
        for ( Node n : getChildren()) {
            getNode().attachChild(n);
        }
    }
    
    @Override
    public GuiLayout clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

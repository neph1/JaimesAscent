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

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import com.simsilica.lemur.Container;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import jme3test.jaimesascent.Main;
import jme3test.jaimesascent.ui.MyLayout;

/**
 * @author wil
 */
public abstract class AbstractScreen extends BaseAppState {

    protected Container rootContainer;

    @Override
    protected void initialize(Application app) {
        AppSettings settings = ((Main) app).getSettings();
        MyLayout layout = new MyLayout(MyLayout.onCreateRootPane(new Vector3f(settings.getWidth(), settings.getHeight(), 1), new Vector3f(1024, 576, 1)));

        rootContainer = new Container();
        rootContainer.setPreferredSize(new Vector3f(layout.getRootPane().getWindow().clone()));
        rootContainer.setLayout(layout);

        init();        
        setEnabled(false);
    }
    
    public JmeCursor createJmeCursorFromClassPath(String path, AssetManager assetManager) {
        Texture cursorTexture = assetManager.loadTexture(path);

        Image image = cursorTexture.getImage();
        ByteBuffer imgByteBuff = (ByteBuffer) image.getData(0).rewind();
        IntBuffer curIntBuff = BufferUtils.createIntBuffer(image.getHeight() * image.getWidth());

        while (imgByteBuff.hasRemaining()) {
            int rgba = imgByteBuff.getInt();
            /*int argb = ((rgba & 255) << 24) | (rgba >> 8);*/
          curIntBuff.put(rgba);
        }

        JmeCursor c = new JmeCursor();
        c.setHeight(image.getHeight());
        c.setWidth(image.getWidth());
        c.setNumImages(1);
        c.setyHotSpot(image.getHeight() - 3);
        c.setxHotSpot(3);
        c.setImagesData((IntBuffer) curIntBuff.rewind());
        return c;
    }

    protected abstract void init();

    @Override
    protected void cleanup(Application application) { }

    @Override
    protected void onEnable() {
        Main app = (Main) getApplication();
        AppSettings settings = app.getSettings();
        
        app.getGuiNode().attachChild(rootContainer);
        rootContainer.setLocalTranslation(0, settings.getHeight(), 0);
    }

    @Override
    protected void onDisable() {
        rootContainer.removeFromParent();
    }
}

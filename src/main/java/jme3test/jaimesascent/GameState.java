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
package jme3test.jaimesascent;

import com.jme3.anim.util.AnimMigrationUtils;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import jme3test.jaimesascent.screen.GameGUIScreen;
import jme3test.jaimesascent.ui.Window;
import jme3test.jaimesascent.ui.WindowListener;

/**
 *
 * Input handling and camera. Loads player character
 *
 * @author rickard
 */
public class GameState extends BaseAppState implements WindowListener<Integer> {

    private final BulletAppState physicsState;
    private InputManager inputManager;
    private ChaseCamera chaseCam;
    private Camera cam;
    private Node rootNode;

    private GameGUIScreen uiScreen;
    
    private BetterCharacterControl physicsCharacter;
    private Node playerNode;
    private MovementControl moveControl;

    private final Vector3f startTranslation = new Vector3f(0f, 2f, 0f);
    private final float startRotation = FastMath.PI * 0.5f;

    public GameState(BulletAppState physicsState) {
        this.physicsState = physicsState;
    }

    @Override
    protected void initialize(Application app) {

        this.inputManager = app.getInputManager();
        this.cam = app.getCamera();
        this.rootNode = ((SimpleApplication) app).getRootNode();

        uiScreen = getState(GameGUIScreen.class);
        uiScreen.getWindow().addWindosListener(this);
        
        setupCharacter(app.getAssetManager());

        setupKeys();

        setupChaseCam();

    }

    @Override
    public void doAction(Integer value) {
        pause(value == Window.OPTION_HIDE);
    }

    private void pause(boolean b) {
        chaseCam.setEnabled(b);
        moveControl.setEnabled(b);
        physicsCharacter.setEnabled(b);
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (playerNode.getLocalTranslation().y < -20) {
            reset();
        }
    }

    @Override
    protected void cleanup(Application aplctn) {
    }

    @Override
    protected void onEnable() {
        uiScreen.setEnabled(true);
    }

    @Override
    protected void onDisable() {
        uiScreen.setEnabled(false);
    }

    private void reset() {
        physicsCharacter.warp(startTranslation);
        chaseCam.setDefaultHorizontalRotation(startRotation);
    }

    private void setupKeys() {
        inputManager.addMapping("Strafe Left",
                new KeyTrigger(KeyInput.KEY_A),
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Strafe Right",
                new KeyTrigger(KeyInput.KEY_D),
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Walk Forward",
                new KeyTrigger(KeyInput.KEY_W),
                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Walk Backward",
                new KeyTrigger(KeyInput.KEY_S),
                new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Jump",
                new KeyTrigger(KeyInput.KEY_F),
                new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset",
                new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Mouse", 
                new KeyTrigger(KeyInput.KEY_LMENU));
        inputManager.addListener(moveControl, "Strafe Left", "Strafe Right");
        inputManager.addListener(moveControl, "Walk Forward", "Walk Backward");
        inputManager.addListener((ActionListener) (String string, boolean bln, float f) -> {
            if ("Mouse".equals(string) && !uiScreen.getWindow().isVisible()) {
                inputManager.setCursorVisible(bln);
                pause(!bln);
            }
        }, "Mouse");
        inputManager.addListener(moveControl, "Jump");
        inputManager.addListener((ActionListener) (String string, boolean bln, float f) -> {
            if (!bln && !uiScreen.getWindow().isVisible()) {
                reset();
            }
        }, "Reset");
    }

    private void setupChaseCam() {
        cam.setLocation(new Vector3f(10f, 6f, -5f));
        chaseCam = new ChaseCamera(cam, playerNode, inputManager);
        chaseCam.setDragToRotate(false);
        chaseCam.setSmoothMotion(true);
        chaseCam.setLookAtOffset(new Vector3f(0, 1f, 0));
        chaseCam.setDefaultDistance(7f);
        chaseCam.setMaxDistance(6f);
        chaseCam.setMinDistance(4f);
        chaseCam.setTrailingSensitivity(50);
        chaseCam.setChasingSensitivity(10);
        chaseCam.setRotationSpeed(5);
        chaseCam.setDefaultHorizontalRotation(startRotation);
    }

    private void setupCharacter(AssetManager assetManager) {

        physicsCharacter = new BetterCharacterControl(0.4f, 2.5f, 1f);

        physicsState.getPhysicsSpace().add(physicsCharacter);

        playerNode = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        playerNode.setLocalScale(1.50f);
        playerNode.setLocalTranslation(new Vector3f(0, 2, 0));

        playerNode.addControl(physicsCharacter);

        // Since Jaime was created using the old animation system
        // it needs to be converted to the new one.
        AnimMigrationUtils.migrate(playerNode);

        moveControl = new MovementControl(cam);
        playerNode.addControl(moveControl);

        rootNode.attachChild(playerNode);
    }

}

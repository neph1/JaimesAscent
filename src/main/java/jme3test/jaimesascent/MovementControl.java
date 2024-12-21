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

import com.jme3.anim.AnimComposer;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * Receives input and handles player movement and animations
 *
 * @author rickard
 */
public class MovementControl extends AbstractControl implements ActionListener {

    private BetterCharacterControl physicsCharacter;
    private AnimComposer animControl;

    final private Vector3f walkDirection = new Vector3f(0, 0, 0);
    final private Vector3f viewDirection = new Vector3f(0, 0, 1);
    final private float moveSpeed = 3f;

    private boolean leftStrafe = false, rightStrafe = false, forward = false, backward = false;

    private String currentAnimation = "Idle";

    private float fallingTime = 0f;

    private final Camera cam;

    public MovementControl(Camera cam) {
        this.cam = cam;
    }

    @Override
    public void controlUpdate(float tpf) {
        final Vector3f modelForwardDir = spatial.getWorldRotation().mult(Vector3f.UNIT_Z);
        final Vector3f modelLeftDir = spatial.getWorldRotation().mult(Vector3f.UNIT_X);

        walkDirection.set(0, 0, 0);
        if (leftStrafe) {
            walkDirection.addLocal(modelLeftDir);
        } else if (rightStrafe) {
            walkDirection.addLocal(modelLeftDir.negate());
        }
        if (forward) {
            walkDirection.addLocal(modelForwardDir);
        } else if (backward) {
            walkDirection.addLocal(modelForwardDir.negate());
        }
        physicsCharacter.setWalkDirection(walkDirection.multLocal(moveSpeed));

        viewDirection.set(cam.getDirection());
        physicsCharacter.setViewDirection(viewDirection);

        if (!physicsCharacter.isOnGround()) {
            fallingTime += tpf;
        } else {
            fallingTime = 0f;
        }

        updateAnimation();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (! enabled) {
            if (!currentAnimation.equals("Idle")) {
                animControl.setCurrentAction(currentAnimation = "Idle");
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) { 
        switch (binding) {
            case "Strafe Left" -> {
                leftStrafe = value;
            }
            case "Strafe Right" -> {
                rightStrafe = value;
            }
            case "Walk Forward" -> {
                forward = value;
            }
            case "Walk Backward" -> {
                backward = value;
            }
            case "Jump" -> {
                physicsCharacter.jump();
                animControl.setCurrentAction("JumpStart");
            }
            default -> {
            }
        }
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        physicsCharacter = spatial.getControl(BetterCharacterControl.class);
        animControl = spatial.getControl(AnimComposer.class);
    }

    /**
     * To avoid starting over all the time, animations are only set if they are
     * different from the current one
     */
    private void updateAnimation() {
        if (fallingTime > 0.25f && !currentAnimation.equals("Jumping")) {
            animControl.setCurrentAction(currentAnimation = "Jumping");
            return;
        }
        if (currentAnimation.equals("JumpStart")) {
            return;
        }
        if (leftStrafe || rightStrafe || forward || backward) {
            if (!currentAnimation.equals("Walk")) {
                animControl.setCurrentAction(currentAnimation = "Walk");
            }
            return;
        }
        if (!currentAnimation.equals("Idle")) {
            animControl.setCurrentAction(currentAnimation = "Idle");
        }
    }

}

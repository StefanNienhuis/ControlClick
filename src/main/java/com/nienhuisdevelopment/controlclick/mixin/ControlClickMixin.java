package com.nienhuisdevelopment.controlclick.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;

@Mixin(Mouse.class)
public abstract class ControlClickMixin {

    @Shadow @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void onMouseButton(long window, int button, int action, int mods);

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        // Same as original - ignore if not current window
        if (window != this.client.getWindow().getHandle()) {
            return;
        }

        // If the control modifier is active, disable it and run the function again.
        if (MinecraftClient.IS_SYSTEM_MAC && button == 0 && (mods & GLFW_MOD_CONTROL) == GLFW_MOD_CONTROL) {
            this.onMouseButton(window, button, action, mods ^ GLFW_MOD_CONTROL);
            ci.cancel();
        }
    }

}

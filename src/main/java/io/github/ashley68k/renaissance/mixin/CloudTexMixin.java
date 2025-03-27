package io.github.ashley68k.renaissance.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.Buffer;
import java.nio.FloatBuffer;

@Mixin(WorldRenderer.class)
public class CloudTexMixin
{
    @Shadow
    private Minecraft client;

    @Shadow
    private TextureManager textureManager;

    @Shadow
    private int ticks = 0;

    @Unique
    private final FloatBuffer fb = BufferUtils.createFloatBuffer(16);

    @Unique
    private FloatBuffer getFb(float f, float h) {
        ((Buffer)this.fb).clear();
        this.fb.put(f).put(0.0F).put(h).put(0.0F);
        ((Buffer)this.fb).flip();
        return this.fb;
    }

    // infdev-20100618 cloud texturing
    @Inject(method = "renderFancyClouds(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;getTextureId(Ljava/lang/String;)I"))
    private void injected(float tickDelta, CallbackInfo ci)
    {
        double am = (this.client.player.lastTickX + (this.client.player.x - this.client.player.lastTickX) * tickDelta + (this.ticks + tickDelta) * 0.03F) / 12.0;
        double an = (this.client.player.lastTickZ + (this.client.player.z - this.client.player.lastTickZ) * tickDelta) / 12.0 + 0.33F;
        int var42 = MathHelper.floor(am / 2048.0);
        int var44 = MathHelper.floor(an / 2048.0);
        am -= var42 << 11;
        an -= var44 << 11;

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureManager.getTextureId("/assets/renaissance/textures/fluff.png"));
        GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
        GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.getFb(1.0F, 0.0F));
        GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
        GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.getFb(0.0F, 1.0F));
        GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
        GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glScalef(0.25F, 0.25F, 0.25F);
        GL11.glTranslatef((float)am, (float)an, 0.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    // Cleanup to stop texture from rendering when it shouldn't
    @Inject(method = "renderFancyClouds(F)V", at = @At("TAIL"))
    private void injected2(float par1, CallbackInfo ci) {
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
    }
}
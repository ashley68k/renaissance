package io.github.ashley68k.renaissance.mixin;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldRenderer.class)
public class StarRenderMixin {
    // default 1500
    @ModifyConstant(method = "renderStars()V", constant = @Constant(intValue = 1500))
    private int getStarDensity(int value)
    {
        return (int)(value * 1.5);
    }
}

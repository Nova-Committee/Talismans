package nova.committee.talismans.common.morph.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;

import java.util.function.Predicate;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:26
 * Version: 1.0
 */
public class AdvancedAbstractClientPlayerEntity extends AbstractClientPlayer
{
    public ResourceLocation skinResourceLocation = DefaultPlayerSkin.getDefaultSkin(getUUID());
    public ResourceLocation elytraResourceLocation = null;
    public ResourceLocation capeResourceLocation = null;
    public String modelName;

    private Predicate<PlayerModelPart> isWearing = null;

    public AdvancedAbstractClientPlayerEntity(ClientLevel world, GameProfile profile)
    {
        super(world, profile);
    }

    @Override
    public ResourceLocation getSkinTextureLocation()
    {
        return skinResourceLocation;
    }

    @Override
    public boolean isCapeLoaded()
    {
        return capeResourceLocation != null;
    }

    @Override
    public boolean isElytraLoaded()
    {
        return elytraResourceLocation != null;
    }

    @Override
    public boolean isModelPartShown(PlayerModelPart part)
    {
        return this.isWearing == null ? true : isWearing.test(part);
    }

    public void setIsWearing(Predicate<PlayerModelPart> isWearing)
    {
        this.isWearing = isWearing;
    }

    @Override
    public ResourceLocation getElytraTextureLocation()
    {
        return elytraResourceLocation;
    }

    @Override
    public ResourceLocation getCloakTextureLocation()
    {
        return capeResourceLocation;
    }

    @Override
    public String getModelName()
    {
        return modelName == null ? DefaultPlayerSkin.getSkinModelName(this.getUUID()) : modelName;
    }
}

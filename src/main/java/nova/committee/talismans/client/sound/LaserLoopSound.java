package nova.committee.talismans.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import nova.committee.talismans.common.item.PigEmblems;
import nova.committee.talismans.init.registry.ModSounds;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/27 19:42
 * Version: 1.0
 */
public class LaserLoopSound extends AbstractTickableSoundInstance {
    private final Player player;

    public LaserLoopSound(Player player, float volume) {
        super(ModSounds.laser, SoundSource.PLAYERS);
        this.player = player;
        this.looping = true;
        this.delay = 0;
        this.volume = volume;
        this.x = (float) player.getX();
        this.y = (float) player.getY();
        this.z = (float) player.getZ();
    }

    public boolean canStartSilent() {
        return true;
    }

    public void tick() {
        ItemStack heldItem = PigEmblems.getPig(player);
        if (!(this.player.isUsingItem() && heldItem.getItem() instanceof PigEmblems)) {
            this.stop();
        } else {
            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();
        }
    }
}

package nova.committee.talismans.util;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/28 21:07
 * Version: 1.0
 */
public class VectorUtil {

    public static BlockHitResult getLookingAt(Player player, ItemStack tool, int range) {
        return getLookingAt(player, ClipContext.Fluid.NONE, range);
    }

    public static BlockHitResult getLookingAt(Player player, ClipContext.Fluid rayTraceFluid, int range) {
        Level world = player.level;

        Vec3 look = player.getLookAngle();
        Vec3 start = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());

        Vec3 end = new Vec3(player.getX() + look.x * (double) range, player.getY() + player.getEyeHeight() + look.y * (double) range, player.getZ() + look.z * (double) range);
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, rayTraceFluid, player);
        return world.clip(context);
    }

    public static AABB getAABB(Vec3 pos1, Vec3 pos2) {
        return new AABB(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
    }

}

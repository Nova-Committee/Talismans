package nova.committee.talismans.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/27 14:26
 * Version: 1.0
 */
public class CodecUtil {
    public static final Codec<Direction> DIRECTION_CODEC = StringRepresentable.fromEnum(Direction::values, Direction::byName);

}

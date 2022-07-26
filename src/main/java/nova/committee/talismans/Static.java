package nova.committee.talismans;

import cn.evolvefield.mods.atomlib.init.registry.BaseTab;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.GameRules;
import nova.committee.talismans.common.morph.VisualMorphDataRegistry;
import org.slf4j.Logger;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/19 16:04
 * Version: 1.0
 */
public class Static {
    public static final String MOD_ID = "talismans";


    private static final Logger LOGGER = LogUtils.getLogger();

    public static BaseTab TAB = new BaseTab(MOD_ID, Items.DIRT);

    public static Rarity RARITY = Rarity.create("POWER", ChatFormatting.DARK_AQUA);

    public static GameRules.Key<GameRules.IntegerValue> MORPH_AGGRO_DURATION;
    public static GameRules.Key<GameRules.BooleanValue> PREVENT_LOOKAT;
    public static VisualMorphDataRegistry VISUAL_MORPH_DATA;


}

package nova.committee.talismans.init.handler;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.talismans.common.cmd.MorphCommand;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 14:30
 * Version: 1.0
 */
@Mod.EventBusSubscriber
public class CmdHandler {

    @SubscribeEvent
    public static void registryCmd(RegisterCommandsEvent event){
        MorphCommand.registerCommands(event.getDispatcher());
    }
}

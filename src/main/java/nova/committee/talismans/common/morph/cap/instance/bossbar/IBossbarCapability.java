package nova.committee.talismans.common.morph.cap.instance.bossbar;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public interface IBossbarCapability
{
	ServerPlayer getPlayer();
	void setBossbar(ServerBossEvent bossbar);
	void clearBossbar();
	Optional<ServerBossEvent> getBossbar();
}

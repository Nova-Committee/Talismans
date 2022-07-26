package nova.committee.talismans.common.morph.cap.instance.bossbar;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class BossbarCapability implements IBossbarCapability
{
	private Optional<ServerBossEvent> bossbar = Optional.empty();
	private ServerPlayer player;

	public BossbarCapability(ServerPlayer player)
	{
		this.player = player;
	}

	@Override
	public ServerPlayer getPlayer()
	{
		return player;
	}

	@Override
	public void setBossbar(ServerBossEvent bossbar)
	{
		clearBossbar();
		this.bossbar = Optional.of(bossbar);
	}

	@Override
	public void clearBossbar()
	{
		if(this.bossbar.isPresent())
		{
			this.bossbar.get().removeAllPlayers();
			this.bossbar = Optional.empty();
		}
	}

	@Override
	public Optional<ServerBossEvent> getBossbar()
	{
		return bossbar;
	}
}

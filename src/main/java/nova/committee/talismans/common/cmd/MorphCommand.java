package nova.committee.talismans.common.cmd;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.ForgeRegistries;
import nova.committee.talismans.common.morph.MorphItem;
import nova.committee.talismans.common.morph.cap.IMorphCapability;
import nova.committee.talismans.common.morph.cap.MorphCapabilityAttacher;
import nova.committee.talismans.init.handler.MorphManagerHandler;
import nova.committee.talismans.util.MorphUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MorphCommand
{
	public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal("morph")
				.requires(sender -> sender.hasPermission(2))
				.then(
						Commands.argument("player", EntityArgument.players()).then(
							Commands.argument("entity", EntitySummonArgument.id())
							.suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
							.executes(ctx ->
							{
								return createEntityMorph(ctx.getArgument("player", EntitySelector.class).findPlayers(ctx.getSource()), ctx.getArgument("entity", ResourceLocation.class), new CompoundTag());
							})
							.then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
							.executes(ctx ->
							{
								return createEntityMorph(ctx.getArgument("player", EntitySelector.class).findPlayers(ctx.getSource()), ctx.getArgument("entity", ResourceLocation.class), ctx.getArgument("nbt", CompoundTag.class));
							})))
						));





		dispatcher.register(Commands.literal("addmorph")
				.requires(sender -> sender.hasPermission(2))
				.then(
						Commands.argument("player", EntityArgument.players()).then(
									Commands.argument("entity", EntitySummonArgument.id())
									.suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
									.executes(ctx ->
									{
										return addMorph(ctx.getArgument("player", EntitySelector.class).findPlayers(ctx.getSource()), ctx.getArgument("entity", ResourceLocation.class), new CompoundTag());
									})
									.then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
									.executes(ctx ->
									{
										return addMorph(ctx.getArgument("player", EntitySelector.class).findPlayers(ctx.getSource()), ctx.getArgument("entity", ResourceLocation.class), ctx.getArgument("nbt", CompoundTag.class));
									}))
								)
						));



		}




	private static int addMorph(List<ServerPlayer> entities, ResourceLocation rs, CompoundTag nbtData)
	{
		MorphItem morphItemToAdd = MorphManagerHandler.FALLBACK.createMorph(ForgeRegistries.ENTITIES.getValue(rs), nbtData, null, true);

		for(ServerPlayer entity : entities)
		{
			IMorphCapability capability = entity.getCapability(MorphCapabilityAttacher.MORPH_CAP).resolve().get();

			if(capability.getMorphList().contains(morphItemToAdd))
				entity.sendMessage(new TextComponent(ChatFormatting.RED + "You may not add a morph to your list that is already present."), new UUID(0, 0));
			else
			{
				entity.sendMessage(new TextComponent("Added " + rs.toString() + " with its NBT data to your morph list."), new UUID(0, 0));

				capability.addToMorphList(morphItemToAdd);
				capability.syncMorphAcquisition(morphItemToAdd);
			}
		}

		return 0;
	}

	private static int createEntityMorph(List<ServerPlayer> entities, ResourceLocation rs, CompoundTag nbtData)
	{
		for(ServerPlayer entity : entities)
		{
			if(rs.toString().equals("bmorph:morph_entity"))
				throw new IllegalArgumentException("You may not morph yourself into the morph entity.");

			nbtData.putString("id", rs.toString());

			MorphUtil.morphToServer(Optional.of(MorphManagerHandler.FALLBACK.createMorph(ForgeRegistries.ENTITIES.getValue(rs), nbtData, null, true)), Optional.empty(), entity);
		}

		return 0;
	}
}

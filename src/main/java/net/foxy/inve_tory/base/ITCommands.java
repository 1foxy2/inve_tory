package net.foxy.inve_tory.base;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.foxy.inve_tory.InveTory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class ITCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal(InveTory.MODID).requires(source -> source.hasPermission(2))
                        .then(Commands.literal("disable").then(
                                Commands.argument("target", EntityArgument.player())
                                        .executes(ctx -> handleToggle(ctx, true))
                                ).executes(ctx -> handleToggleSource(ctx, true))
                        )
                        .then(Commands.literal("enable").then(
                                Commands.argument("target", EntityArgument.player())
                                        .executes(ctx -> handleToggle(ctx, false))
                                ).executes(ctx -> handleToggleSource(ctx, false))
                        )
        );
    }

    public static int handleToggle(CommandContext<CommandSourceStack> context, boolean value) throws CommandSyntaxException {
        Player target = EntityArgument.getPlayer(context, "target");

        return handleToggle(context, value, target);
    }

    public static int handleToggleSource(CommandContext<CommandSourceStack> context, boolean value) throws CommandSyntaxException {
        return handleToggle(context, value, context.getSource().getPlayerOrException());
    }

    public static int handleToggle(CommandContext<CommandSourceStack> context, boolean value, Player target) {
        boolean oldValue = target.getData(ITAttachments.DISABLED);
        if (oldValue == value) {
            context.getSource().sendFailure(Component.translatable(
                    "commands.inve_tory.already_" + (value ? "disabled" : "enabled"),
                    context.getSource().getDisplayName()
            ));
            return 0;
        }

        target.setData(ITAttachments.DISABLED, value);
        context.getSource().sendSuccess(() -> Component.translatable(
                "commands.inve_tory." + (value ? "disabled" : "enabled"),
                context.getSource().getDisplayName()
        ), true);

        return 1;
    }
}

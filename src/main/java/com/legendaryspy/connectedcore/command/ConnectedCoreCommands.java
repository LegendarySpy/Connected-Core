package com.legendaryspy.connectedcore.command;

import com.legendaryspy.connectedcore.ConnectedCore;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class ConnectedCoreCommands {
    private ConnectedCoreCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(Commands.literal("connected-core")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("fireworkboosting")
                .executes(context -> {
                    boolean enabled = ConnectedCore.isFireworkBoostingEnabled();
                    context.getSource().sendSuccess(() -> Component.literal("Firework boosting is " + (enabled ? "enabled" : "disabled")), false);
                    return enabled ? 1 : 0;
                })
                .then(Commands.argument("enabled", BoolArgumentType.bool())
                    .executes(context -> {
                        boolean enabled = BoolArgumentType.getBool(context, "enabled");
                        boolean changed = ConnectedCore.updateFireworkBoosting(enabled);
                        if (changed) {
                            context.getSource().sendSuccess(() -> Component.literal("Set firework boosting to " + (enabled ? "enabled" : "disabled")), true);
                            return 1;
                        }
                        context.getSource().sendFailure(Component.literal("Firework boosting is already " + (enabled ? "enabled" : "disabled")));
                        return 0;
                    }))));
    }
}

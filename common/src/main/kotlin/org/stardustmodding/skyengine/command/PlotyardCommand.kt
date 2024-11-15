package org.stardustmodding.skyengine.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import org.stardustmodding.interstellar.api.command.ICommand
import org.stardustmodding.skyengine.entity.ShipEntity
import org.stardustmodding.skyengine.plotyard.Plotyard

class PlotyardCommand : ICommand<CommandSourceStack> {
    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("plotyard").then(
                literal("position").then(
                    argument(
                        "ship", EntityArgument.entity()
                    ).executes(this::execute)
                )
            )
        )
    }

    override fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val ship = EntityArgument.getEntity(ctx, "ship")

        if (ship is ShipEntity) {
            ctx.source.sendSuccess({
                Component.translatable(
                    "chat.success.interstellar.ship_pos",
                    ship.chunkPos?.x?.times(Plotyard.CHUNK_SIZE) ?: 0,
                    ship.chunkPos?.z?.times(Plotyard.CHUNK_SIZE) ?: 0
                )
            }, false)
            return 1
        } else {
            ctx.source.sendFailure(Component.translatable("chat.error.interstellar.not_ship_entity"))
            return 0
        }
    }
}
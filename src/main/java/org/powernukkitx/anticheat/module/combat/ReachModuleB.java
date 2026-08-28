package org.powernukkitx.anticheat.module.combat;

import org.powernukkitx.event.EventHandler;
import org.powernukkitx.event.server.PacketReceiveEvent;
import java.util.Optional;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.ItemUseOnActorActionType;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.data.InventoryTransactionDataType;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.data.ItemUseOnActorInventoryTransaction;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.powernukkitx.anticheat.AntiCheatPlugin;
import org.powernukkitx.anticheat.module.Module;
import org.powernukkitx.anticheat.module.ModuleType;
import org.powernukkitx.anticheat.player.AntiCheatPlayer;
import org.powernukkitx.anticheat.util.MathUtil;
import org.powernukkitx.anticheat.util.ViolationId;
import org.powernukkitx.entity.Entity;

/**
 * @author Kaooot
 */
public class ReachModuleB extends Module {

    public ReachModuleB(AntiCheatPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInventoryTransaction(PacketReceiveEvent event) {
        if (!(event.getPacket() instanceof InventoryTransactionPacket packet)) {
            return;
        }

        if (!packet.getTransaction().getType()
            .equals(InventoryTransactionDataType.ITEM_USE_ON_ACTOR)) {
            return;
        }

        final ItemUseOnActorInventoryTransaction transaction =
            (ItemUseOnActorInventoryTransaction) packet.getTransaction();

        if (!transaction.getActionType().equals(ItemUseOnActorActionType.ATTACK)) {
            return;
        }

        final Optional<AntiCheatPlayer> optional = this.plugin.getPlayerRegistry()
            .getPlayer(event.getPlayer().getUniqueId());

        if (optional.isEmpty()) {
            return;
        }

        final AntiCheatPlayer player = optional.get();
        final Entity target = player.getLevel().getEntity(transaction.getRuntimeId());

        if (target == null) {
            return;
        }

        final Vector3f eyePosition = player.getPosition()
            .add(0, player.getServerPlayer().getEyeHeight(), 0);
        final Vector3f targetPosition = target.getPosition().toNetwork();
        final float distance = eyePosition.distance(targetPosition);

        final float maxRange = player.getServerPlayer().isCreative() ?
            this.plugin.getMainConfig().getMaxActorAttackRangeCreative() :
            this.plugin.getMainConfig().getMaxActorAttackRange();

        if (distance > maxRange) {
            player.sendViolationWarning(
                ViolationId.EXCEEDED_ACTOR_ATTACK_RANGE, player.getName() +
                    " exceeded the max actor attack range, value: " +
                    MathUtil.round(distance) + ", max: " + maxRange
            );
        }

        if (distance > this.plugin.getMainConfig().getActorAttackRangeLimit()) {
            event.setCancelled();
        }
    }

    @Override
    public String getName() {
        return "ReachB";
    }

    @Override
    public ModuleType getType() {
        return ModuleType.COMBAT;
    }
}

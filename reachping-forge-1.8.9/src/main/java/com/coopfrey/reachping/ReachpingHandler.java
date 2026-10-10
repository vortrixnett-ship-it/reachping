package com.coopfrey.reachping;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ReachpingHandler {
    /** Set by /reachping; the screen is opened on the next tick so the chat GUI doesn't close it again. */
    public static boolean openConfig = false;

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = event.entityPlayer;
        // Only the local player, only on the client side (this event also fires on the integrated server)
        if (!player.worldObj.isRemote || player != mc.thePlayer) return;

        Entity target = event.target;
        if (target == null) return;
        if (ReachpingConfig.onlyPlayers && !(target instanceof EntityPlayer)) return;

        double threshold = ReachpingConfig.distance;
        double distSq = player.getDistanceSqToEntity(target);

        if (distSq > threshold * threshold) {
            if (ReachpingConfig.enabled) {
                player.playSound(ReachpingConfig.soundId, (float) ReachpingConfig.volume, 1.0F);
            }
        } else if (ReachpingConfig.failSoundEnabled) {
            player.playSound(ReachpingConfig.failSoundId, (float) ReachpingConfig.volume, 1.0F);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();

        while (ReachpingMod.toggleKey.isPressed()) {
            ReachpingConfig.enabled = !ReachpingConfig.enabled;
            ReachpingConfig.save();
            if (mc.thePlayer != null) {
                mc.thePlayer.addChatMessage(new ChatComponentText(
                        "ReachPing is now " + (ReachpingConfig.enabled ? "Enabled" : "Disabled")));
            }
        }

        if (openConfig) {
            openConfig = false;
            mc.displayGuiScreen(new ReachpingConfigScreen(null));
        }
    }
}

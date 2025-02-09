package me.thanhmagics.core;

import me.thanhmagics.ItemUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Listeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Data data = ItemUpgrade.getInstance().data;
            if (!data.playerDT.containsKey(player.getUniqueId())) {
                data.playerDT.put(
                        player.getUniqueId(),new PlayerDT(player.getUniqueId())
                );
            }
    }
}

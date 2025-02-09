package me.thanhmagics;

import me.thanhmagics.command.Command;
import me.thanhmagics.command.OpenUpgradeGUI;
import me.thanhmagics.core.Data;
import me.thanhmagics.core.Listeners;
import me.thanhmagics.core.PlayerDT;
import me.thanhmagics.core.UpgradeGUI;
import me.thanhmagics.utils.ObjectSerialization;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

public final class ItemUpgrade extends JavaPlugin {

    public Data data;

    private static ItemUpgrade instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        getServer().getPluginManager().registerEvents(new Listeners(),this);
        getServer().getPluginManager().registerEvents(new UpgradeGUI(),this);
        getCommand("itemupgrade").setExecutor(new Command());
        getCommand("upgrademenu").setExecutor(new OpenUpgradeGUI());
        data = (Data) ObjectSerialization.getDataObject("data-v2.dat");
        if (data == null) data = new Data();
        if (data.upGem == null) data.upGem = ObjectSerialization.isToString(new ItemStack(Material.BOOK));
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!data.playerDT.containsKey(player.getUniqueId())) {
                data.playerDT.put(
                        player.getUniqueId(),new PlayerDT(player.getUniqueId())
                );
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ObjectSerialization.saveObject(data,"data-v2.dat");
    }

    public static ItemUpgrade getInstance() {
        return instance;
    }

}

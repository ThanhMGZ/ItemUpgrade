package me.thanhmagics.command;

import me.thanhmagics.ItemUpgrade;
import me.thanhmagics.core.*;
import me.thanhmagics.utils.MIUtils;
import me.thanhmagics.utils.ObjectSerialization;
import me.thanhmagics.utils.Utils;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
//        ItemStack itemStack = player.getInventory().getItemInMainHand();
//        MMOItem mmoItem = MIUtils.fromStack(itemStack);
//        player.sendMessage(String.valueOf("_------------------"));
//        player.sendMessage(String.valueOf(mmoItem.getMaxUpgradeLevel()));
//        player.sendMessage(String.valueOf(mmoItem.getUpgradeLevel()));
//        player.sendMessage(String.valueOf(mmoItem.getMaxUpgradeLevel()));
//        mmoItem.upgrade();
//        player.sendMessage(String.valueOf(mmoItem.getUpgradeLevel()));
//        player.sendMessage(String.valueOf(mmoItem.getMaxUpgradeLevel()));
//        new SmartGive(player).give(new ItemStack[]{mmoItem.newBuilder().build()});
//        PlayerData.get(player.getUniqueId()).updateInventory();
        if (player.hasPermission("op")) {
            if (strings.length == 0) {
                player.sendMessage(Utils.applyColor("&b&m--------> &f&lItemUpgrade &b&m<---------"));
                player.sendMessage(Utils.applyColor("&7- &a/ItemUpgrade menu&7: Mở setup GUI"));
                player.sendMessage(Utils.applyColor("&7- &a/ItemUpgrade setgem&7: Đặt Item Cho Gemstone (là item trên tay)"));
                player.sendMessage(Utils.applyColor("&7- &c/um&7: Mở Menu nâng cấp"));
                player.sendMessage(Utils.applyColor("&b&m--------> &9ByThanhMagics &b&m<---------"));
            } else {
                if (strings[0].equalsIgnoreCase("setgem")) {
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    if (itemStack.getType().equals(Material.AIR)) {
                        player.sendMessage(Utils.applyColor("&cCầm gemstone trên tay"));
                        return true;
                    }
                    ItemUpgrade.getInstance().data.upGem = ObjectSerialization.isToString(player.getInventory().getItemInMainHand());
                    player.sendMessage(Utils.applyColor("&bSet gemstone thành công"));
                } else if (strings[0].equalsIgnoreCase("menu")) {
                    UpgradesSetupGUI.open(player, 1);
                } else if (strings[0].equalsIgnoreCase("reset")) {
                    ItemUpgrade.getInstance().data = new Data();
                    for (Player playerr : Bukkit.getOnlinePlayers()) {
                        if (!ItemUpgrade.getInstance().data.playerDT.containsKey(player.getUniqueId())) {
                            ItemUpgrade.getInstance().data.playerDT.put(
                                    player.getUniqueId(),new PlayerDT(player.getUniqueId())
                            );
                        }
                    }
                    player.sendMessage(Utils.applyColor("&aReset Thành Công!"));
                } else if (strings[0].equalsIgnoreCase("getgem")) {
                    player.getInventory().addItem(ObjectSerialization.stringToIs(ItemUpgrade.getInstance().data.upGem));
                }
            }
        }
        return true;
    }
}

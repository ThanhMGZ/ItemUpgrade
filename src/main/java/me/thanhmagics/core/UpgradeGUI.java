package me.thanhmagics.core;

import io.lumine.mythic.lib.api.util.SmartGive;
import me.thanhmagics.ItemUpgrade;
import me.thanhmagics.utils.*;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class UpgradeGUI implements Listener {

    public static void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 5, "Nâng Cấp Vật Phẩm");
        for (int i = 0; i < 9 * 5; i++) {
            inventory.setItem(i,
                    new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .setDisplayName("&a").build());
        }
        PlayerDT playerDT = ItemUpgrade.getInstance().data.playerDT.get(player.getUniqueId());
        ItemStack is1 = null, is2 = null;
        if (playerDT.i1 != null) {
            is1 = ObjectSerialization.stringToIs(playerDT.i1);
            inventory.setItem(28, is1);
        } else inventory.setItem(28, new ItemStack(Material.AIR));
        if (playerDT.i2 != null) {
            is2 = ObjectSerialization.stringToIs(playerDT.i2);
            inventory.setItem(34, is2);
        } else inventory.setItem(34, new ItemStack(Material.AIR));
        for (int i = 10; i < 13; i++) {
            inventory.setItem(i, new ItemBuilder(is1 != null ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                    .setDisplayName("&a").build());
        }
        inventory.setItem(19, new ItemBuilder(is1 != null ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("&a").build());
        for (int i = 14; i < 17; i++) {
            inventory.setItem(i, new ItemBuilder(playerDT.i2 != null ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                    .setDisplayName("&a").build());
        }
        inventory.setItem(25, new ItemBuilder(playerDT.i2 != null ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("&a").build());
        boolean upgradable = false, maxlvl = false;
        MMOItem mmoItem = null;
        UpgradeInfo upgradeInfo = null;
        int cost = 0, level = 0;
        if (is1 != null) {
            mmoItem = MIUtils.fromStack(is1);
            upgradeInfo = MIUtils.getByMMOItem(mmoItem);
            level = mmoItem.getUpgradeLevel();
            if (level >= mmoItem.getMaxUpgradeLevel()) {
                maxlvl = true;
            } else {
                if (upgradeInfo != null) {
                    if (upgradeInfo.upGemCost.containsKey(level + 1)) {
                        cost = upgradeInfo.upGemCost.get(level + 1);
                        if (is2 != null && is2.getAmount() >= cost) {
                            for (int i = 14; i < 17; i++) {
                                inventory.setItem(i, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                                        .setDisplayName("&a").build());
                            }
                            inventory.setItem(25, new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                                    .setDisplayName("&a").build());
                            upgradable = true;
                        }
                    }
                }
            }
        }
        if (maxlvl) {
            inventory.setItem(22, new ItemBuilder(Material.ANVIL)
                    .setDisplayName("&cVật Phẩm Đã Ở Cấp Độ Tối Đa! :))")
                    .build());
        } else {
            if (upgradable) {
                mmoItem.upgrade();
                inventory.setItem(13, mmoItem.newBuilder().build());
                inventory.setItem(22, new ItemBuilder(Material.ANVIL)
                        .setDisplayName("&fUPGRADE: " + ItemBuilder.getDisplayName(Objects.requireNonNull(mmoItem.newBuilder().build())))
                        .addLore(" ")
                        .addLore("&8 ▍&f Chi Phí:&d x" + cost + " Gemstone &a✔")
                        .addLore("&8 ▍&f Tỉ Lệ Thành Công:&d " + upgradeInfo.successPercent.get(level + 1) + "% &a✔")
                        .addLore(" ")
                        .addLore("&eẤn Vào Để Nâng Cấp!")
                        .build());
            } else {
                if (is1 == null) {
                    inventory.setItem(22, new ItemBuilder(Material.ANVIL)
                            .setDisplayName("&aNâng Cấp Vật Phẩm")
                            .addLore(" ")
                            .addLore("&cVui Lòng Chọn Vật Phẩm Và Gemstone Vào Để Bắt Đầu!")
                            .build());
                } else {
                    inventory.setItem(22, new ItemBuilder(Material.ANVIL)
                            .setDisplayName("&fUPGRADE: " + ItemBuilder.getDisplayName(Objects.requireNonNull(mmoItem.newBuilder().build())))
                            .addLore(" ")
                            .addLore("&8 ▍&f Chi Phí:&d x" + cost + " Gemstone &c❌")
                            .addLore("&8 ▍&f Tỉ Lệ Thành Công:&d " + upgradeInfo.successPercent.get(level + 1) + " &a✔")
                            .addLore(" ")
                            .addLore("&cVui Lòng Thêm Gemstone Vào Để Nâng Cấp!")
                            .build());
                }
            }
        }

        player.openInventory(inventory);
        players.remove(player.getUniqueId());
        players.add(player.getUniqueId());
    }

    private static final List<UUID> players = new ArrayList<>();
    public static ItemStack gemstone = null;
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (players.contains(player.getUniqueId())) {
            PlayerDT playerDT = ItemUpgrade.getInstance().data.playerDT.get(player.getUniqueId());
            event.setCancelled(true);
            if (gemstone == null) gemstone = ObjectSerialization.stringToIs(ItemUpgrade.getInstance().data.upGem);
            if (event.getClickedInventory() instanceof PlayerInventory) {
                if (event.getCurrentItem() == null) return;
                if (event.getCurrentItem().getType().equals(Material.AIR)) return;
                if (event.getCurrentItem().isSimilar(gemstone)) {
                    ItemStack itemStack = null;
                    if (playerDT.i2 != null) {
                        itemStack = ObjectSerialization.stringToIs(playerDT.i2);
                    }
                    if (itemStack != null) {
                        if (itemStack.getAmount() + event.getCurrentItem().getAmount() <= 64) {
                            itemStack.setAmount(itemStack.getAmount() + event.getCurrentItem().getAmount());
                            player.getInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
                            playerDT.i2 = ObjectSerialization.isToString(itemStack);
                            open(player);
                        } else {
                            event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() -
                                    (64 - itemStack.getAmount()));
                            itemStack.setAmount(64);
                            playerDT.i2 = ObjectSerialization.isToString(itemStack);
                            open(player);
                        }
                    } else {
                        playerDT.i2 = ObjectSerialization.isToString(event.getCurrentItem().clone());
                        open(player);
                                player.getInventory().setItem(event.getSlot(),new ItemStack(Material.AIR));
                                }
                } else {
                    if (MIUtils.isUpgradableItem(event.getCurrentItem())) {
                        ItemStack itemStack = null;
                        if (playerDT.i1 != null) {
                            itemStack = ObjectSerialization.stringToIs(playerDT.i1);
                        }
                        playerDT.i1 = ObjectSerialization.isToString(event.getCurrentItem().clone());
                        player.getInventory().setItem(event.getSlot(),new ItemStack(Material.AIR));
                        if (itemStack != null) new SmartGive(player).give(itemStack);
                        open(player);
                    } else {
                        player.sendMessage(Utils.applyColor("&cVật Phẩm Không Thể Nâng Cấp!"));
                    }
                }
            } else {
                if (event.getSlot() == 28) {
                    if (playerDT.i1 != null) {
                        new SmartGive(player).give(ObjectSerialization.stringToIs(playerDT.i1));
                        playerDT.i1 = null;
                    }
                    open(player);
                } else if (event.getSlot() == 34) {
                    if (playerDT.i2 != null) {
                        new SmartGive(player).give(ObjectSerialization.stringToIs(playerDT.i2));
                        playerDT.i2 = null;
                    }
                    open(player);
                } else if (event.getSlot() == 22) {
                    if (playerDT.i1 == null || playerDT.i2 == null) return;
                    if (event.getClickedInventory().getItem(10).getType().equals(Material.GREEN_STAINED_GLASS_PANE)) {
                        if (event.getClickedInventory().getItem(16).getType().equals(Material.GREEN_STAINED_GLASS_PANE)) {
                            MMOItem mmoItem = MIUtils.fromStack(ObjectSerialization.stringToIs(playerDT.i1));
                            UpgradeInfo info = MIUtils.getByMMOItem(mmoItem);
                            if (info == null) throw new RuntimeException();
                            int cost = info.upGemCost.get(mmoItem.getUpgradeLevel() + 1);
                            double s = info.successPercent.get(mmoItem.getUpgradeLevel() + 1);
                            ItemStack i2 = ObjectSerialization.stringToIs(playerDT.i2);
                            boolean successful = Math.random() < (s / 100);
                            if (successful) {
                                playerDT.i1 = null;
                                if (i2.getAmount() > cost) {
                                    ItemStack gem = ObjectSerialization.stringToIs(ItemUpgrade.getInstance().data.upGem);
                                    for (int i = cost; i < i2.getAmount(); i++) {
                                        new SmartGive(player).give(gem);
                                    }
                                }
                                playerDT.i2 = null;
                                mmoItem.upgrade();
                                ItemStack itemStack = mmoItem.newBuilder().build();
                                new SmartGive(player).give(itemStack);
                                player.sendMessage(Utils.applyColor(" "));
                                player.sendMessage(Utils.applyColor("&a&lNÂNG CẤP THÀNH CÔNG!"));
                                player.sendMessage(Utils.applyColor(" "));
                                player.sendMessage(Utils.applyColor("&e ○ &fBạn đã nâng cấp " + ItemBuilder.getDisplayName(itemStack)));
                                player.sendMessage(Utils.applyColor("&f  thành công! &8[&e"+(mmoItem.getUpgradeLevel() - 1) + " -> " + mmoItem.getUpgradeLevel() + "&8]"));
                                player.sendMessage(Utils.applyColor(" "));

                            } else {
                                ItemStack itemStack = ObjectSerialization.stringToIs(playerDT.i1);
                                playerDT.i1 = null;
                                if (i2.getAmount() > cost) {
                                    ItemStack gem = ObjectSerialization.stringToIs(ItemUpgrade.getInstance().data.upGem);
                                    for (int i = cost; i < i2.getAmount(); i++) {
                                        new SmartGive(player).give(gem);
                                    }
                                }
                                playerDT.i2 = null;
                                MMOItem mmi = MIUtils.fromStack(itemStack);
                                int level = mmi.getUpgradeLevel();
                                if (level > 7)
                                    mmi.getUpgradeTemplate().upgradeTo(mmi,mmi.getUpgradeLevel() - 1);
                                itemStack = mmi.newBuilder().build();
                                new SmartGive(player).give(mmi.newBuilder().build());
                                player.sendMessage(Utils.applyColor(" "));
                                player.sendMessage(Utils.applyColor("&c&lNÂNG CẤP THẤT BẠI!"));
                                player.sendMessage(Utils.applyColor(" "));
                                player.sendMessage(Utils.applyColor("&e ○ &fBạn đã nâng cấp " + ItemBuilder.getDisplayName(itemStack)));
                                player.sendMessage(Utils.applyColor("&f  thất bại! &8[&c"+(level) + " -> " + mmi.getUpgradeLevel() + "&8]"));
                                player.sendMessage(Utils.applyColor(" "));
                            }
                            open(player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        players.remove(player.getUniqueId());
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        players.remove(player.getUniqueId());
    }
}

package me.thanhmagics.core;

import me.thanhmagics.utils.ChatEditing;
import me.thanhmagics.utils.ItemBuilder;
import me.thanhmagics.utils.SelectionGUI2;
import me.thanhmagics.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class CreateUpgrade {

    public static UpgradeInfo coping = null;

    public static void open(Player player, UpgradeInfo upgradeInfo) {
        ItemStack plus = new ItemBuilder(Material.PAPER)
                .setDisplayName("&aThêm Mới").build();
        SelectionGUI2 selectionGUI2 = new SelectionGUI2(new SelectionGUI2.Items() {
            @Override
            public LinkedList<SelectionGUI2.Item> getItems() {
                LinkedList<SelectionGUI2.Item> rs = new LinkedList<>();
                upgradeInfo.successPercent.forEach((k, v) -> {
                    int gemCost = upgradeInfo.upGemCost.get(k);
                    rs.add(new SelectionGUI2.Item(new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                            .setDisplayName("&aUpgrade To Level " + k)
                            .addLore(" ")
                            .addLore("&8| &7 Số Lượng 'gemstone':&d " + gemCost)
                            .addLore("&8| &7 Tỉ Lệ Thành Công:&d " + v + "%")
                            .addLore(" ")
                            .addLore("&eLeft-Click Để Thay Đổi GemCost!")
                            .addLore("&eRight-Click Để Thay Đổi TLTC!").build()) {
                        @Override
                        public void onClick(ClickType clickType, SelectionGUI2 instance) {
                            int index = k;
                            if (clickType.equals(ClickType.LEFT)) {
                                player.sendMessage(Utils.applyColor("&d&m-------------------"));
                                player.sendMessage(Utils.applyColor("&aNhập giá trị xuống chat hoặc '-cancel'"));
                                player.sendMessage(Utils.applyColor("&d&m-------------------"));
                                ChatEditing.newHandler(new ChatEditing.Handler() {
                                    @Override
                                    public boolean onChat(Player player, String input) {
                                        int i = toInteger(input, "&c Vui Lòng Nhập Số", player);
                                        if (Integer.valueOf(i).equals(Integer.MAX_VALUE)) return true;
                                        upgradeInfo.upGemCost.replace(index, i);
                                        open(player, upgradeInfo);
                                        return true;
                                    }

                                    @Override
                                    public void onRefuse(Player player) {
                                        open(player, upgradeInfo);
                                    }
                                }, player.getUniqueId());
                            } else if (clickType.equals(ClickType.RIGHT)) {
                                player.sendMessage(Utils.applyColor("&d&m-------------------"));
                                player.sendMessage(Utils.applyColor("&aNhập giá trị xuống chat hoặc '-cancel'"));
                                player.sendMessage(Utils.applyColor("&d&m-------------------"));
                                ChatEditing.newHandler(new ChatEditing.Handler() {
                                    @Override
                                    public boolean onChat(Player player, String input) {
                                        try {
                                            double i = Double.parseDouble(input);
                                            if (i > 100) i = 100;
                                            upgradeInfo.successPercent.replace(index, i);
                                            open(player, upgradeInfo);
                                        } catch (Exception e) {
                                            player.sendMessage(Utils.applyColor("&cVui Lòng Nhập Số"));
                                            open(player, upgradeInfo);
                                        }
                                        return true;
                                    }

                                    @Override
                                    public void onRefuse(Player player) {
                                        open(player, upgradeInfo);
                                    }
                                }, player.getUniqueId());
                            }
                        }
                    });
                });
                rs.add(new SelectionGUI2.Item(plus) {
                    @Override
                    public void onClick(ClickType clickType, SelectionGUI2 instance) {
                        int s = upgradeInfo.successPercent.keySet().stream().toList().getLast();
                        upgradeInfo.successPercent.put(s + 1, 0.0D);
                        upgradeInfo.upGemCost.put(s + 1, 0);
                        open(player,upgradeInfo);
                    }
                });
                return rs;
            }
        });
        selectionGUI2.setTitle("&a&lEditing:&f " + upgradeInfo.id);
        selectionGUI2.addPageTravelling();
        selectionGUI2.addDecorItems();
        selectionGUI2.addSpecialItemInBottom(0, new SelectionGUI2.Item(new ItemBuilder(Material.PAPER)
                .setDisplayName("&aCopy Nội Dung")
                .addLore(" ")
                .addLore("&7- Đang Copy: " + (coping == null ? "Không_Có" : coping.id))
                .addLore(" ")
                .addLore("&eClick Để Copy Nội Dung Này")
                .build()) {
            @Override
            public void onClick(ClickType clickType, SelectionGUI2 instance) {
                coping = upgradeInfo;
                open(player,upgradeInfo);
            }
        });
        selectionGUI2.addSpecialItemInBottom(1, new SelectionGUI2.Item(new ItemBuilder(Material.PAPER)
                .setDisplayName("&aPaste Nội Dung")
                .addLore(" ")
                .addLore("&7- Đang Copy: " + (coping == null ? "Không_Có" : coping.id))
                .addLore(" ")
                .addLore("&eClick Để Paste").build()) {
            @Override
            public void onClick(ClickType clickType, SelectionGUI2 instance) {
                upgradeInfo.successPercent = new LinkedHashMap<>(coping.successPercent);
                upgradeInfo.upGemCost = new LinkedHashMap<>(coping.upGemCost);
                open(player,upgradeInfo);
            }
        });
        selectionGUI2.open(player, 1);
    }
}

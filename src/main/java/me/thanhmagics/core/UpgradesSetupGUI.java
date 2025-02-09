package me.thanhmagics.core;

import me.thanhmagics.ItemUpgrade;
import me.thanhmagics.utils.ItemBuilder;
import me.thanhmagics.utils.MIUtils;
import me.thanhmagics.utils.SelectionGUI2;
import me.thanhmagics.utils.Utils;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class UpgradesSetupGUI {

    public static void open(Player player, int page) {
        SelectionGUI2 selectionGUI2 = new SelectionGUI2(new SelectionGUI2.Items() {
            @Override
            public LinkedList<SelectionGUI2.Item> getItems() {
                LinkedList<SelectionGUI2.Item> rs = new LinkedList<>();
                for (UpgradeInfo upgradeInfo : ItemUpgrade.getInstance().data.upgradeInfos) {
                    MMOItem mmoItem = MIUtils.fromStrings(upgradeInfo.id, upgradeInfo.type);
                    rs.add(new SelectionGUI2.Item(mmoItem.newBuilder().build()) {
                        @Override
                        public void onClick(ClickType clickType, SelectionGUI2 instance) {
                            if (clickType.equals(ClickType.LEFT)) {
                                CreateUpgrade.open(player, upgradeInfo);
                            } else if (clickType.equals(ClickType.RIGHT)) {
                                ItemUpgrade.getInstance().data.upgradeInfos.remove(upgradeInfo);
                                open(player, instance.getPage());
                            }
                        }
                    });
                }
                rs.add(new SelectionGUI2.Item(new ItemBuilder(Material.PAPER)
                        .setDisplayName("&aClick Vào MMOItem Trong Túi Đồ Để Thêm Vào Đây!")
                        .addLore(" ")
                        .addLore("&bLeft-Click&6 Vào Item Để Chỉnh Sửa Tỉ Lệ/Nguyên Liệu")
                        .addLore("&bRight-Click&6 Để Xóa").build()) {
                    @Override
                    public void onClick(ClickType clickType, SelectionGUI2 instance) {
                        player.sendMessage(Utils.applyColor("&cKhông Phải Item Này!!!!!!"));
                    }
                });
                return rs;
            }
        }).setTitle("Upgrades").addOnClickPI(new SelectionGUI2.SG2_OnClick() {
            @Override
            public void onClick(ItemStack clicked, ClickType type, SelectionGUI2 instance) {
                MMOItem mmoItem = MIUtils.fromStack(clicked);
                if (mmoItem != null) {
                    UpgradeInfo upgradeInfo = MIUtils.getByMMOItem(mmoItem);
                    if (upgradeInfo != null) {
                        player.sendMessage(Utils.applyColor("&cUpgrade Cho Item Đã Tồn Tại Rồi!"));
                        return;
                    }
                    ItemUpgrade.getInstance().data.upgradeInfos.add(new UpgradeInfo(mmoItem.getId(), mmoItem.getType().getName()));
                    open(player, instance.getPage());
                } else {
                    player.sendMessage(Utils.applyColor("&cĐó Không Phải MMOItem @_@"));
                }
            }
        }).addDecorItems().addPageTravelling().open(player, 1);
    }

}

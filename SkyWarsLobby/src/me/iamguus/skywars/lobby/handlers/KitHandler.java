package me.iamguus.skywars.lobby.handlers;

import me.iamguus.skywars.lobby.SkyWarsLobby;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Guus on 7-7-2016.
 */
public class KitHandler {

    public HashMap<UUID, List<Kit>> playerKits;

    private MySQLHandler mysql;
    private SkyWarsLobby plugin;

    public KitHandler(SkyWarsLobby plugin) {
        this.plugin = plugin;
        playerKits = new HashMap<>();
        mysql = plugin.mysql;
    }

    public List<Kit> loadKits(Player player) {
        if (playerKits.containsKey(player.getUniqueId())) {
            return playerKits.get(player.getUniqueId());
        }

        try {
            PreparedStatement st = mysql.conn.prepareStatement("SELECT * FROM player_kit WHERE uuid = ?");

            st.setString(1, player.getUniqueId().toString());

            ResultSet rs = st.executeQuery();

            String kit1 =
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Kit deserialize(UUID owner, int id, String string) {
        String iconS = "";
        String swordS = "";
        String armorS = "";

        for (String s : string.split(" ")) {
            if (s.startsWith("icon")) iconS = s;
            if (s.startsWith("sword")) swordS = s;
            if (s.startsWith("armor")) armorS = s;
        }

        if (!iconS.equals("")) iconS = iconS.split(":")[1];
        if (!swordS.equals("")) swordS = swordS.split(":")[1];
        if (!armorS.equals("")) armorS = armorS.split(":")[1];

        KitIcon icon = KitIcon.getById(Integer.parseInt(iconS));
        KitSword sword = KitSword.getById(Integer.parseInt(swordS));

        List<Material> armor = new ArrayList<Material>();
        for (String armorLoop : armorS.split(",")) {
            armor.add(Material.valueOf(armorLoop.toUpperCase()));
        }

        return new Kit(owner, id, icon, sword, armor);
    }

    public class Kit {

        //TODO: add ability (enum)

        private UUID owner;
        private int id;
        private KitIcon icon;
        private KitSword sword;
        private List<Material> armor;

        public Kit(UUID owner, int id, KitIcon icon, KitSword sword, List<Material> armor) {
            this.owner = owner;
            this.id = id;
            this.icon = icon;
            this.sword = sword;
            this.armor = armor;
        }

        public UUID getOwner() {
            return owner;
        }

        public int getId() {
            return id;
        }

        public KitIcon getIcon() {
            return icon;
        }

        public void setIcon(KitIcon icon) {
            this.icon = icon;
        }

        public KitSword getSword() {
            return sword;
        }

        public void setSword(KitSword sword) {
            this.sword = sword;
        }

        public List<Material> getArmor() {
            return armor;
        }

        public void setArmor(List<Material> armor) {
            this.armor = armor;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("icon:" + icon.getId() + " ");
            sb.append("sword:" + sword.getId() + " ");
            sb.append("armor:");
            String tempArmor = "";
            for (Material mat : armor) {
                tempArmor += mat.name() + ",";
            }

            sb.append(tempArmor.substring(0, tempArmor.length() - 1));

            return sb.toString();
        }
    }

    private enum KitIcon {

        TNT(1, "TNT", false, Material.TNT), DIAMOND(2, "Diamond", false, Material.DIAMOND), BLAZE_ROD(3, "Blaze Rod", false, Material.BLAZE_ROD), NETHER_WARTS(4, "Nether Warts", false, Material.NETHER_WARTS),
        TORCH(5, "Torch", false, Material.TORCH), GLOWSTONE_DUST(6, "Glowstone Dust", false, Material.GLOWSTONE_DUST), WATCH(7, "Clock", false, Material.WATCH), FIREBALL(8, "Fireball", false, Material.FIREBALL),
        ENDER_PEARL(9, "Ender Pearl", false, Material.ENDER_PEARL), EXP_BOTTLE(10, "Bottle o' Enchanting", false, Material.EXP_BOTTLE), CAKE(11, "Cake", false, Material.CAKE),
        INK_SAC(12, "Ink Sac", false, Material.INK_SACK), NETHER_BRICK(13, "Nether Brick", false, Material.NETHER_BRICK_ITEM), QUARTZ(14, "Nether Quartz", false, Material.QUARTZ),
        REDSTONE(15, "Redstone Dust", false, Material.REDSTONE), EMERALD(16, "Emerald", false, Material.EMERALD), NETHER_STAR(0, "Nether Star", true, Material.NETHER_STAR);

        int id;
        String name;
        boolean isDefault;
        Material material;

        KitIcon(int id, String name, boolean isDefault, Material material) {
            this.id = id;
            this.name = name;
            this.isDefault = isDefault;
            this.material = material;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public Material getMaterial() {
            return material;
        }

        public static KitIcon getById(int id) {
            for (KitIcon icon : values()) {
                if (icon.getId() == id) {
                    return icon;
                }
            }

            return null;
        }
    }

    private enum KitSword {

        WOOD_SWORD(1, "Wooden Sword", Material.WOOD_SWORD), GOLD_SWORD(2, "Golden Sword", Material.GOLD_SWORD), STONE_SWORD(3, "Stone Sword", Material.STONE_SWORD), IRON_SWORD(4, "Iron Sword", Material.IRON_SWORD),
        DIAMOND_SWORD(5, "Diamond Sword", Material.DIAMOND_SWORD);

        int id;
        String name;
        Material material;

        KitSword(int id, String name, Material material) {
            this.id = id;
            this.name = name;
            this.material = material;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Material getMaterial() {
            return material;
        }

        public static KitSword getById(int id) {
            for (KitSword sword : values()) {
                if (sword.getId() == id) {
                    return sword;
                }
            }

            return null;
        }
    }
}

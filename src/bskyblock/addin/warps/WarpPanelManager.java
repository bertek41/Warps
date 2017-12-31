package bskyblock.addin.warps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import us.tastybento.bskyblock.api.commands.User;
import us.tastybento.bskyblock.api.panels.ClickType;
import us.tastybento.bskyblock.api.panels.PanelItem;
import us.tastybento.bskyblock.api.panels.PanelItem.ClickHandler;
import us.tastybento.bskyblock.api.panels.builders.PanelBuilder;
import us.tastybento.bskyblock.api.panels.builders.PanelItemBuilder;

public class WarpPanelManager {

    private static final boolean DEBUG = true;
    private static final int PANEL_MAX_SIZE = 47;
    private Warp plugin;
    // This is a cache of heads, so they don't need to be created everytime
    private HashMap<UUID, PanelItem> cachedWarps;


    public WarpPanelManager(Warp plugin) {
        this.plugin = plugin;
        cachedWarps = new HashMap<>();
        createWarpCache();
    }


    /**
     * This method makes the cache of heads based on the warps available
     */
    private void createWarpCache() {
        if (DEBUG)
            Bukkit.getLogger().info("DEBUG: creating warp cache");
        cachedWarps.clear();
        for (UUID warpOwner : plugin.getWarpSignsManager().getSortedWarps()) {
            if (DEBUG)
                Bukkit.getLogger().info("DEBUG: adding warp");
            cachedWarps.put(warpOwner, getPanelItem(warpOwner));          
        }
    }

    private PanelItem getPanelItem(UUID warpOwner) {
        return new PanelItemBuilder()
                .setName(plugin.getBSkyBlock().getPlayers().getName(warpOwner))
                .setDescription(plugin.getWarpSignsManager().getSignText(warpOwner))
                .setIcon(getSkull(warpOwner))
                .setClickHandler(new ClickHandler() {

                    @Override
                    public boolean onClick(User user, ClickType click) {
                        plugin.getWarpSignsManager().warpPlayer(user, warpOwner);
                        return true;
                    }
                }).build();
    }


    /**
     * Gets the skull for this player UUID
     * @param playerUUID
     * @return Player skull item
     */
    @SuppressWarnings("deprecation")
    private ItemStack getSkull(UUID playerUUID) {
        String playerName = plugin.getBSkyBlock().getPlayers().getName(playerUUID);
        if (DEBUG)
            plugin.getLogger().info("DEBUG: name of warp = " + playerName);
        ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (playerName == null) {
            if (DEBUG)
                plugin.getLogger().warning("Warp for Player: UUID " + playerUUID.toString() + " is unknown on this server, skipping...");
            return null;
        }
        SkullMeta meta = (SkullMeta) playerSkull.getItemMeta();
        meta.setOwner(playerName);
        meta.setDisplayName(ChatColor.WHITE + playerName);
        playerSkull.setItemMeta(meta);
        return playerSkull;
    }

    public void showWarpPanel(User user, int index) { 
        List<UUID> warps = new ArrayList<>(plugin.getWarpSignsManager().getSortedWarps());
        if (DEBUG) {
            Bukkit.getLogger().info("DEBUG: showing warps. warps list is " + warps.size());
        }
        if (index < 0) {
            index = 0;
        } else if (index > (warps.size() / PANEL_MAX_SIZE)) {
            index = warps.size() / PANEL_MAX_SIZE;
        }
        PanelBuilder panelBuilder = new PanelBuilder().setUser(user).setName(user.getTranslation("panel.title"));
        int i = index * PANEL_MAX_SIZE;
        for (; i < (index * PANEL_MAX_SIZE + PANEL_MAX_SIZE) && i < warps.size(); i++) {
            UUID owner = warps.get(i);
            if (!cachedWarps.containsKey(owner)) {
                cachedWarps.put(owner, getPanelItem(owner));
            }
            panelBuilder.addItem(cachedWarps.get(owner)); 
        }
        final int panelNum = index;
        // Add signs
        if (i < warps.size()) {
            // Next
            panelBuilder.addItem(new PanelItemBuilder()
                    .setName("Next")
                    .setIcon(new ItemStack(Material.SIGN))
                    .setClickHandler(new ClickHandler() {

                        @Override
                        public boolean onClick(User user, ClickType click) {
                            showWarpPanel(user, panelNum+1);
                            return true;
                        }

                    }).build());
        }
        if (i > PANEL_MAX_SIZE) {
            // Previous
            panelBuilder.addItem(new PanelItemBuilder()
                    .setName("Next")
                    .setIcon(new ItemStack(Material.SIGN))
                    .setClickHandler(new ClickHandler() {

                        @Override
                        public boolean onClick(User user, ClickType click) {
                            showWarpPanel(user, panelNum-1);
                            return true;
                        }

                    }).build());
        }
        panelBuilder.build();
    }
}
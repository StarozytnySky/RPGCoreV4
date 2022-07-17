package me.blutkrone.rpgcore.item;

import me.blutkrone.rpgcore.RPGCore;
import me.blutkrone.rpgcore.api.item.IItemDescriber;
import me.blutkrone.rpgcore.entity.entities.CorePlayer;
import me.blutkrone.rpgcore.hud.editor.index.EditorIndex;
import me.blutkrone.rpgcore.hud.editor.root.item.EditorCraftingRecipe;
import me.blutkrone.rpgcore.hud.editor.root.item.EditorItem;
import me.blutkrone.rpgcore.hud.editor.root.item.EditorModifier;
import me.blutkrone.rpgcore.hud.editor.root.item.EditorRefineRecipe;
import me.blutkrone.rpgcore.item.crafting.CoreCraftingRecipe;
import me.blutkrone.rpgcore.item.data.*;
import me.blutkrone.rpgcore.item.modifier.CoreModifier;
import me.blutkrone.rpgcore.item.refinement.CoreRefinerRecipe;
import me.blutkrone.rpgcore.item.styling.DefaultItemDescriptor;
import me.blutkrone.rpgcore.item.styling.ItemStylingRule;
import me.blutkrone.rpgcore.util.io.ConfigWrapper;
import me.blutkrone.rpgcore.util.io.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all itemization available across the core.
 *
 * @see ItemDataGeneric base interface for all data types
 * @see ItemDataModifier data specific to equipment items
 * @see ItemDataConsumable data specific to consumable items
 */
public class ItemManager implements Listener {

    // indexes used by the item manager
    private EditorIndex<CoreItem, EditorItem> item_index;
    private EditorIndex<CoreModifier, EditorModifier> modifier_index;
    private EditorIndex<CoreRefinerRecipe, EditorRefineRecipe> refine_index;
    private EditorIndex<CoreCraftingRecipe, EditorCraftingRecipe> craft_index;

    // factories used for tracking item data
    private Map<Class, Transformer<AbstractItemData>> data_factory = new HashMap<>();

    // rules which process item design
    private Map<String, ItemStylingRule> styling_rules = new HashMap<>();
    private IItemDescriber describer;

    public ItemManager() {
        this.item_index = new EditorIndex<>("item", EditorItem.class, EditorItem::new);
        this.modifier_index = new EditorIndex<>("modifier", EditorModifier.class, EditorModifier::new);
        this.refine_index = new EditorIndex<>("refine", EditorRefineRecipe.class, EditorRefineRecipe::new);
        this.craft_index = new EditorIndex<>("craft", EditorCraftingRecipe.class, EditorCraftingRecipe::new);

        this.data_factory.put(ItemDataGeneric.class, ItemDataGeneric::new);
        this.data_factory.put(ItemDataJewel.class, ItemDataJewel::new);
        this.data_factory.put(ItemDataDurability.class, ItemDataDurability::new);
        this.data_factory.put(ItemDataModifier.class, ItemDataModifier::new);

        try {
            ConfigWrapper configs = FileUtil.asConfigYML(FileUtil.file("item.yml"));
            configs.forEachUnder("style", (path, root) -> {
                this.styling_rules.put(path.toLowerCase(), new ItemStylingRule(root.getSection(path)));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.describer = new DefaultItemDescriptor();

        Bukkit.getPluginManager().registerEvents(this, RPGCore.inst());
    }

    /**
     * Define a custom implementation which generates the lore
     * of an item.
     *
     * @param describer which descriptor to use.
     */
    public void setDescriber(IItemDescriber describer) {
        this.describer = describer;
    }

    /**
     * Retrieve all rules used for stying items.
     *
     * @return all styling rules
     */
    public Map<String, ItemStylingRule> getStylingRules() {
        return styling_rules;
    }

    /**
     * Fetch a styling rule that controls item design to a given
     * extent.
     *
     * @param id which rule to look up
     * @return the rule that was found
     */
    public ItemStylingRule getStylingRule(String id) {
        return this.styling_rules.get(id);
    }

    /**
     * The index of all items known to RPGCore.
     *
     * @return the index of core items.
     */
    public EditorIndex<CoreItem, EditorItem> getItemIndex() {
        return item_index;
    }

    /**
     * The index of all modifiers known to RPGCore.
     *
     * @return the index of core modifiers.
     */
    public EditorIndex<CoreModifier, EditorModifier> getModifierIndex() {
        return modifier_index;
    }

    /**
     * The index of all refinement rules known to RPGCore.
     *
     * @return the index of refinement rules.
     */
    public EditorIndex<CoreRefinerRecipe, EditorRefineRecipe> getRefineIndex() {
        return refine_index;
    }

    /**
     * The index of all crafting rules known to RPGCore.
     *
     * @return the index of crafting rules.
     */
    public EditorIndex<CoreCraftingRecipe, EditorCraftingRecipe> getCraftIndex() {
        return craft_index;
    }

    /**
     * Core data holds information only required by RPGCore.
     *
     * @param item  the item to read the data from
     * @param clazz which data class to operate with
     * @return the item data that was retrieved
     */
    public <K extends AbstractItemData> K getItemData(ItemStack item, Class<K> clazz) {
        // air cannot have any core data
        if (item == null || item.getType().isAir()) {
            return null;
        }
        // meta is required for core data
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        // ensure that the factory actually exists
        Transformer<AbstractItemData> transformer = this.data_factory.get(clazz);
        if (transformer == null) {
            throw new NullPointerException("Data class '" + clazz + "' has no transformer!");
        }
        // read the raw bytes for this type of data
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String id = "rpgcore_" + clazz.getSimpleName().toLowerCase();
        byte[] retrieved = container.get(new NamespacedKey(RPGCore.inst(), id), PersistentDataType.BYTE_ARRAY);
        if (retrieved == null) {
            return null;
        }
        // attempt to factorize the data we expect
        try {
            return (K) transformer.transform(new ObjectInputStream(new ByteArrayInputStream(retrieved)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // fail-safe in case we couldn't create the data
        return null;
    }

    /**
     * Verify if a player is on their "combat" slot, i.E: The
     * left-most slot of their hotbar.
     *
     * @param player who are we checking
     * @param notify are we to notify the player
     * @return true if we are on our prime slot
     */
    public boolean isOnCombatSlot(Player player, boolean notify) {
        // check for prime slot being active
        if (player.getInventory().getHeldItemSlot() == 0) {
            return true;
        }
        // check if we need a notification to be sent
        if (!notify) {
            return false;
        }
        // inform the player about why they cannot do this
        CorePlayer core_player = RPGCore.inst().getEntityManager().getPlayer(player.getUniqueId());
        if (core_player != null) {
            String warning = RPGCore.inst().getLanguageManager().getTranslation("require_combat_slot");
            core_player.notify(ChatColor.DARK_RED, warning);
            if (core_player.getCooldown("rpgcore:require_combat_slot") <= 0) {
                player.sendTitle("", warning, 5, 30, 5);
                core_player.setCooldown("warn_about_combat_slot", 600);
            }
        }
        // inform about not being on our prime slot
        return false;
    }

    /**
     * Generate an appropriate description for the item.
     *
     * @param item the item we are to describe.
     */
    public void describe(ItemStack item) {
        if (item == null) {
            return;
        }
        if (item.getType().isAir()) {
            return;
        }
        if (!item.hasItemMeta()) {
            return;
        }

        try {
            this.describer.describe(item);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Something went wrong while describing an item ...");
        }
    }

    /**
     * Generate an appropriate description for the item.
     *
     * @param item         the item we are to describe.
     * @param unidentified scramble or hide information.
     */
    public void describe(ItemStack item, boolean unidentified) {
        if (item == null) {
            return;
        }
        if (item.getType().isAir()) {
            return;
        }
        if (!item.hasItemMeta()) {
            return;
        }

        try {
            this.describer.describe(item);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Something went wrong while describing an item ...");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    void on(InventoryClickEvent e) {
        // expect a shift+right click to open the item menu
        if (e.getClick() != ClickType.SHIFT_RIGHT) {
            return;
        }
        // must be in the basic inventory view
        if (e.getView().getTopInventory().getType() != InventoryType.CRAFTING) {
            return;
        }
        // must have a valid item to inspect
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().isAir()) {
            return;
        }
        // make sure we got a player to engage with
        CorePlayer player = RPGCore.inst().getEntityManager().getPlayer(e.getWhoClicked());
        if (player == null) {
            return;
        }
        // identify which menu should be associated with the item
        ItemDataJewel jewel_data = getItemData(e.getCurrentItem(), ItemDataJewel.class);
        if (jewel_data != null && jewel_data.getMaximumSockets() > 0) {
            player.getMenuPersistence().put("jewel_inspection", item);
            e.setCurrentItem(new ItemStack(Material.AIR));
            RPGCore.inst().getHUDManager().getJewelMenu().open((Player) e.getWhoClicked());
            return;
        }
    }

    /**
     * Useful transformer method.
     *
     * @param <K>
     */
    public interface Transformer<K> {
        K transform(ObjectInputStream stream) throws IOException;
    }
}

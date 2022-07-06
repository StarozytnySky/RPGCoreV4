package me.blutkrone.rpgcore.hud.editor.root;

import me.blutkrone.rpgcore.RPGCore;
import me.blutkrone.rpgcore.hud.editor.annotation.EditorCategory;
import me.blutkrone.rpgcore.hud.editor.annotation.EditorTooltip;
import me.blutkrone.rpgcore.hud.editor.annotation.value.EditorBoolean;
import me.blutkrone.rpgcore.hud.editor.annotation.value.EditorWrite;
import me.blutkrone.rpgcore.hud.editor.constraint.ItemConstraint;
import me.blutkrone.rpgcore.hud.editor.constraint.LanguageConstraint;
import me.blutkrone.rpgcore.hud.editor.constraint.StringConstraint;
import me.blutkrone.rpgcore.npc.CoreNPC;
import me.blutkrone.rpgcore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class EditorNPC implements IEditorRoot<CoreNPC> {

    @EditorCategory(icon = Material.CRAFTING_TABLE, info = "General")
    @EditorWrite(name = "Name", constraint = LanguageConstraint.class)
    @EditorTooltip(tooltip = {"Name to use for the NPC", "§cThis is a language code, NOT plaintext."})
    public String lc_name;
    @EditorWrite(name = "Skin", constraint = StringConstraint.class)
    @EditorTooltip(tooltip = {"MineSkin URL", "Paste full URL here!"})
    public String skin = "NOTHINGNESS";

    @EditorCategory(icon = Material.BOOKSHELF, info = "Miscellaneous")
    @EditorBoolean(name = "Staring")
    @EditorTooltip(tooltip = {"Stares at the player"})
    public boolean staring;

    @EditorCategory(icon = Material.IRON_CHESTPLATE, info = "Equipment")
    @EditorWrite(name = "Helmet", constraint = ItemConstraint.class)
    public String item_helmet = "NOTHINGNESS";
    @EditorWrite(name = "Chestplate", constraint = ItemConstraint.class)
    public String item_chestplate = "NOTHINGNESS";
    @EditorWrite(name = "Pants", constraint = ItemConstraint.class)
    public String item_pants = "NOTHINGNESS";
    @EditorWrite(name = "Boots", constraint = ItemConstraint.class)
    public String item_boots = "NOTHINGNESS";
    @EditorWrite(name = "Mainhand", constraint = ItemConstraint.class)
    public String item_mainhand = "NOTHINGNESS";
    @EditorWrite(name = "Offhand", constraint = ItemConstraint.class)
    public String item_offhand = "NOTHINGNESS";

    private transient File file;

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public void save() throws IOException {
        try (FileWriter fw = new FileWriter(file, Charset.forName("UTF-8"))) {
            RPGCore.inst().getGson().toJson(this, fw);
        }
    }

    @Override
    public CoreNPC build(String id) {
        return new CoreNPC(id, this);
    }

    @Override
    public ItemStack getPreview() {
        return ItemBuilder.of(Material.BOOKSHELF)
                .name("§aNPC")
                .appendLore("§fName: " + RPGCore.inst().getLanguageManager().getTranslation(this.lc_name))
                .appendLore("§fSkin: " + this.skin)
                .build();
    }

    @Override
    public String getName() {
        return RPGCore.inst().getLanguageManager().getTranslation(this.lc_name);
    }

    @Override
    public List<String> getInstruction() {
        List<String> instruction = new ArrayList<>();
        instruction.add("§fNPC");
        instruction.add("§fEntity meant for miscellaneous purposes. Use for Quests,");
        instruction.add("§fVendors, Quests, Background. Capable of basic combat, but");
        instruction.add("§fNOT designed for it.");
        return instruction;
    }
}
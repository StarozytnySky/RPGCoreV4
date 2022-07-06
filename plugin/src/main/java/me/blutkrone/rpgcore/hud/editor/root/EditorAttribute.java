package me.blutkrone.rpgcore.hud.editor.root;

import me.blutkrone.rpgcore.RPGCore;
import me.blutkrone.rpgcore.attribute.CoreAttribute;
import me.blutkrone.rpgcore.hud.editor.annotation.EditorTooltip;
import me.blutkrone.rpgcore.hud.editor.annotation.value.EditorBoolean;
import me.blutkrone.rpgcore.hud.editor.annotation.value.EditorList;
import me.blutkrone.rpgcore.hud.editor.annotation.value.EditorNumber;
import me.blutkrone.rpgcore.hud.editor.annotation.value.EditorWrite;
import me.blutkrone.rpgcore.hud.editor.bundle.IEditorBundle;
import me.blutkrone.rpgcore.hud.editor.constraint.AttributeConstraint;
import me.blutkrone.rpgcore.hud.editor.constraint.AttributeInheritConstraint;
import me.blutkrone.rpgcore.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Configurable information of an attribute.
 */
public class EditorAttribute implements IEditorRoot<CoreAttribute> {

    @EditorNumber(name = "Defaults")
    @EditorTooltip(tooltip = "Base amount of this attribute.")
    public double defaults = 0d;
    @EditorList(name = "Inheritance", constraint = AttributeInheritConstraint.class)
    @EditorTooltip(tooltip = "Inherit attributes from other collections.")
    public List<Inherited> inherited = new ArrayList<>();
    @EditorBoolean(name = "Deprecated")
    @EditorTooltip(tooltip = "Warn in case attribute is referenced.")
    public boolean deprecated = false;

    private transient File file;

    public EditorAttribute() {
    }

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
    public CoreAttribute build(String id) {
        return new CoreAttribute(id, this);
    }

    @Override
    public ItemStack getPreview() {
        return ItemBuilder.of(Material.BOOK)
                .name("Attribute")
                .lore("Defaults: " + this.defaults)
                .lore("Inherits: " + this.inherited.size())
                .build();
    }

    @Override
    public String getName() {
        return "Attribute";
    }

    @Override
    public List<String> getInstruction() {
        List<String> instruction = new ArrayList<>();
        instruction.add("§fAttribute");
        instruction.add("An attribute is always read of the entity");
        instruction.add("You can create new attributes as you wish");
        return instruction;
    }

    public static class Inherited implements IEditorBundle {
        @EditorWrite(name = "Source", constraint = AttributeConstraint.class)
        @EditorTooltip(tooltip = "First attribute to multiply")
        public String source = "NOTHINGNESS";
        @EditorWrite(name = "Multiplier", constraint = AttributeConstraint.class)
        @EditorTooltip(tooltip = "Second attribute to multiply")
        public String multiplier = "NOTHINGNESS";

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public ItemStack getPreview() {
            return ItemBuilder.of(Material.BOOK)
                    .name("Attribute Inheritance")
                    .lore("Source: " + this.source)
                    .lore("Multiplier: " + this.multiplier)
                    .build();
        }

        @Override
        public String getName() {
            return "Inheritance";
        }

        @Override
        public List<String> getInstruction() {
            return new ArrayList<>();
        }
    }
}

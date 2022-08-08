package me.blutkrone.rpgcore.hud.editor.constraint.reference.index;

import me.blutkrone.rpgcore.RPGCore;
import me.blutkrone.rpgcore.hud.editor.IEditorConstraint;

import java.util.ArrayList;
import java.util.List;

public class HotspotConstraint implements IEditorConstraint {

    @Override
    public List<String> getHint(String value) {
        value = value.toLowerCase();
        List<String> matched = new ArrayList<>();
        for (String key : RPGCore.inst().getNodeManager().getIndexHotspot().getKeys()) {
            if (key.startsWith(value)) {
                matched.add(key);
            }
        }
        return matched;
    }

    @Override
    public boolean isDefined(String value) {
        value = value.toLowerCase();

        try {
            return RPGCore.inst().getNodeManager().getIndexHotspot().has(value);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void extend(String value) {
        // create a the effect we want
        RPGCore.inst().getNodeManager().getIndexHotspot().get(value);
    }

    @Override
    public boolean canExtend() {
        return true;
    }

    @Override
    public String getConstraintAt(List container, int index) {
        return (String) container.get(index);
    }

    @Override
    public void setElementAt(List container, int index, String value) {
        container.set(index, value);
    }

    @Override
    public void addElement(List container, String value) {
        container.add(value);
    }

    @Override
    public Object asTypeOf(String value) {
        return value;
    }

    @Override
    public String toTypeOf(Object value) {
        return ((String) value);
    }

    @Override
    public List<String> getPreview(List<Object> list) {
        List<String> preview = new ArrayList<>();

        if (list.size() <= 16) {
            for (int i = 0; i < list.size(); i++) {
                preview.add(i + ": " + list.get(i));
            }
        } else {
            for (int i = 0; i < 16; i++) {
                preview.add(i + ": " + list.get(i));
            }

            preview.add("... And " + (list.size() - 16) + " More!");
        }

        return preview;
    }

    @Override
    public List<String> getInstruction() {
        List<String> instruction = new ArrayList<>();
        instruction.add("§fHotspot Node");
        instruction.add("§fUsed for quest tasks, will complete the task should you");
        instruction.add("§fStand close enough to the node");
        return instruction;
    }

    @Override
    public String getPreview(Object object) {
        return "Hotspot";
    }
}
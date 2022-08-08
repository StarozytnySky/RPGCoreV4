package me.blutkrone.rpgcore.hud.editor.constraint.reference.index;

import me.blutkrone.rpgcore.RPGCore;
import me.blutkrone.rpgcore.hud.editor.IEditorConstraint;

import java.util.ArrayList;
import java.util.List;

public class QuestConstraint implements IEditorConstraint {

    @Override
    public List<String> getHint(String value) {
        value = value.toLowerCase();
        List<String> matched = new ArrayList<>();
        for (String id : RPGCore.inst().getQuestManager().getIndexQuest().getKeys()) {
            if (id.startsWith(value)) {
                matched.add(id);
            }
        }
        return matched;
    }

    @Override
    public boolean isDefined(String value) {
        return RPGCore.inst().getQuestManager().getIndexQuest().has(value);
    }

    @Override
    public void extend(String value) {
        RPGCore.inst().getQuestManager().getIndexQuest().get(value);
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
    public String getPreview(Object object) {
        return String.valueOf(object);
    }

    @Override
    public List<String> getInstruction() {
        List<String> instruction = new ArrayList<>();
        instruction.add("§fQuest");
        instruction.add("§fA series of tasks for a player to complete. A quest is");
        instruction.add("§fCompleted once the rewards have been claimed.");
        instruction.add("§f");
        instruction.add("§fQuests are designed to be linear, if you wish to branch");
        instruction.add("§fA quest create two follow ups and make their completion");
        instruction.add("§fAbandon each other.");
        return instruction;
    }
}

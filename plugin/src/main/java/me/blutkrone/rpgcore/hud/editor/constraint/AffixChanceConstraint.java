package me.blutkrone.rpgcore.hud.editor.constraint;

import me.blutkrone.rpgcore.hud.editor.bundle.EditorAffixChance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AffixChanceConstraint extends AbstractMonoConstraint {
    @Override
    public void setElementAt(List container, int index, String value) {
        container.set(index, new EditorAffixChance());
    }

    @Override
    public void addElement(List container, String value) {
        container.add(new EditorAffixChance());
    }

    @Override
    public List<String> getPreview(List<Object> list) {
        Map<String, Double> multipliers = new HashMap<>();
        for (Object o : list) {
            EditorAffixChance eac = (EditorAffixChance) o;
            multipliers.merge(eac.tag, eac.weight, (a,b) -> a+b);
        }
        List<String> output = new ArrayList<>();
        multipliers.forEach((k, v) -> {
            output.add("Tag " + k + " Weight " + v);
        });
        return output;
    }

    @Override
    public List<String> getInstruction() {
        List<String> instruction = new ArrayList<>();
        instruction.add("§fAffix Chance");
        instruction.add("A chance for a certain affix tag to roll");
        instruction.add("Affixes without any tag cannot be rolled");
        return instruction;
    }

    @Override
    public String getPreview(Object object) {
        return "Affix Chance";
    }
}

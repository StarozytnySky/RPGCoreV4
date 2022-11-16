package me.blutkrone.rpgcore.npc.trait.impl;

import me.blutkrone.rpgcore.RPGCore;
import me.blutkrone.rpgcore.entity.entities.CorePlayer;
import me.blutkrone.rpgcore.hud.editor.bundle.npc.EditorQuestTrait;
import me.blutkrone.rpgcore.npc.CoreNPC;
import me.blutkrone.rpgcore.npc.trait.AbstractCoreTrait;
import me.blutkrone.rpgcore.quest.CoreQuest;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A trait intended for progression in a quest, this trait is treated
 * differently then other traits - allowing it to supersede any other
 * traits.
 */
public class CoreQuestTrait extends AbstractCoreTrait {

    // all quests which we do offer
    private Set<String> quests = new LinkedHashSet<>();

    public CoreQuestTrait(EditorQuestTrait editor) {
        super(editor);

        this.quests.addAll(editor.quests);
    }

    @Override
    public boolean isAvailable(CorePlayer player) {
        return super.isAvailable(player)
                && !getQuestAvailable(player).isEmpty();
    }

    /**
     * All quests which are offered by this NPC.
     *
     * @return offered quests.
     */
    public Set<String> getQuests() {
        return quests;
    }

    /**
     * A listing of all quests available to be claimed.
     *
     * @param player whose quests to check
     * @return a list of all quests to claim
     */
    public List<CoreQuest> getQuestAvailable(CorePlayer player) {
        List<CoreQuest> available = new ArrayList<>();

        // search all quests now up for picking
        for (String id : this.quests) {
            // ignore active quests
            if (player.getActiveQuestIds().contains(id)) {
                continue;
            }
            // ignore finished quests
            if (player.getCompletedQuests().contains(id)) {
                continue;
            }
            // ignore conditional quests
            CoreQuest quest = RPGCore.inst().getQuestManager().getIndexQuest().get(id);
            if (quest.canAcceptQuest(player)) {
                available.add(quest);
            }
        }

        return available;
    }

    @Override
    public void engage(Player player, CoreNPC npc) {
        // present a menu allowing to accept quests
        RPGCore.inst().getHUDManager().getQuestMenu().quests(this, player, npc);
    }
}
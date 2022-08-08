package me.blutkrone.rpgcore.hud.editor.bundle.quest;

import me.blutkrone.rpgcore.hud.editor.bundle.IEditorBundle;
import me.blutkrone.rpgcore.quest.reward.AbstractQuestReward;

public abstract class AbstractEditorQuestReward implements IEditorBundle {

    public AbstractEditorQuestReward() {
    }

    /**
     * Transform into run-time instance.
     *
     * @return runtime instance.
     */
    public abstract AbstractQuestReward build();
}

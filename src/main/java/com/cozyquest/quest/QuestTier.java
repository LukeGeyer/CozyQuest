package com.cozyquest.quest;

public enum QuestTier {
    NONE,
    TIER_1,
    TIER_2,
    TIER_3;

    public QuestTier next() {
        QuestTier[] values = values();
        int nextOrdinal = this.ordinal() + 1;
        if (nextOrdinal < values.length) {
            return values[nextOrdinal];
        }
        return this;
    }

    public boolean isMaxTier() {
        return this == TIER_3;
    }

    public String displayName() {
        return switch (this) {
            case NONE   -> "No Tier";
            case TIER_1 -> "Tier 1";
            case TIER_2 -> "Tier 2";
            case TIER_3 -> "Tier 3";
        };
    }
}

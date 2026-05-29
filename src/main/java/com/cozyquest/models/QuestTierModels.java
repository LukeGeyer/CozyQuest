package com.cozyquest.models;

public class QuestTierModels {
    
    public static QuestTier[] initializeQuestTiers() {

        //========================================================================
        //TIERS
        //========================================================================
        QuestTier tier1 = new QuestTier("Tier 1", 1, "The beginning of your journey.");
        QuestTier tier2 = new QuestTier("Tier 2", 2, "A step up in difficulty.");
        QuestTier tier3 = new QuestTier("Tier 3", 3, "The ultimate challenge.");

        //========================================================================
        //TIER 1
        //========================================================================
        Quest[] tier1Quests = {
            new Quest("Gather Wood", "Collect 10 pieces of wood."),
            new Quest("Build a Campfire", "Use the wood to build a campfire.")
        };
        tier1.initializeQuests(tier1Quests);

        //========================================================================
        //TIER 2
        //========================================================================
        Quest[] tier2Quests = {
            new Quest("Defeat Goblins", "Defeat 5 goblins in the forest."),
            new Quest("Find the Lost Amulet", "Locate the lost amulet in the cave.")
        };
        tier2.initializeQuests(tier2Quests);

        //========================================================================
        //TIER 3
        //========================================================================
        Quest[] tier3Quests = {
            new Quest("Slay the Dragon", "Defeat the dragon terrorizing the village."),
            new Quest("Rescue the Princess", "Rescue the princess from the tower.")
        };
        tier3.initializeQuests(tier3Quests);

        return new QuestTier[]{tier1, tier2, tier3};
    }

    public static QuestTier QuestTierGetQuestTierByLevel(int level) {
        QuestTier[] tiers = QuestTierModels.initializeQuestTiers();

        return tiers[level];
    }
}

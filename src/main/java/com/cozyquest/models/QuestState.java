package com.cozyquest.models;

public class QuestState {
    private final int maxShrineTier = 3;
    private boolean[] placedShrines = new boolean[maxShrineTier + 1];
    private QuestTier[] questTiers;

    public static void Initialize() {
        //Loads stored "placedShrines" from storage, if it exists. Otherwise, initializes to all false.
    }

    public int getGlobalShrineTier() {
        if (placedShrines[0] == false) {
            return 0;
        }

        for (int i = 1; i <= maxShrineTier;) {
            if (placedShrines[i] == false) {
                return i-1;
            } else {
                break;
            }
        }
        return maxShrineTier;
    }// Returns the the players current global shrine tier, which is the highest tier shrine they have placed. If they have not placed any shrines, returns 0.
    // If tier they have placed tier 1 - 4 and 6 but not 5, returns 4. If they have placed all 6, returns 6.

    public void registerShrinePlacement(int tier) {
        placedShrines[tier] = true;
    } 

    public void initializeTiers(QuestTier[] questTiers) {
        this.questTiers = questTiers;
    }
}

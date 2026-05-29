package com.cozyquest;

import com.cozyquest.quest.QuestTier;
import com.cozyquest.registry.ModBlocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class QuestManager {

    // --- Tier tracking ---
    private static final Map<UUID, QuestTier> playerTiers = new HashMap<>();

    // --- Quest completion ---
    private static final Set<UUID> completedDiamondQuest = new HashSet<>();

    // --- Spawn / book tracking ---
    private static boolean shrineHasBeenPlaced = false;
    private static final Set<UUID> receivedStarterKit = new HashSet<>();
    private static final Set<UUID> bookUpdated = new HashSet<>();

    // -------------------------------------------------------
    // Tier access
    // -------------------------------------------------------

    public static QuestTier getPlayerTier(ServerPlayer player) {
        return playerTiers.getOrDefault(player.getUUID(), QuestTier.NONE);
    }

    // -------------------------------------------------------
    // Player join - give starter kit, update book if needed
    // -------------------------------------------------------

    public static void onPlayerJoin(ServerPlayer player) {
        UUID id = player.getUUID();
        if (!receivedStarterKit.contains(id)) {
            receivedStarterKit.add(id);
            player.getInventory().add(createQuestBook(shrineHasBeenPlaced));
            player.getInventory().add(new ItemStack(ModBlocks.SHRINE_TIER_1));
        } else if (shrineHasBeenPlaced && !bookUpdated.contains(id)) {
            updatePlayerBook(player);
        }
    }

    // -------------------------------------------------------
    // Shrine placement - called when any shrine is placed
    // -------------------------------------------------------

    public static void onShrinePlaced(MinecraftServer server) {
        if (shrineHasBeenPlaced) return;
        shrineHasBeenPlaced = true;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            updatePlayerBook(player);
        }
    }

    // -------------------------------------------------------
    // Book helpers
    // -------------------------------------------------------

    private static void updatePlayerBook(ServerPlayer player) {
        UUID id = player.getUUID();
        if (bookUpdated.contains(id)) return;
        bookUpdated.add(id);

        // Search main inventory (36 slots) for the CozyQuests book and replace it
        List<ItemStack> slots = player.getInventory().getNonEquipmentItems();
        for (int i = 0; i < slots.size(); i++) {
            ItemStack stack = slots.get(i);
            if (stack.is(Items.WRITTEN_BOOK)) {
                WrittenBookContent content = stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
                if (content != null && content.title().raw().equals("CozyQuests")) {
                    slots.set(i, createQuestBook(true));
                    player.sendSystemMessage(Component.literal("[CozyQuest] Your quest book has been updated!"));
                    return;
                }
            }
        }
        // Book not found (e.g. dropped it) - give a fresh one
        player.getInventory().add(createQuestBook(true));
        player.sendSystemMessage(Component.literal("[CozyQuest] Your quest book has been updated!"));
    }

    private static ItemStack createQuestBook(boolean shrineUnlocked) {
        List<Filterable<Component>> pages = new ArrayList<>();

        if (!shrineUnlocked) {
            pages.add(Filterable.passThrough(
                Component.literal(
                    "Welcome to CozyQuest!\n\n" +
                    "You have been given a Tier 1 Shrine Block.\n\n" +
                    "Your first quest is to build a shrine and place the shrine block in it to unlock your Tier 1 quests!"
                )
            ));
        } else {
            pages.add(Filterable.passThrough(
                Component.literal(
                    "=== Tier 1 Quests ===\n\n" +
                    "[ ] Acquire a bed\n\n" +
                    "[ ] Full set of iron armor\n\n" +
                    "[ ] Collect 4 different saplings\n\n" +
                    "[ ] Have 6 unique flowers"
                )
            ));
            pages.add(Filterable.passThrough(
                Component.literal(
                    "=== Tier 1 Quests ===\n\n" +
                    "[ ] Obtain a stack of food\n\n" +
                    "[ ] Build a house\n\n" +
                    "[ ] Build a farm:\nchickens, cows, sheep,\nwheat, potatoes, carrots,\nbeets, sugar cane\n(obtain a stack each)"
                )
            ));
        }

        WrittenBookContent content = new WrittenBookContent(
            Filterable.passThrough("CozyQuests"),
            "CozyQuest",
            0,
            pages,
            true
        );

        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, content);
        return book;
    }

    // -------------------------------------------------------
    // Shrine interaction
    // -------------------------------------------------------

    public static void onShrineInteract(ServerPlayer player, QuestTier shrineTier) {
        QuestTier currentTier = getPlayerTier(player);

        if (shrineTier.ordinal() == currentTier.ordinal() + 1) {
            if (hasCompletedCurrentTierQuests(player, currentTier)) {
                playerTiers.put(player.getUUID(), shrineTier);
                player.sendSystemMessage(Component.literal(
                    "[CozyQuest] Shrine activated! " + shrineTier.displayName() + " quests unlocked!"));
            } else {
                player.sendSystemMessage(Component.literal(
                    "[CozyQuest] Complete your current quests before activating this shrine."));
            }
        } else if (shrineTier.ordinal() <= currentTier.ordinal()) {
            player.sendSystemMessage(Component.literal(
                "[CozyQuest] You have already unlocked this shrine's tier."));
        } else {
            player.sendSystemMessage(Component.literal(
                "[CozyQuest] You must unlock previous tiers first!"));
        }
    }

    // -------------------------------------------------------
    // Quest completion checks per tier
    // -------------------------------------------------------

    private static boolean hasCompletedCurrentTierQuests(ServerPlayer player, QuestTier tier) {
        return switch (tier) {
            case NONE   -> true;
            case TIER_1 -> completedDiamondQuest.contains(player.getUUID());
            case TIER_2 -> true; // TODO
            case TIER_3 -> true; // TODO
        };
    }

    // -------------------------------------------------------
    // Per-tick quest checks
    // -------------------------------------------------------

    public static void checkQuests(ServerPlayer player) {
        UUID playerId = player.getUUID();

        if (!completedDiamondQuest.contains(playerId)) {
            if (player.getInventory().hasAnyOf(Set.of(Items.DIAMOND))) {
                completedDiamondQuest.add(playerId);
                player.sendSystemMessage(Component.literal(
                    "[CozyQuest] Quest complete: Got a Diamond! Well done, " + player.getName().getString() + "!"));
            }
        }
    }
}

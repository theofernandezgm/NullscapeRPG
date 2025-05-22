package com.theofernandez.rpg.engine;

import com.theofernandez.rpg.game.Player;
import java.util.Random;

public class PlayerStatEngine {

    private final Random random = new Random();

    public void processPlayerStateChanges(Player player) {
        if (player == null) {
            System.err.println("[STAT ENGINE] Error: Player object is null. Cannot process state changes.");
            return;
        }

        if (!player.isAlive()) {
            player.resetTemporaryModifiers(); // Clear any lingering effects
            return;
        }

        // 1. Handle Adrenaline Decay & Reset General Temporary Modifiers at the start of each cycle
        if (player.getAdrenalineRushTurns() > 0) {
            player.setAdrenalineRushTurns(player.getAdrenalineRushTurns() - 1);
            if (player.getAdrenalineRushTurns() == 0) {
                player.resetTemporaryModifiers(); // Adrenaline wore off, clear all temp modifiers
                player.setFatiguePercent(player.getFatiguePercent() + 50); // Adrenaline crash
                logEffect(player, "adrenaline rush wears off, feeling an intense crash!");
            }
            // If adrenaline is still active, its specific modifiers (set in applyEmotionalAndMentalEffects)
            // will persist or be reapplied. General reset is skipped.
        } else {
            // No adrenaline active, so reset all general temporary modifiers for a clean slate this cycle.
            player.resetTemporaryModifiers();
        }

        // 2. Apply Effects in a Logical Order
        // These methods will either set temporary player stat modifiers
        // or directly adjust the player's base stats via their setters (which handle clamping).
        applyEnvironmentalAndPhysicalEffects(player);
        applyOrganIntegrityEffects(player);
        applyNeedsEffects(player);
        applyHealthEffects(player);
        applyEmotionalAndMentalEffects(player); // This can trigger adrenaline and its specific modifiers

        // 3. Update Derived Summary States like Mood
        updatePlayerMood(player);

        // 4. Final sanity check for player status
        if (player.getHealth() <= 0 && player.isAlive()) {
            player.setAlive(false); // Ensure isAlive flag is correct if health dropped to 0
            logEffect(player, "has succumbed due to status deterioration during this cycle.");
        }
    }

    private void applyEnvironmentalAndPhysicalEffects(Player player) {
        int tempNormal = Player.DEFAULT_PLAYER_BODY_TEMPERATURE;
        int tempCurrent = player.getBodyTemperature();
        int tempDeviation = tempCurrent - tempNormal;

        if (tempDeviation > 7 || tempDeviation < -10) { // Severe Hyper/Hypothermia
            player.setHealth(player.getHealth() - 5);
            player.setConscious(false);
            logEffect(player, "suffers from extreme body temperature, losing consciousness!");
            // Apply large penalties to temporary modifiers
            player.setTempDexterityModifier(player.getTempDexterityModifier() - 500000);
            player.setTempAgilityModifier(player.getTempAgilityModifier() - 500000);
            player.setTempSpeedModifier(player.getTempSpeedModifier() - 500000);
            player.setTempIntelligenceModifier(player.getTempIntelligenceModifier() - 500000);
            player.setTempFocusModifier(player.getTempFocusModifier() - (Player.MAX_STAT_VALUE_PERCENTAGE / 2)); // Max penalty to focus
            player.setComfort(Player.MIN_STAT_VALUE_PERCENTAGE);
            player.setStress(Player.MAX_STAT_VALUE_PERCENTAGE);
        } else if (tempDeviation > 4 || tempDeviation < -5) { // Significant Hyper/Hypothermia
            player.setHealth(player.getHealth() - 2);
            logEffect(player, "is struggling with body temperature.");
            player.setTempDexterityModifier(player.getTempDexterityModifier() - 300000);
            player.setTempAgilityModifier(player.getTempAgilityModifier() - 300000);
            player.setTempIntelligenceModifier(player.getTempIntelligenceModifier() - 300000);
            player.setTempFocusModifier(player.getTempFocusModifier() - (Player.MAX_STAT_VALUE_PERCENTAGE / 3));
            player.setComfort(modifyPlayerAttributeByPercentage(player.getComfort(), -0.60, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 25); // Direct modification, setter clamps
        } else if (tempDeviation > 2 || tempDeviation < -2) { // Mild Hyper/Hypothermia
            logEffect(player, "feels uncomfortable due to temperature.");
            player.setTempFocusModifier(player.getTempFocusModifier() - (Player.MAX_STAT_VALUE_PERCENTAGE / 5));
            player.setComfort(modifyPlayerAttributeByPercentage(player.getComfort(), -0.25, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 10);
        }
    }

    private void applyOrganIntegrityEffects(Player player) {
        double neuralHealthRatio = (double) player.getNeural() / Player.MAX_STAT_VALUE_PERCENTAGE;

        if (neuralHealthRatio < 0.15) { // Critically low neural health
            logEffect(player, "suffers severe neurological distress!");
            if (random.nextDouble() < 0.25) { // 25% chance of seizure
                player.setExperiencingSeizure(true);
            }

            if (player.isExperiencingSeizure()) {
                logEffect(player, "is experiencing a seizure!");
                player.setConscious(false);
                // Max penalties during seizure, directly setting temp modifiers to large negative values
                player.setTempDexterityModifier(-Player.MAX_STAT_VALUE);
                player.setTempAgilityModifier(-Player.MAX_STAT_VALUE);
                player.setTempSpeedModifier(-Player.MAX_STAT_VALUE);
                player.setTempIntelligenceModifier(-Player.MAX_STAT_VALUE);
                player.setTempFocusModifier(-Player.MAX_STAT_VALUE_PERCENTAGE);
                player.setTempMemoryModifier(-Player.MAX_STAT_VALUE_PERCENTAGE);

                player.setNeural(modifyPlayerAttributeByPercentage(player.getNeural(), -0.05, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE)); // Further neural damage
                player.setHealth(player.getHealth() - 5);
            } else { // Critically low neural health, but no seizure this cycle
                // Apply significant penalties to temporary modifiers based on current base stats
                player.setTempDexterityModifier(player.getTempDexterityModifier() + calculateDeltaFromBaseStat(player.getDexterity(), -0.70));
                player.setTempAgilityModifier(player.getTempAgilityModifier() + calculateDeltaFromBaseStat(player.getAgility(), -0.70));
                player.setTempIntelligenceModifier(player.getTempIntelligenceModifier() + calculateDeltaFromBaseStat(player.getIntelligence(), -0.50));
                player.setTempFocusModifier(player.getTempFocusModifier() + calculateDeltaFromBaseStat(player.getFocus(), -0.60));
                player.setTempMemoryModifier(player.getTempMemoryModifier() + calculateDeltaFromBaseStat(player.getMemory(), -0.60));
            }
            player.setStress(player.getStress() + 30);
            player.setHappiness(modifyPlayerAttributeByPercentage(player.getHappiness(), -0.40, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));

            if (player.getConcussionCount() > 2 && neuralHealthRatio < 0.20) { // Permanent damage from concussions
                logEffect(player, "Past concussions and low neural health cause lasting damage.");
                player.setIntelligence(player.getIntelligence() - (int) (Player.MAX_STAT_VALUE * 0.001)); // Small permanent reduction to base Intelligence
                player.setMemory(modifyPlayerAttributeByPercentage(player.getMemory(), -0.01, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE)); // Small permanent reduction to base Memory
                player.setConcussionCount(0); // Reset counter after applying permanent damage
            }
        } else if (neuralHealthRatio < 0.40) { // Impaired neural health
            logEffect(player, "feels a bit disoriented and mentally foggy.");
            player.setTempIntelligenceModifier(player.getTempIntelligenceModifier() + calculateDeltaFromBaseStat(player.getIntelligence(), -0.15));
            player.setTempFocusModifier(player.getTempFocusModifier() + calculateDeltaFromBaseStat(player.getFocus(), -0.25));
            player.setTempMemoryModifier(player.getTempMemoryModifier() + calculateDeltaFromBaseStat(player.getMemory(), -0.20));
        }

        // Cardiovascular & Respiratory Effects
        double cardioHealthRatio = (double) player.getCardiovascular() / Player.MAX_STAT_VALUE_PERCENTAGE;
        double respHealthRatio = (double) player.getRespiratory() / Player.MAX_STAT_VALUE_PERCENTAGE;

        if (cardioHealthRatio < 0.3 || respHealthRatio < 0.3) {
            logEffect(player, "heart is racing and breathing is difficult.");
            player.setEndurance(player.getEndurance() - (int) (Player.MAX_STAT_VALUE * 0.003)); // Directly impacts base Endurance
            player.setFatiguePercent(player.getFatiguePercent() + 10);
            player.setStress(player.getStress() + 10);
            if (cardioHealthRatio < 0.1 || respHealthRatio < 0.1) { // Critical failure
                player.setHealth(player.getHealth() - 2);
                logEffect(player, "is critically struggling to breathe/circulate blood!");
            }
        }

        // Digestive Effects
        if (player.getDigestive() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.2) {
            player.setHunger(modifyPlayerAttributeByPercentage(player.getHunger(), -0.15, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE)); // Becomes effectively hungrier
            logEffect(player, "digestive system is compromised, feeling hungrier.");
        }

        // Immune System Effects
        if (player.getImmune() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.2 && player.getHygiene() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.2) {
            player.setHealth(player.getHealth() - 1);
            logEffect(player, "Feels unwell due to poor hygiene and weak immunity.");
        }
    }

    private void applyNeedsEffects(Player player) {
        int baseWillpower = player.getWillpower(); // Use current base willpower (0-500 scale) for resistance calculations

        double hungerRatio = (double) player.getHunger() / Player.MAX_STAT_VALUE_PERCENTAGE; // Higher is better
        if (hungerRatio < 0.05) { // Critically Starving
            player.setHealth(player.getHealth() - 3);
            applyPenaltiesForSevereNeed(player, baseWillpower, 0.50, "starvation", true);
            player.setComfort(modifyPlayerAttributeByPercentage(player.getComfort(), -0.60, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 30);
        } else if (hungerRatio < 0.20) { // Very Hungry
            applyPenaltiesForSevereNeed(player, baseWillpower, 0.25, "extreme hunger", true);
            player.setComfort(modifyPlayerAttributeByPercentage(player.getComfort(), -0.25, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 15);
        } else if (hungerRatio < 0.40) { // Hungry
            applyPenaltiesForSevereNeed(player, baseWillpower, 0.10, "hunger", false); // Affects fewer stats
            player.setStress(player.getStress() + 5);
        }

        double thirstRatio = (double) player.getThirst() / Player.MAX_STAT_VALUE_PERCENTAGE; // Higher is better
        if (thirstRatio < 0.05) { // Critically Dehydrated
            player.setHealth(player.getHealth() - 5);
            applyPenaltiesForSevereNeed(player, baseWillpower, 0.65, "critical dehydration", true);
            player.setComfort(modifyPlayerAttributeByPercentage(player.getComfort(), -0.70, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 40);
            if (random.nextDouble() < 0.20) { // 20% chance of losing consciousness
                player.setConscious(false);
                logEffect(player, "loses consciousness from dehydration!");
            }
        } else if (thirstRatio < 0.20) { // Very Thirsty
            applyPenaltiesForSevereNeed(player, baseWillpower, 0.35, "dehydration", true);
            player.setComfort(modifyPlayerAttributeByPercentage(player.getComfort(), -0.35, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 20);
        } else if (thirstRatio < 0.40) { // Thirsty
            applyPenaltiesForSevereNeed(player, baseWillpower, 0.15, "mild thirst", false);
            player.setStress(player.getStress() + 7);
        }

        double fatigueLevelRatio = (double) player.getFatiguePercent() / Player.MAX_STAT_VALUE_PERCENTAGE; // Higher is worse
        if (fatigueLevelRatio > 0.95) { // Utterly Exhausted
            applyPenaltiesForSevereNeed(player, baseWillpower, 0.50, "utter exhaustion", true);
            player.setSleep(Player.MIN_STAT_VALUE_PERCENTAGE); // Forces restedness (Sleep stat) to minimum
            player.setHappiness(modifyPlayerAttributeByPercentage(player.getHappiness(), -0.40, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 25);
            if (random.nextDouble() < 0.10) {
                player.setConscious(false);
                logEffect(player, "collapses from exhaustion!");
            }
        } else if (fatigueLevelRatio > 0.70) { // Very Fatigued
            applyPenaltiesForSevereNeed(player, baseWillpower, 0.25, "severe fatigue", true);
            player.setSleep(modifyPlayerAttributeByPercentage(player.getSleep(), -0.20, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE)); // Reduces restedness
            player.setHappiness(modifyPlayerAttributeByPercentage(player.getHappiness(), -0.20, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 10);
        }

        double sleepLevelRatio = (double) player.getSleep() / Player.MAX_STAT_VALUE_PERCENTAGE; // Lower is worse (less rested)
        if (sleepLevelRatio < 0.10) { // Critically Sleep Deprived
            // Apply penalties to temporary modifiers, calculated from base stats
            player.setTempFocusModifier(player.getTempFocusModifier() + calculateDeltaFromBaseStat(player.getFocus(), -0.50));
            player.setTempIntelligenceModifier(player.getTempIntelligenceModifier() + calculateDeltaFromBaseStat(player.getIntelligence(), -0.30));
            player.setTempMemoryModifier(player.getTempMemoryModifier() + calculateDeltaFromBaseStat(player.getMemory(), -0.40));
            // Directly affect base Willpower
            player.setWillpower(modifyPlayerAttributeByPercentage(player.getWillpower(), -0.40, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setHappiness(modifyPlayerAttributeByPercentage(player.getHappiness(), -0.30, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 50);
            player.setFatiguePercent(player.getFatiguePercent() + 50); // Increases fatigue
            player.setComfort(modifyPlayerAttributeByPercentage(player.getComfort(), -0.80, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            logEffect(player, "is critically sleep deprived.");
            if (random.nextDouble() < 0.15) {
                player.setConscious(false);
                logEffect(player, "passes out from sleep deprivation!");
            }
        } else if (sleepLevelRatio < 0.30) { // Very Sleep Deprived
            player.setTempFocusModifier(player.getTempFocusModifier() + calculateDeltaFromBaseStat(player.getFocus(), -0.25));
            player.setTempIntelligenceModifier(player.getTempIntelligenceModifier() + calculateDeltaFromBaseStat(player.getIntelligence(), -0.15));
            player.setTempMemoryModifier(player.getTempMemoryModifier() + calculateDeltaFromBaseStat(player.getMemory(), -0.20));
            player.setWillpower(modifyPlayerAttributeByPercentage(player.getWillpower(), -0.20, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setHappiness(modifyPlayerAttributeByPercentage(player.getHappiness(), -0.15, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 20);
            player.setFatiguePercent(player.getFatiguePercent() + 20);
            player.setComfort(modifyPlayerAttributeByPercentage(player.getComfort(), -0.50, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            logEffect(player, "is very sleep deprived.");
        } else if (sleepLevelRatio < 0.50) { // Sleep Deprived
            player.setTempFocusModifier(player.getTempFocusModifier() + calculateDeltaFromBaseStat(player.getFocus(), -0.10));
            player.setTempIntelligenceModifier(player.getTempIntelligenceModifier() + calculateDeltaFromBaseStat(player.getIntelligence(), -0.05));
            player.setTempMemoryModifier(player.getTempMemoryModifier() + calculateDeltaFromBaseStat(player.getMemory(), -0.10));
            player.setWillpower(modifyPlayerAttributeByPercentage(player.getWillpower(), -0.10, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setHappiness(modifyPlayerAttributeByPercentage(player.getHappiness(), -0.10, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 10);
            player.setFatiguePercent(player.getFatiguePercent() + 10);
            player.setComfort(modifyPlayerAttributeByPercentage(player.getComfort(), -0.20, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            logEffect(player, "is sleep deprived.");
        }
    }

    private void applyPenaltiesForSevereNeed(Player player, int baseWillpower, double severityFactor, String cause, boolean affectAllPhysical) {
        logEffect(player, "is suffering from " + cause + ".");

        double willpowerRatio = (double) baseWillpower / Player.MAX_STAT_VALUE_PERCENTAGE;
        double resistanceFromWillpower = willpowerRatio * 0.35; // Max 35% reduction in penalty severity from willpower
        double effectiveSeverityFactor = severityFactor * (1.0 - resistanceFromWillpower);
        effectiveSeverityFactor = Math.max(severityFactor * 0.50, effectiveSeverityFactor); // Penalty is at least 50% of its original severity

        if (affectAllPhysical) {
            // Penalties are applied to temporary modifiers. These modifiers accumulate based on effects within one cycle.
            player.setTempStrengthModifier(player.getTempStrengthModifier() - (int) (Player.MAX_STAT_VALUE * effectiveSeverityFactor * 0.5)); // Assuming MAX_STAT_VALUE for strength scale
            player.setTempAgilityModifier(player.getTempAgilityModifier() - (int) (Player.MAX_STAT_VALUE * effectiveSeverityFactor));
            player.setTempSpeedModifier(player.getTempSpeedModifier() - (int) (Player.MAX_STAT_VALUE * effectiveSeverityFactor));
            player.setTempDexterityModifier(player.getTempDexterityModifier() - (int) (Player.MAX_STAT_VALUE * effectiveSeverityFactor));
        }
        // Endurance (base stat, 0-MAX_STAT_VALUE scale) takes a direct hit, less resisted by willpower as it's very physical
        player.setEndurance(player.getEndurance() - (int) (Player.MAX_STAT_VALUE * severityFactor * 0.1)); // Original severity for direct endurance hit

        // Focus (temporary modifier, based on base Focus 0-MAX_STAT_VALUE_PERCENTAGE scale)
        player.setTempFocusModifier(player.getTempFocusModifier() - (int) (player.getFocus() * effectiveSeverityFactor));

        // Willpower itself (base stat, 0-MAX_STAT_VALUE_PERCENTAGE scale) erodes due to hardship
        player.setWillpower(modifyPlayerAttributeByPercentage(player.getWillpower(), -severityFactor * 0.5, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
    }

    private void applyHealthEffects(Player player) {
        double healthRatio = (double) player.getHealth() / Player.DEFAULT_PLAYER_HEALTH; // 0.0 to 1.0
        int baseWillpower = player.getWillpower();

        if (healthRatio <= 0) {
            if (player.isAlive()) { // Should have been set by player.setHealth() but good to ensure
                player.setAlive(false);
            }
            logEffect(player, "has no health remaining.");
            return; // No further effects if not alive
        }

        // This specific log might be redundant if player.setConscious(false) is called elsewhere for low health.
        // if (!player.isConscious() && healthRatio < 0.05 && player.getHealth() > 0) {
        // logEffect(player, "is barely clinging to life and unconscious.");
        // }

        if (healthRatio < 0.15) { // Critically Injured (<15 HP)
            applyPenaltiesForSevereNeed(player, baseWillpower, 0.70, "critical injuries", true);
            player.setComfort(modifyPlayerAttributeByPercentage(player.getComfort(), -0.60, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setHappiness(modifyPlayerAttributeByPercentage(player.getHappiness(), -0.50, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 50);
            if (!player.isConscious() && player.getHealth() > 0) { // If health is critical AND already lost consciousness
                logEffect(player, "is critically injured and unconscious.");
            } else if (player.getHealth() <= Player.DEFAULT_PLAYER_HEALTH * 0.05 && player.isConscious()){ // Health very low, risk of losing consciousness
                player.setConscious(false); // Knock out if not already
                logEffect(player, "loses consciousness due to critical injuries.");
            }
        } else if (healthRatio < 0.40) { // Significantly Injured (<40 HP)
            applyPenaltiesForSevereNeed(player, baseWillpower, 0.30, "significant injuries", true);
            player.setComfort(modifyPlayerAttributeByPercentage(player.getComfort(), -0.25, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 20);
        } else if (healthRatio < 0.70) { // Moderately Injured (<70 HP)
            applyPenaltiesForSevereNeed(player, baseWillpower, 0.10, "injuries", false); // Milder penalties
            player.setComfort(modifyPlayerAttributeByPercentage(player.getComfort(), -0.10, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setStress(player.getStress() + 10);
        }
    }

    private void applyEmotionalAndMentalEffects(Player player) {
        int baseWillpower = player.getWillpower();

        // Adrenaline Rush due to low confidence (fear)
        if (player.getConfidence() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.10 && player.getAdrenalineRushTurns() == 0) {
            logEffect(player, "is overcome by intense fear, triggering an adrenaline rush!");
            player.setStress(modifyPlayerAttributeByPercentage(player.getStress(), 0.50, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            player.setAdrenalineRushTurns(3 + random.nextInt(3)); // Adrenaline lasts a few cycles

            // Adrenaline applies temporary modifiers. These are *additive* to any existing temp modifiers from this cycle.
            // The base stats are used to calculate the magnitude of the adrenaline bonus/penalty.
            player.setTempStrengthModifier(player.getTempStrengthModifier() + player.getUpperBodyStrength() / 2); // Bonus from base stat
            player.setTempAgilityModifier(player.getTempAgilityModifier() + player.getAgility() / 2);
            player.setTempSpeedModifier(player.getTempSpeedModifier() + player.getSpeed() / 2);
            player.setTempPerceptionModifier(player.getTempPerceptionModifier() + player.getSight() / 3); // Bonus from base Sight

            player.setTempIntelligenceModifier(player.getTempIntelligenceModifier() - player.getIntelligence() / 2); // Penalty from base stat
            player.setTempFocusModifier(player.getTempFocusModifier() - player.getFocus() / 2);
            player.setTempMemoryModifier(player.getTempMemoryModifier() - player.getMemory() / 2);

            player.setHappiness(Player.MIN_STAT_VALUE_PERCENTAGE); // Fear state associated with adrenaline trigger
        }

        if (player.getAdrenalineRushTurns() > 0) {
            player.setFatiguePercent(player.getFatiguePercent() + 10); // Adrenaline is physically taxing each cycle it's active
        }

        // General Stress Effects (only if not currently in an adrenaline rush, as adrenaline has its own mental effects)
        double stressRatio = (double) player.getStress() / Player.MAX_STAT_VALUE_PERCENTAGE;
        if (player.getAdrenalineRushTurns() == 0) { // Only apply these if not adrenalized
            if (stressRatio > 0.85) {
                logEffect(player, "is under extreme stress.");
                double willpowerFactor = (double) baseWillpower / Player.MAX_STAT_VALUE_PERCENTAGE;
                double penaltyMultiplier = 0.35 * (1.0 - (willpowerFactor * 0.5)); // Willpower resists up to 50% of this penalty factor
                player.setTempFocusModifier(player.getTempFocusModifier() - (int) (player.getFocus() * penaltyMultiplier));
                player.setTempIntelligenceModifier(player.getTempIntelligenceModifier() - (int) (player.getIntelligence() * penaltyMultiplier * 0.5));
                player.setTempMemoryModifier(player.getTempMemoryModifier() - (int) (player.getMemory() * penaltyMultiplier));
                player.setWillpower(modifyPlayerAttributeByPercentage(player.getWillpower(), -0.30, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE)); // Direct erosion of base willpower
                player.setHappiness(modifyPlayerAttributeByPercentage(player.getHappiness(), -0.40, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            } else if (stressRatio > 0.60) {
                logEffect(player, "is feeling significantly stressed.");
                double willpowerFactor = (double) baseWillpower / Player.MAX_STAT_VALUE_PERCENTAGE;
                double penaltyMultiplier = 0.15 * (1.0 - (willpowerFactor * 0.4));
                player.setTempFocusModifier(player.getTempFocusModifier() - (int) (player.getFocus() * penaltyMultiplier));
                player.setTempMemoryModifier(player.getTempMemoryModifier() - (int) (player.getMemory() * penaltyMultiplier * 0.5));
                player.setWillpower(modifyPlayerAttributeByPercentage(player.getWillpower(), -0.10, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
                player.setHappiness(modifyPlayerAttributeByPercentage(player.getHappiness(), -0.20, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
            }
        }

        // Boredom Effects
        if (player.getBoredom() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.80) {
            logEffect(player, "is very bored, finding it hard to concentrate.");
            player.setTempFocusModifier(player.getTempFocusModifier() - (int) (player.getFocus() * 0.05)); // Small temporary hit to focus
            player.setHappiness(modifyPlayerAttributeByPercentage(player.getHappiness(), -0.05, Player.MIN_STAT_VALUE_PERCENTAGE, Player.MAX_STAT_VALUE_PERCENTAGE));
        }
    }

    private void updatePlayerMood(Player player) {
        if (!player.isAlive()) {
            player.setMood(null); // Or a specific "DECEASED" mood
            return;
        }

        // Highest priority moods based on critical states
        if (player.isExperiencingSeizure()) { player.setMood(Player.Mood.CRITICAL); return; }
        if (!player.isConscious()) { player.setMood(Player.Mood.CRITICAL); return; } // Or more specific "UNCONSCIOUS"
        if (player.getHealth() <= Player.DEFAULT_PLAYER_HEALTH * 0.05 && player.getHealth() > 0) { player.setMood(Player.Mood.CRITICAL); return; }
        if (player.getAdrenalineRushTurns() > 0) { player.setMood(Player.Mood.ADRENALIZED); return; }

        int tempDeviation = player.getBodyTemperature() - Player.DEFAULT_PLAYER_BODY_TEMPERATURE;
        if (tempDeviation > 7 || tempDeviation < -10) { player.setMood(Player.Mood.CRITICAL); return; }

        // Next level of urgency: dire needs (can override less severe moods)
        if (player.getHunger() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.05 || player.getThirst() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.05) {
            player.setMood(Player.Mood.STRESSED); // Could be "DESPERATE" or similar if added
            return; // Return to ensure this dire need mood takes precedence
        }
        if (player.getSleep() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.10 && player.getFatiguePercent() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.90) {
            player.setMood(Player.Mood.TIRED); // Or "EXHAUSTED"
            return;
        }

        // General mood logic based on emotional and stress levels
        // Order matters: more impactful negative states checked before positive ones.
        if (player.getConfidence() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.15) { player.setMood(Player.Mood.FEARFUL); }
        else if (player.getStress() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.80) { player.setMood(Player.Mood.STRESSED); }
        else if (player.getHappiness() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.20) { player.setMood(Player.Mood.SAD); }
        else if (player.getBoredom() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.80) { player.setMood(Player.Mood.BORED); }
        // Positive states
        else if (player.getHappiness() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.80 &&
                player.getStress() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.20 &&
                player.getFatiguePercent() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.30) { player.setMood(Player.Mood.ENERGETIC); }
        else if (player.getHappiness() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.70) { player.setMood(Player.Mood.HAPPY); }
        // Functional states
        else if (player.getEffectiveFocus() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.70 && // Check *effective* focus for being focused
                player.getStress() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.40) { player.setMood(Player.Mood.FOCUSED); }
        else if (player.getFatiguePercent() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.65) { player.setMood(Player.Mood.TIRED); }
        // Default/Fallback to Neutral
        else {
            Player.Mood currentMood = player.getMood();
            boolean isCurrentlyNegativeOrTired = currentMood == Player.Mood.SAD || currentMood == Player.Mood.STRESSED ||
                    currentMood == Player.Mood.ANGRY || currentMood == Player.Mood.FEARFUL ||
                    currentMood == Player.Mood.TIRED || currentMood == Player.Mood.BORED ||
                    currentMood == Player.Mood.CRITICAL; // Critical counts as a state to recover from

            if (isCurrentlyNegativeOrTired) {
                // Conditions for recovering to NEUTRAL from a negative/tired/critical state
                if (player.getHappiness() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.45 &&
                        player.getStress() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.45 &&
                        player.getFatiguePercent() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.45 &&
                        player.getHealth() > Player.DEFAULT_PLAYER_HEALTH * 0.50 && // Ensure some stability
                        !player.isExperiencingSeizure() && player.isConscious()) { // Not in an immediate critical episode
                    player.setMood(Player.Mood.NEUTRAL);
                }
                // Otherwise, the negative/tired/critical mood persists if conditions for neutral aren't met.
            } else if (currentMood == null) { // If mood was somehow null (e.g., initial state before first processing)
                player.setMood(Player.Mood.NEUTRAL);
            }
            // If current mood is already NEUTRAL or a positive one not overridden by above rules, it can persist.
        }
    }

    private void logEffect(Player player, String effectDescription) {
        String playerName = (player != null && player.getName() != null) ? player.getName() : "System";
        System.out.println("[STAT ENGINE] " + playerName + ": " + effectDescription);
    }

    /**
     * Modifies an attribute's current value by a percentage and returns the new value,
     * clamped between specified min and max.
     * @param currentAttributeValue The current value of the attribute.
     * @param percentageModifier The modifier (e.g., -0.10 for 10% reduction, 0.20 for 20% increase).
     * @param minValue The minimum allowed value for the attribute.
     * @param maxValue The maximum allowed value for the attribute.
     * @return The new attribute value, after modification and clamping.
     */
    private int modifyPlayerAttributeByPercentage(int currentAttributeValue, double percentageModifier, int minValue, int maxValue) {
        int change = (int) (currentAttributeValue * percentageModifier);
        int newValue = currentAttributeValue + change;
        return Math.max(minValue, Math.min(newValue, maxValue));
    }

    /**
     * Calculates the change (delta) to be applied to a temporary modifier,
     * based on a percentage of the stat's *base* value.
     * @param baseStatValue The base value of the stat (e.g., player.getIntelligence(), player.getFocus()).
     * @param percentage The percentage to calculate the change from (e.g., -0.20 for a -20% effect).
     * @return The calculated change (can be positive or negative), intended to be added to a temporary modifier.
     */
    private int calculateDeltaFromBaseStat(int baseStatValue, double percentage) {
        return (int) (baseStatValue * percentage);
    }
}
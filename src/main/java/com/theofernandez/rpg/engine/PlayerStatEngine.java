package com.theofernandez.rpg.engine;

import com.theofernandez.rpg.game.Player;
import java.util.Random;

public class PlayerStatEngine {

    private Random random = new Random();

    public void processPlayerStateChanges(Player player) {
        if (!player.isAlive()) {
            player.resetTemporaryModifiers(); // Clear any lingering effects if somehow called
            return;
        }

        // Reset temporary modifiers at the start of each full update cycle,
        // then re-apply them based on current conditions.
        // More sophisticated effects might have durations handled elsewhere (e.g., adrenaline).
        if (player.getAdrenalineRushTurns() > 0) {
            player.setAdrenalineRushTurns(player.getAdrenalineRushTurns() - 1);
            if (player.getAdrenalineRushTurns() == 0) {
                player.resetTemporaryModifiers(); // Adrenaline wore off
                player.setFatiguePercent(player.getFatiguePercent() + 50); // Adrenaline crash
                System.out.println(player.getName() + "'s adrenaline rush wears off, feeling an intense crash!");
            }
        } else {
            // Only reset general modifiers if not in adrenaline rush, as adrenaline sets its own.
            player.resetTemporaryModifiers();
        }


        // Apply effects in a logical order
        applyEnvironmentalAndPhysicalEffects(player); // Temperature, existing injuries
        applyOrganIntegrityEffects(player);
        applyNeedsEffects(player); // Hunger, thirst, fatigue, sleep, hygiene etc.
        applyEmotionalAndMentalEffects(player); // Stress, happiness, confidence, fear -> adrenaline

        // Update derived summary states like Mood
        updatePlayerMood(player);
    }

    // Helper for stat modification, now always applies to the *base* stat via Player's setters
    // Effective stats are read via player.getEffective...()
    // The 'percentageModifier' is of the base stat's current value or a fixed amount.
    // For temporary modifiers, we'll use player.setTemp...Modifier()
    private void applyStatChange(Player player, String statName, int change) {
        // This is a placeholder for a more robust way to set stats by name if needed
        // For now, we directly call setters.
        // Example: if (statName.equals("Focus")) player.setFocus(player.getFocus() + change);
    }


    private void applyEnvironmentalAndPhysicalEffects(Player player) {
        int willpower = player.getWillpower(); // Effective willpower could be used for resistance

        // --- Body Temperature ---
        int tempNormal = Player.DEFAULT_PLAYER_BODY_TEMPERATURE;
        int tempCurrent = player.getBodyTemperature();
        int tempDeviation = tempCurrent - tempNormal;

        if (tempDeviation > 7 || tempDeviation < -10) { // Severe Hyper/Hypothermia (e.g., >44C or <27C)
            player.setHealth(player.getHealth() - 5);
            player.setConscious(false);
            logEffect(player, "suffers from extreme body temperature, losing consciousness!");
            // Severe motor and cognitive impairment
            player.setTempDexterityModifier(-50 * (Player.MAX_STAT_VALUE / 100)); // Assuming dexterity is 0-100 for this modifier
            player.setTempAgilityModifier(-50 * (Player.MAX_STAT_VALUE / 100));
            player.setTempSpeedModifier(-50 * (Player.MAX_STAT_VALUE / 100));
            player.setTempFocusModifier(-70 * (Player.MAX_STAT_VALUE_PERCENTAGE / 100));
            player.setTempIntelligenceModifier(-50 * (Player.MAX_STAT_VALUE / 100));
            player.setComfort(Player.MIN_STAT_VALUE_PERCENTAGE);
            player.setStress(Player.MAX_STAT_VALUE_PERCENTAGE);
        } else if (tempDeviation > 4 || tempDeviation < -5) { // Significant Hyper/Hypothermia (e.g., >41C or <32C)
            player.setHealth(player.getHealth() - 2);
            logEffect(player, "is struggling with body temperature.");
            player.setTempDexterityModifier(-30 * (Player.MAX_STAT_VALUE / 100));
            player.setTempAgilityModifier(-30 * (Player.MAX_STAT_VALUE / 100));
            player.setTempFocusModifier(-40 * (Player.MAX_STAT_VALUE_PERCENTAGE / 100));
            player.setComfort(applyModifier(player.getComfort(), -0.60));
            player.setStress(player.getStress() + 25);
        } else if (tempDeviation > 2 || tempDeviation < -2) { // Mild Hyper/Hypothermia
            logEffect(player, "feels uncomfortable due to temperature.");
            player.setTempFocusModifier(-20 * (Player.MAX_STAT_VALUE_PERCENTAGE / 100));
            player.setComfort(applyModifier(player.getComfort(), -0.25));
            player.setStress(player.getStress() + 10);
        }
    }


    private void applyOrganIntegrityEffects(Player player) {
        // --- Neural Health ---
        double neuralHealthPercent = (double) player.getNeural() / Player.MAX_STAT_VALUE_PERCENTAGE;
        if (neuralHealthPercent < 0.15) { // Critically low neural health
            logEffect(player, "suffers severe neurological distress!");
            player.setExperiencingSeizure(random.nextDouble() < 0.25); // 25% chance of seizure per update cycle
            if (player.isExperiencingSeizure()) {
                logEffect(player, "is experiencing a seizure!");
                player.setConscious(false); // Lose consciousness during seizure
                player.setTempDexterityModifier(-Player.MAX_STAT_VALUE); // Cannot control body
                player.setTempAgilityModifier(-Player.MAX_STAT_VALUE);
                player.setTempSpeedModifier(-Player.MAX_STAT_VALUE);
                player.setTempFocusModifier(-Player.MAX_STAT_VALUE_PERCENTAGE);
                // Seizures can cause further neural damage or health damage
                player.setNeural(applyModifier(player.getNeural(), -0.05)); // 5% further neural damage
                player.setHealth(player.getHealth() - 5);
            } else {
                // Even without seizure, severe motor and cognitive impairment
                player.setTempDexterityModifier(applyModifierToTemp(player.getEffectiveDexterity(), -0.70));
                player.setTempAgilityModifier(applyModifierToTemp(player.getEffectiveAgility(), -0.70));
                player.setTempFocusModifier(applyModifierToTemp(player.getEffectiveFocus(), -0.60));
                player.setTempIntelligenceModifier(applyModifierToTemp(player.getEffectiveIntelligence(), -0.50));
            }
            player.setStress(player.getStress() + 30);
            player.setHappiness(applyModifier(player.getHappiness(), -0.40));
            // Check for permanent damage from concussions if neuro health is critically low
            if (player.getConcussionCount() > 2 && neuralHealthPercent < 0.20) {
                logEffect(player, "Past concussions and low neural health seem to have caused lasting damage.");
                player.setIntelligence(applyModifier(player.getIntelligence(), -0.01)); // Permanent 1% base reduction
                player.setMemory(applyModifier(player.getMemory(), -0.01)); // Permanent 1% base reduction
                player.setConcussionCount(0); // Reset counter after applying permanent damage to avoid repeated immediate hits
            }
        } else if (neuralHealthPercent < 0.40) { // Impaired neural health
            logEffect(player, "feels a bit disoriented and mentally foggy.");
            player.setTempFocusModifier(applyModifierToTemp(player.getEffectiveFocus(), -0.25));
            player.setTempIntelligenceModifier(applyModifierToTemp(player.getEffectiveIntelligence(), -0.15));
            player.setTempMemoryModifier(applyModifierToTemp(player.getEffectiveMemory(), -0.20)); // Assuming Player has tempMemoryModifier
        }

        // --- Cardiovascular & Respiratory ---
        double cardioHealth = (double) player.getCardiovascular() / Player.MAX_STAT_VALUE_PERCENTAGE;
        double respHealth = (double) player.getRespiratory() / Player.MAX_STAT_VALUE_PERCENTAGE;
        if (cardioHealth < 0.3 || respHealth < 0.3) {
            logEffect(player, "feels their heart racing and breathing is difficult.");
            player.setEndurance(applyModifier(player.getEndurance(), -0.30)); // Base endurance penalty
            player.setFatiguePercent(player.getFatiguePercent() + 10); // Extra fatigue
            player.setStress(player.getStress() + 10);
            if (cardioHealth < 0.1 || respHealth < 0.1) { // Critical
                player.setHealth(player.getHealth() - 2); // Health drain
                logEffect(player, "is critically struggling to breathe/circulate blood!");
            }
        }
        // ... other organ effects ...
        // Digestive: if low, food might provide fewer benefits (requires food system) or hunger decays faster.
        if (player.getDigestive() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.2) {
            player.setHunger(applyModifier(player.getHunger(), -0.15)); // Effective hunger increases faster
        }
        // Immune: if low, higher chance of sickness from environment/low hygiene (requires sickness system)
        // For now, if immune is low and hygiene is bad, direct small health penalty.
        if (player.getImmune() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.2 && player.getHygiene() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.2) {
            player.setHealth(player.getHealth() - 1);
            logEffect(player, "Feels unwell due to poor hygiene and weak immunity.");
        }
    }


    private void applyNeedsEffects(Player player) {
        int willpower = player.getEffectiveWillpower(); // Use effective willpower for resistance

        // --- Hunger ---
        double hungerPercent = (double) player.getHunger() / Player.MAX_STAT_VALUE_PERCENTAGE;
        if (hungerPercent < 0.05) { // Critically Starving
            player.setHealth(player.getHealth() - 3); // Increased health loss
            setPenaltiesForSevereNeed(player, willpower, 0.50, "starvation");
            player.setComfort(applyModifier(player.getComfort(), -0.60));
            player.setStress(player.getStress() + 30);
        } else if (hungerPercent < 0.20) { // Very Hungry
            setPenaltiesForSevereNeed(player, willpower, 0.25, "hunger");
            player.setComfort(applyModifier(player.getComfort(), -0.25));
        } else if (hungerPercent < 0.40) { // Hungry
            setPenaltiesForSevereNeed(player, willpower, 0.10, "mild hunger", false); // Apply only to focus/endurance
        }

        // --- Thirst ---
        double thirstPercent = (double) player.getThirst() / Player.MAX_STAT_VALUE_PERCENTAGE;
        if (thirstPercent < 0.05) { // Critically Dehydrated
            player.setHealth(player.getHealth() - 5); // Severe health loss
            setPenaltiesForSevereNeed(player, willpower, 0.65, "critical dehydration");
            player.setComfort(applyModifier(player.getComfort(), -0.70));
            player.setStress(player.getStress() + 40);
            player.setConscious(random.nextDouble() > 0.8); // 20% chance of losing consciousness
        } else if (thirstPercent < 0.20) { // Very Thirsty
            setPenaltiesForSevereNeed(player, willpower, 0.35, "dehydration");
            player.setComfort(applyModifier(player.getComfort(), -0.35));
        } else if (thirstPercent < 0.40) { // Thirsty
            setPenaltiesForSevereNeed(player, willpower, 0.15, "mild thirst", false);
        }

        // ... (Fatigue, Sleep, Hygiene, Bladder, Comfort with willpower resistance on mental stats) ...
        // --- Fatigue ---
        double fatigueRatio = (double) player.getFatiguePercent() / Player.MAX_STAT_VALUE_PERCENTAGE;
        if (fatigueRatio > 0.95) { // Utterly Exhausted
            setPenaltiesForSevereNeed(player, willpower, 0.50, "utter exhaustion");
            player.setSleep(Player.MIN_STAT_VALUE_PERCENTAGE); // Force sleep need to max
            player.setHappiness(applyModifier(player.getHappiness(), -0.40));
            player.setStress(player.getStress() + 25);
            if(random.nextDouble() < 0.1) { player.setConscious(false); logEffect(player, "collapses from exhaustion!");}
        } else if (fatigueRatio > 0.70) { // Very Fatigued
            setPenaltiesForSevereNeed(player, willpower, 0.25, "severe fatigue");
            player.setSleep(applyModifier(player.getSleep(), -0.20));
            player.setHappiness(applyModifier(player.getHappiness(), -0.20));
        }

        // --- Sleep (Restedness) ---
        double sleepPercent = (double) player.getSleep() / Player.MAX_STAT_VALUE_PERCENTAGE;
        if (sleepPercent < 0.10) { // Critically Sleep Deprived
            player.setTempFocusModifier(applyModifierToTemp(player.getEffectiveFocus(), -0.50));
            player.setTempIntelligenceModifier(applyModifierToTemp(player.getEffectiveIntelligence(), -0.30));
            player.setWillpower(applyModifier(player.getWillpower(), -0.40)); // Base willpower hit
            player.setHappiness(applyModifier(player.getHappiness(), -0.30));
            player.setStress(player.getStress() + 50);
            player.setFatiguePercent(player.getFatiguePercent() + 50);
            player.setComfort(applyModifier(player.getComfort(), -0.80));
            if(random.nextDouble() < 0.15) { player.setConscious(false); logEffect(player, "collapses from sleep deprivation!");}
        } else if (sleepPercent < 0.30) { // Very Sleep Deprived
            player.setTempFocusModifier(applyModifierToTemp(player.getEffectiveFocus(), -0.25));
            player.setTempIntelligenceModifier(applyModifierToTemp(player.getEffectiveIntelligence(), -0.15));
            player.setWillpower(applyModifier(player.getWillpower(), -0.20)); // Base willpower hit
            player.setHappiness(applyModifier(player.getHappiness(), -0.15));
            player.setStress(player.getStress() + 20);
            player.setFatiguePercent(player.getFatiguePercent() + 20);
            player.setComfort(applyModifier(player.getComfort(), -0.50));
        } else if (sleepPercent < 0.50) { // Sleep Deprived
            player.setTempFocusModifier(applyModifierToTemp(player.getEffectiveFocus(), -0.10));
            player.setTempIntelligenceModifier(applyModifierToTemp(player.getEffectiveIntelligence(), -0.05));
            player.setWillpower(applyModifier(player.getWillpower(), -0.10)); // Base willpower hit
            player.setHappiness(applyModifier(player.getHappiness(), -0.10));
            player.setStress(player.getStress() + 10);
            player.setFatiguePercent(player.getFatiguePercent() + 10);
            player.setComfort(applyModifier(player.getComfort(), -0.20));
        } else if (sleepPercent < 0.70) { // Tired
            player.setTempFocusModifier(applyModifierToTemp(player.getEffectiveFocus(), -0.05));
            player.setTempIntelligenceModifier(applyModifierToTemp(player.getEffectiveIntelligence(), -0.05));
            player.setWillpower(applyModifier(player.getWillpower(), -0.05)); // Base willpower hit
            player.setHappiness(applyModifier(player.getHappiness(), -0.05));
            player.setStress(player.getStress() + 5);
            player.setFatiguePercent(player.getFatiguePercent() + 5);
            player.setComfort(applyModifier(player.getComfort(), -0.10));
        } else if (sleepPercent < 0.90) { // Rested
            // No penalties, but still a bit of stress if sleep is low
            player.setStress(player.getStress() + 2);
        } else if (sleepPercent < 1.0) { // Fully Rested
            // No penalties, but still a bit of stress if sleep is low
            player.setStress(player.getStress() + 1);
        }
        // --- Sleep Deprivation Effects ---
        if (sleepPercent < 0.50) {
            player.setTempFocusModifier(applyModifierToTemp(player.getEffectiveFocus(), -0.20));
            player.setTempIntelligenceModifier(applyModifierToTemp(player.getEffectiveIntelligence(), -0.10));
            player.setWillpower(applyModifier(player.getWillpower(), -0.15)); // Base willpower hit
            player.setHappiness(applyModifier(player.getHappiness(), -0.25));
            player.setStress(player.getStress() + 30);
            player.setFatiguePercent(player.getFatiguePercent() + 30);
            player.setComfort(applyModifier(player.getComfort(), -0.20));
            if(random.nextDouble() < 0.05) { player.setConscious(false); logEffect(player, "passes out from lack of sleep!");}
        } // ... other tiers for sleep ...
    }

    // Helper for common penalties, now using temp modifiers resisted by willpower for some
    private void setPenaltiesForSevereNeed(Player player, int willpower, double severityFactor, String cause, boolean affectAllPhysical) {
        logEffect(player, "is suffering from " + cause + ".");
        // Physical stats directly affected, willpower doesn't resist physical frailty as much
        if (affectAllPhysical) {
            player.setTempStrengthModifier(applyModifierToTemp(player.getEffectiveUpperBodyStrength(), -severityFactor)); // Using a helper for temp mods
            player.setTempAgilityModifier(applyModifierToTemp(player.getEffectiveAgility(), -severityFactor));
            player.setTempSpeedModifier(applyModifierToTemp(player.getEffectiveSpeed(), -severityFactor));
            player.setTempDexterityModifier(applyModifierToTemp(player.getEffectiveDexterity(), -severityFactor));
        }
        player.setEndurance(applyModifier(player.getEndurance(), -(severityFactor + 0.1))); // Base endurance hit

        // Mental stats resisted by willpower
        player.setTempFocusModifier(calculateModifiedStatForTemp(player.getEffectiveFocus(), -severityFactor, willpower));
        player.setWillpower(applyModifier(player.getWillpower(), -severityFactor * 0.5)); // Willpower itself erodes
    }
    private void setPenaltiesForSevereNeed(Player player, int willpower, double severityFactor, String cause) {
        setPenaltiesForSevereNeed(player, willpower, severityFactor, cause, true);
    }


    // Helper for applying percentage modifier to temporary modifier fields
    private int applyModifierToTemp(int currentEffectiveStat, double percentageOfCurrentEffective) {
        return (int) (currentEffectiveStat * percentageOfCurrentEffective);
    }
    // Helper for applying percentage modifier resisted by willpower to temporary modifier fields
    private int calculateModifiedStatForTemp(int currentEffectiveStat, double percentageModifier, int willpower) {
        double actualModifier = percentageModifier;
        if (percentageModifier < 0) { // If it's a penalty
            double willpowerFactor = (double) willpower / Player.MAX_STAT_VALUE_PERCENTAGE;
            double resistance = willpowerFactor * 0.35;
            actualModifier = percentageModifier * (1.0 - resistance);
        }
        return (int) (currentEffectiveStat * actualModifier);
    }


    private void applyHealthEffects(Player player) {
        // ... (largely as before, but can use setPenaltiesForSevereNeed or more specific temp modifiers)
        double healthRatio = (double) player.getHealth() / Player.DEFAULT_PLAYER_HEALTH;
        int willpower = player.getEffectiveWillpower();

        if (healthRatio < 0.01 && player.isAlive()) {
            player.setConscious(false);
            logEffect(player, "is on the brink of death and loses consciousness.");
        }

        if (healthRatio < 0.15) { // Critically Injured
            setPenaltiesForSevereNeed(player, willpower, 0.70, "critical injuries");
            player.setComfort(applyModifier(player.getComfort(), -0.60));
            player.setHappiness(applyModifier(player.getHappiness(), -0.50));
            player.setStress(player.getStress() + 50);
        } else if (healthRatio < 0.40) { // Significantly Injured
            setPenaltiesForSevereNeed(player, willpower, 0.30, "significant injuries");
            player.setComfort(applyModifier(player.getComfort(), -0.25));
            player.setStress(player.getStress() + 20);
        }
    }


    private void applyEmotionalEffects(Player player) {
        int willpower = player.getEffectiveWillpower();

        // --- Fear (Critically Low Confidence) -> Adrenaline Rush ---
        if (player.getConfidence() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.10 && player.getAdrenalineRushTurns() == 0) {
            logEffect(player, "is overcome by intense fear, triggering an adrenaline rush!");
            player.setMood(Player.Mood.FEARFUL); // Then ADRENALIZED
            player.setStress(applyModifier(player.getStress(), 0.50)); // Massive stress increase
            player.setAdrenalineRushTurns(3 + random.nextInt(3)); // Lasts 3-5 "turns" or update cycles

            // Adrenaline effects:
            player.setTempStrengthModifier(player.getUpperBodyStrength() / 2); // +50% of base strength
            player.setTempAgilityModifier(player.getAgility() / 2);    // +50% of base agility
            player.setTempSpeedModifier(player.getSpeed() / 2);      // +50% of base speed
            player.setTempPerceptionModifier(player.getSight() / 3); // +33% to perception (sight/hearing)

            player.setTempIntelligenceModifier(-(player.getIntelligence() / 2)); // -50% to base intelligence (tunnel vision)
            player.setTempFocusModifier(-(player.getFocus() / 2));          // -50% to base focus
            player.setHappiness(Player.MIN_STAT_VALUE_PERCENTAGE); // Fear isn't happy
        }

        if (player.getAdrenalineRushTurns() > 0) {
            player.setMood(Player.Mood.ADRENALIZED);
            // Adrenaline also causes rapid fatigue increase as a direct effect while active
            player.setFatiguePercent(player.getFatiguePercent() + 10);
        }

        // --- Other general stress effects ---
        double stressRatio = (double) player.getStress() / Player.MAX_STAT_VALUE_PERCENTAGE;
        if (stressRatio > 0.85 && player.getAdrenalineRushTurns() == 0) { // If not already adrenalized
            player.setTempFocusModifier(calculateModifiedStatForTemp(player.getEffectiveFocus(), -0.35, willpower));
            player.setWillpower(applyModifier(player.getWillpower(), -0.30));
            player.setHappiness(applyModifier(player.getHappiness(), -0.40));
            // ... (other stress effects from before)
        }
        // ... (other happiness, boredom effects as before, using effective willpower for resistance where appropriate)
    }

    private void updatePlayerMood(Player player) {
        // ... (Mood logic as before, but can now also check for ADRENALIZED, FEARFUL, etc.
        // and also check player.isExperiencingSeizure())
        if (!player.isAlive()) { player.setMood(null); return; } // Or a specific "deceased" mood

        if (player.isExperiencingSeizure()) {
            player.setMood(Player.Mood.CRITICAL); // Or a "Seizing" mood
            return;
        }
        if (player.getAdrenalineRushTurns() > 0) {
            player.setMood(Player.Mood.ADRENALIZED);
            return;
        }
        if (player.getHealth() < Player.DEFAULT_PLAYER_HEALTH * 0.10) {
            player.setMood(Player.Mood.CRITICAL);
        } else if (player.getConfidence() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.15) {
            player.setMood(Player.Mood.FEARFUL);
            // ... rest of the mood logic from response #42, which was already quite comprehensive ...
            // Ensure it checks the most critical conditions first.
        } else if (player.getBodyTemperature() < 30 || player.getBodyTemperature() > 42) { // Extreme temp
            player.setMood(Player.Mood.CRITICAL);
        } else if (player.getHunger() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.05 ||
                player.getThirst() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.05) {
            player.setMood(Player.Mood.STRESSED); // Desperate
        } else if (player.getSleep() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.10 &&
                player.getFatiguePercent() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.90) {
            player.setMood(Player.Mood.TIRED); // Exhausted
        } else if (player.getStress() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.8) {
            player.setMood(Player.Mood.STRESSED);
        } else if (player.getHappiness() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.2) {
            player.setMood(Player.Mood.SAD);
        } else if (player.getBoredom() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.8) {
            player.setMood(Player.Mood.BORED);
        } else if (player.getHappiness() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.8 && player.getStress() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.2 && player.getFatiguePercent() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.3) {
            player.setMood(Player.Mood.ENERGETIC);
        } else if (player.getHappiness() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.7) {
            player.setMood(Player.Mood.HAPPY);
        } else if (player.getFocus() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.7 && player.getStress() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.4) {
            player.setMood(Player.Mood.FOCUSED);
        } else if (player.getFatiguePercent() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.65) {
            player.setMood(Player.Mood.TIRED);
        } else {
            if (player.getMood() != Player.Mood.NEUTRAL && player.getMood() != Player.Mood.HAPPY && player.getMood() != Player.Mood.ENERGETIC && player.getMood() != Player.Mood.FOCUSED) {
                if (player.getHappiness() > Player.MAX_STAT_VALUE_PERCENTAGE * 0.4 && player.getStress() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.5 && player.getFatiguePercent() < Player.MAX_STAT_VALUE_PERCENTAGE * 0.5) {
                    player.setMood(Player.Mood.NEUTRAL);
                }
            } else if (player.getMood() == null) { // Initial state if not set
                player.setMood(Player.Mood.NEUTRAL);
            }
        }
    }

    // Helper for logging effects (can be expanded)
    private void logEffect(Player player, String effectDescription) {
        // This could later integrate with an event system or specific UI logging.
        System.out.println("[STAT ENGINE] " + player.getName() + ": " + effectDescription);
        // You might want to also push some of these to the in-game eventLogArea
        // via a callback or by having GameWorldViewController pass a Consumer<String> to the engine.
    }
    // Helper for applying a percentage modifier to a stat
    private int applyModifier(int currentStat, double percentageModifier) {
        return (int) (currentStat * (1.0 + percentageModifier));
    }
}
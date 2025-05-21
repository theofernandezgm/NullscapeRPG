package com.theofernandez.rpg.game;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Player implements Serializable {

    private static final long serialVersionUID = 3L; // Incremented due to new fields

    // ... (All existing constants and Enums, including expanded Mood enum) ...
    public static final int MAX_STAT_VALUE = 1000000;
    public static final int MIN_STAT_VALUE = 0;
    public static final int MAX_STAT_VALUE_PERCENTAGE = 500;
    public static final int MIN_STAT_VALUE_PERCENTAGE = 0;

    public static enum Sex implements Serializable { MALE, FEMALE }
    public static enum BodyType implements Serializable { ECTOMORPH, MESOMORPH, ENDOMORPH }
    public static enum BloodType implements Serializable { O_POSITIVE, O_NEGATIVE, A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, AB_POSITIVE, AB_NEGATIVE, UNKNOWN }
    public static enum Mood implements Serializable { NEUTRAL, HAPPY, SAD, ANGRY, STRESSED, BORED, TIRED, ENERGETIC, FOCUSED, DISTRACTED, FEARFUL, ADRENALIZED, CRITICAL, NUMB /* Expanded */ }


    // --- Default Player Values (as before) ---
    // (Ensure these are comprehensive and make sense for starting values)
    public static final int DEFAULT_PLAYER_AGE = 22;
    public static final String DEFAULT_PLAYER_NAME = "Player";
    public static final Sex DEFAULT_PLAYER_SEX = Sex.MALE;
    public static final String DEFAULT_PLAYER_ETHNICITY = "Caucasian";
    public static final double DEFAULT_PLAYER_HEIGHT = 1.75;
    public static final BloodType DEFAULT_PLAYER_BLOOD_TYPE = BloodType.O_POSITIVE;
    public static final double DEFAULT_PLAYER_WEIGHT = 70.0;
    public static final BodyType DEFAULT_PLAYER_BODY_TYPE = BodyType.MESOMORPH;
    public static final int DEFAULT_PLAYER_LUCK = 50; // MAX_STAT_VALUE scale
    public static final int DEFAULT_PLAYER_BEAUTY = 50; // MAX_STAT_VALUE scale
    public static final int DEFAULT_PLAYER_INTELLIGENCE = 50; // MAX_STAT_VALUE scale
    public static final int DEFAULT_PLAYER_UPPER_BODY_STRENGTH = 50; // MAX_STAT_VALUE scale
    public static final int DEFAULT_PLAYER_LOWER_BODY_STRENGTH = 50; // MAX_STAT_VALUE scale
    public static final int DEFAULT_PLAYER_ENDURANCE = 50; // MAX_STAT_VALUE scale
    public static final int DEFAULT_PLAYER_AGILITY = 50; // MAX_STAT_VALUE scale
    public static final int DEFAULT_PLAYER_SPEED = 50; // MAX_STAT_VALUE scale
    public static final int DEFAULT_PLAYER_DEXTERITY = 50; // MAX_STAT_VALUE scale
    public static final int DEFAULT_PLAYER_HEALTH = 100; // Max health for display purposes, uses MAX_STAT_VALUE for clamping
    public static final int DEFAULT_PLAYER_BODY_TEMPERATURE = 37;
    public static final int DEFAULT_PLAYER_BLOOD_PRESSURE = 120;
    public static final int DEFAULT_PLAYER_HEART_RATE = 70;
    public static final int DEFAULT_PLAYER_CARDIOVASCULAR = Player.MAX_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_RESPIRATORY = Player.MAX_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_NEURAL = Player.MAX_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_DIGESTIVE = Player.MAX_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_IMMUNE = Player.MAX_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_SIGHT = Player.MAX_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_HEARING = Player.MAX_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_SMELL_TASTE = Player.MAX_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_HUNGER = (int) (Player.MAX_STAT_VALUE_PERCENTAGE * 0.8);
    public static final int DEFAULT_PLAYER_THIRST = (int) (Player.MAX_STAT_VALUE_PERCENTAGE * 0.8);
    public static final int DEFAULT_PLAYER_FATIGUE_PERCENT = Player.MIN_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_SLEEP = Player.MAX_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_HYGIENE = Player.MAX_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_BLADDER = Player.MIN_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_COMFORT = Player.MAX_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_MEMORY = (int) (Player.MAX_STAT_VALUE_PERCENTAGE * 0.5);
    public static final int DEFAULT_PLAYER_FOCUS = (int) (Player.MAX_STAT_VALUE_PERCENTAGE * 0.5);
    public static final int DEFAULT_PLAYER_WILLPOWER = (int) (Player.MAX_STAT_VALUE_PERCENTAGE * 0.5);
    public static final int DEFAULT_PLAYER_VERBAL = (int) (Player.MAX_STAT_VALUE_PERCENTAGE * 0.5);
    public static final int DEFAULT_PLAYER_MATHS = (int) (Player.MAX_STAT_VALUE_PERCENTAGE * 0.5);
    public static final int DEFAULT_PLAYER_KNOWLEDGE = (int) (Player.MAX_STAT_VALUE_PERCENTAGE * 0.2);
    public static final int DEFAULT_PLAYER_HAPPINESS = (int) (Player.MAX_STAT_VALUE_PERCENTAGE * 0.7);
    public static final int DEFAULT_PLAYER_SOCIAL = (int) (Player.MAX_STAT_VALUE_PERCENTAGE * 0.5);
    public static final int DEFAULT_PLAYER_STRESS = Player.MIN_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_BOREDOM = Player.MIN_STAT_VALUE_PERCENTAGE;
    public static final Mood DEFAULT_PLAYER_MOOD = Mood.NEUTRAL;
    public static final int DEFAULT_PLAYER_CONFIDENCE = (int) (Player.MAX_STAT_VALUE_PERCENTAGE * 0.5);
    public static final boolean DEFAULT_PLAYER_IS_ALIVE = true;
    public static final boolean DEFAULT_PLAYER_IS_CONSCIOUS = true;
    public static final boolean DEFAULT_PLAYER_IS_AWAKE = true;


    // --- Player Attributes (Fields) ---
    // (Keep all existing fields from response #36)
    private String name;
    private int age;
    private Sex sex;
    private String ethnicity;
    private double height;
    private BloodType bloodType;
    private int luck;
    private int beauty;
    private int intelligence; // Base Intelligence
    private double weight;
    private BodyType bodyType;
    private int upperBodyStrength; // Base Strength
    private int lowerBodyStrength; // Base Strength
    private int endurance; // Base Endurance
    private int agility; // Base Agility
    private int speed; // Base Speed
    private int dexterity; // Base Dexterity
    private int health;
    private int bodyTemperature;
    private int bloodPressure;
    private int heartRate;
    private int cardiovascular;
    private int respiratory;
    private int neural;
    private int digestive;
    private int immune;
    private int sight; // Base Sight
    private int hearing; // Base Hearing
    private int smellTaste;
    private int hunger;
    private int thirst;
    private int fatiguePercent;
    private int sleep;
    private int hygiene;
    private int bladder;
    private int comfort;
    private int memory; // Base Memory
    private int focus; // Base Focus
    private int willpower; // Base Willpower
    private int verbal;
    private int maths;
    private int knowledge;
    private int happiness;
    private int social;
    private int stress;
    private int boredom;
    private Mood mood;
    private int confidence;
    private boolean isAlive;
    private boolean isConscious;
    private boolean isAwake;
    private List<String> inventory;

    // --- Fields for Temporary Effects & Conditions ---
    private int tempStrengthModifier = 0;
    private int tempAgilityModifier = 0;
    private int tempSpeedModifier = 0;
    private int tempDexterityModifier = 0;
    private int tempIntelligenceModifier = 0;
    private int tempFocusModifier = 0;
    private int tempPerceptionModifier = 0; // General perception (sight/hearing)

    private boolean experiencingSeizure = false;
    private int concussionCount = 0; // Simple counter for now
    // Duration for temporary effects can be managed by PlayerStatEngine or game loop ticks
    private int adrenalineRushTurns = 0; // Example: turns remaining for adrenaline

    private transient Random needsRandom;


    // --- Constructors ---
    public Player() {
        // ... (Initialize all stats to DEFAULT values as in response #36)
        this.name = DEFAULT_PLAYER_NAME;
        this.setAge(DEFAULT_PLAYER_AGE); // Use setters for clamping if applicable
        this.sex = DEFAULT_PLAYER_SEX;
        this.ethnicity = DEFAULT_PLAYER_ETHNICITY;
        this.height = DEFAULT_PLAYER_HEIGHT;
        this.bloodType = DEFAULT_PLAYER_BLOOD_TYPE;
        this.setLuck(DEFAULT_PLAYER_LUCK);
        this.setBeauty(DEFAULT_PLAYER_BEAUTY);
        this.setIntelligence(DEFAULT_PLAYER_INTELLIGENCE);
        this.weight = DEFAULT_PLAYER_WEIGHT;
        this.bodyType = DEFAULT_PLAYER_BODY_TYPE;
        this.setUpperBodyStrength(DEFAULT_PLAYER_UPPER_BODY_STRENGTH);
        this.setLowerBodyStrength(DEFAULT_PLAYER_LOWER_BODY_STRENGTH);
        this.setEndurance(DEFAULT_PLAYER_ENDURANCE);
        this.setAgility(DEFAULT_PLAYER_AGILITY);
        this.setSpeed(DEFAULT_PLAYER_SPEED);
        this.setDexterity(DEFAULT_PLAYER_DEXTERITY);
        this.setHealth(DEFAULT_PLAYER_HEALTH);
        this.setBodyTemperature(DEFAULT_PLAYER_BODY_TEMPERATURE);
        this.setBloodPressure(DEFAULT_PLAYER_BLOOD_PRESSURE);
        this.setHeartRate(DEFAULT_PLAYER_HEART_RATE);
        this.setCardiovascular(DEFAULT_PLAYER_CARDIOVASCULAR);
        this.setRespiratory(DEFAULT_PLAYER_RESPIRATORY);
        this.setNeural(DEFAULT_PLAYER_NEURAL);
        this.setDigestive(DEFAULT_PLAYER_DIGESTIVE);
        this.setImmune(DEFAULT_PLAYER_IMMUNE);
        this.setSight(DEFAULT_PLAYER_SIGHT);
        this.setHearing(DEFAULT_PLAYER_HEARING);
        this.setSmellTaste(DEFAULT_PLAYER_SMELL_TASTE);
        this.setHunger(DEFAULT_PLAYER_HUNGER);
        this.setThirst(DEFAULT_PLAYER_THIRST);
        this.setFatiguePercent(DEFAULT_PLAYER_FATIGUE_PERCENT);
        this.setSleep(DEFAULT_PLAYER_SLEEP);
        this.setHygiene(DEFAULT_PLAYER_HYGIENE);
        this.setBladder(DEFAULT_PLAYER_BLADDER);
        this.setComfort(DEFAULT_PLAYER_COMFORT);
        this.setMemory(DEFAULT_PLAYER_MEMORY);
        this.setFocus(DEFAULT_PLAYER_FOCUS);
        this.setWillpower(DEFAULT_PLAYER_WILLPOWER);
        this.setVerbal(DEFAULT_PLAYER_VERBAL);
        this.setMaths(DEFAULT_PLAYER_MATHS);
        this.setKnowledge(DEFAULT_PLAYER_KNOWLEDGE);
        this.setHappiness(DEFAULT_PLAYER_HAPPINESS);
        this.setSocial(DEFAULT_PLAYER_SOCIAL);
        this.setStress(DEFAULT_PLAYER_STRESS);
        this.setBoredom(DEFAULT_PLAYER_BOREDOM);
        this.mood = DEFAULT_PLAYER_MOOD;
        this.setConfidence(DEFAULT_PLAYER_CONFIDENCE);
        this.isAlive = DEFAULT_PLAYER_IS_ALIVE;
        this.isConscious = DEFAULT_PLAYER_IS_CONSCIOUS;
        this.isAwake = DEFAULT_PLAYER_IS_AWAKE;
        this.inventory = new ArrayList<>();
        this.needsRandom = new Random();
        // Initialize new fields
        resetTemporaryModifiers();
    }

    public Player(String name, Sex sex, BodyType bodyType) {
        this();
        this.setName(name);
        this.setSex(sex);
        this.setBodyType(bodyType);
    }

    // --- Getters for Effective Stats (incorporating temporary modifiers) ---
    public int getEffectiveUpperBodyStrength() { return Math.max(0, upperBodyStrength + tempStrengthModifier); }
    public int getEffectiveLowerBodyStrength() { return Math.max(0, lowerBodyStrength + tempStrengthModifier); } // Assuming same modifier for now
    public int getEffectiveAgility() { return Math.max(0, agility + tempAgilityModifier); }
    public int getEffectiveSpeed() { return Math.max(0, speed + tempSpeedModifier); }
    public int getEffectiveDexterity() { return Math.max(0, dexterity + tempDexterityModifier); }
    public int getEffectiveIntelligence() { return Math.max(0, intelligence + tempIntelligenceModifier); }
    public int getEffectiveFocus() { return Math.max(0, focus + tempFocusModifier); }
    public int getEffectiveSight() { return Math.max(Player.MIN_STAT_VALUE_PERCENTAGE, sight + tempPerceptionModifier); } // uses % clamp
    public int getEffectiveHearing() { return Math.max(Player.MIN_STAT_VALUE_PERCENTAGE, hearing + tempPerceptionModifier); } // uses % clamp

    // --- Setters/Getters for Temporary Modifiers & Conditions ---
    public void setTempStrengthModifier(int value) { this.tempStrengthModifier = value; }
    public void setTempAgilityModifier(int value) { this.tempAgilityModifier = value; }
    public void setTempSpeedModifier(int value) { this.tempSpeedModifier = value; }
    public void setTempDexterityModifier(int value) { this.tempDexterityModifier = value; }
    public void setTempIntelligenceModifier(int value) { this.tempIntelligenceModifier = value; }
    public void setTempFocusModifier(int value) { this.tempFocusModifier = value; }
    public void setTempPerceptionModifier(int value) { this.tempPerceptionModifier = value; }

    public void resetTemporaryModifiers() {
        this.tempStrengthModifier = 0;
        this.tempAgilityModifier = 0;
        this.tempSpeedModifier = 0;
        this.tempDexterityModifier = 0;
        this.tempIntelligenceModifier = 0;
        this.tempFocusModifier = 0;
        this.tempPerceptionModifier = 0;
        this.adrenalineRushTurns = 0;
        this.experiencingSeizure = false;
    }

    public boolean isExperiencingSeizure() { return experiencingSeizure; }
    public void setExperiencingSeizure(boolean experiencingSeizure) { this.experiencingSeizure = experiencingSeizure; }

    public int getConcussionCount() { return concussionCount; }
    public void addConcussion() { this.concussionCount++; }
    public void setConcussionCount(int count) { this.concussionCount = Math.max(0, count); }


    public int getAdrenalineRushTurns() { return adrenalineRushTurns; }
    public void setAdrenalineRushTurns(int turns) { this.adrenalineRushTurns = Math.max(0, turns); }


    // --- `updateNeeds` method from response #43 (with minor adjustments if needed) ---
    public void updateNeeds(int minutesPassed) {
        if (!isAlive) return;
        if (this.needsRandom == null) this.needsRandom = new Random();

        // Modify rates if not fully conscious/awake, or based on other conditions
        int effectiveMinutes = minutesPassed;
        if (!isConscious || !isAwake) {
            effectiveMinutes /= 2; // Slower metabolism if unconscious/asleep (general case)
        }
        if (effectiveMinutes <= 0 && minutesPassed > 0) effectiveMinutes = 1; // Ensure at least 1min passes if original >0

        // Specific logic for sleep recovery / fatigue change if sleeping
        if (!isAwake && isConscious) { // Asleep and conscious (normal sleep)
            setSleep(this.sleep + (minutesPassed / (5 + needsRandom.nextInt(6)))); // Recover restedness
            setFatiguePercent(this.fatiguePercent - (minutesPassed / (10 + needsRandom.nextInt(11)))); // Reduce fatigue
        } else if (isAwake) { // Only increase fatigue if awake
            int fatigueRate = 45 + needsRandom.nextInt(31);
            setFatiguePercent(this.fatiguePercent + (effectiveMinutes / fatigueRate));
        }

        // Hunger & Thirst always decay, rate affected by effectiveMinutes
        int hungerRate = 20 + needsRandom.nextInt(11);
        setHunger(this.hunger - (effectiveMinutes / hungerRate));
        int thirstRate = 15 + needsRandom.nextInt(11);
        setThirst(this.thirst - (effectiveMinutes / thirstRate));

        // Hygiene, Bladder
        int hygieneRate = 120 + needsRandom.nextInt(61);
        setHygiene(this.hygiene - (effectiveMinutes / hygieneRate));
        int bladderRate = 90 + needsRandom.nextInt(61);
        setBladder(this.bladder + (effectiveMinutes / bladderRate));
    }

    // ... (All other existing Getters and Setters from response #36) ...
    // Make sure they use the base stat fields (e.g. getIntelligence returns 'intelligence', not 'effectiveIntelligence')
    // The 'effective' getters are new additions.

    // Basic Information Getters/Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = Math.max(MIN_STAT_VALUE, Math.min(age, MAX_STAT_VALUE)); }
    public Sex getSex() { return sex; }
    public void setSex(Sex sex) { this.sex = sex; }
    public String getEthnicity() { return ethnicity; }
    public void setEthnicity(String ethnicity) { this.ethnicity = ethnicity; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public BloodType getBloodType() { return bloodType; }
    public void setBloodType(BloodType bloodType) { this.bloodType = bloodType; }

    // Natural Givens Getters/Setters
    public int getLuck() { return luck; }
    public void setLuck(int luck) { this.luck = Math.max(MIN_STAT_VALUE, Math.min(luck, MAX_STAT_VALUE)); }
    public int getBeauty() { return beauty; }
    public void setBeauty(int beauty) { this.beauty = Math.max(MIN_STAT_VALUE, Math.min(beauty, MAX_STAT_VALUE)); }
    public int getIntelligence() { return intelligence; } // Base
    public void setIntelligence(int intelligence) { this.intelligence = Math.max(MIN_STAT_VALUE, Math.min(intelligence, MAX_STAT_VALUE)); }

    // Physical Attributes Getters/Setters
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public BodyType getBodyType() { return bodyType; }
    public void setBodyType(BodyType bodyType) { this.bodyType = bodyType; }

    // Physical Capabilities Getters/Setters
    public int getUpperBodyStrength() { return upperBodyStrength; } // Base
    public void setUpperBodyStrength(int upperBodyStrength) { this.upperBodyStrength = Math.max(MIN_STAT_VALUE, Math.min(upperBodyStrength, MAX_STAT_VALUE)); }
    public int getLowerBodyStrength() { return lowerBodyStrength; } // Base
    public void setLowerBodyStrength(int lowerBodyStrength) { this.lowerBodyStrength = Math.max(MIN_STAT_VALUE, Math.min(lowerBodyStrength, MAX_STAT_VALUE)); }
    public int getEndurance() { return endurance; } // Base
    public void setEndurance(int endurance) { this.endurance = Math.max(MIN_STAT_VALUE, Math.min(endurance, MAX_STAT_VALUE)); }
    public int getAgility() { return agility; } // Base
    public void setAgility(int agility) { this.agility = Math.max(MIN_STAT_VALUE, Math.min(agility, MAX_STAT_VALUE)); }
    public int getSpeed() { return speed; } // Base
    public void setSpeed(int speed) { this.speed = Math.max(MIN_STAT_VALUE, Math.min(speed, MAX_STAT_VALUE)); }
    public int getDexterity() { return dexterity; } // Base
    public void setDexterity(int dexterity) { this.dexterity = Math.max(MIN_STAT_VALUE, Math.min(dexterity, MAX_STAT_VALUE)); }

    // Health Attributes Getters/Setters
    public int getHealth() { return health; }
    public void setHealth(int health) {
        this.health = Math.max(MIN_STAT_VALUE, Math.min(health, Player.DEFAULT_PLAYER_HEALTH)); // Max is DEFAULT_PLAYER_HEALTH
        if (this.health <= 0) {
            this.isAlive = false; this.isConscious = false; this.isAwake = false;
        }
    }
    public int getBodyTemperature() { return bodyTemperature; }
    public void setBodyTemperature(int bodyTemperature) { this.bodyTemperature = Math.max(25, Math.min(bodyTemperature, 45)); } // Example physiological clamp
    public int getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(int bloodPressure) { this.bloodPressure = Math.max(MIN_STAT_VALUE, Math.min(bloodPressure, MAX_STAT_VALUE)); }
    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = Math.max(MIN_STAT_VALUE, Math.min(heartRate, MAX_STAT_VALUE)); }

    // Organ Integrity Getters/Setters
    public int getCardiovascular() { return cardiovascular; }
    public void setCardiovascular(int cardiovascular) { this.cardiovascular = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(cardiovascular, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getRespiratory() { return respiratory; }
    public void setRespiratory(int respiratory) { this.respiratory = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(respiratory, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getNeural() { return neural; }
    public void setNeural(int neural) { this.neural = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(neural, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getDigestive() { return digestive; }
    public void setDigestive(int digestive) { this.digestive = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(digestive, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getImmune() { return immune; }
    public void setImmune(int immune) { this.immune = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(immune, MAX_STAT_VALUE_PERCENTAGE)); }

    // Sensory Attributes Getters/Setters
    public int getSight() { return sight; } // Base
    public void setSight(int sight) { this.sight = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(sight, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getHearing() { return hearing; } // Base
    public void setHearing(int hearing) { this.hearing = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(hearing, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getSmellTaste() { return smellTaste; }
    public void setSmellTaste(int smellTaste) { this.smellTaste = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(smellTaste, MAX_STAT_VALUE_PERCENTAGE)); }

    // Basic Needs Getters/Setters
    public int getHunger() { return hunger; }
    public void setHunger(int hunger) { this.hunger = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(hunger, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getThirst() { return thirst; }
    public void setThirst(int thirst) { this.thirst = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(thirst, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getFatiguePercent() { return fatiguePercent; }
    public void setFatiguePercent(int fatiguePercent) { this.fatiguePercent = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(fatiguePercent, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getSleep() { return sleep; }
    public void setSleep(int sleep) { this.sleep = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(sleep, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getHygiene() { return hygiene; }
    public void setHygiene(int hygiene) { this.hygiene = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(hygiene, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getBladder() { return bladder; }
    public void setBladder(int bladder) { this.bladder = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(bladder, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getComfort() { return comfort; }
    public void setComfort(int comfort) { this.comfort = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(comfort, MAX_STAT_VALUE_PERCENTAGE)); }

    // Mental/Cognitive Getters/Setters
    public int getMemory() { return memory; } // Base
    public void setMemory(int memory) { this.memory = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(memory, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getFocus() { return focus; } // Base
    public void setFocus(int focus) { this.focus = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(focus, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getWillpower() { return willpower; } // Base
    public void setWillpower(int willpower) { this.willpower = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(willpower, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getVerbal() { return verbal; }
    public void setVerbal(int verbal) { this.verbal = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(verbal, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getMaths() { return maths; }
    public void setMaths(int maths) { this.maths = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(maths, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getKnowledge() { return knowledge; }
    public void setKnowledge(int knowledge) { this.knowledge = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(knowledge, MAX_STAT_VALUE_PERCENTAGE)); }

    // Emotional Getters/Setters
    public int getHappiness() { return happiness; }
    public void setHappiness(int happiness) { this.happiness = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(happiness, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getSocial() { return social; }
    public void setSocial(int social) { this.social = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(social, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getStress() { return stress; }
    public void setStress(int stress) { this.stress = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(stress, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getBoredom() { return boredom; }
    public void setBoredom(int boredom) { this.boredom = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(boredom, MAX_STAT_VALUE_PERCENTAGE)); }
    public Mood getMood() { return mood; }
    public void setMood(Mood mood) { this.mood = mood; }
    public int getConfidence() { return confidence; }
    public void setConfidence(int confidence) { this.confidence = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(confidence, MAX_STAT_VALUE_PERCENTAGE)); }

    // Status Flags Getters/Setters
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive; if (!isAlive) { isConscious = false; isAwake = false; } }
    public boolean isConscious() { return isConscious; }
    public void setConscious(boolean conscious) { isConscious = conscious; if (!isConscious) { isAwake = false; } else if (!isAlive) { isAlive = true; } }
    public boolean isAwake() { return isAwake; }
    public void setAwake(boolean awake) { isAwake = awake; if (isAwake) { if (!isConscious) setConscious(true); } }

    // Inventory Methods (as before)
    public void addItem(String itemName) { if (itemName != null && !itemName.trim().isEmpty()) { if (this.inventory == null) this.inventory = new ArrayList<>(); this.inventory.add(itemName.trim()); System.out.println(this.name + " acquired: " + itemName.trim()); } }
    public boolean removeItem(String itemName) { if (itemName != null && this.inventory != null) { boolean removed = this.inventory.remove(itemName.trim()); if (removed) System.out.println(itemName.trim() + " removed from " + this.name + "'s inventory."); return removed; } return false; }
    public List<String> getInventory() { if (this.inventory == null) this.inventory = new ArrayList<>(); return Collections.unmodifiableList(this.inventory); }
    public boolean hasItem(String itemName) { if (itemName == null || this.inventory == null) return false; return this.inventory.contains(itemName.trim()); }

    // toString Method
    @Override
    public String toString() { /* ... as before ... */ return "Player{name='" + name + '\'' + ", health=" + health + '}'; }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.needsRandom = new Random();
        // Initialize temporary modifiers to default values
        resetTemporaryModifiers();
        // Reinitialize any transient fields or other objects needed
        // For example, if you have a list of items or other collections, reinitialize them here
    }

    // Method to reset the Player instance to default values (for reinitialization)
    public void reset() {
        this.name = DEFAULT_PLAYER_NAME;
        this.setAge(DEFAULT_PLAYER_AGE);
        if (this.inventory == null) this.inventory = new ArrayList<>();
        if (this.needsRandom == null) this.needsRandom = new Random();
        // Reinitialize other fields as necessary
        // For example, if you have a list of items or other collections, reinitialize them here
    }
}
package com.theofernandez.rpg.game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Player implements Serializable {

    private static final long serialVersionUID = 3L; // Incremented due to new fields

    public static final int MAX_STAT_VALUE = 1000000;
    public static final int MIN_STAT_VALUE = 0;
    public static final int MAX_STAT_VALUE_PERCENTAGE = 500;
    public static final int MIN_STAT_VALUE_PERCENTAGE = 0;

    public static enum Sex implements Serializable { MALE, FEMALE }
    public static enum BodyType implements Serializable { ECTOMORPH, MESOMORPH, ENDOMORPH }
    public static enum BloodType implements Serializable { O_POSITIVE, O_NEGATIVE, A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, AB_POSITIVE, AB_NEGATIVE, UNKNOWN }
    public static enum Mood implements Serializable { NEUTRAL, HAPPY, SAD, ANGRY, STRESSED, BORED, TIRED, ENERGETIC, FOCUSED, DISTRACTED, FEARFUL, ADRENALIZED, CRITICAL, NUMB }


    // --- Default Player Values ---
    public static final int DEFAULT_PLAYER_AGE = 22;
    public static final String DEFAULT_PLAYER_NAME = "Player";
    public static final Sex DEFAULT_PLAYER_SEX = Sex.MALE;
    public static final String DEFAULT_PLAYER_ETHNICITY = "Caucasian";
    public static final double DEFAULT_PLAYER_HEIGHT = 1.75;
    public static final BloodType DEFAULT_PLAYER_BLOOD_TYPE = BloodType.O_POSITIVE;
    public static final double DEFAULT_PLAYER_WEIGHT = 70.0;
    public static final BodyType DEFAULT_PLAYER_BODY_TYPE = BodyType.MESOMORPH;
    public static final int DEFAULT_PLAYER_LUCK = 50;
    public static final int DEFAULT_PLAYER_BEAUTY = 50;
    public static final int DEFAULT_PLAYER_INTELLIGENCE = 50;
    public static final int DEFAULT_PLAYER_UPPER_BODY_STRENGTH = 50;
    public static final int DEFAULT_PLAYER_LOWER_BODY_STRENGTH = 50;
    public static final int DEFAULT_PLAYER_ENDURANCE = 50;
    public static final int DEFAULT_PLAYER_AGILITY = 50;
    public static final int DEFAULT_PLAYER_SPEED = 50;
    public static final int DEFAULT_PLAYER_DEXTERITY = 50;
    public static final int DEFAULT_PLAYER_HEALTH = 100; // Max health
    public static final int DEFAULT_PLAYER_BODY_TEMPERATURE = 37;
    public static final int DEFAULT_PLAYER_BLOOD_PRESSURE = 120; // Systolic, for simplicity
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
    public static final int DEFAULT_PLAYER_SLEEP = Player.MAX_STAT_VALUE_PERCENTAGE; // Restedness
    public static final int DEFAULT_PLAYER_HYGIENE = Player.MAX_STAT_VALUE_PERCENTAGE;
    public static final int DEFAULT_PLAYER_BLADDER = Player.MIN_STAT_VALUE_PERCENTAGE; // Fullness
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
    private int sight; // Base Sight (as percentage)
    private int hearing; // Base Hearing (as percentage)
    private int smellTaste; // Base Smell/Taste (as percentage)
    private int hunger;
    private int thirst;
    private int fatiguePercent;
    private int sleep; // Restedness level
    private int hygiene;
    private int bladder; // Fullness level
    private int comfort;
    private int memory; // Base Memory (as percentage)
    private int focus; // Base Focus (as percentage)
    private int willpower; // Base Willpower (as percentage)
    private int verbal; // (as percentage)
    private int maths; // (as percentage)
    private int knowledge; // (as percentage)
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
    private int tempIntelligenceModifier = 0; // Note: This seems to be modifying the 0-1000000 scale intelligence
    private int tempFocusModifier = 0; // This modifies the 0-500 scale focus
    private int tempPerceptionModifier = 0; // General perception (sight/hearing) (modifies 0-500 scale)
    // Added tempMemoryModifier based on PlayerStatEngine usage
    private int tempMemoryModifier = 0;


    private boolean experiencingSeizure = false;
    private int concussionCount = 0;
    private int adrenalineRushTurns = 0;

    private transient Random needsRandom; // Marked transient, will be re-initialized


    // --- Constructors ---
    public Player() {
        this.name = DEFAULT_PLAYER_NAME;
        this.setAge(DEFAULT_PLAYER_AGE);
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
        resetTemporaryModifiers();
    }

    public Player(String name, Sex sex, BodyType bodyType) {
        this(); // Calls the default constructor
        this.setName(name); // Ensure name is not null or empty if required
        this.setSex(sex);
        this.setBodyType(bodyType);
    }

    // --- Getters for Effective Stats (incorporating temporary modifiers) ---
    public int getEffectiveUpperBodyStrength() { return Math.max(MIN_STAT_VALUE, upperBodyStrength + tempStrengthModifier); }
    public int getEffectiveLowerBodyStrength() { return Math.max(MIN_STAT_VALUE, lowerBodyStrength + tempStrengthModifier); }
    public int getEffectiveAgility() { return Math.max(MIN_STAT_VALUE, agility + tempAgilityModifier); }
    public int getEffectiveSpeed() { return Math.max(MIN_STAT_VALUE, speed + tempSpeedModifier); }
    public int getEffectiveDexterity() { return Math.max(MIN_STAT_VALUE, dexterity + tempDexterityModifier); }
    public int getEffectiveIntelligence() { return Math.max(MIN_STAT_VALUE, intelligence + tempIntelligenceModifier); }
    public int getEffectiveFocus() { return Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(focus + tempFocusModifier, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getEffectiveSight() { return Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(sight + tempPerceptionModifier, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getEffectiveHearing() { return Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(hearing + tempPerceptionModifier, MAX_STAT_VALUE_PERCENTAGE)); }
    public int getEffectiveMemory() { return Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(memory + tempMemoryModifier, MAX_STAT_VALUE_PERCENTAGE)); }


    // --- Setters/Getters for Temporary Modifiers & Conditions ---
    public int getTempStrengthModifier() { return tempStrengthModifier; }
    public void setTempStrengthModifier(int value) { this.tempStrengthModifier = value; }

    public int getTempAgilityModifier() { return tempAgilityModifier; }
    public void setTempAgilityModifier(int value) { this.tempAgilityModifier = value; }

    public int getTempSpeedModifier() { return tempSpeedModifier; }
    public void setTempSpeedModifier(int value) { this.tempSpeedModifier = value; }

    public int getTempDexterityModifier() { return tempDexterityModifier; }
    public void setTempDexterityModifier(int value) { this.tempDexterityModifier = value; }

    public int getTempIntelligenceModifier() { return tempIntelligenceModifier; }
    public void setTempIntelligenceModifier(int value) { this.tempIntelligenceModifier = value; }

    public int getTempFocusModifier() { return tempFocusModifier; }
    public void setTempFocusModifier(int value) { this.tempFocusModifier = value; }

    public int getTempPerceptionModifier() { return tempPerceptionModifier; }
    public void setTempPerceptionModifier(int value) { this.tempPerceptionModifier = value; }

    public int getTempMemoryModifier() { return tempMemoryModifier; }
    public void setTempMemoryModifier(int value) { this.tempMemoryModifier = value; }


    public void resetTemporaryModifiers() {
        this.tempStrengthModifier = 0;
        this.tempAgilityModifier = 0;
        this.tempSpeedModifier = 0;
        this.tempDexterityModifier = 0;
        this.tempIntelligenceModifier = 0;
        this.tempFocusModifier = 0;
        this.tempPerceptionModifier = 0;
        this.tempMemoryModifier = 0;
        // this.adrenalineRushTurns = 0; // Should not be reset here, but when effect wears off or via specific logic
        this.experiencingSeizure = false; // This is fine to reset if it's a per-cycle check
    }

    public boolean isExperiencingSeizure() { return experiencingSeizure; }
    public void setExperiencingSeizure(boolean experiencingSeizure) { this.experiencingSeizure = experiencingSeizure; }

    public int getConcussionCount() { return concussionCount; }
    public void addConcussion() { this.concussionCount++; }
    public void setConcussionCount(int count) { this.concussionCount = Math.max(0, count); }


    public int getAdrenalineRushTurns() { return adrenalineRushTurns; }
    public void setAdrenalineRushTurns(int turns) { this.adrenalineRushTurns = Math.max(0, turns); }


    public void updateNeeds(int minutesPassed) {
        if (!isAlive) return;
        if (this.needsRandom == null) this.needsRandom = new Random(); // Defensive re-initialization if somehow null

        if (minutesPassed <= 0) return; // No time passed, no needs change

        // Modify rates if not fully conscious/awake, or based on other conditions
        int effectiveMinutes = minutesPassed;
        if (!isConscious || !isAwake) {
            // Slower metabolism if unconscious/asleep (general case)
            // Let's make this a bit more impactful but ensure it doesn't stop entirely
            effectiveMinutes = Math.max(1, minutesPassed / 2);
        }

        // Specific logic for sleep recovery / fatigue change if sleeping
        if (!isAwake && isConscious) { // Asleep and conscious (normal sleep)
            setSleep(getSleep() + (minutesPassed / (5 + needsRandom.nextInt(6)))); // Recover restedness (approx 10-20 per hour)
            setFatiguePercent(getFatiguePercent() - (minutesPassed / (10 + needsRandom.nextInt(11)))); // Reduce fatigue (approx 5-10 per hour)
        } else if (isAwake) { // Only increase fatigue if awake
            // Fatigue increases faster, e.g., 1 point every 45-75 minutes of being awake
            setFatiguePercent(getFatiguePercent() + (effectiveMinutes / (45 + needsRandom.nextInt(31))));
        }

        // Hunger & Thirst always decay, rate affected by effectiveMinutes
        // Hunger: 1 point every 20-30 minutes
        setHunger(getHunger() - (effectiveMinutes / (20 + needsRandom.nextInt(11))));
        // Thirst: 1 point every 15-25 minutes
        setThirst(getThirst() - (effectiveMinutes / (15 + needsRandom.nextInt(11))));

        // Hygiene, Bladder
        // Hygiene: 1 point every 2-3 hours
        setHygiene(getHygiene() - (effectiveMinutes / (120 + needsRandom.nextInt(61))));
        // Bladder: 1 point every 1.5-2.5 hours
        setBladder(getBladder() + (effectiveMinutes / (90 + needsRandom.nextInt(61))));
    }


    // --- Standard Getters and Setters (with clamping) ---

    // Basic Information
    public String getName() { return name; }
    public void setName(String name) { this.name = (name != null && !name.trim().isEmpty()) ? name.trim() : DEFAULT_PLAYER_NAME; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = Math.max(0, Math.min(age, 200)); } // Assuming 0-200 is a reasonable age range

    public Sex getSex() { return sex; }
    public void setSex(Sex sex) { this.sex = sex; }

    public String getEthnicity() { return ethnicity; }
    public void setEthnicity(String ethnicity) { this.ethnicity = ethnicity; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = Math.max(0.5, Math.min(height, 3.0)); } // Meters

    public BloodType getBloodType() { return bloodType; }
    public void setBloodType(BloodType bloodType) { this.bloodType = bloodType; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = Math.max(10.0, Math.min(weight, 500.0)); } // Kilograms

    public BodyType getBodyType() { return bodyType; }
    public void setBodyType(BodyType bodyType) { this.bodyType = bodyType; }

    // Natural Givens (0-100,000 scale)
    public int getLuck() { return luck; }
    public void setLuck(int luck) { this.luck = Math.max(MIN_STAT_VALUE, Math.min(luck, MAX_STAT_VALUE)); }

    public int getBeauty() { return beauty; }
    public void setBeauty(int beauty) { this.beauty = Math.max(MIN_STAT_VALUE, Math.min(beauty, MAX_STAT_VALUE)); }

    public int getIntelligence() { return intelligence; } // Base
    public void setIntelligence(int intelligence) { this.intelligence = Math.max(MIN_STAT_VALUE, Math.min(intelligence, MAX_STAT_VALUE)); }

    // Physical Capabilities (0-100,000 scale)
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

    // Health Attributes
    public int getHealth() { return health; }
    public void setHealth(int health) {
        this.health = Math.max(MIN_STAT_VALUE, Math.min(health, Player.DEFAULT_PLAYER_HEALTH)); // Max is DEFAULT_PLAYER_HEALTH (e.g., 100)
        if (this.health <= 0) {
            this.health = 0; // Ensure it doesn't go below 0
            this.isAlive = false;
            this.isConscious = false;
            this.isAwake = false;
        }
    }

    public int getBodyTemperature() { return bodyTemperature; }
    public void setBodyTemperature(int bodyTemperature) { this.bodyTemperature = Math.max(20, Math.min(bodyTemperature, 45)); } // Celsius physiological clamp

    public int getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(int bloodPressure) { this.bloodPressure = Math.max(40, Math.min(bloodPressure, 300)); } // Systolic, mmHg

    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = Math.max(0, Math.min(heartRate, 300)); } // BPM, 0 if dead

    // Organ Integrity (Percentage 0-500 scale)
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

    // Sensory Attributes (Percentage 0-500 scale)
    public int getSight() { return sight; } // Base
    public void setSight(int sight) { this.sight = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(sight, MAX_STAT_VALUE_PERCENTAGE)); }

    public int getHearing() { return hearing; } // Base
    public void setHearing(int hearing) { this.hearing = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(hearing, MAX_STAT_VALUE_PERCENTAGE)); }

    public int getSmellTaste() { return smellTaste; }
    public void setSmellTaste(int smellTaste) { this.smellTaste = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(smellTaste, MAX_STAT_VALUE_PERCENTAGE)); }

    // Basic Needs (Percentage 0-500 scale)
    public int getHunger() { return hunger; } // Higher value means more satisfied/less hungry
    public void setHunger(int hunger) { this.hunger = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(hunger, MAX_STAT_VALUE_PERCENTAGE)); }

    public int getThirst() { return thirst; } // Higher value means more satisfied/less thirsty
    public void setThirst(int thirst) { this.thirst = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(thirst, MAX_STAT_VALUE_PERCENTAGE)); }

    public int getFatiguePercent() { return fatiguePercent; } // Higher value means more fatigued
    public void setFatiguePercent(int fatiguePercent) { this.fatiguePercent = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(fatiguePercent, MAX_STAT_VALUE_PERCENTAGE)); }

    public int getSleep() { return sleep; } // Higher value means more rested
    public void setSleep(int sleep) { this.sleep = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(sleep, MAX_STAT_VALUE_PERCENTAGE)); }

    public int getHygiene() { return hygiene; } // Higher value means cleaner
    public void setHygiene(int hygiene) { this.hygiene = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(hygiene, MAX_STAT_VALUE_PERCENTAGE)); }

    public int getBladder() { return bladder; } // Higher value means more full/urgent
    public void setBladder(int bladder) { this.bladder = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(bladder, MAX_STAT_VALUE_PERCENTAGE)); }

    public int getComfort() { return comfort; } // Higher value means more comfortable
    public void setComfort(int comfort) { this.comfort = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(comfort, MAX_STAT_VALUE_PERCENTAGE)); }

    // Mental/Cognitive (Percentage 0-500 scale)
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

    // Emotional (Percentage 0-500 scale)
    public int getHappiness() { return happiness; }
    public void setHappiness(int happiness) { this.happiness = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(happiness, MAX_STAT_VALUE_PERCENTAGE)); }

    public int getSocial() { return social; }
    public void setSocial(int social) { this.social = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(social, MAX_STAT_VALUE_PERCENTAGE)); }

    public int getStress() { return stress; } // Higher value means more stressed
    public void setStress(int stress) { this.stress = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(stress, MAX_STAT_VALUE_PERCENTAGE)); }

    public int getBoredom() { return boredom; } // Higher value means more bored
    public void setBoredom(int boredom) { this.boredom = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(boredom, MAX_STAT_VALUE_PERCENTAGE)); }

    public Mood getMood() { return mood; }
    public void setMood(Mood mood) { this.mood = mood; }

    public int getConfidence() { return confidence; }
    public void setConfidence(int confidence) { this.confidence = Math.max(MIN_STAT_VALUE_PERCENTAGE, Math.min(confidence, MAX_STAT_VALUE_PERCENTAGE)); }

    // Status Flags
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) {
        isAlive = alive;
        if (!isAlive) {
            isConscious = false;
            isAwake = false;
            // Potentially set health to 0 if not already handled by setHealth
            if (health > 0) health = 0;
        }
    }

    public boolean isConscious() { return isConscious; }
    public void setConscious(boolean conscious) {
        isConscious = conscious;
        if (!isConscious) {
            isAwake = false; // Cannot be awake if not conscious
        } else {
            // If becoming conscious, ensure player is marked as alive
            // (unless health is 0, which setHealth/setAlive should manage)
            if (!isAlive && health > 0) { // Check health to avoid reviving a 0 HP player just by setting conscious
                isAlive = true;
            }
        }
    }

    public boolean isAwake() { return isAwake; }
    public void setAwake(boolean awake) {
        isAwake = awake;
        if (isAwake) {
            if (!isConscious) setConscious(true); // If waking up, must be conscious
            if (!isAlive && health > 0) isAlive = true; // And alive
        }
    }

    // Inventory Methods
    public void addItem(String itemName) {
        if (itemName != null && !itemName.trim().isEmpty()) {
            if (this.inventory == null) this.inventory = new ArrayList<>();
            this.inventory.add(itemName.trim());
            System.out.println(this.name + " acquired: " + itemName.trim());
        }
    }

    public boolean removeItem(String itemName) {
        if (itemName != null && this.inventory != null) {
            boolean removed = this.inventory.remove(itemName.trim());
            if (removed) System.out.println(itemName.trim() + " removed from " + this.name + "'s inventory.");
            return removed;
        }
        return false;
    }

    public List<String> getInventory() {
        if (this.inventory == null) this.inventory = new ArrayList<>();
        return Collections.unmodifiableList(this.inventory);
    }

    public boolean hasItem(String itemName) {
        if (itemName == null || this.inventory == null) return false;
        return this.inventory.contains(itemName.trim());
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", health=" + health + "/" + DEFAULT_PLAYER_HEALTH +
                ", isAlive=" + isAlive +
                ", isConscious=" + isConscious +
                ", isAwake=" + isAwake +
                ", mood=" + mood +
                '}';
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Re-initialize transient fields
        this.needsRandom = new Random();

        // Ensure temporary modifiers are reset to default values upon loading a save.
        // These should reflect current state, not saved state.
        // However, if some temporary effects *should* persist across saves (e.g. a long-term poison),
        // they would need to be saved and this logic adjusted. For now, assuming they reset.
        resetTemporaryModifiers(); // This was already here and is good.

        // Defensive null check for inventory, though defaultReadObject should handle it if saved correctly.
        if (this.inventory == null) {
            this.inventory = new ArrayList<>();
        }
    }

    // Method to reset the Player instance to default values (for reinitialization in New Game, etc.)
    public void resetToDefaults() {
        // Call the default constructor's logic essentially
        this.name = DEFAULT_PLAYER_NAME;
        this.setAge(DEFAULT_PLAYER_AGE);
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
        this.setHealth(DEFAULT_PLAYER_HEALTH); // This will also set isAlive = true if health > 0
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
        this.isAlive = DEFAULT_PLAYER_IS_ALIVE; // Ensure these are explicitly reset
        this.isConscious = DEFAULT_PLAYER_IS_CONSCIOUS;
        this.isAwake = DEFAULT_PLAYER_IS_AWAKE;

        if (this.inventory != null) {
            this.inventory.clear();
        } else {
            this.inventory = new ArrayList<>();
        }

        this.concussionCount = 0;
        this.adrenalineRushTurns = 0;
        this.experiencingSeizure = false;
        resetTemporaryModifiers();

        if (this.needsRandom == null) {
            this.needsRandom = new Random();
        }
    }
}
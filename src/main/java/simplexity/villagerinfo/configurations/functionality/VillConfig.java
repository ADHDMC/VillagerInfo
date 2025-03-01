package simplexity.villagerinfo.configurations.functionality;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import simplexity.villagerinfo.VillagerInfo;
import simplexity.villagerinfo.configurations.locale.ServerMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class VillConfig {
    private static VillConfig instance;

    private VillConfig() {
    }

    public static VillConfig getInstance() {
        if (instance == null) instance = new VillConfig();
        return instance;
    }

    private boolean isWhitelist, outputEnabled, purpurLobotomized, babyVillagerAge, zombieVillagerConversionTime, health,
            profession, jobSiteLocation, lastWorkTime, bedLocation, lastSleepTime, villagerInventory, restocksToday,
            playerReputation, highlightWorkstationOnOutput, playSoundOnOutput, highlightBedOnOutput;
    private Sound configuredSound;
    private Color defaultColor;
    private int configuredHighlightTime, blockLight, skyLight;
    private float configuredSoundVolume, configuredSoundPitch, xOffset, yOffset, zOffset;
    private final HashMap<Material, Color> poiBlockHighlightColorsMap = new HashMap<>();
    String error = ServerMessage.CONFIGURATION_ERROR_PREFIX.getMessage();


    private final Set<Material> itemSet = new HashSet<>();

    private final Logger logger = VillagerInfo.getInstance().getVillagerInfoLogger();

    public void reloadVillConfig(FileConfiguration config) {
        reloadBlockDisplayDefaults(config);
        reloadSound(config);
        reloadSoundPitch(config);
        reloadSoundVolume(config);
        reloadHighlightTime(config);
        reloadToggles(config);
        reloadColors(config);
        reloadItemList(config);
    }

    public void reloadBlockDisplayDefaults(FileConfiguration config) {
        xOffset = (float) config.getDouble("defaults.scale-offset.x-offset", -0.03);
        yOffset = (float) config.getDouble("defaults.scale-offset.y-offset", -0.03);
        zOffset = (float) config.getDouble("defaults.scale-offset.z-offset", -0.03);
        List<Integer> rawColor = config.getIntegerList("defaults.color");
        if (rawColor.size() != 3) {
            defaultColor = Color.WHITE;
        } else {
            defaultColor = Color.fromRGB(rawColor.get(0), rawColor.get(1), rawColor.get(2));
        }
        blockLight = config.getInt("defaults.block-light", 15);
        skyLight = config.getInt("defaults.sky-light", 15);
    }

    public void reloadSound(FileConfiguration config) {
        configuredSound = null;
        try {
            configuredSound = Sound.valueOf(config.getString("sound", "BLOCK_AMETHYST_BLOCK_BREAK").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            logger.warning(error + configuredSound + ServerMessage.CONFIGURED_SOUND_ERROR.getMessage());
            configuredSound = Sound.BLOCK_AMETHYST_BLOCK_BREAK;
        }
    }

    public void reloadSoundPitch(FileConfiguration config) {
        configuredSoundPitch = 0;
        double pitch = config.getDouble("sound-pitch");
        if (withinBounds(0, 2, pitch)) {
            configuredSoundPitch = (float) pitch;
        } else {
            logger.warning(error + ServerMessage.CONFIGURED_PITCH_ERROR.getMessage());
            configuredSoundPitch = 1.5F;
        }
    }

    public void reloadSoundVolume(FileConfiguration config) {
        configuredSoundVolume = 0;
        double volume = config.getDouble("sound-volume");
        if (withinBounds(0, 2, volume)) {
            configuredSoundVolume = (float) volume;
        } else {
            logger.warning(error + ServerMessage.CONFIGURED_VOLUME_ERROR.getMessage());
            configuredSoundVolume = 0.5F;
        }
    }

    public void reloadHighlightTime(FileConfiguration config) {
        configuredHighlightTime = 0;
        if (config.getInt("highlight-time", 10) <= 0) {
            logger.warning(error + ServerMessage.CONFIGURED_HIGHLIGHT_TIME_ERROR.getMessage());
            configuredHighlightTime = 10;
        } else {
            configuredHighlightTime = config.getInt("highlight-time");
        }
    }

    public void reloadToggles(FileConfiguration config) {
        outputEnabled = config.getBoolean("output-enabled", true);
        purpurLobotomized = config.getBoolean("toggles.DISPLAY_PURPUR_LOBOTOMIZED", false);
        babyVillagerAge = config.getBoolean("toggles.DISPLAY_BABY_VILLAGER_AGE", true);
        zombieVillagerConversionTime = config.getBoolean("toggles.DISPLAY_ZOMBIE_VILLAGER_CONVERSION_TIME", true);
        health = config.getBoolean("toggles.DISPLAY_HEALTH", true);
        profession = config.getBoolean("toggles.DISPLAY_PROFESSION", true);
        jobSiteLocation = config.getBoolean("toggles.DISPLAY_JOB_SITE_LOCATION", true);
        lastWorkTime = config.getBoolean("toggles.DISPLAY_LAST_WORK_TIME", true);
        bedLocation = config.getBoolean("toggles.DISPLAY_BED_LOCATION", true);
        lastSleepTime = config.getBoolean("toggles.DISPLAY_LAST_SLEEP_TIME", true);
        villagerInventory = config.getBoolean("toggles.DISPLAY_VILLAGER_INVENTORY", true);
        restocksToday = config.getBoolean("toggles.DISPLAY_RESTOCKS_TODAY", true);
        playerReputation = config.getBoolean("toggles.DISPLAY_PLAYER_REPUTATION", true);
        playSoundOnOutput = config.getBoolean("play-sound-on-output", true);
        highlightWorkstationOnOutput = config.getBoolean("highlight-workstation-on-output", true);
        highlightBedOnOutput = config.getBoolean("highlight-bed-on-output", true);
    }

    public void reloadColors(FileConfiguration config) {
        ConfigurationSection colorSection = config.getConfigurationSection("workstation-highlight-color");
        ConfigurationSection bedSection = config.getConfigurationSection("bed-highlight-color");
        if (colorSection == null || bedSection == null) {
            logger.warning(error + ServerMessage.NO_COLOR_SECTION_FOUND.getMessage() + ServerMessage.ERROR_CHECK_FOR_TABS.getMessage());
            return;
        }
        poiBlockHighlightColorsMap.clear();
        loadSection(colorSection);
        loadSection(bedSection);
    }

    private void loadSection(ConfigurationSection section) {
        Set<String> colorKeys = section.getKeys(false);
        Material blockMaterial;
        for (String key : colorKeys) {
            blockMaterial = Material.getMaterial(key);
            if (blockMaterial == null) {
                logger.warning(error + key + ServerMessage.ERROR_NOT_A_MATERIAL.getMessage());
                continue;
            }
            List<Integer> rgbList = section.getIntegerList(key);
            if (rgbList.size() < 3) {
                logger.warning(error + ServerMessage.ERROR_COLOR_DECLARED_INCORRECTLY.getMessage() + rgbList);
                continue;
            }
            Color color = Color.fromRGB(rgbList.get(0), rgbList.get(1), rgbList.get(2));
            poiBlockHighlightColorsMap.put(blockMaterial, color);
        }
    }

    private boolean withinBounds(double min, double max, double value) {
        return value >= min && value <= max;
    }

    public void reloadItemList(FileConfiguration config) {
        isWhitelist = config.getBoolean("whitelist", false);
        for (String item : config.getStringList("items")) {
            Material material = Material.getMaterial(item);
            if (material != null) {
                itemSet.add(material);
                continue;
            }
            Logger logger = VillagerInfo.getInstance().getVillagerInfoLogger();
            logger.warning("Invalid material in item list: " + item);
        }
    }

    public Sound getConfiguredSound() {
        return configuredSound;
    }

    public int getConfiguredHighlightTime() {
        return configuredHighlightTime;
    }

    public float getConfiguredSoundPitch() {
        return configuredSoundPitch;
    }

    public float getConfiguredSoundVolume() {
        return configuredSoundVolume;
    }

    public Map<Material, Color> getPoiBlockHighlightColorsMap() {
        return Collections.unmodifiableMap(poiBlockHighlightColorsMap);
    }

    public boolean isValidItem(Material material) {
        return itemSet.contains(material) == isWhitelist;
    }

    public boolean isOutputEnabled() {
        return outputEnabled;
    }

    public boolean displayPurpurLobotomized() {
        return purpurLobotomized;
    }

    public boolean displayBabyVillagerAge() {
        return babyVillagerAge;
    }

    public boolean displayZombieVillagerConversionTime() {
        return zombieVillagerConversionTime;
    }

    public boolean displayHealth() {
        return health;
    }

    public boolean displayRestocksToday() {
        return restocksToday;
    }

    public boolean isHighlightBedOnOutput() {
        return highlightBedOnOutput;
    }

    public boolean displayVillagerInventory() {
        return villagerInventory;
    }

    public boolean displayLastSleepTime() {
        return lastSleepTime;
    }

    public boolean shouldPlaySoundOnOutput() {
        return playSoundOnOutput;
    }

    public boolean displayBedLocation() {
        return bedLocation;
    }

    public boolean displayLastWorkTime() {
        return lastWorkTime;
    }

    public boolean shouldHighlightWorkstationOnOutput() {
        return highlightWorkstationOnOutput;
    }

    public boolean displayJobSiteLocation() {
        return jobSiteLocation;
    }

    public boolean displayProfession() {
        return profession;
    }

    public boolean displayPlayerReputation() {
        return playerReputation;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public int getBlockLight() {
        return blockLight;
    }

    public int getSkyLight() {
        return skyLight;
    }

    public float getXOffset() {
        return xOffset;
    }

    public float getYOffset() {
        return yOffset;
    }

    public float getZOffset() {
        return zOffset;
    }
}

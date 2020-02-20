package ru.soknight.dailyrewards.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Getter;
import ru.soknight.dailyrewards.DailyRewards;
import ru.soknight.dailyrewards.utils.Logger;

public class Config {

	@Getter private static FileConfiguration config;
	@Getter private static List<String> rewardMessage = new ArrayList<>();
	@Getter private static Map<String, Double> bonuses = new HashMap<>();
	
	public static void refresh() {
		DailyRewards instance = DailyRewards.getInstance();
		File datafolder = instance.getDataFolder();
		if(!datafolder.isDirectory()) datafolder.mkdirs();
		File file = new File(instance.getDataFolder(), "config.yml");
		if(!file.exists()) {
			try {
				Files.copy(instance.getResource("config.yml"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Logger.info("Generated new config file.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
		
		config.getStringList("reward-message").forEach(s -> rewardMessage.add(s.replace("&", "\u00A7")));
		
		Set<String> keys = config.getConfigurationSection("group-bonuses").getKeys(false);
		if(!keys.isEmpty())
			keys.forEach(key -> {
				double bonus = getDouble("group-bonuses." + key);
				bonuses.put("group." + key, bonus);
			});
	}
	
	public static String getString(String section) {
		return config.getString(section, "").replace("&", "\u00A7");
	}
	
	public static double getDouble(String section) {
		return config.getDouble(section, 0);
	}
	
	public static boolean getBoolean(String section) {
		return config.getBoolean(section, true);
	}

	public static int getInt(String section) {
		return config.getInt(section, 0);
	}
	
}

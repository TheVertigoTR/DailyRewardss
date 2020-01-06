package ru.soknight.dailyrewards;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ru.soknight.dailyrewards.database.Database;
import ru.soknight.dailyrewards.database.DatabaseManager;
import ru.soknight.dailyrewards.files.Config;
import ru.soknight.dailyrewards.handlers.PlayerHandler;
import ru.soknight.dailyrewards.utils.Logger;

public class DailyRewards extends JavaPlugin {

	private static DailyRewards instance;
	private Database database;
	
	@Override
	public void onEnable() {
		instance = this;
		
		// Loading config file
		Config.refresh();
		
		// Loading database
		try {
			database = new Database();
			DatabaseManager.loadFromDatabase();
		} catch (Exception e) {
			Logger.error("Couldn't connect database type " + Config.getString("database.type") + ":");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// Register handler
		Bukkit.getPluginManager().registerEvents(new PlayerHandler(), this);
		
		Logger.info("Enabled!");
	}

	@Override
	public void onDisable() {
		DatabaseManager.saveToDatabase();
	}

	public static DailyRewards getInstance() {
		return instance;
	}

	public Database getDatabase() {
		return database;
	}
	
}

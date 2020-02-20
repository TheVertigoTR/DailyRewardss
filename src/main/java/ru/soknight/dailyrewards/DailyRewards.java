package ru.soknight.dailyrewards;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import ru.soknight.dailyrewards.database.Database;
import ru.soknight.dailyrewards.database.DatabaseManager;
import ru.soknight.dailyrewards.files.Config;
import ru.soknight.dailyrewards.handlers.PlayerHandler;
import ru.soknight.dailyrewards.utils.Logger;

public class DailyRewards extends JavaPlugin {

	@Getter private static DailyRewards instance;
	@Getter private DatabaseManager DBManager;
	
	@Override
	public void onEnable() {
		instance = this;
		
		// Loading config file
		Config.refresh();
		
		// Loading database
		try {
			Database database = new Database();
			DBManager = new DatabaseManager(database);
		} catch (Exception e) {
			Logger.error("Database initialization failed: " + e.getLocalizedMessage());
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// Register handler
		Bukkit.getPluginManager().registerEvents(new PlayerHandler(), this);
	}

	@Override
	public void onDisable() {
		DBManager.shutdown();
	}
	
}

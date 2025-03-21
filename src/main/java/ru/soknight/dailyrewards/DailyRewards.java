package ru.soknight.dailyrewards;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ru.soknight.dailyrewards.database.Database;
import ru.soknight.dailyrewards.database.DatabaseManager;
import ru.soknight.dailyrewards.listener.PlayerJoinListener;
import ru.soknight.lib.configuration.Configuration;
import ru.soknight.lib.configuration.Messages;
package ru.soknight.dailyrewards.utils;

import me.glaremasters.itemsadder.api.ItemsAdderAPI;
import org.bukkit.entity.Player;

public class ItemsAdderUtils {
    // Player için öğe ID'sine göre prefix al
    public static String getItemPrefix(Player player, String itemID) {
        // ItemsAdder'dan prefix veya görsel bilgisi çek
        String prefix = ItemsAdderAPI.getItemIcon(itemID);  // Bu metod ile item ID'sine ait görseli alabilirsiniz
        
        // Eğer görsel yoksa varsayılan bir değer döndür
        return prefix != null ? prefix : "defaultPrefix";


public class DailyRewards extends JavaPlugin {
	
	protected DatabaseManager databaseManager;
	
	@Override
	public void onEnable() {
		long start = System.currentTimeMillis();
		
		// Configs initialization
		Configuration pluginConfig = new Configuration(this, "config.yml");
		pluginConfig.refresh();
		
		Messages messages = new Messages(this, "messages.yml");
		
		// Database initialization
		try {
			Database database = new Database(this, pluginConfig);
			this.databaseManager = new DatabaseManager(this, database);
		} catch (Exception e) {
			getLogger().severe("Failed to initialize database: " + e.getLocalizedMessage());
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		// Join event listener initialization
		PlayerJoinListener joinListener = new PlayerJoinListener(this, pluginConfig, messages, databaseManager);
		getServer().getPluginManager().registerEvents(joinListener, this);
		
		long time = System.currentTimeMillis() - start;
		getLogger().info("Bootstrapped in " + time + " ms.");
	}

	@Override
	public void onDisable() {
		if(databaseManager != null)
			databaseManager.shutdown();
	}
	
}

package ru.soknight.dailyrewards.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import ru.soknight.dailyrewards.DailyRewards;
import ru.soknight.dailyrewards.database.DatabaseManager;
import ru.soknight.dailyrewards.database.PlayerProfile;
import ru.soknight.lib.configuration.Configuration;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.format.FloatFormatter;
import ru.soknight.peconomy.PEcoAPI;
import ru.soknight.peconomy.PEconomy;
import ru.soknight.peconomy.configuration.CurrencyInstance;
import ru.soknight.peconomy.database.Wallet;

public class PlayerJoinListener implements Listener {

	private final DailyRewards plugin;
	private final BukkitScheduler scheduler;
	
	private final List<String> rewardMessage;
	
	private final Configuration config;
	private final Messages messages;
	private final DatabaseManager databaseManager;
	
	public PlayerJoinListener(DailyRewards plugin, Configuration config, Messages messages,
			DatabaseManager databaseManager) {
		
		this.plugin = plugin;
		this.scheduler = Bukkit.getScheduler();
		
		this.rewardMessage = messages.getColoredList("reward-message");
		
		this.config = config;
		this.messages = messages;
		this.databaseManager = databaseManager;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		
		// Using async reward giving
		this.scheduler.runTaskAsynchronously(this.plugin, () -> giveReward(player));
	}
	
	private void giveReward(Player player) {
		String name = player.getName();
		
		PlayerProfile profile = databaseManager.getProfile(name);
		boolean isProfileExist = true;
		
		if(profile == null) {
			profile = new PlayerProfile(name);
			isProfileExist = false;
		}
		
		long lastday = profile.getLastDay();
		long current = System.currentTimeMillis() / 86400000;
		int stage = profile.getStageInQueue();
		
		// Sends message about already received reward if is enabled
		if(lastday == current) {
			if(messages.getBoolean("already-received.enabled"))
				messages.getAndSend(player, "already-received.message");
			
			return;
		}
		
		// Sends message about skipped day if it is enabled
		long elapsed = current - lastday;
		if(stage != 0 && elapsed > 1) {
			stage = 0;
			
			if(messages.getBoolean("skipped-day.enabled"))
				messages.getAndSend(player, "skipped-day.message");
			
			return;
		}
		
		// Refreshes data in database
		if(stage >= config.getInt("rewards.max-queue-length"))
			profile.setStageInQueue(1);
		else profile.setStageInQueue(stage + 1);
		
		profile.setLastDay(current);
		
		if(!isProfileExist)
			databaseManager.createProfile(profile);
		else databaseManager.updateProfile(profile);
		
		// Giving the reward
		try {
			PEcoAPI api = PEconomy.getAPI();
			
			String currencyid = config.getString("rewards.currency");
			CurrencyInstance currency = api.getCurrencyByID(currencyid);
			
			String symbol = currency == null ? "?" : currency.getSymbol();
			float reward = calculateReward(player, stage);
			
			if(reward != 0) {
				Wallet wallet = api.addAmount(name, currencyid, reward);
				api.updateWallet(wallet);
				
				String amount = new FloatFormatter('.').shortToString(reward, 2);
				List<String> message = new ArrayList<>();
				
				final int day = stage + 1;
				
				this.rewardMessage
						.forEach(m -> message.add(messages.format(m,
								"%reward%", amount,
								"%currency%", symbol,
								"%day%", day))
						);
				
				message.forEach(m -> player.sendMessage(m));
			}
		} catch (Exception e) {
			plugin.getLogger().severe("Failed to give reward to player " + name + ": " + e.getMessage());
		}
	}
	
	public float calculateReward(Player player, int day) {
		float start = (float) (double) config.getDouble("rewards.start");
		if(start == 0) return 0;
		
		float multiplier = (float) (double) config.getDouble("rewards.multiplier");
		float group = getGroupBonus(player);
		
		float reward = start + start * (multiplier * day) * (1 + group);
		return reward;
	}
	
	private float getGroupBonus(Player p) {
		ConfigurationSection section = config.getFileConfig().getConfigurationSection("group-bonuses");
		Set<String> keys = section.getKeys(false);
		if(keys == null || keys.isEmpty())
			return 0;
		
		float bonus = 0;
		
		for(String g : keys) {
			String perm = "group." + g;
			if(!p.hasPermission(perm))
				continue;
			
			float permBonus = (float) section.getDouble(g);
			
			if(permBonus > bonus)
				bonus = permBonus;
		}
		
		return bonus;
	}
	
}

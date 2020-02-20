package ru.soknight.dailyrewards.handlers;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import ru.soknight.dailyrewards.DailyRewards;
import ru.soknight.dailyrewards.database.DatabaseManager;
import ru.soknight.dailyrewards.database.Profile;
import ru.soknight.dailyrewards.files.Config;
import ru.soknight.dailyrewards.utils.Logger;
import ru.soknight.dailyrewards.utils.Utils;
import ru.soknight.peconomy.PEcoAPI;

public class PlayerHandler implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		
		DatabaseManager dbm = DailyRewards.getInstance().getDBManager();
		Profile profile = dbm.getOrCreate(name);
		
		long lastday = profile.getLastDay();
		long current = Utils.getCurrentDay();
		int stage = profile.getStage();
		
		// Send message about already received if is enabled
		if(lastday == current) {
			if(Config.getBoolean("already-received.enabled"))
				p.sendMessage(Config.getString("already-received.message"));
			return;
		}
		
		// Send message about skipped day if it is enabled
		long dayspassed = current - lastday;
		if(stage != 0 && dayspassed > 1 && Config.getBoolean("skipped-day.enabled")) {
			stage = 0;
			p.sendMessage(Config.getString("skipped-day.message"));
		}
		
		// Refreshing data
		if(stage >= Config.getInt("rewards.max-queue-length")) profile.setStage(1);
		else profile.setStage(stage + 1);
		
		profile.setLastDay(current);
		dbm.update(profile);
		
		// Giving reward
		double dollars = Utils.calculateDollarsReward(p, stage);
		double euro = Utils.calculateEuroReward(p, stage);
		
		try {
			if(dollars != 0) PEcoAPI.addAmount(name, (float) dollars, "dollars");
			if(euro != 0) PEcoAPI.addAmount(name, (float) euro, "euro");
		} catch (Exception ex) {
			Logger.error("PEconomy is not installed, couldn't give reward to " + name + "!");
			return;
		}
		
		List<String> message = Utils.formatRewardMessage(stage + 1, dollars, euro);
		message.forEach(s -> p.sendMessage(s));
		return;
	}
	
}

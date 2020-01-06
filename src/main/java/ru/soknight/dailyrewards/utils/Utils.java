package ru.soknight.dailyrewards.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import ru.soknight.dailyrewards.files.Config;

public class Utils {
	
	private static DecimalFormat df = new DecimalFormat("#0.00");
	
	public static long getCurrentDay() {
		return System.currentTimeMillis() / 86400000;
	}
	
	public static List<String> formatRewardMessage(int day, double dollars, double euro) {
		List<String> output = new ArrayList<>(), format = Config.REWARD_MESSAGE;
		String fd = formatDouble(dollars), ed = formatDouble(euro), daystr = "" + day;
		
		format.forEach(s -> {
			output.add(s.replace("%dollars%", fd).replace("%euro%", ed).replace("%day%", daystr));
		});
		return output;
	}
	
	private static String formatDouble(double source) {
		if(source == 0) return "0";
		
		String raw = df.format(source);
		String[] parts = raw.split(",");
		
		if(parts[1].length() == 2 && parts[1].equals("00")) raw = parts[0];
		return raw;
	}
	
	public static double calculateDollarsReward(Player p, int day) {
		double s = Config.getDouble("rewards.start.dollars");
		if(s == 0) return 0;
		
		double k = Config.getDouble("rewards.multiplier");
		double g = getBonus(p);
		
		double reward = s + (s * (k * day) * (1 + g));
		return reward;
	}
	
	public static double calculateEuroReward(Player p, int day) {
		double s = Config.getDouble("rewards.start.euro");
		if(s == 0) return 0;
		
		double k = Config.getDouble("rewards.multiplier");
		double g = getBonus(p);
		
		double reward = s + (s * (k * day) * (1 + g));
		return reward;
	}
	
	private static double getBonus(Player p) {
		double bonus = 0;
		for(String perm : Config.bonuses.keySet()) {
			if(!p.hasPermission(perm)) continue;
			double pbonus = Config.bonuses.get(perm);
			if(pbonus > bonus) bonus = pbonus;
		}
		return bonus;
	}
	
}

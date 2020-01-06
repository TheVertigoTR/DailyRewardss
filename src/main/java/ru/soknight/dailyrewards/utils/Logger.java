package ru.soknight.dailyrewards.utils;

import ru.soknight.dailyrewards.DailyRewards;

public class Logger {
	
	public static void infoWithoutPrefix(String info) {
		java.util.logging.Logger.getLogger("Minecraft").info(info);
		return;
	}
	
    public static void info(String info) {
        DailyRewards.getInstance().getLogger().info(info);
        return;
    }
    
    public static void warning(String warn) {
        DailyRewards.getInstance().getLogger().warning(warn);
        return;
    }
    
    public static void error(String error) {
        DailyRewards.getInstance().getLogger().severe(error);
        return;
    }
    
}

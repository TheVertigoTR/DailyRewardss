package ru.soknight.dailyrewards.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import ru.soknight.dailyrewards.DailyRewards;
import ru.soknight.dailyrewards.utils.Logger;

public class DatabaseManager {
	
	private static Map<String, Profile> profiles = new HashMap<>();
	
    public static void loadFromDatabase() {
    	Database db = DailyRewards.getInstance().getDatabase();
		String query = "SELECT player, lastday, stage FROM times";
		
		try {
			Connection connection = db.getConnection();
			Statement statement = connection.createStatement();
			
			ResultSet output = statement.executeQuery(query);
			long start = System.currentTimeMillis();
			while(output.next()) {
				String name = output.getString("player");
				long lastday = output.getLong("lastday");
				int stage = output.getInt("stage");
				// Setup profile for user
				Profile profile = new Profile(name, lastday, stage);
				profiles.put(name, profile);
			}
			long current = System.currentTimeMillis();
			Logger.info("Loaded " + profiles.size() + " profiles. Time took: " + (current - start) + " ms.");
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public static void saveToDatabase() {
    	if(profiles.isEmpty()) {
    		Logger.info("Profiles list empty, skiped saving to database.");
    		return;
    	}
    	
		Database db = DailyRewards.getInstance().getDatabase();
		String insert = "INSERT INTO times (player, lastday, stage) VALUES (?, ?, ?);";
		String clean = "DELETE FROM times;";
		
		try {
			Connection connection = db.getConnection();
			connection.prepareStatement(clean).executeUpdate();
			
			PreparedStatement stm = connection.prepareStatement(insert);
			for(String name : profiles.keySet()) {
				Profile profile = profiles.get(name);
				stm.setString(1, name);
				stm.setLong(2, profile.getLastDay());
				stm.setInt(3, profile.getStage());
				stm.executeUpdate();
				stm.clearParameters();
			}
			stm.close();
			
			Logger.info(profiles.size() + " profiles saved to database.");
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    //
    // Profiles
    //
    
    public static Profile getProfile(String name) {
    	if(hasProfile(name)) return profiles.get(name);
    	else return new Profile(name);
    }
    
    public static boolean hasProfile(String name) {
    	return profiles.containsKey(name);
    }
    
    public static void setProfile(String name, Profile profile) {
		profiles.put(name, profile);
	}
    
    public static void removeProfile(String name) {
		profiles.remove(name);
	}
	
}

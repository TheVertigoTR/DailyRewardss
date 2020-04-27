package ru.soknight.dailyrewards.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import ru.soknight.dailyrewards.DailyRewards;

public class DatabaseManager {
	
	private final Logger logger;
	private final ConnectionSource source;
	private final Dao<PlayerProfile, String> profilesDao;
	
	public DatabaseManager(DailyRewards plugin, Database database) throws SQLException {
		this.logger = plugin.getLogger();
		this.source = database.getConnection();
		
		this.profilesDao = DaoManager.createDao(source, PlayerProfile.class);
	}
	
	public void shutdown() {
		try {
			this.source.close();
			this.logger.info("Database connection closed.");
		} catch (IOException e) {
			this.logger.severe("Failed to close database connection: " + e.getLocalizedMessage());
		}
	}
	
	/*
	 * Players profiles
	 */
	
	public boolean createProfile(PlayerProfile profile) {
		try {
			return this.profilesDao.create(profile) != 0;
		} catch (SQLException e) {
			logger.severe("Failed to create profile of player '" + profile.getPlayer() + "': " + e.getMessage());
			return false;
		}
	}
	
	public PlayerProfile getProfile(String player) {
		try {
			return this.profilesDao.queryForId(player);
		} catch (SQLException e) {
			logger.severe("Failed to get profile for player '" + player + "': " + e.getMessage());
			return null;
		}
	}
	
	public boolean hasProfile(String player) {
		return getProfile(player) != null;
	}
	
	public boolean updateProfile(PlayerProfile profile) {
		try {
			return this.profilesDao.update(profile) != 0;
		} catch (SQLException e) {
			logger.severe("Failed to update profile of player '" + profile.getPlayer() + "': " + e.getMessage());
			return false;
		}
	}
}

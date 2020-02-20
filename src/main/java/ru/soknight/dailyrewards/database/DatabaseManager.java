package ru.soknight.dailyrewards.database;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import ru.soknight.dailyrewards.utils.Logger;

public class DatabaseManager {
	
	private ConnectionSource source;
	private Dao<Profile, String> dao;
	
	public DatabaseManager(Database database) throws SQLException {
		source = database.getConnection();
		dao = DaoManager.createDao(source, Profile.class);
	}
	
	public void shutdown() {
		try {
			source.close();
			Logger.info("Database connection closed.");
		} catch (IOException e) {
			Logger.error("Failed close database connection: " + e.getLocalizedMessage());
		}
	}
	
	public Profile getOrCreate(String name) {
		try {
			Profile profile = dao.queryForId(name);
			if(profile != null) return profile;
			
			profile = new Profile(name);
			create(profile);
			return profile;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int create(Profile profile) {
		try {
			return dao.create(profile);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int update(Profile profile) {
		try {
			return dao.update(profile);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
}

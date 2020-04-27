package ru.soknight.dailyrewards.database;

import java.io.File;
import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import ru.soknight.dailyrewards.DailyRewards;
import ru.soknight.lib.configuration.Configuration;

public class Database {

	private final String url;
	private final boolean useSQLite;
	
	private	String user;
	private String password;
	
	public Database(DailyRewards plugin, Configuration config) throws Exception {
		this.useSQLite = config.getBoolean("database.use-sqlite", true);
		if(!useSQLite) {
			String host = config.getString("database.host", "localhost");
			String name = config.getString("database.name", "dailyrewards");
			int port = config.getInt("database.port", 3306);
			this.user = config.getString("database.user", "admin");
			this.password = config.getString("database.password", "dailyrewards");
			this.url = "jdbc:mysql://" + host + ":" + port + "/" + name;
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} else {
			String file = config.getString("database.file", "database.db");
			this.url = "jdbc:sqlite:" + plugin.getDataFolder().getPath() + File.separator + file;
			Class.forName("org.sqlite.JDBC").newInstance();
		}
		
		// Allowing only ORMLite errors logging
		System.setProperty("com.j256.ormlite.logger.type", "LOCAL");
		System.setProperty("com.j256.ormlite.logger.level", "ERROR");
				
		ConnectionSource source = getConnection();

		TableUtils.createTableIfNotExists(source, PlayerProfile.class);
		
		source.close();
		
		plugin.getLogger().info("Database type " + (useSQLite ? "SQLite" : "MySQL") + " connected!");
	}
	
	public ConnectionSource getConnection() throws SQLException {
		return useSQLite ? new JdbcConnectionSource(url) : new JdbcConnectionSource(url, user, password);
	}
	
}

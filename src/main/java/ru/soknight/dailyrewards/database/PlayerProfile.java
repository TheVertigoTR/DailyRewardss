package ru.soknight.dailyrewards.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "profiles")
public class PlayerProfile {

	@DatabaseField(id = true)
	private String player;
	@DatabaseField(columnName = "lastday")
	private long lastDay;
	@DatabaseField
	private int stageInQueue;
	
	public PlayerProfile(String player) {
		this.player = player;
		this.lastDay = 0;
		this.stageInQueue = 0;
	}
	
}

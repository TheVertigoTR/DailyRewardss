package ru.soknight.dailyrewards.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "profiles")
public class Profile {

	@DatabaseField(id = true)
	private String name;
	@DatabaseField(columnName = "lastday")
	private long lastDay;
	@DatabaseField
	private int stage;
	
	public Profile(String name) {
		this.name = name;
		this.lastDay = 0;
		this.stage = 0;
	}
	
}

package ru.soknight.dailyrewards.database;

public class Profile {

	private String name;
	private long lastDay;
	private int stage;
	
	public Profile(String name, long lastDay, int stage) {
		this.name = name;
		this.lastDay = lastDay;
		this.stage = stage;
	}
	
	public Profile(String name) {
		this.name = name;
		this.lastDay = 0;
		this.stage = 0;
	}

	public String getName() {
		return name;
	}

	public long getLastDay() {
		return lastDay;
	}

	public void setLastDay(long lastDay) {
		this.lastDay = lastDay;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}
	
}

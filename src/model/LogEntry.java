package model;

public class LogEntry{
	public Double batteryLevel;
	public Integer duration;
	public Double timeStamp;
	public Integer fullCapacity;
	
	@Override
	public String toString() {
		return String.valueOf(batteryLevel) + " " + 
				String.valueOf(duration) + " " +
				String.valueOf(timeStamp);
	}
}

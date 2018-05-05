package model;

public class TestResult{
	
	public Integer drainedCapacity;
	public Integer fullCapacity;
	public long onlineTime;
	public Double result; //battery drain(%)
	
	public TestResult(){
		drainedCapacity = 0;
		fullCapacity = 0;
		onlineTime = 0;
		result = 0.0;
	}
	
	@Override
	public String toString() {
		return "\n\tDrained cap.: " + String.valueOf(drainedCapacity) +
				"\n\tFull cap.: " + String.valueOf(fullCapacity) +
				"\n\tOnline time: " + String.valueOf(onlineTime) +
				"\n\tResult (%/1h): " + String.valueOf(result);
	}
}

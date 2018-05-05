package base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.CumulativeTestResult;
import model.LogEntry;
import model.Printer;
import model.TestResult;
import util.MapUtil;

public class LogProcessor {
	
    private Map<String, List<LogEntry>> logEntries = new HashMap<>();
    private Map<String, TestResult> testResults;
	
	public Map<Integer, Double> execute(String folderPath) throws IOException{

		List<String> fileNames = listFilesForFlatFolder(new File(folderPath));

		for(String fileName: fileNames) {

		    String patternString = ".DS_Store";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(fileName);
            if(matcher.matches()){ // ignore DS_Store files
                continue;
            }

			String jsonStr = readFile(folderPath + "/" + fileName);
			testResults = parseLogFile(jsonStr);
		}

        Map<Integer, Double> plotData = new HashMap<>();

		if(testResults == null){
		    return plotData;
        }

        Printer.printMap(testResults);

		Map<Integer, CumulativeTestResult> aTestResults = cumulateResults();
		
		for(Map.Entry<Integer, CumulativeTestResult> entry : aTestResults.entrySet()) {
			CumulativeTestResult aPeerResult = entry.getValue();

			Double sumLocalResult = 0.0;
			for(int i = 0; i < aPeerResult.count; ++i) {
				sumLocalResult += aPeerResult.localResults.get(i);
			}
			
			Integer keyCapacity = entry.getKey();
			Double globResult = sumLocalResult / aPeerResult.count;
			
			plotData.put(keyCapacity, globResult);
		}
		
		Printer.printMap(plotData);
		
		return MapUtil.sort(plotData);
	}

	private Map<Integer, CumulativeTestResult> cumulateResults() {
		//aggregate on capacity
		Map<Integer, CumulativeTestResult> cTestResults = new HashMap<>();

		for (Map.Entry<String, TestResult> entry : testResults.entrySet())
		{
			String peerID = entry.getKey();
			TestResult testResult = entry.getValue();
			Integer fullCapacity = testResult.fullCapacity;
			Double localResult = testResult.result;
			
			CumulativeTestResult aPeerResult;
				
			if(cTestResults.containsKey(fullCapacity)) {
				aPeerResult = cTestResults.get(fullCapacity);
			}else {
				aPeerResult = new CumulativeTestResult();
			}
			aPeerResult.count++;
			aPeerResult.peerIDs.add(peerID);
			aPeerResult.localResults.add(localResult);
			cTestResults.put(fullCapacity, aPeerResult);
		}
		
		return cTestResults;
	}

	private static List<String> listFilesForFlatFolder(final File folder){
		List<String> fileNames = new ArrayList<>();
	    for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
	    	fileNames.add(fileEntry.getName());
	    }
	    return fileNames;
	}

	private Map<String, TestResult> parseLogFile(String jsonStr) {
		try {
	        JSONObject rootObject = new JSONObject(jsonStr);
	        JSONArray rows = rootObject.getJSONArray("logs");

	        for(int i=0; i < rows.length(); i++) {
	        	JSONObject row = rows.getJSONObject(i);
	        	LogEntry entry = new LogEntry();
	            String key = row.getString("id");
	            entry.batteryLevel = row.getDouble("batteryLevel");
	            entry.duration = row.getInt("duration");
	            entry.timeStamp = row.getDouble("timeStamp");
	            entry.fullCapacity = row.getInt("fullCapacity");
	            
	            List<LogEntry> groupedEntries;
	            
	            if(logEntries.containsKey(key)) {
	            	groupedEntries = logEntries.get(key);
	            }else {
	            	groupedEntries = new ArrayList<>();
	            }
	            
	            groupedEntries.add(entry);
            	logEntries.put(key, groupedEntries);
	        }
	        
	    } catch (JSONException e) {
	        // JSON Parsing error
	        e.printStackTrace();
	    }
	
	    return gatherPeerResults();
	}

	private Map<String, TestResult> gatherPeerResults() {
		Map<String, TestResult> peerResults = new HashMap<>();
		
		for (Map.Entry<String, List<LogEntry>> entry : logEntries.entrySet())
		{
			TestResult testResult = new TestResult();
			
			List<LogEntry> groupedEntries = entry.getValue();
			long sumDuration = 0;
			Integer tmpMaxDuration = -1;
			
			int i = 0;
			int numEntries = groupedEntries.size();
			for(; i < numEntries; ++i) {
				LogEntry e = groupedEntries.get(i);
				if(e.duration < tmpMaxDuration) {
					sumDuration += tmpMaxDuration;
					tmpMaxDuration = e.duration;
				}else {
					tmpMaxDuration = e.duration;
				}
			}
			
			LogEntry lastEntry = groupedEntries.get(numEntries-1);
			sumDuration += lastEntry.duration;
			
			Double lastRecordedBatteryLevel = lastEntry.batteryLevel;
			
			testResult.fullCapacity = lastEntry.fullCapacity;
			testResult.drainedCapacity = (int) ((1.0-lastRecordedBatteryLevel) * testResult.fullCapacity);
			testResult.onlineTime = sumDuration;
			testResult.result = (1.0-lastRecordedBatteryLevel) / (lastEntry.timeStamp / 60);
			peerResults.put(entry.getKey(), testResult);
		}
		
		return peerResults;
	}

	private static String readFile(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        }
	}
}

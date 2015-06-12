import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class Benchmark {
	
	static String dirname = "D:\\Users\\hanson\\Desktop\\data\\uniform\\5_82_5c_10t_ 1000r_100000o_rl1_wl1_500ops\\";
	static String num = "3";
	
	
	static int replica_num = 5;
	
	public static void main(String[] args) throws IOException {
		//deltaAndGamma();
		beta();
		tInterval();
	}
	
	
	public static void deltaAndGamma() throws IOException {
		File logFile = new File(dirname + "client" + num + ".log");
		calculate(logFile);
	}
	
	public static void beta() throws IOException {
		File logFile = new File(dirname + "beta" + num + ".log");
		calculate(logFile);
	}
	
	public static void tInterval() throws IOException {
		File logFile = new File(dirname + "staleness" + num + ".log");
		ArrayList<Long> al = calInterval(logFile);
		//HashMap<String, HashMap<String, Interval>> map = calInterval(logFile);
		long max = 0;
		long min = Long.MAX_VALUE;
//		String key = "";
//		String value = "";
		long total = 0;
		for (long num : al) {
			max = Math.max(max, num);
			min = Math.min(min, num);
			total += num;
		}
		
//		Iterator it = map.entrySet().iterator();
//		while (it.hasNext()) {
//			Map.Entry entry = (Entry) it.next();
//			HashMap<String, Interval> m = (HashMap<String, Interval>) entry.getValue();
//			Iterator it1 = m.entrySet().iterator();
//			while (it1.hasNext()) {
//				Map.Entry entry1 = (Entry) it1.next();
//				Interval itv = (Interval) entry1.getValue();
//				if (itv.getCount() == 3) {
//					total += itv.getStart();
//					count++;
//					//System.out.println(itv.getStart());
//				}
//				if (itv.getCount() == 3 && max < itv.getStart()) {
//					max = itv.getStart();
//					key = (String) entry.getKey();
//					value = (String) entry1.getKey();
//				}
//			}
//		}
		System.out.println("max :" + max + "  min:" + min + "  avg:" + total / al.size());

	}
	
	public static HashMap<String, Boolean> readKey(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
	    try {
	        String line = br.readLine();
	        HashMap<String, Boolean> keys = new HashMap<String, Boolean>();
	        while (line != null) {
	        	int index = line.indexOf("#");
	        	if (index < 0) continue;
	        	String key = line.substring(0, index);
	        	if (!keys.containsKey(key)) {
	        		keys.put(key, true);
	        	}
	            line = br.readLine();
	        }
	        
	        return keys;
	    } finally {
	        br.close();
	    }
	}
	
	public static void calculate(File logFile) throws IOException {
		Zone dz1 = null;
		Zone dz2 = null;
		
		Zone gz1 = null;
		Zone gz2 = null;
		
		long dtotal = 0;
		long dnotAto = 0;
		long gtotal = 0;
		long gnotAto = 0;
		
		long delta = 0;
		long gamma = 0;
		HashMap<String, Boolean> keys = readKey(logFile);
		Iterator<String> it = keys.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			BufferedReader br = new BufferedReader(new FileReader(logFile));
			HashMap<String, ArrayList<LogEntry>> hm = new HashMap<String, ArrayList<LogEntry>>();
			String line = br.readLine();
			while (line != null) {
				int othersLen = 30;
				if (line.length() < othersLen) continue;
				int index = line.indexOf("#");
				if (index < 0) continue;
				String keyStr = line.substring(0, index);
				String valStr = line.substring(index + 1, line.length() - 30);
				String others = line.substring(line.length() - 29);
				if (keyStr.equals(key)) {
					String[] splits = others.split("#");
					assert(splits.length == 3);
					ArrayList<LogEntry> al = null;
					if (hm.containsKey(valStr)) {
						al = hm.get(valStr);
					} else {
						al = new ArrayList<LogEntry>();
					}
					LogEntry log = new LogEntry(keyStr, valStr, 
							Integer.parseInt(splits[0]), 
							Long.parseLong(splits[1]), 
							Long.parseLong(splits[2]));
					al.add(log);
					hm.put(valStr, al);
				}
				line = br.readLine();
			}
			ArrayList<Zone> zoneList = Calculator.generateZone(hm);
			Result result = Calculator.calculateDelta(zoneList);
			Result result1 = Calculator.calculateGamma(zoneList);
			dtotal += result.total;
			dnotAto += result.count;
			gtotal += result1.total;
			gnotAto += result1.count;
//			delta = Math.max(delta, result[2]);
//			gamma = Math.max(gamma, result1[2]);
			if (delta < result.score) {
				delta = result.score;
				dz1 = result.z1;
				dz2 = result.z2;

			}
			
			if (gamma < result1.score) {
				gamma = result1.score;
				gz1 = result1.z1;
				gz2 = result1.z2;

			}
		}
		System.out.println("delta:" + delta + "  total:" + dtotal + "  notAto:" + dnotAto);
		System.out.println(dz1);
		System.out.println(dz2);
		System.out.println("gamma:" + gamma + "  total:" + gtotal + "  notAto:" + gnotAto);
		System.out.println(gz1);
		System.out.println(gz2);
	}
	
	
	public static ArrayList<Long> calInterval(File file) throws IOException {
		ArrayList<Long> al = new ArrayList<Long>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		int n = replica_num;
	    try {
	        String line = br.readLine();
	        HashMap<String, HashMap<String, Interval>> map = 
	        		new HashMap<String, HashMap<String,Interval>>();
	        while (line != null) {
	        	int timesLen = 14;
				if (line.length() < timesLen) continue;
				int index = line.indexOf("#");
				if (index < 0) continue;
				String keyStr = line.substring(0, index);
				String valStr = line.substring(index + 1, line.length() - 14);
				long time = Long.parseLong(line.substring(line.length() - 13));
				if (map.containsKey(keyStr)) {
					HashMap<String, Interval> intervals = map.get(keyStr);
					Interval itv = null;
					if (intervals.containsKey(valStr)) {
						itv = intervals.get(valStr);
						itv.setStart(Math.min(itv.getStart(), time));
						itv.setEnd(Math.max(itv.getEnd(), time));
						itv.setCount(itv.getCount() + 1);
					} else {
						itv = new Interval(time, time, 1);
						intervals.put(valStr, itv);
					}
					if (itv.getCount() == n) {
						al.add(itv.getEnd() - itv.getStart());
						//itv.setStart(itv.getEnd() - itv.getStart());
					}
				} else {
					HashMap<String, Interval> intervals = new HashMap<String, Interval>();
					Interval itv = new Interval(time, time, 1);
					intervals.put(valStr, itv);
					map.put(keyStr, intervals);
					if (itv.getCount() == n) {
						al.add(itv.getEnd() - itv.getStart());
						//itv.setStart(itv.getEnd() - itv.getStart());
					}
				}
	            line = br.readLine();
	        }
	        return al;
	        

	    } finally {
	        br.close();
	    }
	}
	

//	public static void main(String[] args) {
//		//test case
//		//two forward
////		LogEntry l1 = new LogEntry("a", "b", 1, 1, 2);
////		LogEntry l2 = new LogEntry("a", "b", 0, 9, 11);
////		
////		LogEntry l3 = new LogEntry("a", "c", 1, 3, 4);
////		LogEntry l4 = new LogEntry("a", "c", 0, 5, 6);
//		
//		//forward & backward
//		LogEntry l1 = new LogEntry("a", "b", 1, 1, 2);
//		LogEntry l2 = new LogEntry("a", "b", 0, 9, 11);
//		
//		LogEntry l3 = new LogEntry("a", "c", 1, 3, 5);
//		LogEntry l4 = new LogEntry("a", "c", 0, 4, 8);
//		
//		//two backward
////		LogEntry l1 = new LogEntry("a", "b", 1, 1, 9);
////		LogEntry l2 = new LogEntry("a", "b", 0, 2, 11);
////		
////		LogEntry l3 = new LogEntry("a", "c", 1, 3, 7);
////		LogEntry l4 = new LogEntry("a", "c", 0, 5, 8);
//		
//		HashMap<String, ArrayList<LogEntry>> map = new HashMap<String, ArrayList<LogEntry>>();
//		ArrayList<LogEntry> al = new ArrayList<LogEntry>();
//		al.add(l1);
//		al.add(l2);
//		map.put("b", al);
//		
//		al = new ArrayList<LogEntry>();
//		al.add(l3);
//		al.add(l4);
//		map.put("c", al);
//		
//		ArrayList<Zone> zlist = Calculator.generateZone(map);
//		long[] result = Calculator.calculateGamma(zlist);
//		System.out.print(result[0] + " " + result[1] + " " + result[2]);
//	}

}

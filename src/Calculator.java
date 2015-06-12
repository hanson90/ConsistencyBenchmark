import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class Calculator {
	
	public static Result calculateDelta(ArrayList<Zone> zoneList) {		
		long delta = 0;
		
		Zone dz1 = null;
		Zone dz2 = null;
		
		HashMap<Zone, Boolean> hm = new HashMap<Zone, Boolean>();
		
		for (int i = 0; i < zoneList.size(); i++) {
			Zone z1 = zoneList.get(i);
			for (int j = i + 1; j < zoneList.size(); j++) {
				Zone z2 = zoneList.get(j);
				long score = getTwoZoneDeltaScore(z1, z2);
				if (score > 0) {
					if (!hm.containsKey(z1)) {
						hm.put(z1, true);
					}
					if (!hm.containsKey(z2)) {
						hm.put(z2, true);
					}
				}
//				delta = Math.max(score, delta);
				if (score > delta) {
					delta = score;
					dz1 = z1;
					dz2 = z2;
				}
			}
		}
		return new Result(zoneList.size(), hm.size(), delta, dz1, dz2);
	}
	
	public static Result calculateGamma(ArrayList<Zone> zoneList) {
		long gamma = 0;
		
		Zone gz1 = null;
		Zone gz2 = null;
		
		HashMap<Zone, Boolean> hm = new HashMap<Zone, Boolean>();
		
		for (int i = 0; i < zoneList.size(); i++) {
			Zone z1 = zoneList.get(i);
			for (int j = i; j < zoneList.size(); j++) {
				Zone z2 = zoneList.get(j);
				long score = getTwoZoneGammaScore(z1, z2);
				if (score > 0) {
					if (!hm.containsKey(z1)) {
						hm.put(z1, true);
					}
					if (!hm.containsKey(z2)) {
						hm.put(z2, true);
					}
				}
//				gamma = Math.max(score, gamma);
				
				if (score > gamma) {
					gamma = score;
					gz1 = z1;
					gz2 = z2;
				}
				
			}
		}
		return new Result(zoneList.size(), hm.size(), gamma, gz1, gz2);
	}
	
	public static long getTwoZoneGammaScore(Zone z1, Zone z2) {
		if (z1 == z2) {//the same zone
			return z1.getWriteStartTime() > z1.getLeft() ? z1.getWriteStartTime() - z1.getLeft() : 0;
		} else if (!z1.isForward() && !z2.isForward()) {
			return 0;
		} else if (z1.isForward() && z2.isForward()) {
			if (z1.getLeft() > z2.getRight() || z1.getRight() <= z2.getLeft()) {
				return 0;
			} else {
				return Math.min(Math.abs(z1.getLeft() - z2.getRight()), 
								Math.abs(z1.getRight() - z2.getLeft()));
			}
		} else {
			if (z1.isForward()) {
				if (z1.getLeft() < z2.getLeft() && z1.getRight() > z2.getRight()) {
					return Math.min(Math.abs(z1.getLeft() - z2.getLeft()),
									Math.abs(z1.getRight() - z2.getRight()));
				} else {
					return 0;
				}
			} else {
				if (z2.getLeft() < z1.getLeft() && z2.getRight() > z2.getRight()) {
					return Math.min(Math.abs(z1.getLeft() - z2.getLeft()),
									Math.abs(z1.getRight() - z2.getRight()));
				} else {
					return 0;
				}
			}
			
		}		
	}
	
	public static long getTwoZoneDeltaScore(Zone z1, Zone z2) {
		if (!z1.isForward() && !z2.isForward()) {//two backward zone
			return 0;
		} else if (z1.isForward() && z2.isForward()) {//two forward zone
			if (z1.getLeft() >= z2.getRight() || z1.getRight() <= z2.getLeft()) {//not overlap
				return 0;
			} else {
				return Math.min(Math.abs(z1.getLeft() - z2.getRight()), 
								Math.abs(z1.getRight() - z2.getLeft()));
			}
		} else {//one forward, one backward
			if (z1.isForward()) {
				if (z1.getLeft() < z2.getLeft() && z1.getRight() > z2.getRight()) {
					if (z2.getWriteStartTime() != 0 && z2.getWriteStartTime() < z1.getLeft()) {
						return Math.min(Math.abs(z1.getLeft() - z2.getLeft()), 
										Math.abs(z1.getRight() - z2.getRight()));
					} else {
						return Math.abs(z1.getRight() - z2.getRight());
					}
				} else {
					return 0;
				}
				
			} else {
				if (z2.getLeft() < z1.getLeft() && z2.getRight() > z2.getRight()) {
					if (z1.getWriteStartTime() != 0 && z1.getWriteStartTime() < z2.getLeft()) {
						return Math.min(Math.abs(z1.getLeft() - z2.getLeft()), 
								Math.abs(z1.getRight() - z2.getRight()));
					} else {
						return Math.abs(z1.getRight() - z2.getRight());
					}
				} else {
					return 0;
				}
				
				
			}
		}
	}
	
	/**
	 * generate all zone for one key
	 * @param map(value-->log)
	 * @return
	 */
	public static ArrayList<Zone> generateZone(HashMap<String, ArrayList<LogEntry>> map) {
		Iterator it = map.entrySet().iterator();
		ArrayList<Zone> zoneList = new ArrayList<Zone>();
		while(it.hasNext()) {
			long maxStartTime = 0;
			long minEndTime = Long.MAX_VALUE;
			long writeStartTime = 0;
			Map.Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			ArrayList<LogEntry> al = (ArrayList<LogEntry>) entry.getValue();
			for (int i = 0; i < al.size(); i++) {
				maxStartTime = Math.max(maxStartTime, al.get(i).getStime());
				minEndTime = Math.min(minEndTime, al.get(i).getEtime());
				if (al.get(i).getType() == 1) {//write operation
					assert(writeStartTime == 0);
					writeStartTime = al.get(i).getStime();
				}
			}
			Zone zone = new Zone();
			zone.setValue(key);
			if (maxStartTime >= minEndTime) {
				zone.setLeft(minEndTime);
				zone.setRight(maxStartTime);
				zone.setForward(true);
				zone.setWriteStartTime(writeStartTime);
			} else {
				zone.setLeft(maxStartTime);
				zone.setRight(minEndTime);
				zone.setForward(false);
				zone.setWriteStartTime(writeStartTime);
			}
			zoneList.add(zone);
			
		}
		return zoneList;
	}

}

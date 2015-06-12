
public class Interval {
	
	private long start;
	private long end;
	private int count;
	
	public Interval(long start, long end, int count) {
		this.start = start;
		this.end = end;
		this.count = count;
	}
	
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	
}


public class LogEntry{
	
	private String key;
	private String value;
	private int type;
	private long stime;
	private long etime;
	
	public LogEntry(String key, String value, int type, long stime, long etime) {
		this.key = key;
		this.value = value;
		this.type = type;
		this.stime = stime;
		this.etime = etime;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getStime() {
		return stime;
	}
	public void setStime(long stime) {
		this.stime = stime;
	}
	public long getEtime() {
		return etime;
	}
	public void setEtime(long etime) {
		this.etime = etime;
	}

}


public class Zone {
	
	private String value;
	private long left;
	private long right;
	private boolean isForward;
	private long writeStartTime;
	
	public String getValue() {
		return value;
	}
	public long getLeft() {
		return left;
	}
	public void setLeft(long left) {
		this.left = left;
	}
	public long getRight() {
		return right;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setRight(long right) {
		this.right = right;
	}
	public boolean isForward() {
		return isForward;
	}
	public void setForward(boolean isForward) {
		this.isForward = isForward;
	}
	public long getWriteStartTime() {
		return writeStartTime;
	}
	public void setWriteStartTime(long writeStartTime) {
		this.writeStartTime = writeStartTime;
	}
	
	public String toString() {
		return "value:" + value + "  [" + left + ", " + right + "]" + "  isForward:" + isForward;
	}
	
	

}

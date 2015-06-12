
public class Result {
	
	public final long total;
	public final long count;
	public final long score;
	public final Zone z1;
	public final Zone z2;
	
	public Result(long t, long c, long s, Zone z1, Zone z2) {
		total = t;
		count = c;
		score = s;
		this.z1 = z1;
		this.z2 = z2;
	}

}

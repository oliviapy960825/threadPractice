package AtomicOperations;

public class MinMaxMetrics {
	private volatile long minValue;
	private volatile long maxValue;
	
	public MinMaxMetrics(){
		this.maxValue = Long.MIN_VALUE;
		this.minValue = Long.MAX_VALUE;
	}
	public void addSample(long newSample){
		synchronized(this){
			this.minValue = Math.min(minValue, newSample);
			this.maxValue = Math.max(maxValue, newSample);
		}
	}
	
	public long getMin(){
		return this.minValue;
	}
	
	public long getMax(){
		return this.maxValue;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

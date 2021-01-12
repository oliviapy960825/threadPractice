package AtomicOperations;

import java.util.Random;

public class Metrics {
	private long count = 0;
	private volatile double average = 0.0;
	public static class MetricsPrinter extends Thread{
		private Metrics metrics;
		
		public MetricsPrinter(Metrics metrics){
			this.metrics = metrics;
		}
		
		public void run(){
			while(true){
				try{
					Thread.sleep(100);
				}
				catch(InterruptedException e){
					
				}
				
				double currentAverage = metrics.getAverage();
				
				System.out.println("Current average is " + currentAverage);
			}
		}
	}
	public static class BusinessLogic extends Thread{
		private Metrics metrics;
		private Random random = new Random();
		
		public BusinessLogic(Metrics metrics){
			this.metrics = metrics;
		}
		
		public void run(){
			while(true){
			long start = System.currentTimeMillis();
			
			try{
				Thread.sleep(random.nextInt(10));
			}
			catch(InterruptedException e){
				long end = System.currentTimeMillis();
				metrics.addSample(end - start);
			}
		}
		}
	}
	public void addSample(long sample){
		double currSum = average * count;
		count++;
		average = (currSum + sample) / count;
	}
	
	public double getAverage(){
		return average;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Metrics metrics = new Metrics();
		BusinessLogic businessLogicThread1 = new BusinessLogic(metrics);
		BusinessLogic businessLogicThread2 = new BusinessLogic(metrics);
		
		MetricsPrinter metricsPrinter = new MetricsPrinter(metrics);
		
		businessLogicThread1.start();
		businessLogicThread2.start();
		metricsPrinter.start();
	}

}

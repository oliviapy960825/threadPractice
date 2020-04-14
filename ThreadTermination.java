package thread.creation.example;

import java.math.BigInteger;

public class ThreadTermination {
private static class BlockingTask implements Runnable{
	@Override
	public void run(){
		try {
			Thread.sleep(500000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Exiting blocking thread");
	}
}
	private static class LongComputationTask implements Runnable{
		private BigInteger base;
		private BigInteger power;
		public LongComputationTask(BigInteger base, BigInteger power){
			this.base=base;
			this.power=power;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println(base+"^"+power+" = "+pow(base,power));
		}
		private BigInteger pow(BigInteger base, BigInteger power){
			BigInteger result=BigInteger.ONE;
			
			for(BigInteger i=BigInteger.ZERO;i.compareTo(power)!=0;i=i.add(BigInteger.ONE)){
				if (Thread.currentThread().isInterrupted()){
					System.out.println("Prematuraely interrupted computation");
					return BigInteger.ZERO;
				}
				result=result.multiply(base);
			}
			return result;
			
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Thread thread=new Thread(new BlockingTask());
		//	thread.start();
		//	thread.interrupt();
			
		Thread thread=new Thread(new LongComputationTask(new BigInteger("2"),new BigInteger("10")));
		thread.setDaemon(true);//if we don't care about handling the thread interruption gracefully
		//Daemon threads are background threads that do not prevent the application from exiting
		//if the main thread terminates
		thread.start();
		thread.interrupt();
	}

}

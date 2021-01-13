package ReentrantLockExample;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PriceContainer {
	private Lock lockObject = new ReentrantLock();
	private double bitcoinPrice;
	private double etherPrice;
	private double litecoinPrice;
	private double bitcoinCashPrice;
	private double ripplePrice;
	
	public Lock getLock(){
		return this.lockObject;
	}
	public void setRipplePrice(double num){
		this.ripplePrice = num;
	}
	
	public void setLitecoinPrice(double num){
		this.litecoinPrice = num;
	}
	
	public void setBitcoinCashPrice(double num){
		this.bitcoinCashPrice = num;
	}
	
	public void setEtherPrice(double num){
		this.etherPrice = num;
	}
	
	public void setBitcoinPrice(double num){
		this.bitcoinPrice = num;
	}
	
	public static class PriceUpdater extends Thread{
		private PriceContainer priceContainer;
		private Random random = new Random();
		public PriceUpdater(PriceContainer priceContainer){
			this.priceContainer = priceContainer;
		}
		
		@Override
		public void run(){
			while(true){
				priceContainer.getLock().lock();
				
				try{
					try{
						Thread.sleep(1000);
					}
					catch(InterruptedException e){
						
					}
					priceContainer.setBitcoinPrice(random.nextInt(20000));
					priceContainer.setEtherPrice(random.nextInt(20000));
					priceContainer.setBitcoinCashPrice(random.nextInt(20000));
					priceContainer.setLitecoinPrice(random.nextInt(20000));
					priceContainer.setRipplePrice(random.nextInt(20000));
				}
				finally{
					priceContainer.getLock().unlock();
				}
				
				try{
					Thread.sleep(1000);
				}
				catch(InterruptedException e){
					
				}
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

package ReentrantReadWriteLockExample;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InventoryDatabase {
	public static final int HIGHEST_NUMBER = 1000;
	private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
	private ReentrantLock lock = new ReentrantLock();
	private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	private Lock readLock = rwLock.readLock();
	private Lock writeLock = rwLock.writeLock();
	
	public int getNumberOfItemsInPriceRange(int lowerBound, int upperBound){
		//lock.lock();
		readLock.lock();
		try{
		Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
		Integer toKey = priceToCountMap.floorKey(upperBound);
		
		if(fromKey == null || toKey == null) return 0;
		NavigableMap<Integer, Integer> rangeOfPrice = priceToCountMap.subMap(fromKey, true, toKey, true);
		
		int sum = 0;
		for(int numberOfItemsForPrice : rangeOfPrice.values()){
			sum += numberOfItemsForPrice;
		}
		return sum;
		}
		finally{
			//lock.unlock();
			readLock.unlock();
		}
	}
	public void addItem(int price){
		//lock.lock();
		writeLock.lock();
		try{
		Integer numberOfItem = priceToCountMap.get(price);
		if(numberOfItem == null) priceToCountMap.put(price, 1);
		else priceToCountMap.put(price, numberOfItem + 1);
		}
		finally{
			//lock.unlock();
			writeLock.unlock();
		}
	}
	public void removeItem(int price){
		//lock.lock();
		writeLock.lock();
		try{
		Integer numberOfItem = priceToCountMap.get(price);
		if(numberOfItem == null || numberOfItem == 1) priceToCountMap.remove(price);
		else priceToCountMap.put(price, numberOfItem - 1);
		}
		finally{
			//lock.unlock();
			writeLock.unlock();
		}
	}
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		InventoryDatabase id = new InventoryDatabase();
		Random rand = new Random();
		
		for(int i = 0; i < 10000; i++){
			id.addItem(rand.nextInt(HIGHEST_NUMBER));
		}
		
		Thread writer = new Thread(() ->{
			while(true){
				id.addItem(rand.nextInt(HIGHEST_NUMBER));
				id.removeItem(rand.nextInt(HIGHEST_NUMBER));
				
				try{
					Thread.sleep(10);
				}
				catch(InterruptedException e){
					
				}
			}
		});
		writer.setDaemon(true);
		writer.start();
		
		int numberOfThread = 7;
		List<Thread> readers = new ArrayList();
		for(int i = 0; i < numberOfThread; i++){
			Thread reader = new Thread(() ->{
				for(int j = 0; j < 1000; j++){
					int upperBound = rand.nextInt(HIGHEST_NUMBER);
					int lowerBound = upperBound > 0 ? rand.nextInt(upperBound) : 0;
					id.getNumberOfItemsInPriceRange(lowerBound, upperBound);
				}
			});
			
			reader.setDaemon(true);
			readers.add(reader);
		}
		
		long startTime = System.currentTimeMillis();
		for(Thread reader : readers){
			reader.start();
		}
		
		for(Thread reader : readers){
			reader.join();
		}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("The time it took was " + (endTime - startTime));
	}

}

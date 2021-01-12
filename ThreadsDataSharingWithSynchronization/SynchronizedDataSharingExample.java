package ThreadsDataSharingWithSynchronization;

public class SynchronizedDataSharingExample {

	public static void main(String[] args) throws InterruptedException{
		// TODO Auto-generated method stub
		InventoryCounter inventoryCounter = new InventoryCounter();
		IncrementThread it = new IncrementThread(inventoryCounter);
		DecrementThread dt = new DecrementThread(inventoryCounter);
		
		it.start();
		it.join();
		
		dt.start();
		dt.join();
		
		System.out.println("We now have " + inventoryCounter.getInventory() + "items");
		//should be 0
		
		//however, this method will return random number
		it.start();
		dt.start();
		it.join();
		dt.join();
		
		//notes: item++ operation is not atomic. item++ actually include 3 steps, the first one is get the item from stack
		//the second step is to increase the item by 1, the third step is to store it back
		//to the stack. And because stack is not shared among threads, the state of the varianle
		//item is different
		
	}
	public static class InventoryCounter{
		private int inventory;
		
		public synchronized void increment(){
			inventory++;
		}
		public synchronized void decrement(){
			inventory--;
		}
		
		public synchronized int getInventory(){
			return inventory;
		}
	}
	
	public static class IncrementThread extends Thread{
		private InventoryCounter inventoryCounter;
		public IncrementThread(InventoryCounter inventoryCounter){
			this.inventoryCounter = inventoryCounter;
		}
		@Override
		public void run(){
			for(int i = 0; i < 1000; i++){
				inventoryCounter.increment();
			}
		}
	}
	
	public static class DecrementThread extends Thread{
		private InventoryCounter inventoryCounter;
		public DecrementThread(InventoryCounter inventoryCounter){
			this.inventoryCounter = inventoryCounter;
		}
		@Override
		public void run(){
			for(int i = 0; i < 1000; i++){
				inventoryCounter.decrement();
			}
		}
	}
}

package AtomicXExample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LockFreeStack {
	public static class StackNode<T>{
		public T value;
		public StackNode<T> next;
		
		public StackNode(T value){
			this.value = value;
			this.next = next;
		}
	}
	
	public static class StandardStack<T>{
		private StackNode<T> head;
		private int counter = 0;
		
		public synchronized void push(T value){
			StackNode<T> newHead = new StackNode(value);
			newHead.next = head;
			head = newHead;
			counter++;
		}
		
		public synchronized T pop(){
			if(head == null){
				counter++;
				return null;
			}
			
			T value = head.value;
			head = head.next;
			counter++;
			return value;
		}
	
		public int getCounter(){
			return counter;
		}
	}

	public static class LockFreeStack<T>{
		private AtomicReference<StackNode<T>> head = new AtomicReference<>();
		private AtomicInteger counter = new AtomicInteger();
		public void push(T value){
			StackNode<T> newHead = new StackNode(value);
			
			while(true){
				StackNode<T> currHead = head.get();
				newHead.next = currHead;
				if(head.compareAndSet(currHead, newHead)){
					break;
				}
				else LockSupport.parkNanos(1);
			}
			
			counter.incrementAndGet();
		}
		
		public T pop(){
			StackNode<T> node = head.get();
			StackNode<T> newHead;
			
			while(node != null){
				newHead = node.next;
				if(head.compareAndSet(node, newHead)){
					break;
				}
				else LockSupport.parkNanos(1);
			}
			
			counter.decrementAndGet();
			return node != null ? node.value : null;
		}
		
		public int getCounter(){
			return counter.get();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//StandardStack<Integer> stack = new StandardStack();
		LockFreeStack<Integer> stack = new LockFreeStack();
		Random rand = new Random();
		
		List<Thread> threads = new ArrayList();
		
		int pushingThreads = 2;
		int poppingThreads = 2;
		
		for(int i = 0; i < pushingThreads; i++){
			Thread thread = new Thread(() ->{
				while(true){
					stack.push(rand.nextInt());
				}
			});
			
			thread.setDaemon(true);
			threads.add(thread);
		}
		
		for(int i = 0; i < poppingThreads; i++){
			Thread thread = new Thread(() ->{
				while(true){
					stack.pop();
				}
			});
			
			thread.setDaemon(true);
			threads.add(thread);
		}
		
		for(Thread thread : threads){
			thread.start();
		}
		
		Thread.sleep(10000);
		
		System.out.println("%,d operations were performed in 10 seconds.", stack.getCounter());
	}

}

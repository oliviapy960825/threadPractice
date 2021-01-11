package thread.creation.example;
import java.util.ArrayList;
import java.util.List;

public class MultiExecutor {
	private final List<Runnable> tasks;
	public MultiExecutor(List<Runnable> tasks) {
        // Complete your code here
		this.tasks=tasks;
    }

    /**
     * Starts and executes all the tasks concurrently
     */
    public void executeAll() {
    	List<Thread> threads=new ArrayList<Thread>();
        // complete your code here
    	/*
    	 *  for (Runnable task:tasks){
    		Thread thread=new Thread(task);
    		thread.start();
    			}
    		}
    	 * */
    	for (Runnable task:tasks){
    		Thread thread=new Thread(task);
    		threads.add(thread);
    	}
    	for(Thread thread:threads){
    		thread.start();
    	}
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

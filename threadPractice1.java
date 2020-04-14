package thread.creation.example;
public class threadPractice1 {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		Thread thread=new Thread(new Runnable(){
			@Override
			public void run(){
				//code that within the run method will be run in the new thread
				//as soon as it is scheduled by the operating system
				System.out.println("We are in thread "+Thread.currentThread().getName());
				System.out.println("Current priority is "+ Thread.currentThread().getPriority());
				throw new RuntimeException("Internal Exception");
			}
		});
		thread.setName("New Worker Thread");
		thread.setPriority(Thread.MAX_PRIORITY);
		System.out.println("We are in thread "+Thread.currentThread().getName()+" before starting a new thread");
		thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				// TODO Auto-generated method stub
				System.out.println("a critical error happened in thread " + t.getName()+" and the error message is "+e.getMessage() );
			}
		});
		
		thread.start();
		System.out.println("We are in thread "+Thread.currentThread().getName()+" after starting a new thread");
		Thread.sleep(10000);
	
	}

}

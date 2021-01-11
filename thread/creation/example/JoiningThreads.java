package thread.creation.example;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class JoiningThreads{
	
	public static void main(String[] args) throws InterruptedException{
		// TODO Auto-generated method stub
		List<Long> inputNumbers = Arrays.asList(10000000000L, 3435L, 35435L, 2324L, 4656L, 23L, 2435L, 5556L);
		List<FactorialThreads> threads = new ArrayList<>();
		for(long inputNumber : inputNumbers){
			threads.add(new FactorialThreads(inputNumber));
		}
		for(Thread thread : threads){
			thread.setDaemon(true);
			thread.start();
		}
		for(Thread thread : threads){
			thread.join(2000);
		}
		for(int i = 0; i < inputNumbers.size(); i++){
			FactorialThreads factorialThread = threads.get(i);
			if(factorialThread.isFinished()){
				System.out.println("Factorial of " + inputNumbers.get(i) +" is " + factorialThread.getResult());
			}
			else{
				System.out.println("The calculation of "+ inputNumbers.get(i) +" is still in progress");
			}
		}
}
public static class FactorialThreads extends Thread{
	private long inputNumbers;
	private BigInteger result = BigInteger.ZERO;
	private boolean isFinished = false;
	public FactorialThreads(long inputNumbers){
		this.inputNumbers = inputNumbers;
	}
	public void run(){
		this.result = factorial(inputNumbers);
		this.isFinished = true;
	}
	public BigInteger factorial(long n){
		BigInteger tempResult = BigInteger.ONE;
		for(long i = n; i > 0; i--){
			tempResult = tempResult.multiply(new BigInteger(Long.toString(i)));
		}
		return tempResult;
	}
	
	public boolean isFinished(){
		return isFinished;
	}
	public BigInteger getResult(){
		return result;
	}
}
}

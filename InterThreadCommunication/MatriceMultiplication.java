package InterThreadCommunication;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

public class MatriceMultiplication {
	private static final int N = 10;
	private static final INPUT_FILE = "";
	private static final OUTPUT_FILE = "";
	private static class MatricePair{
		public float[][] matrix1;
		public float[][] matrix2;
	}
	private static class MatriceMultiplierConsumer extends Thread{
		private ThreadSafeQueue q;
		private FileWriter w;
		
		public MatriceMultiplierConsumer(ThreadSafeQueue q, FileWriter w){
			this.q = q;
			this.w = w;
		}
		
		private float[][] multiply(float[][] m1, float[][] m2){
			float[][] res = new float[N][N];
			
			for(int i = 0; i < N; i++){
				for(int j = 0; j < N; j++){
					for(int k = 0; k < N; k++){
						res[i][j] += m1[i][k] * m2[k][j];
					}
				}
			}
			
			return res;
		}
		
		private static void saveMatrixToFile(FileWriter w, float[][] res) throws IOException{
			for(int i = 0; i < N; i++){
				StringJoiner joiner = new StringJoiner(",");
				for(int j = 0; j < N; j++){
					joiner.add(String.format("%.2f", res[i][j]));
				}
				w.write(joiner.toString());
				w.write('\n');
			}
			
			w.write('\n');
		}
		
		@Override
		public void run(){
			while(true){
				MatricePair pair = q.remove();
				
				if(pair == null){
					System.out.println("There are no more matrices to consume, consumer is terminating");
					break;
				}
				
				float[][] res = multiply(pair.matrix1, pair.matrix2);
				saveMatrixToFile(w, res);
			}
			
			try{
				w.flush();
				w.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	private static class MatriceReaderProducer extends Thread{
		private Scanner scanner;
		private ThreadSafeQueue q;
		
		public MatriceReaderProducer(FileReader reader, ThreadSafeQueue q){
			this.scanner = new Scanner(reader);
			this.q = q;
		}
		
		private float[][] readMatrices(){

			float[][] matrix = new float[N][N];
			for(int i = 0; i < N; i++){
				if(!scanner.hasNext()) return null;
				String[] line = scanner.nextLine().split(",");
				for(int j = 0; j < N; j++){
					matrix[i][j] = Float.valueOf(line[j]);
				}
			}
			scanner.nextLine();
			return matrix;
		}
		@Override
		public void run(){
			while(true){
				float[][] matrix1 = readMatrices();
				float[][] matrix2 = readMatrices();
				
				if(matrix1 == null || matrix2 == null){
					q.terminate();
					System.out.println("We don't have more matrices to read from, producer thread is terminating");
					return ;
				}
				
				MatricePair pair = new MatricePair();
				pair.matrix1 = matrix1;
				pair.matrix2 = matrix2;
				q.add(pair);
			}
		}
		
	}
	private static class ThreadSafeQueue{
		private Queue<MatricePair> q = new LinkedList();
		private boolean isEmpty = true;
		private boolean isTerminated = false;
		private static final int CAPACITY = 5;
		public synchronized void add(MatricePair matricePair){
			while(q.size() == CAPACITY){
				try{
					wait();
				}
				catch(InterruptedException e){
					
				}
			}
			q.add(matricePair);
			this.isEmpty = false;
			notify();
		}
		
		public synchronized MatricePair remove(){
			MatricePair pair = null;
			while(!isEmpty && !isTerminated){
				try{
					wait();
				}
				catch(InterruptedException e){
					
				}
			}
			
			if(q.size() == 1){
				isEmpty = true;
			}
			if(q.size() == 0 && isTerminated) return null;
			
			System.out.println("Current size is " + q.size());
			
			pair = q.remove();
			
			if(q.size() == CAPACITY - 1){
				notifyAll();
			}
			return pair;
			//return q.remove();
		}
		
		public synchronized void terminate(){
			isTerminated = true;
			notifyAll();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ThreadSafeQueue q = new ThreadSafeQueue();
		File input = new File(INPUT_FILE);
		File output = new File(OUTPUT_FILE);
		
		MatriceReaderProducer producer = new MatriceReaderProducer(new FileReader(input), q);
		MatriceMultiplierConsumer consumer = new MatriceMultiplierConsumer(q, new FileWriter(output));
	
		consumer.start();
		producer.start();
	}

}

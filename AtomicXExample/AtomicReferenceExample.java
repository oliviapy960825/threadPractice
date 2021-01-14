package AtomicXExample;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String oldName = "Old name";
		String newName = "New name";
		
		AtomicReference<String> atomicReference = new AtomicReference(oldName);
		if(atomicReference.compareAndSet(oldName, newName)){
			System.out.println("New value is " + atomicReference.get());
		}
		else{
			System.out.println("Nothing happened");
		}
	}

}

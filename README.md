# thread practice
This is the practice from Udemy class "Java Multithreading, Concurrency & Performance Optimization"

### Synchronization Notes

object/variables shared by multiple method are stored on the heap and is not atomic (can be called by multiple method which would cause the status to be different)

i.e., item++ includes actually 3 steps, the fist one is to get current status from the heap, the second step is item = item + 1, the third step is to store item back to the heap, so item++ is not atomic, and may run into issues when being shared/accessed by multiple methods

In order to solve this problem, we could identify the code that we need to execute atomically by declaring it as a critical section by using synchronized keyword:
  1. Simple way : add synchronized keyword in front of a method
  2. On an explict object: more flexible and granular, but also more verbose

### Atomic Operations

All reference assignments are atomic, so we can get and set references to objects atomically -> all getters and setters are atomic and we don't need to synchronize those

All assignments to primitive types are safe EXCEPT long and double -> reading from and writing to int, short, byte, float, char, boolean is safe
long and double are exceptions because they are 64-bits, so the lower 32-bits and the higher 32-bits might be processed by 2 operations -> use volatile keyword

Classes in java.util.concurrent.atomic have more advanced operations


### Race Condition

Condition when multiple threads are accessing a shared resource and at least one thread is modifying the resource. The timing of thread scheduling may cause incorrect results. The core of the problem is the atomic operations performed on shared resources

### Data Race

Compiler and CPU may execute the instructions out of order to optimize performance and utilization. They will do so while maintaining the logical correctness of the code. Out of order execution by the compiler and CPU are important features to speed up the code

Volatile keyword solves all data races by guaranteeing order

### Deadlock

Conditions for deadlock
  1. Mutual Exclusion - only one thread can have exclusive access to a resource
  2. Hold and Wait - At least one thread is holding a resource and is waiting for another resource
  3. Non-preemptive allocation - A resource is released only after the thread is done using it
  4. Circular wait - A chain of at least 2 threads each one is holding one resource and is waiting for another resource

Deadlock is best solved by avoiding circular wait and hold by enforcing lock orders

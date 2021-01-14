# thread practice
This is the practice from Udemy class "Java Multithreading, Concurrency & Performance Optimization"

## Synchronization Notes

object/variables shared by multiple method are stored on the heap and is not atomic (can be called by multiple method which would cause the status to be different)

i.e., item++ includes actually 3 steps, the fist one is to get current status from the heap, the second step is item = item + 1, the third step is to store item back to the heap, so item++ is not atomic, and may run into issues when being shared/accessed by multiple methods

In order to solve this problem, we could identify the code that we need to execute atomically by declaring it as a critical section by using synchronized keyword:
  1. Simple way : add synchronized keyword in front of a method
  2. On an explict object: more flexible and granular, but also more verbose

## Atomic Operations

All reference assignments are atomic, so we can get and set references to objects atomically -> all getters and setters are atomic and we don't need to synchronize those

All assignments to primitive types are safe EXCEPT long and double -> reading from and writing to int, short, byte, float, char, boolean is safe
long and double are exceptions because they are 64-bits, so the lower 32-bits and the higher 32-bits might be processed by 2 operations -> use volatile keyword

Classes in java.util.concurrent.atomic have more advanced operations


## Race Condition

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

## Advanced Locking

### Reentrant Lock

Works just like the synchronized keyword applied on an object, but requires explicit locking and unlocking, so the disadvantage of using reentrant lock is that the user may forget to unlock after it's done -> solution : surround it with try catch and put unlock into final block, and every reentrant lock should be thoroughly tested. 

The reward of using reentrant lock:
  1. More control over the lock
  2. More lock operations
    Query methods - For testing
      1. getQueuedThreads() -> return a list of threads waiting to acquire a lock
      2. getOwner() -> Returns the thread that currently owns the lock
      3. isHeldByCurrentThread() -> Queries if the lock is held by the current thread
      4. isLocked() -> Queries if the lock is held by any thread
  3. More control over lock's fairness -> by default, the reentrant lock and synchronized keyword do not guarantee any fairness
    By setting the parameter to true when initializing reentrant lock (i.e., ReentrantLock(true)), fairness can be guaranteed. However, the downside is it may 
    reduce the throughput of the application
  4. LockInterruptibly() -> watchdog for deadlock detection and recovery. It would wake up and application to clean up and close the application
  5. tryLock() -> boolean tryLock()
                  boolean tryLock(long timeout, TimeUnit unit) 
                    return true and acquires a lock if available
                    return false and does not get suspended, if the lock is unavailable
     Under no circumstances does the tryLock() method block. Regardless of the state of the lock, it always returns immediately
          Used in real time applications where suspending a thread on a lock() method is unacceptable.
              e.g. Video/Image processing
                   High speed/low latency trading
                   User interface applications
    tryLock() could avoid blocking real time thread and keep application responsive while performing operations atomically and avoided race condition


### ReentrantReadWriteLock

Synchronized keyword and ReentrantLock both only allow one thread to access the shared resource at a time. However, when the majority of the operations are reading which is not modifying the shared resource, or when the read operations are not as fast (read from multiple variables or read from a complex data stucture), then mutual exclusion of reading threads negatively impacts the performance. 

  Use readLock and writeLock and have mutual exclusion between readLock and writeLock
  
    1. Use writeLock to lock the critical section where the shared resource is being modified. If writeLock is acquired, no thread can acquire a readLock
    
    2. Use readLock to guard the critical section when it is being readed, multiple threads could access the readLock simultaneously. If at least one thread holds a 
    readLock, then no thread can acquire writeLock

## Inter-Thread Communication

### Semaphore
  1. Can be used to restrict the number of "users" to a particular resource or a group of resources
  2. Unlike the locks that allow only one "user" per resource
  3. The semaphore can restrict any given number of users to a resource
  
### Semaphore V.S. Locks

  1. Semaphore does not have a notion of owner thread
  2. Many threads can acquire a permit
  3. The same thread can acquire the same semaphore multiple times
  4. The binary semaphore (initialized with 1) is not reentrant
  
### Inter-thread - Condition.await()
  Functions:
    void await() -> unlock lock, wait until signalled
    long awaitNanos(long nanosTimeout) -> wait no longer than nanosTimeout
    boolean await(long time, TimeUnit unit) -> wait no longer than time, in given time units
    boolean awaitUntil(Date deadline) -> wake up before the deadline date
    void signal() -> wakes up a single thread, waiting on the condition variable
                     A thread that wakes up has to reacquire the lock associated with the condition variable
                     If currently no thread is waiting on the condition variable, the signal method does not do anything
    void signalAll() -> Broadcast a signal to all threads currently waiting on the condition variable
                        Does not need to know how many (if at all) threads are waiting on the condition variable 
                        
### wait(), notify() and notifyAll()
  The Object Class contains the following methods:
    public final void wait() throws InterruptedException
    public final void notify()
    public final void notifyAll()
  Because every Java Class inherits from the Object Class, we can use any object as a condition variable and a lock (use the synchronized keyword)
  
    wait()
      In the wait state, the thread is not consuming any CPU
      Two ways to wake up the waiting thread:
        notify() -> Wakes up a *single* thread waiting on that object
        notifyAll() -> Wakes up *all* the threads waiting on that object
      To call wait(), notify(), notifyAll(), we need to acquire the monitor of that object (use synchronized on that object)
      
### Rule of Thumb
  
  Whenever using a queue to decouple multithreaded components, apply back-pressure and limit the size of the queue to guard against OutOfMemoryException


## Disadvantages of Locks

### Deadlocks

  1. Deadlocks are generally unrecoverable
  2. Can bring the application to a complete halt
  3. The more locks in the application, the higher the chances for a deadlock
  
### Slow Critical Section
  Multiple threads using the same lock -> One thread holds the lock for very long -> That thread will slow down all other threads -> All threads become as slow as the slowest thread
  
### Priority Inversion
  Two threads share the same lock
    low priority thread (document saver)
    high priority thread (UI)
  Low priority thread acquire the lock, and is preempted (scheduled out) -> high priority thread cannot progress because of the slow priority thread is not scheduled to release the lock
  
### Thread not releasing a lock (Kill Tolerance)
  Thread dies, gets interrupted or forgets to release a lock -> Leaves all threads hanging forever -> unrecoverable, just like a deadlock -> To avoid, developers need to write very complex code
  
### Performance 
  Performance overhead in having contention over a lock
    Thread A acquire a lock
    Thread B tries to acquire a lock and gets blocked
    Thread B is scheduled out (context switch)
    Thread B is scheduled back (context switch)
  Application overhead may not be noticeable for most applications
  But for latency sensitive applications, this overhead can be significant 
  
### AtomicX class
  Class located in the java.util.concurrent.atomic package
  Internally uses the Unsafe Class which provides access to low level, native methods
  Utilize platform specific implementation of atomic operations
  
  AtomicInteger
    Pros:
      Simplicity
      No need to for locks or synchronization
      No race condition or data races
    Cons:
      Only the operation itself is atomic
      There's still race condition between 2 separate atomic operations 
      
  AtomicReference<T>
    AtomicReference (V initialValue)
    V get() -> return the current value
    void set(V newValue) -> Sets the value to newValue
    boolean compareAndSet(V expectedValue, V newValue) -> Assign new value if current value == expected value, ignore the new value if the current value != expected value
  
  CAS - CompareAndSet
    Available in all Atomic Class
    Compiles into an atomic hardware operation
    Many other atomic methods are internally implemented using CAS
  
  
  
  

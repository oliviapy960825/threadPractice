# thread practice
This is the practice from Udemy class "Java Multithreading, Concurrency & Performance Optimization"

### Synchronization Notes

object/variables shared by multiple method are stored on the heap and is not atomic (can be called by multiple method which would cause the status to be different)

i.e., item++ includes actually 3 steps, the fist one is to get current status from the heap, the second step is item = item + 1, the third step is to store item back to the heap, so item++ is not atomic, and may run into issues when being shared/accessed by multiple methods

In order to solve this problem, we could identify the code that we need to execute atomically by declaring it as a critical section by using synchronized keyword:
  1. Simple way : add synchronized keyword in front of a method
  2. On an explict object: more flexible and granular, but also more verbose

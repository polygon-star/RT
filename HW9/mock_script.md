### s3 link

https://qa-walkthrough-01.s3.us-east-2.amazonaws.com/2026-06-09%2017-42-01.mp4?response-content-disposition=inline&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEA4aCXVzLWVhc3QtMiJGMEQCIEG7lZN%2BHLiOdXN7jfC3%2FNWHQ38TynnTyKIch0NZ%2BTMTAiAr7f%2FAAOVmJluk%2BjxqM9lo3URQdws9%2BXMfUl%2BdgNw%2FYSq%2BAwjX%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAAaDDAzMDE3OTMxMDU4OCIM6kgts0xWm15J2BY7KpIDizbE%2FtrM2iJWmjoqpQe3KqeKTSen%2B9MTj4yCyxVvNwuB%2FPJM7bAQCtlnAGD7uHpIyqT44RrKQ9P%2FDRKNj4wko%2FXzQFmKUqveYCiGKFJ3ltBCt4P4vZo%2BAqo6%2FWPUuK0NMdhdhjtLKRsohPTjYCq%2B%2Bc%2FMRdJ7CcrhaDldY47ItURFSExy%2BNEk8hN5Rlj3E9CAe89K9pnnmqzQwad3oqVJh%2B2Za8CUyNiZyiGtYNLRd%2Fy0X66tp%2B6lYjJ7cJ5NIVprkIlRbyBd5CPQMsfiV1zHMVC4KbrHI18ILpB%2Bvy2MpdZSBU%2BvHJnwqg2kJmjH61VvKpyu74tesMe8SEtKVuVgsp0ozVDGELZ8D6JvXW1t%2FIQVjVsQeq9ei5AYsIMz%2BmUT9H%2FqcFkAN9IDW1vJvkwHjM1Jk9ZK31KXV4x3kcrOfVmNBq2YgNElvpcOpVjPyIvS3Y4eQcu9b%2FQjxKtu70VY8zfxbvsdFas1sA1jorZ4AdqrsIoje10v0A9SSHsQdXK4lxBKLH0rBscVqNeaGGfOhRJvMPaVotEGOt8CvMhTAGlF0gfVOnllrG%2FjEICwQxzIHaeaBzkdYoO6tA2yLrxV12PEDqgbyg5bD8r6yRKvAbQtI4oXNWDx6DLpuRzIX3lhPsbNmcd35GmStUlW%2BOzpyVErKsAavvXcjsyjTVACA4SUxLBWzsHIMkhfwnZL1g60ZV9Lxlf0p3UlMtM6WpuWvOWl8D085lXN%2FWuArn61SmZmpHaSIYV%2F0Ku78ZSvTPJFUeN6bokjb7AaxCo9gQeL9OkeBuYkfKk%2FV8P%2BJl62mnUO5Mvb2jhKKQ%2FNGvG7aq%2BD80aCmNPReAX%2FiKb1yi3YvbYACbBE9PrDkXN3OYZy7MpxYJs8QiOE94X2o%2BLdNfMt1HQH%2BKXOh%2FzjRv7daaRbQA4eyC9VyfkCw5MrXDnzsqbIvGq1cUevGsrEJolorU%2BVIk%2FY0MUhfuOvGn6TmlXbS9%2F%2Fblb0UTc7%2FEyasZylkozs84Ml65rtZDk9&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIAQOBWTXP6FEGINQA5%2F20260609%2Fus-east-2%2Fs3%2Faws4_request&X-Amz-Date=20260609T215338Z&X-Amz-Expires=43200&X-Amz-SignedHeaders=host&X-Amz-Signature=ab5cea4262c9372ef769a4a7a62b6eb937edcf38d80200618dac095c6cd8b27c

### HashMap workflow and big O?

HashMap stores key-value pairs in buckets. It computes the key's hashCode(), maps it to a bucket index, and stores the entry there. If multiple keys map to the same bucket, collisions are handled using a linked list, which becomes a Red-Black Tree in Java 8+ when the bucket gets large. Average time complexity for put(), get(), and remove() is O(1), while the worst case is O(log n) due to the tree structure. 

### what are wait and notify?

They are used for inter-thread communication when multiple threads need to coordinate access to shared resources.

how Wait() works

1. Releases the object's monitor(lock)
2. Enters the WAITING state
3. Stops execution
4. Waits until another thread calls notify() or notifyAll()

how notify works

1. Wakes up one waiting thread
2. Does NOT immediately give up the lock
3. The awakened thread must wait until the lock becomes available

Must call wait() and notify() inside a synchronized block or method.
package benchmarks

import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Measurement
import org.openjdk.jmh.annotations.*
import kotlinx.benchmark.State
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantReadWriteLock

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 10, time = 2, TimeUnit.SECONDS)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class MyBenchmark {
    private var counter: Long = 0L


    //@Benchmark
    fun incCounterSingleThread(blackhole: Blackhole) {
        for (i in 0 .. 500_000_000) {
            counter++
        }

        blackhole.consume(counter)
    }

    //@Benchmark
    fun incCounterTwoThreadNoSync(blackhole: Blackhole) {
        val monitor = false
        val latch = CountDownLatch(2)
        val t1 = Thread {
            for (i in 0 .. 250_000_000) {
                counter++
            }
            latch.countDown()
        }
        val t2 = Thread {
            for (i in 0 .. 250_000_000) {
                counter++
            }
            latch.countDown();
        }

        t1.start()
        t2.start()
        latch.await()
        blackhole.consume(counter)
    }


    //@Benchmark
    fun incCounterTwoThreadSynchronized(blackhole: Blackhole) {
        val monitor = false
        val latch = CountDownLatch(2)
        val t1 = Thread {
            for (i in 0 .. 250_000_000) {
                synchronized(monitor) {
                  counter++
                }
            }
            latch.countDown()
        }
        val t2 = Thread {
            for (i in 0 .. 250_000_000) {
                synchronized(monitor) {
                  counter++
                }
            }
            latch.countDown();
        }

        t1.start()
        t2.start()
        latch.await()

        blackhole.consume(counter)
    }

    //@Benchmark
    fun incCounterTwoThreadAtomic(blackhole: Blackhole) {
        val atomicCounter = AtomicLong(0)
        val latch = CountDownLatch(2)
        val t1 = Thread {
            for (i in 0 .. 250_000_000) {
                atomicCounter.incrementAndGet()
            }
            latch.countDown()
        }
        val t2 = Thread {
            for (i in 0 .. 250_000_000) {
                atomicCounter.incrementAndGet()
            }
            latch.countDown();
        }

        t1.start()
        t2.start()
        latch.await()

        blackhole.consume(counter)
    }

    @Benchmark
    fun incCounterTwoThreadReentrantLock(blackhole: Blackhole) {
        val lock = ReentrantReadWriteLock()

        val latch = CountDownLatch(2)
        val t1 = Thread {
            for (i in 0 .. 250_000_000) {
                try {
                    lock.writeLock().lock()
                    counter++
                } finally {
                    lock.writeLock().unlock()
                }
            }
            latch.countDown()
        }
        val t2 = Thread {
            for (i in 0 .. 250_000_000) {
                try {
                    lock.writeLock().lock()
                    counter++
                } finally {
                    lock.writeLock().unlock();
                }
            }
            latch.countDown();
        }

        t1.start()
        t2.start()
        latch.await()

        blackhole.consume(counter)
    }

}
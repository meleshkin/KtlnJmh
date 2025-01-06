package benchmarks

import kotlinx.benchmark.Measurement
import org.openjdk.jmh.annotations.*
import kotlinx.benchmark.State
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.random.Random

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 3)
@Measurement(iterations = 10, time = 1, TimeUnit.SECONDS)
@BenchmarkMode(Mode.SampleTime)
class MyBenchmark {
    private var data = 0.0

    @Setup
    fun setUo() {
        data = Random.nextDouble()
    }

    @Benchmark
    fun sqrtBenchmark(): Double {
        return sqrt(data)
    }

    fun cosBenchmark(): Double {
        return cos(data)
    }
}
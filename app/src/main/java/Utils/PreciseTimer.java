package Utils;

/**
 * Created by brb55_000 on 4/1/2015.
 */

// Returns time in milliseconds
public class PreciseTimer {

    private long startTime;

    // Constructor, also starts the benchmark
    public PreciseTimer() {
        start();
    }

    // Starts a benchmark
    public void start() {
        startTime = System.nanoTime();
    }

    // Returns time in milliseconds since last start call
    public double stop() {
        return (double)(System.nanoTime() - startTime) / 1000000.0;
    }
}

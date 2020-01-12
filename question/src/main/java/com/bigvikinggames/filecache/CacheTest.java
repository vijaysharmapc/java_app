package com.bigvikinggames.filecache;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Cache test used for running a single caching test.
 */
public class CacheTest {
    /**
     * The amount of bytes to check at the beginning of a file to see if they're identical.
     */
    private static final int NUM_BYTES_TO_CHECK = 16;

    /**
     * The first bytes of a file by the composite name.
     */
    private final Map<String, byte[]> firstBytesByName;

    /**
     * The list of steps in the test.
     */
    private final List<TestStep> testSteps;

    /**
     * The name of the test.
     */
    private final String testName;

    /**
     * Create a cache test.
     *
     * @param testName The name of the test.
     * @param firstBytesByName The first bytes of the needed files.
     * @param testSteps The list of steps in the test.
     */
    private CacheTest(String testName, Map<String, byte[]> firstBytesByName, List<TestStep> testSteps) {
        this.testName = testName;
        this.firstBytesByName = firstBytesByName;
        this.testSteps = testSteps;
    }

    /**
     * Get the name of the test.
     *
     * @return The name of the test.
     */
    public String getTestName() {
        return testName;
    }

    /**
     * Run a single test step.
     *
     * @param step The step to run.
     * @param cache The cache to use.
     * @return Whether or not the test step succeeded.
     */
    private boolean runTestStep(TestStep step, CacheInterface cache) {
        String fileName = step.getFileName();
        int version = step.getVersion();
        var maybeFileBytes = cache.getFile(fileName, version);

        // check for an expected cache failure
        if (maybeFileBytes.isEmpty() && step.isExpectFailure()) {
            return true;
        } else if (maybeFileBytes.isEmpty()) {
            System.err.println("Got an empty file for " + fileName + " version " + Integer.toUnsignedString(version) + ", but expected a file.");
            return false;
        } else if (step.isExpectFailure()) {
            System.err.println("Got a file when none was expected for " + fileName + " version " + Integer.toUnsignedString(version) + ".");
            return false;
        }

        // make sure we've got the same first few bytes
        String name = getCompositeFileName(fileName, version);
        byte[] expectedBytes = firstBytesByName.get(name);
        byte[] loadedBytes = maybeFileBytes.get();
        boolean areBytesEqual = Arrays.equals(expectedBytes, 0, NUM_BYTES_TO_CHECK - 1, loadedBytes, 0, NUM_BYTES_TO_CHECK - 1);
        if (! areBytesEqual) {
            System.err.println("Loaded file for " + fileName + " version " + Integer.toUnsignedString(version) + " did not match expected.");
            return false;
        }
        return true;
    }

    /**
     * Run the entire test on the given cache.
     *
     * @param cache The cache to run the test on.
     * @return Whether or not the test succeeded.
     */
    public boolean runTest(CacheInterface cache) {
        return testSteps.stream().allMatch(step -> runTestStep(step, cache));
    }

    /**
     * Get the actual composite filename of the file we want to load from the filename and version number.
     *
     * @param fileName The name of the file to load.
     * @param version The version number of the file to load.
     * @return The composite name of the file.
     */
    public static String getCompositeFileName(String fileName, int version) {
        int lastDotIdx = StringUtils.lastIndexOf(fileName, ".");
        String name = StringUtils.substring(fileName, 0, lastDotIdx);
        String extension = StringUtils.substring(fileName, lastDotIdx + 1, fileName.length());
        return name + "." + Integer.toUnsignedString(version) + "." + extension;
    }

    /**
     * Create a cache test using the root path to load data from and the path to the test config.
     *
     * @param rootDataPath The root path of the data files.
     * @param testConfigPath The path to the test config file.
     * @param testName The name of the test.
     * @return An optional containing the cache test if it could be created.
     */
    public static Optional<CacheTest> createCacheTest(String rootDataPath, Path testConfigPath, String testName) {
        // load the test steps
        List<TestStep> testSteps;
        try {
            testSteps = Files.readAllLines(testConfigPath)
                                               .stream()
                                               .map(TestStep::tryCreateTestStep)
                                               .flatMap(Optional::stream)
                                               .collect(Collectors.toList());
            //System.out.println(testSteps);
        } catch (IOException e) {
            System.err.println("Could not read all lines for " + testConfigPath + ": " + e.getLocalizedMessage());
            return Optional.empty();
        }

        if (testSteps.isEmpty()) {
            System.err.println("Was not able to load any tests for " + testConfigPath + ".");
            return Optional.empty();
        }

        // since we need to verify if the tests succeed but don't want to keep all the files in memory we're loading
        // the first few bytes of each file we want to verify
        Map<String, byte[]> firstBytesByName = new HashMap<>();
        for (var step : testSteps) {
        	//System.out.println(step);
            if (step.isExpectFailure()) {
                // if failure is expected we don't need to care
                continue;
            }

            // get the full name of the file with version
            String fileName = step.getFileName();
            int version = step.getVersion();
            String name = getCompositeFileName(fileName, version);
            if (firstBytesByName.containsKey(name)) {
                // only need to load unique
                continue;
            }

            // find the file in the data files
            var path = Paths.get(rootDataPath, name);
            if (! Files.exists(path)) {
                System.err.println("Could not find file at: " + path.toString());
                continue;
            }
            if (! Files.isReadable(path)) {
                System.err.println("File at " + path.toString() + " is not readable.");
                continue;
            }

            // get the first few bytes
            try (var inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
                byte[] firstBytes = inputStream.readNBytes(NUM_BYTES_TO_CHECK);
                firstBytesByName.put(name, firstBytes);
            } catch (IOException e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
        return Optional.of(new CacheTest(testName, firstBytesByName, testSteps));
    }
}

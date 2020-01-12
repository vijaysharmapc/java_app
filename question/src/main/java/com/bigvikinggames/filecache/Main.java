package com.bigvikinggames.filecache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import vehicles.com.ConcreteFancyCar;

/**
 * The main class for the file caching test.
 */
public class Main {
	/**
	 * The path to the config directory.
	 */
	private static String CONFIG_DIRECTORY = "config";

	/**
	 * The name of the main config file, which just contains a list of the other test config files.
	 */
	private static String MAIN_CONFIG_FILE = "filecache.conf";

	/**
	 * The path to the data directory.
	 */
	public static String ROOT_DATA_DIRECTORY = "data";

	/**
	 * Load all of the cache tests.
	 *
	 * @return The list of cache tests to run.
	 */
	private static List<CacheTest> loadCacheTests() {
		var configPath = Paths.get(CONFIG_DIRECTORY, MAIN_CONFIG_FILE);
		List<String> lines;
		try {
			lines = Files.readAllLines(configPath);
		} catch (IOException e) {
			System.err.println("Could not read the base config file: " + configPath.toString());
			return Collections.emptyList();
		}

		return lines.stream()
				.map(line -> CacheTest.createCacheTest(ROOT_DATA_DIRECTORY, Paths.get(CONFIG_DIRECTORY, line), line))
				.flatMap(Optional::stream)
				.collect(Collectors.toList());
	}

	/**
	 * Create the cache to read files from.
	 *
	 * @return The created cache.
	 */
	private static CacheInterface getCache() {
		// TODO: this is where you'll create your new cache instance
		return new AlwaysFailCache();
	}

	/**
	 * Entry point.
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		FileFeatures s = new FileFeatures();
		System.out.println(s.fileName());
		System.out.println(s.version());
/**		
		
		List<CacheTest> tests = loadCacheTests();
		System.out.println("Running " + Integer.toUnsignedString(tests.size()) + " Tests");
		var cache = getCache();
		var numFailures = tests.stream()
				.map(test -> {
					System.out.println("Running test: " + test.getTestName());
					boolean wasSuccessful = test.runTest(cache);
					if (wasSuccessful) {
						System.out.println("SUCCESS");
					} else {
						System.out.println("FAILURE");
					}
					return wasSuccessful;
				}).filter(success -> ! success)
				.count();
		if (numFailures == 0) {
			System.out.println("All Tests Passed");
		} else {
			System.out.println(Long.toUnsignedString(numFailures) + " Test Failures");
		}
	}
**/
}
}

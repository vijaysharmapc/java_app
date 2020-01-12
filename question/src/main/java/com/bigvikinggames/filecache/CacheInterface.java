package com.bigvikinggames.filecache;

import java.util.Optional;

/**
 * The expected for a caching system.
 */
public interface CacheInterface {
    /**
     * Given a filename and a version, return the bytes for the file if it could be found.
     *
     * @param fileName The name of the file to load.
     * @param version The version number of the file to load.
     * @return The bytes of the file if they could be found.
     */
    //Optional<byte[]> getFile(String fileName, int version);
	String fileName();
	int version();
}

package com.bigvikinggames.filecache;

import java.util.Optional;

/**
 * Implementation of a cache that will always fail.
 */
public class AlwaysFailCache implements CacheInterface {
    @Override
    public Optional<byte[]> getFile(String fileName, int version) {
        return Optional.empty();
    }
}

package com.bigvikinggames.filecache;

import java.util.Arrays;
import java.util.Optional;

/**
 * Implementation of a cache that always finds a file but just returns 0.
 */
public class FindZeroCache implements CacheInterface {
    @Override
    public Optional<byte[]> getFile(String fileName, int version) {
        byte[] bytes = new byte[32];
        Arrays.fill(bytes, (byte)0);
        return Optional.of(bytes);
    }
}

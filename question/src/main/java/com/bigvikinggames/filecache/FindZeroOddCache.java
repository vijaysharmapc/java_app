package com.bigvikinggames.filecache;

import java.util.Optional;

/**
 * A hybrid of AlwaysFailCache and FindZeroCache that swaps behaviour depending on the oddity of the version.
 */
public class FindZeroOddCache implements CacheInterface {
    private final AlwaysFailCache failCache = new AlwaysFailCache();
    private final FindZeroCache zeroCache = new FindZeroCache();

    @Override
    public Optional<byte[]> getFile(String fileName, int version) {
        if (version % 2 == 1) {
            return zeroCache.getFile(fileName, version);
        } else {
            return failCache.getFile(fileName, version);
        }
    }
}

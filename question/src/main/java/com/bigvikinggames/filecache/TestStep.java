package com.bigvikinggames.filecache;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Optional;

/**
 * Class for representing a test step.
 */
public final class TestStep {
    /**
     * The name of the file to load in this step.
     */
    private final String fileName;

    /**
     * The version of the file to load in this step.
     */
    private final int version;

    /**
     * Whether or not to expect failure from this step.
     */
    private final boolean expectFailure;

    /**
     * Create a test step.
     *
     * @param fileName The name of the file to load.
     * @param version The version of the file to load.
     * @param expectFailure Whether or not we'd expect failre.
     */
    private TestStep(String fileName, int version, boolean expectFailure) {
        this.fileName = fileName;
        this.version = version;
        this.expectFailure = expectFailure;
    }

    /**
     * Get the name of the file to load in this step.
     *
     * @return The name of the file to load.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Get the version of the file to load in this step.
     *
     * @return The version of the file to load.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Get whether or not this step is expected to fail.
     *
     * @return Whether or not this step is expected to fail.
     */
    public boolean isExpectFailure() {
        return expectFailure;
    }

    /**
     * Attempt to create a test step from a line loaded from a config file.
     *
     * @param line The line from the config file.
     * @return An optional containing the created test step if it could be created.
     */
    public static Optional<TestStep> tryCreateTestStep(String line) {
        var parts = StringUtils.split(line, ",");
        if (parts.length != 3) {
            System.err.println("Line \"" + line + "\" could not be split into 3 parts as expected.");
            return Optional.empty();
        }
        String fileName = parts[0];
        int version;
        try {
            version = Integer.parseUnsignedInt(parts[1]);
        } catch (NumberFormatException e) {
            System.err.println("Unable to parse unsigned integer from \"" + parts[1] + "\".");
            return Optional.empty();
        }
        boolean expectedFailure = Boolean.parseBoolean(parts[2]);

        return Optional.of(new TestStep(fileName, version, expectedFailure));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                       .append("fileName", fileName)
                       .append("version", version)
                       .append("expectFailure", expectFailure)
                       .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestStep testStep = (TestStep)o;

        return new EqualsBuilder()
                       .append(getVersion(), testStep.getVersion())
                       .append(isExpectFailure(), testStep.isExpectFailure())
                       .append(getFileName(), testStep.getFileName())
                       .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                       .append(getFileName())
                       .append(getVersion())
                       .append(isExpectFailure())
                       .toHashCode();
    }
}

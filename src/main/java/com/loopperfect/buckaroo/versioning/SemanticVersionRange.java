package com.loopperfect.buckaroo.versioning;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.loopperfect.buckaroo.SemanticVersion;
import com.loopperfect.buckaroo.SemanticVersionRequirement;

import java.util.Objects;

public final class SemanticVersionRange implements SemanticVersionRequirement {

    public final SemanticVersion minimumVersion;
    public final SemanticVersion maximumVersion;

    private SemanticVersionRange(final SemanticVersion minimumVersion, final SemanticVersion maximumVersion) {

        this.minimumVersion = Preconditions.checkNotNull(minimumVersion);
        this.maximumVersion = Preconditions.checkNotNull(maximumVersion);

        Preconditions.checkArgument(minimumVersion.compareTo(maximumVersion) <= 0);
    }

    public boolean isSatisfiedBy(final SemanticVersion version) {
        return (minimumVersion.compareTo(version) <= 0) &&
            (maximumVersion.compareTo(version) >= 0);
    }

    public ImmutableSet<SemanticVersion> hints() {
        return ImmutableSet.of(minimumVersion, maximumVersion);
    }

    @Override
    public String encode() {
        return minimumVersion + "-" + maximumVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimumVersion, maximumVersion);
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof SemanticVersionRange)) {
            return false;
        }

        final SemanticVersionRange other = (SemanticVersionRange) obj;

        return Objects.equals(minimumVersion, other.minimumVersion) &&
            Objects.equals(maximumVersion, other.maximumVersion);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("minimumVersion", minimumVersion)
            .add("maximumVersion", maximumVersion)
            .toString();
    }

    public static SemanticVersionRange of(final SemanticVersion minimumVersion, final SemanticVersion maximumVersion) {
        return new SemanticVersionRange(minimumVersion, maximumVersion);
    }
}

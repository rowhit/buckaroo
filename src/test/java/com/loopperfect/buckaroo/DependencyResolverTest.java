package com.loopperfect.buckaroo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sun.tools.doclets.formats.html.markup.HtmlStyle.bar;
import static org.junit.Assert.*;

/**
 * Created by gaetano on 16/02/17.
 */
public class DependencyResolverTest {

    @Test
    public void resolveSimple() throws Exception {

        final Project project = Project.of("project", DependencyGroup.of(ImmutableMap.of(
            Identifier.of("foo"), ExactSemanticVersion.of(SemanticVersion.of(1)),
            Identifier.of("bar"), ExactSemanticVersion.of(SemanticVersion.of(1)))));

        final DependencyFetcher fetcher = DependencyFetcherFromMap.of(
                ImmutableMap.of(
                        Identifier.of("foo"), ImmutableMap.of(SemanticVersion.of(1), DependencyGroup.of()),
                        Identifier.of("bar"), ImmutableMap.of(
                                SemanticVersion.of(1),
                                DependencyGroup.of(ImmutableMap.of(
                                        Identifier.of("baz"), ExactSemanticVersion.of(SemanticVersion.of(1))))),
                        Identifier.of("baz"), ImmutableMap.of(
                                SemanticVersion.of(1), DependencyGroup.of())));

        final Either<ImmutableList<DependencyResolverException>, ImmutableMap<Identifier, SemanticVersion>> expected =
                Either.right(ImmutableMap.of(
                        Identifier.of("foo"), SemanticVersion.of(1),
                        Identifier.of("bar"), SemanticVersion.of(1),
                        Identifier.of("baz"), SemanticVersion.of(1)));

        final Either<ImmutableList<DependencyResolverException>, ImmutableMap<Identifier, SemanticVersion>> actual =
                DependencyResolver.resolve(project.dependencies, fetcher);

        assertEquals(expected, actual);
    }

    @Test
    public void resolveCircular() throws Exception {

        final Project project = Project.of("project", DependencyGroup.of(ImmutableMap.of(
                Identifier.of("foo"), ExactSemanticVersion.of(SemanticVersion.of(1)),
                Identifier.of("bar"), ExactSemanticVersion.of(SemanticVersion.of(1)))));

        final DependencyFetcher fetcher = DependencyFetcherFromMap.of(ImmutableMap.of(
                Identifier.of("foo"), ImmutableMap.of(SemanticVersion.of(1), DependencyGroup.of()),
                Identifier.of("bar"), ImmutableMap.of(
                        SemanticVersion.of(1), DependencyGroup.of(ImmutableMap.of(
                                Identifier.of("baz"), ExactSemanticVersion.of(SemanticVersion.of(1))))),
                Identifier.of("baz"), ImmutableMap.of(
                        SemanticVersion.of(1), DependencyGroup.of(ImmutableMap.of(
                                Identifier.of("bar"), ExactSemanticVersion.of(SemanticVersion.of(1)))))));

        final Either<ImmutableList<DependencyResolverException>, ImmutableMap<Identifier, SemanticVersion>> expected =
                Either.right(ImmutableMap.of(
                        Identifier.of("foo"), SemanticVersion.of(1),
                        Identifier.of("bar"), SemanticVersion.of(1),
                        Identifier.of("baz"), SemanticVersion.of(1)));

        final Either<ImmutableList<DependencyResolverException>, ImmutableMap<Identifier, SemanticVersion>> actual =
                DependencyResolver.resolve(project.dependencies, fetcher);

        assertEquals(expected, actual);
    }


    @Test
    public void resolveFailure() throws Exception {

        final Project project = Project.of("project", DependencyGroup.of(ImmutableMap.of(
                Identifier.of("foo"), ExactSemanticVersion.of(SemanticVersion.of(2)),
                Identifier.of("bar"), ExactSemanticVersion.of(SemanticVersion.of(1)))));

        final DependencyFetcher fetcher = DependencyFetcherFromMap.of(ImmutableMap.of(
                Identifier.of("foo"), ImmutableMap.of(SemanticVersion.of(1), DependencyGroup.of()),
                Identifier.of("bar"), ImmutableMap.of(SemanticVersion.of(1), DependencyGroup.of(ImmutableMap.of(
                        Identifier.of("baz"), ExactSemanticVersion.of(SemanticVersion.of(1))))),
                Identifier.of("baz"), ImmutableMap.of(SemanticVersion.of(1), DependencyGroup.of(ImmutableMap.of(
                        Identifier.of("bar"), ExactSemanticVersion.of(SemanticVersion.of(1)))))));

        final Either<ImmutableList<DependencyResolverException>, ImmutableMap<Identifier, SemanticVersion>> expected =
                Either.left(ImmutableList.of(
                        new VersionRequirementNotSatisfiedException(
                                Identifier.of("foo"),
                                ExactSemanticVersion.of(SemanticVersion.of(2)))));

        final Either<ImmutableList<DependencyResolverException>, ImmutableMap<Identifier, SemanticVersion>> actual =
                DependencyResolver.resolve(project.dependencies, fetcher);

        assertEquals(expected, actual);
    }
}
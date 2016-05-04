package com.vesperin.cue;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Floats;
import com.vesperin.base.Source;
import com.vesperin.cue.utils.IO;
import com.vesperin.cue.utils.Similarity;
import com.vesperin.cue.utils.Sources;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Huascar Sanchez
 */
public class CueTest {
  private static final Source SRC = Source.from("Foo",
    Joiner.on("\n").join(
      ImmutableList.of(
        "public class Foo {"
        , " public File processFile(){"
        , "   int x = new ConfigCode().consumeException;"
        , "   try {"
        , "     // exit if x is zero or negative"
        , "     if(x <= 0) {"
        , "       throw new IllegalArgumentException();"
        , "     }"
        , "   } catch(IllegalArgumentException e){"
        , "     System.err.println(1);"
        , "   }"
        , "   "
        , "   return createTxtFile();"
        , " }"
        , " "
        , " public File createTxtFile(){"
        , "   return new File(\"/foo.txt\");"
        , " }"
        , " "
        , " public static class ConfigCode {"
        , "   final int consumeException = 1;"
        , " }"
        , "}"
      )
    )
  );

  @Test public void testCueBasic() throws Exception {
    final Cue cue = new Cue();
    final Set<String> expected = Sets.newHashSet(
      "file", "create", "text", "process", "code", "configuration"
    );

    final List<String> concepts = cue.assignedConcepts(SRC).stream().sorted().collect(Collectors.toList());

    assertEquals(concepts.size(), expected.size());

    for(String each : concepts){
      assertThat(expected.contains(each), is(true));
    }

    final List<String> concepts2 = cue.assignedConcepts(Lists.newArrayList(SRC)).stream().sorted().collect(Collectors.toList());

    assertEquals(concepts, concepts2);
  }

  @Test public void testCueCodeRegion() throws Exception {
    final Cue cue = new Cue();

    final Set<String> names = Sets.newHashSet("processFile");

    final Set<String> expected = Sets.newHashSet(
      "file", "create", "text", "code", "configuration", "process"
    );

    final Set<String> concepts = cue.assignedConcepts(SRC, names).stream()
      .collect(Collectors.toSet());
    assertThat(!concepts.isEmpty(), is(true));

    assertEquals(expected, concepts);

    for(String each : concepts){
      assertThat(expected.contains(each), is(true));
    }

  }

  @Test public void testTypicalityScore() throws Exception {
    final Cue cue = new Cue();

    final Set<String> relevant = new HashSet<>();
    final List<Source> typical = cue.typicalityQuery(Code.corpus(), relevant, 1);
    final Source mostTypical = typical.get(0);

    assertEquals(mostTypical, Code.four());
  }

  @Test public void testMostTypicalSortingImplementation() throws Exception {
    final List<Source> files = collectJavaFilesInResources().stream()
      .map(Sources::from).collect(Collectors.toList());

    assertThat(!files.isEmpty(), is(true));

    final Set<String> relevant = new HashSet<>();

    final Cue cue = new Cue();
    final List<Source> typical = cue.typicalityQuery(files, relevant, 1);

    assertThat(!typical.isEmpty(), is(true));
  }

  @Test public void testMostTypicalSortingWithDiffBandwidth() throws Exception {
    final List<Source> files = collectJavaFilesInResources().stream()
      .map(Sources::from).collect(Collectors.toList());

    assertThat(!files.isEmpty(), is(true));

    final Set<String> relevant = new HashSet<>();

    final Cue cue = new Cue();
    final List<Source> typical1 = cue.typicalityQuery(files, relevant, 0.7, 1);
    assertThat(!typical1.isEmpty(), is(true));
  }

  @Test public void testMostFrequentConcept() throws Exception {
    final List<Source> files = collectJavaFilesInResources().stream()
      .map(Sources::from).collect(Collectors.toList());

    final Cue cue = new Cue();
    final List<String> concepts = cue.assignedConcepts(files);

    assertThat(concepts.isEmpty(), is(false));

  }

  private static List<File> collectJavaFilesInResources() {
    return IO.collectFiles(Paths.get(CueTest.class.getResource("/").getPath()), "java");
  }


  @Test public void testCommutativePropertyOfSimilarity() throws Exception {

    assertThat(
      Floats.compare(
        Similarity.similarityScore("text", "txt"),
        Similarity.similarityScore("txt", "text")
      ) == 0, is(true));

    assertThat(
      Floats.compare(
        Similarity.normalizeDistance("text", "txt"),
        Similarity.normalizeDistance("txt", "text")
      ) == 0, is(true));
  }
}

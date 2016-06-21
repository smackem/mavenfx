package net.smackem.mavenfx.model;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author pbo
 */
public class PathTest {
    @Test
    public void testAddStep() {
        final Path<Integer> begin = new Path<>(100);
        final Path<Integer> middle = begin.addStep(101, 1);
        final Path<Integer> end = middle.addStep(102, 2);

        assertThat(begin, is(middle.getTail()));
        assertThat(middle, is(end.getTail()));
        assertThat(middle.getTotalCost(), is(1.0));
        assertThat(end.getTotalCost(), is(3.0));

        assertThat(Arrays.asList(102, 101, 100), contains(end.getNodes().toArray()));
    }
}

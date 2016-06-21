package net.smackem.mavenfx.model;

//closed = {}
//q = emptyqueue;
//q.enqueue(0.0, makepath(start))
//while q is not empty
//    p = q.dequeueCheapest
//    if closed contains p.last then continue;
//    if p.last == destination then return p
//    closed.add(p.last)
//    foreach n in p.last.neighbours
//        newpath = p.continuepath(n)
//        q.enqueue(newpath.TotalCost + estimateCost(n, destination), newpath)
//return null
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;

/**
 * @author pbo
 */
public final class Path<TNode> {
    private final TNode head;
    private final Path<TNode> tail;
    private final double totalCost;

    @FunctionalInterface
    public interface DistanceFunc<TNode> {
        double calc(Path<TNode> path, TNode destination);
    }

    public TNode getHead() {
        return this.head;
    }

    public Path<TNode> getTail() {
        return this.tail;
    }

    public double getTotalCost() {
        return this.totalCost;
    }

    /**
     * @return an immutable collection containing the {@code TNode}s
     *      that make up this path.
     */
    public Collection<TNode> getNodes() {
        final Collection<TNode> collection = new LinkedList<>();

        for (Path<TNode> p = this; p != null; p = p.tail) {
            collection.add(p.head);
        }

        return Collections.unmodifiableCollection(collection);
    }

    public static <TNode> Path<TNode> findPath(TNode origin,
                                               TNode destination,
                                               DistanceFunc<TNode> distance,
                                               Function<TNode, Double> estimate,
                                               Function<TNode, Collection<TNode>> neighbours) {
        Objects.requireNonNull(origin);
        Objects.requireNonNull(destination);
        Objects.requireNonNull(distance);
        Objects.requireNonNull(estimate);
        Objects.requireNonNull(neighbours);

        final Set<TNode> closed = new HashSet<>();
        final PriorityQueue<PrioritizedPath<TNode>> open = new PriorityQueue<>();

        open.add(new PrioritizedPath<>(0.0, new Path<>(origin)));

        while (open.isEmpty() == false) {
            final Path<TNode> path = open.poll().path;

            if (closed.contains(path.head)) {
                continue;
            }

            if (Objects.equals(path.head, destination)) {
                return path;
            }

            closed.add(path.head);

            for (final TNode node : neighbours.apply(path.head)) {
                final double d = distance.calc(path, node);
                final Path<TNode> newPath = path.addStep(node, d);

                open.add(new PrioritizedPath<>(newPath.totalCost + estimate.apply(node), newPath));
            }
        }

        return null;
    }

    /////////////////////////////////////////////////////////////////

    private Path(TNode head, Path<TNode> tail, double totalCost) {
        Objects.requireNonNull(head);

        this.head = head;
        this.tail = tail;
        this.totalCost = totalCost;
    }

    Path(TNode head) {
        this(head, null, 0);
    }

    Path<TNode> addStep(TNode step, double stepCost) {
        return new Path<>(step, this, this.totalCost + stepCost);
    }

    private static class PrioritizedPath<TNode> implements Comparable<PrioritizedPath<TNode>> {
        final double estimatedCost;
        final Path<TNode> path;

        PrioritizedPath(double estimatedCost, Path<TNode> path) {
            this.estimatedCost = estimatedCost;
            this.path = path;
        }

        @Override
        public int compareTo(PrioritizedPath<TNode> o) {
            return Double.compare(this.estimatedCost, o.estimatedCost);
        }
    }
}

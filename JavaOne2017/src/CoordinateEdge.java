public class CoordinateEdge {
    private final Coordinate source;
    private final Coordinate destination;
    private final long weight;

    public CoordinateEdge(Coordinate source, Coordinate destination, long weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public Coordinate getSource() {
        return source;
    }

    public Coordinate getDestination() {
        return destination;
    }

    public long getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoordinateEdge that = (CoordinateEdge) o;

        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        return !(destination != null ? !destination.equals(that.destination) : that.destination != null);

    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CoordinateEdge{" +
                "source=" + source +
                ", destination=" + destination +
                ", weight=" + weight +
                '}';
    }
}

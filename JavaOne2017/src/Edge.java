public class Edge {
    private final String id;
    private final String name;
    private final Vertex source;
    private final Vertex destination;
    private final long weight;

    public Edge(String id, String name, Vertex source, Vertex destination, long weight) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public long getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (weight != edge.weight) return false;
        if (id != null ? !id.equals(edge.id) : edge.id != null) return false;
        if (name != null ? !name.equals(edge.name) : edge.name != null) return false;
        if (source != null ? !source.equals(edge.source) : edge.source != null) return false;
        return !(destination != null ? !destination.equals(edge.destination) : edge.destination != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (int) (weight ^ (weight >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", source=" + source +
                ", destination=" + destination +
                ", weight=" + weight +
                '}';
    }
}

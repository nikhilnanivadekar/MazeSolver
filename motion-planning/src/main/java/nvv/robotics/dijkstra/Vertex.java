package nvv.robotics.dijkstra;

public class Vertex
{
    private final String id;
    private final String name;
    private final long x;
    private final long y;

    public Vertex(String id, String name, long x, long y)
    {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public long getX()
    {
        return x;
    }

    public long getY()
    {
        return y;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        if (x != vertex.x) return false;
        if (y != vertex.y) return false;
        if (id != null ? !id.equals(vertex.id) : vertex.id != null) return false;
        return !(name != null ? !name.equals(vertex.name) : vertex.name != null);
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (x ^ (x >>> 32));
        result = 31 * result + (int) (y ^ (y >>> 32));
        return result;
    }

    @Override
    public String toString()
    {
        return "Vertex{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}

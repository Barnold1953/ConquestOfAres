package Graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by brb55_000 on 2/20/2015.
 */
public class ColorMesh {

    public final int FLOATS_PER_VERTEX = 6;
    public FloatBuffer m_vertexBuffer;
    public short[] m_indexBuffer = null;

    private int m_capacity;
    private int m_numIndices = 0;
    private final int BYTES_PER_FLOAT = 4;
    private int m_position = 0;
    private int m_indexPosition = 0;

    /// Gets capacity in vertices
    public int getCapacity() { return m_capacity; }
    public int getNumIndices() { return m_numIndices; }

    /// Creates and sets the capacity of the mesh in vertices
    /// Creates and sets the number of indices
    /// Will clear any existing data
    public void init(int capacity, int numIndices) {
        m_capacity = capacity;
        m_numIndices = numIndices;
        m_vertexBuffer = ByteBuffer.allocateDirect(capacity * FLOATS_PER_VERTEX * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        m_indexBuffer = new short[numIndices];
        m_position = 0;
    }

    /// Adds a vertex to the mesh. Make sure capacity is set
    public void addVertex(float x, float y, float z, float r, float g, float b) {
        m_vertexBuffer.put(x);
        m_vertexBuffer.put(y);
        m_vertexBuffer.put(z);
        m_vertexBuffer.put(r);
        m_vertexBuffer.put(g);
        m_vertexBuffer.put(b);
        m_position += FLOATS_PER_VERTEX;
    }

    /// Adds an index to the mesh
    public void addIndex(short index) {
        m_indexBuffer[m_indexPosition++] = index;
    }


}

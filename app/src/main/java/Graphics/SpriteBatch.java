package Graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * Created by brb55_000 on 4/20/2015.
 */
public class SpriteBatch {

    class RenderBatch {
        int offset;
        int numVertices;
        int texture;
    }

    // Buffers
    private IntBuffer m_vbo = null;
    private ByteBuffer m_buffer;
    // Uniform handles
    private int m_positionHandle;
    private int m_colorHandle;
    private int m_MVPMatrixHandle;
    private int m_TCoordHandle;
    private int m_textureHandle;
    // Constants
    private static final int BYTES_PER_VERTEX = 20;
    private static final int VERTS_PER_QUAD = 6;


    Vector<SpriteBatchGlyph> m_glyphs = new Vector<SpriteBatchGlyph>();
    Vector<RenderBatch> m_renderBatches = new Vector<RenderBatch>();

    // Begins a frame of rendering
    public void begin() {
        // Lazy init
        if (m_vbo == null) {
            m_vbo = IntBuffer.allocate(1);
            GLES20.glGenBuffers(1, m_vbo);

            int ph = ShaderHelper.getShader("simple");
            m_MVPMatrixHandle = GLES20.glGetUniformLocation(ph, "uMVPMatrix");
            m_positionHandle = GLES20.glGetAttribLocation(ph, "vPosition");
            m_colorHandle = GLES20.glGetAttribLocation(ph, "vColor");
            m_TCoordHandle = GLES20.glGetAttribLocation(ph, "vTextCoords");
            m_textureHandle = GLES20.glGetAttribLocation(ph, "texture");
        }
        m_glyphs.clear();
        m_renderBatches.clear();
    }

    // Draws a glyph
    public void draw(int texture, float x, float y, float w, float h, byte color[]) {
        SpriteBatchGlyph glyph = new SpriteBatchGlyph();
        glyph.x = x;
        glyph.y = y;
        glyph.w = w;
        glyph.h = h;
        glyph.r = color[0];
        glyph.g = color[1];
        glyph.b = color[2];
    }

    // Ends a frame of rendering
    public void end() {
        sortGlyphs();
        createRenderBatches();
    }

    // Renders to the sreen
    public void render(Camera camera) {
        GLES20.glUseProgram(ShaderHelper.getShader("simple"));

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glUniform1i(m_textureHandle, 0);
        GLES20.glUniformMatrix4fv(m_MVPMatrixHandle, 1, false, camera.getVPMatrix(), 0);

        GLES20.glEnableVertexAttribArray(m_positionHandle);
        GLES20.glEnableVertexAttribArray(m_colorHandle);
        GLES20.glEnableVertexAttribArray(m_TCoordHandle);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m_vbo.get(0));
        for (RenderBatch b : m_renderBatches) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, b.texture);

            GLES20.glVertexAttribPointer(m_positionHandle, 2, GLES20.GL_FLOAT, false, BYTES_PER_VERTEX, 0);
            GLES20.glVertexAttribPointer(m_TCoordHandle, 2, GLES20.GL_FLOAT, false, BYTES_PER_VERTEX, 8);
            GLES20.glVertexAttribPointer(m_colorHandle, 3, GLES20.GL_BYTE, false, BYTES_PER_VERTEX, 16);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, b.numVertices);
        }
        GLES20.glDisableVertexAttribArray(m_positionHandle);
        GLES20.glDisableVertexAttribArray(m_colorHandle);
        GLES20.glDisableVertexAttribArray(m_TCoordHandle);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    // For sorting glyphs
    class GlyphComparator implements Comparator<SpriteBatchGlyph> {
        public int compare(SpriteBatchGlyph a, SpriteBatchGlyph b) {
            return a.texture < b.texture ? -1 : a.texture == b.texture ? 0 : 1;
        }
    }

    private void sortGlyphs() {
        Collections.sort(m_glyphs, new GlyphComparator());
    }

    private void createRenderBatches() {
        if (m_glyphs.isEmpty()) return;
        // Allocate buffer storage
        final int bufferSize = m_glyphs.size() * BYTES_PER_VERTEX * VERTS_PER_QUAD;
        m_buffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
        m_buffer.position(0);

        RenderBatch batch = new RenderBatch();
        batch.offset = 0;
        batch.numVertices = 0;
        batch.texture = m_glyphs.get(0).texture;
        m_renderBatches.add(batch);

        int offset = 0;

        for (SpriteBatchGlyph g : m_glyphs) {

            if (g.texture != batch.texture) {
                batch.numVertices = offset - batch.offset;
                batch = new RenderBatch();
                batch.offset = offset;
                batch.numVertices = 0;
                batch.texture = g.texture;
                m_renderBatches.add(batch);
            }

            // Add the 6 vertices
            m_buffer.putFloat(g.x);
            m_buffer.putFloat(g.y + g.h);
            m_buffer.putFloat(g.u);
            m_buffer.putFloat(g.v);
            m_buffer.put(g.r);
            m_buffer.put(g.g);
            m_buffer.put(g.b);
            m_buffer.put(g.a);

            m_buffer.putFloat(g.x);
            m_buffer.putFloat(g.y);
            m_buffer.putFloat(g.u);
            m_buffer.putFloat(g.v);
            m_buffer.put(g.r);
            m_buffer.put(g.g);
            m_buffer.put(g.b);
            m_buffer.put(g.a);

            m_buffer.putFloat(g.x + g.w);
            m_buffer.putFloat(g.y);
            m_buffer.putFloat(g.u);
            m_buffer.putFloat(g.v);
            m_buffer.put(g.r);
            m_buffer.put(g.g);
            m_buffer.put(g.b);
            m_buffer.put(g.a);

            m_buffer.putFloat(g.x + g.w);
            m_buffer.putFloat(g.y);
            m_buffer.putFloat(g.u);
            m_buffer.putFloat(g.v);
            m_buffer.put(g.r);
            m_buffer.put(g.g);
            m_buffer.put(g.b);
            m_buffer.put(g.a);

            m_buffer.putFloat(g.x + g.w);
            m_buffer.putFloat(g.y + g.h);
            m_buffer.putFloat(g.u);
            m_buffer.putFloat(g.v);
            m_buffer.put(g.r);
            m_buffer.put(g.g);
            m_buffer.put(g.b);
            m_buffer.put(g.a);

            m_buffer.putFloat(g.x);
            m_buffer.putFloat(g.y + g.h);
            m_buffer.putFloat(g.u);
            m_buffer.putFloat(g.v);
            m_buffer.put(g.r);
            m_buffer.put(g.g);
            m_buffer.put(g.b);
            m_buffer.put(g.a);

            offset += VERTS_PER_QUAD;
        }
        batch.numVertices = offset - batch.offset;

        m_buffer.position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m_vbo.get(0));
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bufferSize, m_buffer, GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

}

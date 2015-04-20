package Graphics;

/**
 * Created by brb55_000 on 4/19/2015.
 */
public class Laser {
    public ColorMesh mesh = new ColorMesh();
    public float width = 10.0f;
    public boolean needsFinish = true;

    // Returns false when the laser is gone
    public boolean render(float[] vpMatrix) {
        width -= 0.4f;
        if (width <= 0.0f) return true;
        float alpha = width / 10.0f;
        mesh.renderLines(vpMatrix, width, alpha);
        return false;
    }
}

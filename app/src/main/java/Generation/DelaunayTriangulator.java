package Generation;

import java.util.*;

/** Delaunay triangulation. Adapted from Paul Bourke's triangulate: http://paulbourke.net/papers/triangulate/
 * @author Nathan Sweet */
public class DelaunayTriangulator {
	static private final float EPSILON = 0.000001f;
	static private final int INSIDE = 0;
	static private final int COMPLETE = 1;
	static private final int INCOMPLETE = 2;

	private final ArrayList<Integer> quicksortStack = new ArrayList<Integer>();
	private final ArrayList<Integer> triangles = new ArrayList<Integer>(16);
	private final ArrayList<Integer> edges = new ArrayList<Integer>();
	private final ArrayList<Boolean> complete = new ArrayList<Boolean>(16);
	private final float[] superTriangle = new float[6];

	public ArrayList<Integer> computeTriangles (ArrayList<Float> points, boolean sorted) {
		return computeTriangles(points, 0, points.size(), sorted);
	}

	/** Triangulates the given point cloud to a list of triangle indices that make up the Delaunay triangulation.
	 * @param points x,y pairs describing points. Duplicate points will result in undefined behavior.
	 * @param sorted If false, the points will be sorted by the x coordinate, which is required by the triangulation algorithm.
	 * @return triples of indexes into the points that describe the triangles in clockwise order. Note the returned array is reused
	 *         for later calls to the same method. */
	public ArrayList<Integer> computeTriangles (ArrayList<Float> points, int offset, int count, boolean sorted) {
		int end = offset + count;

		if (!sorted) quicksortPairs(points, offset, end - 1);

		// Determine bounds for super triangle.
		float xmin = points.get(0), ymin = points.get(1);
		float xmax = xmin, ymax = ymin;
		for (int i = offset + 2; i < end; i++) {
			float value = points.get(i);
			if (value < xmin) xmin = value;
			if (value > xmax) xmax = value;
			i++;
			value = points.get(i);
			if (value < ymin) ymin = value;
			if (value > ymax) ymax = value;
		}
		float dx = xmax - xmin, dy = ymax - ymin;
		float dmax = (dx > dy ? dx : dy) * 20f;
		float xmid = (xmax + xmin) / 2f, ymid = (ymax + ymin) / 2f;

		// Setup the super triangle, which contains all points.
		float[] superTriangle = this.superTriangle;
		superTriangle[0] = xmid - dmax;
		superTriangle[1] = ymid - dmax;
		superTriangle[2] = xmid;
		superTriangle[3] = ymid + dmax;
		superTriangle[4] = xmid + dmax;
		superTriangle[5] = ymid - dmax;

        ArrayList<Integer> edges = this.edges;
		edges.ensureCapacity(count / 2);

		ArrayList<Boolean> complete = this.complete;
		complete.ensureCapacity(count);

        ArrayList<Integer> triangles = this.triangles;
		triangles.clear();
		triangles.ensureCapacity(count);

		// Add super triangle.
		triangles.add(end);
		triangles.add(end + 2);
		triangles.add(end + 4);
		complete.add(false);

		// Include each point one at a time into the existing mesh.
		for (int pointIndex = offset; pointIndex < end; pointIndex += 2) {
			float x = points.get(pointIndex), y = points.get(pointIndex + 1);

			// If x,y lies inside the circumcircle of a triangle, the edges are stored and the triangle removed.
			for (int triangleIndex = triangles.size() - 1; triangleIndex >= 0; triangleIndex -= 3) {
				int completeIndex = triangleIndex / 3;
				if (complete.get(completeIndex)) continue;
				int p1 = triangles.get(triangleIndex - 2);
				int p2 = triangles.get(triangleIndex - 1);
				int p3 = triangles.get(triangleIndex);
				float x1, y1, x2, y2, x3, y3;
				if (p1 >= end) {
					int i = p1 - end;
					x1 = superTriangle[i];
					y1 = superTriangle[i + 1];
				} else {
					x1 = points.get(p1);
					y1 = points.get(p1 + 1);
				}
				if (p2 >= end) {
					int i = p2 - end;
					x2 = superTriangle[i];
					y2 = superTriangle[i + 1];
				} else {
					x2 = points.get(p2);
					y2 = points.get(p2 + 1);
				}
				if (p3 >= end) {
					int i = p3 - end;
					x3 = superTriangle[i];
					y3 = superTriangle[i + 1];
				} else {
					x3 = points.get(p3);
					y3 = points.get(p3 + 1);
				}
				switch (circumCircle(x, y, x1, y1, x2, y2, x3, y3)) {
				case COMPLETE:
					complete.set(completeIndex, true);
					break;
				case INSIDE:
					edges.add(p1);
					edges.add(p2);
					edges.add(p2);
					edges.add(p3);
					edges.add(p3);
					edges.add(p1);

					triangles.remove(triangleIndex);
					triangles.remove(triangleIndex - 1);
					triangles.remove(triangleIndex - 2);
					complete.remove(completeIndex);
					break;
				}
			}

			for (int i = 0, n = edges.size(); i < n; i += 2) {
				// Skip multiple edges. If all triangles are anticlockwise then all interior edges are opposite pointing in direction.
				int p1 = edges.get(i);
				if (p1 == -1) continue;
				int p2 = edges.get(i + 1);
				boolean skip = false;
				for (int ii = i + 2; ii < n; ii += 2) {
					if (p1 == edges.get(ii + 1) && p2 == edges.get(ii)) {
						skip = true;
                        edges.set(ii, -1);
					}
				}
				if (skip) continue;

				// Form new triangles for the current point. Edges are arranged in clockwise order.
				triangles.add(p1);
				triangles.add(edges.get(i + 1));
				triangles.add(pointIndex);
				complete.add(false);
			}
			edges.clear();
		}
		complete.clear();

		for (int i = triangles.size() - 1; i >= 0; i -= 3) {
			if (triangles.get(i) >= end || triangles.get(i - 1) >= end || triangles.get(i - 2) >= end) {
				triangles.remove(i);
				triangles.remove(i - 1);
				triangles.remove(i - 2);
			}
		}
		return triangles;
	}

	/** Returns INSIDE if point xp,yp is inside the circumcircle made up of the points x1,y1, x2,y2, x3,y3. Returns COMPLETE if xp
	 * is to the right of the entire circumcircle. Otherwise returns INCOMPLETE. Note: a point on the circumcircle edge is
	 * considered inside. */
	private int circumCircle (float xp, float yp, float x1, float y1, float x2, float y2, float x3, float y3) {
		float xc, yc;
		float y1y2 = Math.abs(y1 - y2);
		float y2y3 = Math.abs(y2 - y3);
		if (y1y2 < EPSILON) {
			if (y2y3 < EPSILON) return INCOMPLETE;
			float m2 = -(x3 - x2) / (y3 - y2);
			float mx2 = (x2 + x3) / 2f;
			float my2 = (y2 + y3) / 2f;
			xc = (x2 + x1) / 2f;
			yc = m2 * (xc - mx2) + my2;
		} else {
			float m1 = -(x2 - x1) / (y2 - y1);
			float mx1 = (x1 + x2) / 2f;
			float my1 = (y1 + y2) / 2f;
			if (y2y3 < EPSILON) {
				xc = (x3 + x2) / 2f;
				yc = m1 * (xc - mx1) + my1;
			} else {
				float m2 = -(x3 - x2) / (y3 - y2);
				float mx2 = (x2 + x3) / 2f;
				float my2 = (y2 + y3) / 2f;
				xc = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2);
				yc = m1 * (xc - mx1) + my1;
			}
		}

		float dx = x2 - xc;
		float dy = y2 - yc;
		float rsqr = dx * dx + dy * dy;

		dx = xp - xc;
		dx *= dx;
		dy = yp - yc;
		if (dx + dy * dy - rsqr <= EPSILON) return INSIDE;
		return xp > xc && dx > rsqr ? COMPLETE : INCOMPLETE;
	}

	/** Sorts x,y pairs of values by the x value.
	 * @param lower Start x index.
	 * @param upper End x index. */
	private void quicksortPairs (ArrayList<Float> values, int lower, int upper) {
        ArrayList<Integer> stack = quicksortStack;
		stack.add(lower);
		stack.add(upper - 1);
		while (stack.size() > 0) {
			upper = stack.remove(stack.size()-1);
			lower = stack.remove(stack.size()-1);
			if (upper <= lower) continue;
			int i = quicksortPartition(values, lower, upper);
			if (i - lower > upper - i) {
				stack.add(lower);
				stack.add(i - 2);
			}
			stack.add(i + 2);
			stack.add(upper);
			if (upper - i >= i - lower) {
				stack.add(lower);
				stack.add(i - 2);
			}
		}
	}

	private int quicksortPartition (final ArrayList<Float> values, int lower, int upper) {
		float value = values.get(lower);
		int up = upper;
		int down = lower;
		float temp;
		while (down < up) {
			while (values.get(down) <= value && down < up)
				down = down + 2;
			while (values.get(up) > value)
				up = up - 2;
			if (down < up) {
				temp = values.get(down);
				values.set(down, values.get(up));
				values.set(up, temp);

				temp = values.get(down + 1);
				values.set(down + 1, values.get(up + 1));
				values.set(up + 1,  temp);
			}
		}
		values.set(lower, values.get(up));
		values.set(up, value);

		temp = values.get(lower + 1);
		values.set(lower + 1, values.get(up + 1));
		values.set(up + 1, temp);
		return up;
	}
}


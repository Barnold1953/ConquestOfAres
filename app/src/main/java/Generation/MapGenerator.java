package Generation;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;

import java.nio.*;
import java.util.*;

import Game.Territory;
import Graphics.ColorMesh;
import Utils.Utils;
import simplevoronoi.GraphEdge;
import simplevoronoi.Voronoi;

/**
 * Created by brb55_000 on 1/21/2015.
 */
public class MapGenerator {

    private static final int BYTES_PER_FLOAT = 4;
    private static Random m_random = new Random();

    // Change these to affect the border modulation
    final static float MODULATE_SCALE = 32.0f; ///< Size of variation
    final static double MODULATE_PERSISTENCE = 0.8; ///< How much weight is passed down to each consecutive octave
    final static double MODULATE_FREQUENCY = 0.01; ///< Frequency of the modulation
    final static int MODULATE_OCTAVES = 5; ///< Number of fractal iterations
    private static OpenSimplexNoise osNoise = new OpenSimplexNoise();

    public static MapData mapData;

    /// Generates a map and returns the map data
    // TODO(Ben): Finish this
    public MapData generateMap(MapGenerationParams p) throws BufferOverflowException{
        mapData = new MapData();
        mapData.params = p;

        mapData.width = p.mapSize.getWidth();
        mapData.height = p.mapSize.getHeight();

        // random.setSeed(p.seed);
        generateTerritories(Utils.fastFloor(mapData.width), Utils.fastFloor(mapData.height), 100);

        // TODO(Ben): Segment heightmap into territories
        mapData.isDoneGenerating = true;
        return mapData;
    }

    /// Generates a raw heightmap
    public void generateHeightmap(int width, int height, double[][] heightMap, int seed) {
        // Generate the height data
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                heightMap[y][x] = getHeightValue(x, y, seed);
            }
        }
    }

    public double getHeightValue(int x, int y, int seed) {
        return octaveNoise2D((double)(seed + y), (double)(x - seed), 0.86, 0.0015, 7);
    }

    public void generateTerritories(int width, int height, int numTerritories) {

        ArrayList<Byte> colors = new ArrayList<Byte>();
        // Generate random territories
        // TODO(Ben): Generate more uniform points

        float xStep = 64.0f;
        float yStep = 64.0f;
        final float minDist = 96.0f;
        final float minDist2 = minDist * minDist;
        int maxIteration = 100;
        int pindex = 0;
        float dx, dy;

        mapData.territoryIndices = new int[Utils.fastFloor(height)][Utils.fastFloor(width)];

        // set array as uninitialized
        for(int i = 0; i < mapData.territoryIndices.length; i++){
            Arrays.fill(mapData.territoryIndices[i],0);
        }

        // Determine bounds for placing territories based on wrap mode
        float startX = 0.0f, startY = 0.0f, genWidth = width, genHeight = height;
        switch(mapData.params.mapSymmetry) {
            case HORIZONTAL:
                startX = minDist / 2.0f;
                genWidth = width / 2.0f - startX * 2.0f;
                break;
            case VERTICAL:
                startY = minDist / 2.0f;
                genHeight = height / 2.0f - startY * 2.0f;
                break;
            case RADIAL:
                startX = minDist / 2.0f;
                genWidth = width / 2.0f - startX * 2.0f;
                startY = minDist / 2.0f;
                genHeight = height / 2.0f - startY * 2.0f;
                break;
        }

        // Generate territories
        mapData.territories = new Vector<Territory>();
        while (true) {
            Territory territory = new Territory();
            boolean stop = false;
            int iter = 0;
            do {
                territory.x = m_random.nextFloat() * genWidth + startX;
                territory.y = m_random.nextFloat() * genHeight + startY;
                stop = true;
                for (Territory t : mapData.territories) {
                    if (mapData.params.horizontalWrap) {
                        dx = Math.abs(t.x - territory.x);
                        dy = Math.abs(t.y - territory.y);
                        dx = Math.min(dx, width - dx);
                        dy = Math.min(dy, height - dy);
                    } else {
                        dx = t.x - territory.x;
                        dy = t.y - territory.y;
                    }
                    if (dx * dx + dy * dy < minDist2) {
                        stop = false;
                        break;
                    }
                }
                iter++;
            } while (stop == false && iter != maxIteration);

            if (iter == maxIteration) {
                break;
            }

            territory.height = (float) getHeightValue((int) territory.x, (int) territory.y, mapData.params.seed);
            // TODO(Ben): More terrain types, humidity / temperature distribution
            mapData.territories.add(territory);
        }

        // Clone territories for symmetry
        int size = mapData.territories.size();
        switch(mapData.params.mapSymmetry) {
            case HORIZONTAL:
                for (int i = 0; i < size; i++) {
                    Territory newTerritory = new Territory(mapData.territories.get(i));
                    newTerritory.x = width - newTerritory.x;
                    mapData.territories.add(newTerritory);
                }
                break;
            case RADIAL:
                for (int i = 0; i < size; i++) {
                    Territory newTerritory = new Territory(mapData.territories.get(i));
                    newTerritory.x = width - newTerritory.x;
                    mapData.territories.add(newTerritory);
                }
                size = mapData.territories.size();
                // Purposely omitting break!
            case VERTICAL:
                for (int i = 0; i < size; i++) {
                    Territory newTerritory = new Territory(mapData.territories.get(i));
                    newTerritory.y = height - newTerritory.y;
                    mapData.territories.add(newTerritory);
                }
                break;
        }

        // Set color values
        for (Territory t : mapData.territories) {
            if (t.height < 0.0f) {
                t.terrainType = Territory.TerrainType.Ocean;
                colors.add((byte)0);
                colors.add((byte)0);
                colors.add((byte)170);
            } else if (t.height < 0.4f) {
                t.terrainType = Territory.TerrainType.Grassland;
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
            } else {
                t.terrainType = Territory.TerrainType.Mountain;
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
            }
        }

        // set indices
        for(int i=0; i < mapData.territories.size(); i++) mapData.territories.get(i).index = i;

        // create list of indices
        double xs[] = new double[mapData.territories.size()];
        double ys[] = new double[mapData.territories.size()];
        for(int i=0; i < mapData.territories.size(); i++) {
            xs[i] = mapData.territories.get(i).x;
            ys[i] = mapData.territories.get(i).y;
        }


        // set all diagram edges to -1
        float c;
        Voronoi veronoi = new Voronoi(0);
        List<GraphEdge> veronoiDiagram = veronoi.generateVoronoi(xs,ys,startX,genWidth-1,startY,genHeight-1);
        for(GraphEdge edge : veronoiDiagram) {
            // check to see if the difference is greater in the x or y direction
            Boolean basedY = Math.abs((edge.y2 - edge.y1)/(edge.x2 - edge.x1)) < 1;

            // get the length of the line to be drawn, as well as the point to start
            int lineLength;
            double slope;
            int initial, initialSecondary;
            if(basedY) {
                lineLength = Math.abs(Utils.fastFloor(edge.x1) - Utils.fastFloor(edge.x2));
                initial = Utils.fastFloor(edge.x1);
                initialSecondary = Utils.fastFloor(edge.y1);
                slope = (edge.y2 - edge.y1)/(edge.x2 - edge.x1);
            } else {
                lineLength = Math.abs(Utils.fastFloor(edge.y1) - Utils.fastFloor(edge.y2));
                initial = Utils.fastFloor(edge.y1);
                initialSecondary = Utils.fastFloor(edge.x1);
                slope = (edge.x2 - edge.x1)/(edge.y2 - edge.y1);
            }
            for(int i=0; i <= lineLength; i++) {
                int firstCoord = Utils.fastFloor(slope*i) + initialSecondary;
                int secondCoord = initial + i;
                if(!basedY) mapData.territoryIndices[secondCoord][firstCoord] = 1;
                else mapData.territoryIndices[firstCoord][secondCoord] = 1;

            }
        }

        /*// now do a depth first search to do everything else
        for(Territory territory : mapData.territories) {
            int intialx = Utils.fastFloor(territory.x);
            int initialy = Utils.fastFloor(territory.y);

            // saves unexplored coordinate paths
            Stack<Point> freeCoords = new Stack<>();
            freeCoords.push(new Point(intialx,initialy));
            while(!freeCoords.empty()) {
                Point position = freeCoords.pop();
                if(mapData.territoryIndices[position.y][position.x] != -2 ) continue;
                while(true) {
                    mapData.territoryIndices[Utils.fastFloor(position.y)][Utils.fastFloor(position.x)] = 0;
                    if(position.x > territory.maxX) territory.maxX = position.x;
                    if(position.y > territory.maxY) territory.maxY = position.y;
                    if(position.x < territory.textureX) territory.textureX = position.x;
                    if(position.y < territory.textureY) territory.textureY = position.y;

                    // pick a direction to fill in, if it hasn't been filled in.
                    // if there are other possible options, save them on the stack for later
                    Boolean madeSelection = false;
                    if(position.y < genHeight - 1 && mapData.territoryIndices[position.y + 1][position.x] == -2) {
                        position.y++;
                        madeSelection = true;
                    }
                    if(position.x < genWidth-1 && mapData.territoryIndices[position.y][position.x+1] == -2) {
                        if(!madeSelection) {
                            position.x++;
                            madeSelection = true;
                        }
                        else freeCoords.push(new Point(position.x+1,position.y));
                    }
                    if(position.y > 0 && mapData.territoryIndices[position.y-1][position.x] == -2) {
                        if(!madeSelection) {
                            position.y--;
                            madeSelection = true;
                        }
                        else freeCoords.push(new Point(position.x,position.y-1));
                    }
                    if(position.x > 0 && mapData.territoryIndices[position.y][position.x-1] == -2) {
                        if(!madeSelection) position.x--;
                        else freeCoords.push(new Point(position.x-1,position.y));
                    }
                    else break;
                }
            }
        }

        for(int i=0; i < height; i++) {
            for(int j=0; j < width; j++) {
                if(mapData.territoryIndices[i][j] == -2 || mapData.territoryIndices[i][j] == -1) {
                    mapData.territoryIndices[i][j] = 0;
                }
            }
        }*/

        for(int i=0; i < mapData.territories.size(); i++) {
            if(i==0) {
                Territory t = mapData.territories.get(i);
                t.textureWidth = width;
                t.textureHeight = height;
                t.pixelBuffer = ByteBuffer.allocateDirect(t.textureHeight * t.textureWidth * 4).order(ByteOrder.nativeOrder());
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        if (mapData.territoryIndices[y][x] == 0) {
                            t.pixelBuffer.put((byte) 0);
                            t.pixelBuffer.put((byte) 0);
                            t.pixelBuffer.put((byte) 255);
                            t.pixelBuffer.put((byte) 255);
                        }
                    }
                }
            } else if(i==1) {
                Territory t = mapData.territories.get(i);
                t.textureWidth = width;
                t.textureHeight = height;
                t.pixelBuffer = ByteBuffer.allocateDirect(t.textureHeight * t.textureWidth * 4).order(ByteOrder.nativeOrder());
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        if (mapData.territoryIndices[y][x] == 0) {
                            t.pixelBuffer.put((byte) 0);
                            t.pixelBuffer.put((byte) 255);
                            t.pixelBuffer.put((byte) 0);
                            t.pixelBuffer.put((byte) 255);
                        }
                    }
                }
            }
            else {
                Territory t = mapData.territories.get(i);
                t.textureWidth = 0;
                t.textureHeight = 0;
                t.pixelBuffer = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder());
            }
        }

       /* // Generate all the textures for the territories
        for (int i = 0; i < mapData.territories.size(); i++) {
            Territory t = mapData.territories.get(i);
            t.textureWidth = t.maxX - t.textureX;
            t.textureHeight = t.maxY - t.textureY;
            t.pixelBuffer = ByteBuffer.allocateDirect(t.textureHeight * t.textureWidth * 4).order(ByteOrder.nativeOrder());
            for (int y = t.textureY; y < t.maxY; y++) {
                for (int x = t.textureX; x < t.maxX; x++) {
                    if (mapData.territoryIndices[y][x] != i) {
                        t.pixelBuffer.put((byte) 0);
                        t.pixelBuffer.put((byte) 0);
                        t.pixelBuffer.put((byte) 0);
                        t.pixelBuffer.put((byte) 0);
                    } else {
                        int newIndex;
                        boolean isEdge = false;
                        Territory nextTerritory;
                        if (x < width - 1) {
                            newIndex = mapData.territoryIndices[y][x + 1];
                            if (newIndex != i) {
                                // Add neighbor territory if needed
                                if(newIndex > mapData.territories.size()) {
                                    Log.d("","");
                                }
                                nextTerritory = mapData.territories.get(newIndex);
                                if (!t.neighbors.contains(nextTerritory)) {
                                    t.neighbors.add(nextTerritory);
                                }
                                isEdge = true;
                            } else if (x + 1 < width - 1) {
                                // For thicker borders
                                if (mapData.territoryIndices[y][x + 2] != i) isEdge = true;
                            }
                        }
                        if (x > 0) {
                            newIndex = mapData.territoryIndices[y][x - 1];
                            if (newIndex != i) {
                                nextTerritory = mapData.territories.get(newIndex);
                                if (!t.neighbors.contains(nextTerritory)) {
                                    t.neighbors.add(nextTerritory);
                                }
                                isEdge = true;
                            } else if (x - 1 > 0) {
                                // For thicker borders
                                if (mapData.territoryIndices[y][x - 2] != i) isEdge = true;
                            }
                        }
                        if (y < height - 1) {
                            newIndex = mapData.territoryIndices[y + 1][x];
                            if (newIndex != i) {
                                nextTerritory = mapData.territories.get(newIndex);
                                if (!t.neighbors.contains(nextTerritory)) {
                                    t.neighbors.add(nextTerritory);
                                }
                                isEdge = true;
                            } else if (y + 1 < height - 1) {
                                // For thicker borders
                                if (mapData.territoryIndices[y + 2][x] != i) isEdge = true;
                            }
                        }
                        if (y > 0) {
                            newIndex = mapData.territoryIndices[y - 1][x];
                            if (newIndex != i) {
                                nextTerritory = mapData.territories.get(newIndex);
                                if (!t.neighbors.contains(nextTerritory)) {
                                    t.neighbors.add(nextTerritory);
                                }
                                isEdge = true;
                            } else if (y - 1 > 0) {
                                // For thicker borders
                                if (mapData.territoryIndices[y - 2][x] != i) isEdge = true;
                            }
                        }
                        if (isEdge) {
                            t.pixelBuffer.put((byte) 168);
                            t.pixelBuffer.put((byte) 168);
                            t.pixelBuffer.put((byte) 168);
                        } else {
                            t.pixelBuffer.put((byte) 255);
                            t.pixelBuffer.put((byte) 255);
                            t.pixelBuffer.put((byte) 255);
                        }
                        // Alpha
                        t.pixelBuffer.put((byte) 255);
                    }
                }
            }
        }*/

        // Add graph lines
        float r, g, b;
        mapData.territoryGraphMesh = new ColorMesh();
        for (Territory t1 : mapData.territories) {
            for (Territory t2 : t1.neighbors) {
                if (t1.terrainType == Territory.TerrainType.Ocean || t2.terrainType == Territory.TerrainType.Ocean) {
                    r = 0;
                    g = 1.0f;
                    b = 1.0f;
                } else {
                    r = 1.0f;
                    g = 0.0f;
                    b = 0.0f;
                }
                mapData.territoryGraphMesh.addVertex((t1.x / width) * 2.0f - 1.0f, (t1.y / height) * 2.0f - 1.0f, 0.0f, r, g, b);
                mapData.territoryGraphMesh.addVertex((t2.x / width) * 2.0f - 1.0f, (t2.y / height) * 2.0f - 1.0f, 0.0f, r, g, b);
            }
        }
    }

    public static Territory getClosestTerritory(float x, float y, MapData mapData) {
        return mapData.territories.get(getClosestTerritoryIndex(x, y, mapData));
    }

    private static int getClosestTerritoryIndex(float x, float y, MapData mapData) {
        return mapData.territoryIndices[(int)Math.round(y)][(int)Math.round(x)];
    }

    private static double octaveNoise2D(double x, double y, double persistence, double frequency, int octaves) {
        double total = 0.0;
        double amplitude = 1.0;
        double maxAmplitude = 0.0;

        for (int i = 0; i < octaves; i++) {
            total += osNoise.eval(x * frequency, y * frequency) * amplitude;
            frequency *= 2.0;
            maxAmplitude += amplitude;
            amplitude *= persistence;
        }
        return total / maxAmplitude;
    }

    private static double ridgedOctaveNoise2D(double x, double y, double persistence, double frequency, int octaves) {
        double total = 0.0;
        double amplitude = 1.0;
        double maxAmplitude = 0.0;

        for (int i = 0; i < octaves; i++) {
            total += ((1.0 - Math.abs(osNoise.eval(x * frequency, y * frequency))) * 2.0 - 1.0) * amplitude;
            frequency *= 2.0;
            maxAmplitude += amplitude;
            amplitude *= persistence;
        }
        return total / maxAmplitude;
    }
}

package Game;

import java.util.Vector;

import Generation.MapGenerator;

/**
 * Created by Nathan on 3/29/2015.
 */
public class PathFinding {
    private Territory findMin(Territory territory){
        Territory t = territory.neighbors.get(0);
        for(int i = 1; i < territory.neighbors.size(); i++){
            t = t.distance < territory.neighbors.get(i).distance ? t : territory.neighbors.get(i);
        }
        return t;
    }

    private double getDistance(Territory t1, Territory t2){
        float x1 = t1.x, x2 = t2.x;
        float y1 = t1.y, y2 = t1.y;
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y1) * (y1 - y2));
    }

    private void setNeighbors(Territory territory){
        territory.visited = true;
        for(int i = 0; i < territory.neighbors.size(); i++){
            Territory t = territory.neighbors.get(i);
            if(territory.owner == t.owner){
                double dist = getDistance(territory, t) + territory.distance;
                t.distance = dist < t.distance ? dist : t.distance;
            }
            if(!t.visited){
                setNeighbors(t);
            }
        }
    }

    public Vector<Territory> getPath(Territory source, Territory destination){
        Vector<Territory> notVisited = new Vector<>();
        Vector<Territory> visited = new Vector<>();
        Vector<Territory> path = new Vector<>();

        for(int i = 0; i < MapGenerator.mapData.territories.size(); i++){
            Territory territory = MapGenerator.mapData.territories.get(i);
            territory.distance = 1000000;
            territory.visited = false;
        }
        source.distance = 0;
        source.visited = true;

        setNeighbors(source);

        if(destination.distance == 1000000){
            return null;
        }

        Territory current = destination;
        path.add(destination);
        while(current != source){
            current = findMin(current);
            path.add(current);
        }
        return path;
    }
}

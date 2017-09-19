package com.gameinstance.submarine.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gringo on 30.12.2016 20:38.
 *
 */
public class MathUtils {

    public static float distance(float [] p1, float [] p2) {
        float x = p2[0] - p1[0];
        float y = p2[1] - p1[1];
        return (float)Math.sqrt(x * x + y * y);
    }

    public static boolean coordEquals(float [] p1, float [] p2) {
        return p1[0] == p2[0] && p1[1] == p2[1];
    }

    public static boolean testQuads(float [] c1, float [] c2, float w1, float h1, float w2, float h2) {
        float minx1 = c1[0] - w1 / 2.0f;
        float maxx1 = c1[0] + w1 / 2.0f;
        float miny1 = c1[1] - h1 / 2.0f;
        float maxy1 = c1[1] + h1 / 2.0f;
        float minx2 = c2[0] - w2 / 2.0f;
        float maxx2 = c2[0] + w2 / 2.0f;
        float miny2 = c2[1] - h2 / 2.0f;
        float maxy2 = c2[1] + h2 / 2.0f;
        return !(maxx1 < minx2 || minx1 > maxx2 || maxy1 < miny2 || miny1 > maxy2);
    }

    public static float [] getDirection(float [] p1, float [] p2) {
        float [] dir = new float[2];
        float dist = distance(p1, p2);
        dir[0] = p2[0] - p1[0];
        dir[1] = p2[1] - p1[1];
        if (dist != 0) {
            dir[0] /= dist;
            dir[1] /= dist;
        }
        return dir;
    }

    public static float [] vecSum(float [] p1, float [] p2) {
        float [] p = new float[2];
        p[0] = p1[0] + p2[0];
        p[1] = p1[1] + p2[1];
        return p;
    }

    public static float [] vecMulFloat(float [] p, float x) {
        return new float[] {p[0] * x, p[1] * x};
    }

    public static List<Integer> findPath(int [] map, int start, int finish, int width, int height) {
        int [] newMap = new int[map.length];
        for (int i = 0; i < newMap.length; i++) {
            newMap[i] = -1;
        }
        markPath(map, start, finish, width, height, 0, newMap);
        if (newMap[finish] == -1) {
            //recalculate path
            int newFinish = getNearestNode(finish, newMap, width);
            return buildPath(newFinish, newMap, width, height);
        } else {
            return buildPath(finish, newMap, width, height);
        }
    }

    private static void markPath(int [] map, int start, int finish, int width, int height,
                                 int curDist, int[] newMap) {
        newMap[start] = curDist;
        List<Integer> startWave = Collections.singletonList(start);
        if (start != finish) {
            List<Integer> newWave = getNextWave(startWave, width, height, newMap, map);
            while (newWave != null && !newWave.contains(finish)) {
                newWave = getNextWave(newWave, width, height, newMap, map);
            }
        }
    }

    private static List<Integer> getNextWave(List<Integer> currentWave, int width,
                                             int height, int [] newMap, int [] map) {
        List<Integer> acceptedNeighbors = new ArrayList<>();
        for (Integer currentNode : currentWave) {
            List<Integer> neighbors = getNeighbors(currentNode, width, height);
            for (Integer neighbor : neighbors) {
                if (newMap[neighbor] == -1 && map[neighbor] != 1) {
                    newMap[neighbor] = newMap[currentNode] + 1;
                    acceptedNeighbors.add(neighbor);
                }
            }
        }
        return acceptedNeighbors;
    }

    private static List<Integer> getNeighbors(int pos, int width, int height) {
        List<Integer> neighbors = new ArrayList<>();
        int n1 = getNeighbor(pos, -1, 0, width, height);
        if (n1 != -1)
            neighbors.add(n1);
        int n2 = getNeighbor(pos, 1, 0, width, height);
        if (n2 != -1)
            neighbors.add(n2);
        int n3 = getNeighbor(pos, 0, -1, width, height);
        if (n3 != -1)
            neighbors.add(n3);
        int n4 = getNeighbor(pos, 0, 1, width, height);
        if (n4 != -1)
            neighbors.add(n4);
        return neighbors;
    }

    private static int getNeighbor(int pos, int dx, int dy, int width, int height) {
        int x = pos % width + dx;
        int y = pos / width + dy;
        if (x < 0 || y < 0 || x >= width || y >= height)
            return -1;
        return y * width + x;
    }

    private static List<Integer> buildPath(int finish, int [] newMap, int width, int height) {
        List<Integer> path = new ArrayList<>();
        getNextNode(finish, newMap, path, width, height);
        return path;
    }

    private static void getNextNode(int pos, int [] newMap, List<Integer> path, int width, int height) {
        path.add(pos);
        if (newMap[pos] != 0) {
            int nextValue = newMap[pos] - 1;
            List<Integer> neighbors = getNeighbors(pos, width, height);
            for (Integer neighbor : neighbors) {
                if (newMap[neighbor] == nextValue) {
                    getNextNode(neighbor, newMap, path, width, height);
                    break;
                }
            }
        }
    }

    private static int getNearestNode(int finish, int[] newMap, int width) {
        float minDist = 100000.0f;
        int minCell = 0;
        int fx = finish % width;
        int fy = finish / width;
        float [] ff = new float[] {fx, fy};
        for (int pos = 0; pos < newMap.length; pos++) {
            if (newMap[pos] != -1) {
                int x = pos % width;
                int y = pos / width;
                float dist = distance(new float[] {x, y}, ff);
                if (dist < minDist) {
                    minDist = dist;
                    minCell = pos;
                }
            }
        }
        return minCell;
    }

    public static List<float []> getPath (float [] startPos, float [] finishPos, int [] map,
                                          int width, int height, float cellSize) {
        int startX = (int)((startPos[0] + 0.5f * width * cellSize) / cellSize);
        int startY = (int)((-startPos[1] + 0.5f * height * cellSize) / cellSize);
        int finishX = (int)((finishPos[0] + 0.5f * width * cellSize) / cellSize);
        int finishY = (int)((-finishPos[1] + 0.5f * height * cellSize) / cellSize);
        int start = startX + width * startY;
        int finish = finishX + width * finishY;
        List<Integer> path = findPath(map, start, finish, width, height);
        List<float[]> points = new ArrayList<>();
        for (int i = path.size() - 1; i >= 0; i-- ) {
            float x = (path.get(i) % width) * cellSize + 0.5f * cellSize - 0.5f * width * cellSize;
            float y = -(path.get(i) / width) * cellSize - 0.5f * cellSize + 0.5f * height * cellSize;
            points.add(new float[] {x, y});
        }
        return points;
    }

    public static float min(float x1, float x2) {
        return x1 < x2 ? x1 : x2;
    }
}


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class BattleSnakeHandlers
{

    public static final String NAME = "Inland-Taipans";
    public static final double MAX_VALID_CUTOFF = 0.90f;

    public Object handleStart(Map<String, Object> requestBody)
    {
        // Dummy Response
        parseStart(requestBody);
        Map<String, Object> responseObject = new HashMap<String, Object>();
        responseObject.put("name", NAME);
        responseObject.put("color", "#80F700");
        responseObject.put("head_url", "https://img.4plebs.org/boards/s4s/image/1390/48/1390481001892.png");
        responseObject.put("taunt", "Get Shreked");
        return responseObject;
    }

    public Object handleMove(Map<String, Object> requestBody)
    {
        this.parseBoard(requestBody);
        // Dummy Response
        Map<String, Object> responseObject = new HashMap<String, Object>();
        String move = getMove();
        
       // printDistanceBoard();
        
        responseObject.put("move", move);
        responseObject.put("taunt", "Get Shreked");
        return responseObject;
    }
    
    private void printDistanceBoard()
    {
        for(int j = 0; j < Board.height; j++)
        {
            for(int i = 0; i < Board.width; i++)
            {
                System.err.print(String.format("[%3d]", Board.distanceMap[i][j]));
            }
            System.err.println();
        }
    }

    public Object handleEnd(Map<String, Object> requestBody)
    {
        // No response required
        Map<String, Object> responseObject = new HashMap<String, Object>();
        return responseObject;
    }

    private String getMove()
    {
        narrowValidMoves();
        calculateShortestDistanceMap();
        
        return foodDirection();
    }
    
    /**
     * Narrows down valid moves based on how much of the map the snake would cut off by moving a certain direction
     */
    private void narrowValidMoves()
    {
        int leftValid = 0;
        int rightValid = 0;
        int upValid = 0;
        int downValid = 0;
        
        if(!reverseDir().equals("left"))
        {
            calculateShortestDistanceMap(ourNextCoords("left"), true);
            leftValid = getValidMoves();
        } else if(!reverseDir().equals("right"))
        {
            calculateShortestDistanceMap(ourNextCoords("right"), true);
            rightValid = getValidMoves();
        } else if(!reverseDir().equals("up"))
        {
            calculateShortestDistanceMap(ourNextCoords("up"), true);
            upValid = getValidMoves();
        } else
        {
            calculateShortestDistanceMap(ourNextCoords("down"), true);
            downValid = getValidMoves();
        }
        
        double maxValid = getMaxValidMoves() * MAX_VALID_CUTOFF;
        if(leftValid < maxValid)
            Board.invalidCoords.add(ourNextCoords("left"));
        if(rightValid < maxValid)
            Board.invalidCoords.add(ourNextCoords("right"));
        if(upValid < maxValid)
            Board.invalidCoords.add(ourNextCoords("up"));
        if(downValid < maxValid)
            Board.invalidCoords.add(ourNextCoords("down"));
    }
    
    /**
     * @return The number of open positions on the map that you can reach
     */
    private int getValidMoves()
    {
        int valid = 0;
        int max = Board.width * Board.height;
        for(int i = 0; i < Board.distanceMap.length; i++)
        {
            for(int j = 0; j < Board.distanceMap[0].length; j++)
            {
                if(Board.distanceMap[i][j] != -1 || Board.distanceMap[i][j] != max)
                    valid++;
            }
        }
        return valid;
    }
    
    /**
     * @return The number of open positions on the map
     */
    private int getMaxValidMoves()
    {
        int valid = 0;
        int max = Board.width * Board.height;
        for(int i = 0; i < Board.distanceMap.length; i++)
        {
            for(int j = 0; j < Board.distanceMap[0].length; j++)
            {
                if(Board.distanceMap[i][j] != -1)
                    valid++;
            }
        }
        return valid;
    }

    private String foodDirection()
    {
        int[] t = findShortestFoodCoord();
        int sp[] = shortestPath(t[0], t[1], -1);
        //System.err.println("Shortest X: " + sp[0] + " Y: " + sp[1]);
        if(sp[0] == -1 && sp[1] == -1) // No Path Available To Food
        {
            return stall();
        }
        return coordToDir(sp);
    }
    
    private String stall()
    {
        findLargestAccessibleArea();
        
        return curDir();
    }
    
    private void findLargestAccessibleArea()
    {
        
    }
    
    
    /**
     * @param c The coordinates to check. Must be a neighbouring coordinate
     * @return The direction in which the coordinates are coming from
     */
    private String coordToDir(int[] c)
    {
        if (Arrays.equals(ourNextCoords("left"), c))
        {
            return "left";
        }
        if (Arrays.equals(ourNextCoords("right"), c))
        {
            return "right";
        }
        if (Arrays.equals(ourNextCoords("up"), c))
        {
            return "up";
        }
        if (Arrays.equals(ourNextCoords("down"), c))
        {
            return "down";
        }
        return curDir();

    }

    /**
     * @param x1 The x coordinate of the first location
     * @param y1 The y coordinate of the first location
     * @param x2 The x coordinate of the second location
     * @param y2 The y coordinate of the second location
     * @return -1 If (x1,y1) and (x2,y2) are invalid. 0 If the distance to (x1,y1) < (x2,y2) else 1
     */
    private int whichOne(int x1, int y1, int x2, int y2)
    {
        if (checkXY(x1, y1))
        {
            if (checkXY(x2, y2))
            {
                return Board.distanceMap[x1][y1] <= Board.distanceMap[x2][y2] ? 0 : 1;
            } else
            {
                return 0;
            }
        } else if (checkXY(x2, y2))
        {
            return 1;
        }

        return -1;
    }

    /**
     * @param x x Coordinate to check
     * @param y y Coordinate to check
     * @return true if (x,y) is on the map and can be reached from the current location
     */
    private boolean checkXY(int x, int y)
    {
        return validX(x) && validY(y) && Board.distanceMap[x][y] != -1 && Board.distanceMap[x][y] != (Board.width * Board.height);
    }

    /**
     * @param x x Coordinate to check
     * @return true if x is on the map
     */
    private boolean validX(int x)
    {
        return (x >= 0 && x < Board.width);
    }

    /**
     * @param y y Coordinate to check
     * @return true if y is on the map
     */
    private boolean validY(int y)
    {
        return (y >= 0 && y < Board.height);
    }

    /**
     * @param x x Coordinate to check
     * @param y y Coordinate to check
     * @return true if (x,y) is the head of our snake
     */
    private boolean isHead(int x, int y)
    {
        return (ourCoords()[0] == x && ourCoords()[1] == y);
    }

    // 
    /**
     * @param x x Coordinate to check
     * @param y y Coordinate to check
     * @param lastMov Last action that was taken 0: x-1, 1: x+1, 2: y-1, 3: y+1
     * @return The first coordinates for the shortest path to (x,y)
     */
    private int[] shortestPath(int x, int y, int lastMov)
    {
        int x0VSx1 = whichOne(x - 1, y, x + 1, y);

        int x0VSy0 = whichOne(x - 1, y, x, y - 1);
        int x0VSy1 = whichOne(x - 1, y, x, y + 1);

        int x1VSy0 = whichOne(x + 1, y, x, y - 1);
        int x1VSy1 = whichOne(x + 1, y, x, y + 1);

        int y0VSy1 = whichOne(x, y - 1, x, y + 1);

        if (x0VSx1 == 0 && x0VSy0 == 0 && x0VSy1 == 0)
        {
            if (isHead(x - 1, y))
            {
                return toIntArray(x, y);
            }
            if (lastMov != 1)
            {
                return shortestPath(x - 1, y, 0);
            } else
            {
                x0VSx1 = whichOne(-1, -1, x + 1, y);
                x0VSy0 = whichOne(-1, -1, x, y - 1);
                x0VSy1 = whichOne(-1, -1, x, y + 1);
            }
        }
        if (x0VSx1 == 1 && x1VSy0 == 0 && x1VSy1 == 0)
        {
            if (isHead(x + 1, y))
            {
                return toIntArray(x, y);
            }
            if (lastMov != 0)
            {
                return shortestPath(x + 1, y, 1);
            } else
            {
                x1VSy0 = whichOne(-1, -1, x, y - 1);
                x1VSy1 = whichOne(-1, -1, x, y + 1);
            }
        }
        if (x0VSy0 == 1 && x1VSy0 == 1 && y0VSy1 == 0)
        {
            if (isHead(x, y - 1))
            {
                return toIntArray(x, y);
            }
            if (lastMov != 3)
            {
                return shortestPath(x, y - 1, 2);
            } else
            {
                y0VSy1 = whichOne(-1, -1, x, y + 1);
            }
        }
        if (x0VSy1 == 1 && x1VSy1 == 1 && y0VSy1 == 1)
        {
            if (isHead(x, y + 1))
            {
                return toIntArray(x, y);
            }
            if (lastMov != 2)
            {
                return shortestPath(x, y + 1, 3);
            }
        }
        return toIntArray(-1, -1);
    }

    private int[] toIntArray(int x, int y)
    {
        int[] t =
        {
            x, y
        };
        return t;
    }

    /**
     * @return The closest food coordinates
     */
    private int[] findShortestFoodCoord()
    {
        int shortest = Board.width * Board.height;
        int coord[] = new int[2];
        for (int i = 0; i < Board.food.length; i++)
        {
            int x = Board.food[i][0];
            int y = Board.food[i][1];
            if (Board.distanceMap[x][y] < shortest)
            {
                shortest = Board.distanceMap[x][y];
                coord[0] = x;
                coord[1] = y;
            }
        }
        return coord;
    }

    /**
     * Creates a shortest distance map
     */
    private void calculateShortestDistanceMap()
    {
        calculateShortestDistanceMap(ourCoords(), false);
    }
    
    /**
     * Creates a shortest distance map with the head in a custom position
     * @param head The coordinates for the custom head position
     */
    private void calculateShortestDistanceMap(int[] head, boolean removeTail)
    {
        resetDistanceMap();
        Board.distanceMap[head[0]][head[1]] = 0;
        if(removeTail)
        {
            int[] tail = ours().coords[ours().coords.length-1];
            Board.distanceMap[tail[0]][tail[1]] = Board.width * Board.height;
        }
        calcShortDist(ourCoords()[0], ourCoords()[1]);
    }

    
    private void calcShortDist(int x, int y)
    {
        calcShortHelper(x, y, x - 1, y);
        calcShortHelper(x, y, x + 1, y);
        calcShortHelper(x, y, x, y - 1);
        calcShortHelper(x, y, x, y + 1);
    }

    private void calcShortHelper(int x1, int y1, int x2, int y2)
    {
        int curDist = Board.distanceMap[x1][y1];

        if (validX(x2) && validY(y2) && Board.distanceMap[x2][y2] != -1)
        {
            if (Board.distanceMap[x2][y2] > curDist + 1)
            {
                Board.distanceMap[x2][y2] = curDist + 1;
                calcShortDist(x2, y2);
            }
        }
    }

    private int[] ourNextCoords(String dir)
    {
        if (dir.equals("left"))
        {
            return ourNextCoords(-1, 0);
        }
        if (dir.equals("right"))
        {
            return ourNextCoords(1, 0);
        }
        if (dir.equals("up"))
        {
            return ourNextCoords(0, -1);
        }
        return ourNextCoords(0, 1);
    }

    private int[] ourNextCoords(int x, int y)
    {
        int[] c = ourCoords().clone();
        c[0] += x;
        c[1] += y;

        return c;
    }

    private int[] ourCoords()
    {
        return ours().coords[0];
    }

    private Snake ours()
    {
        return Board.snakes.get(NAME);
    }

    private BoardTile coordToTile(int[] t)
    {
        return coordToTile(t[0], t[1]);
    }

    private BoardTile coordToTile(int x, int y)
    {
        return Board.board[x][y];
    }

    private String reverseDir()
    {
        if (ours().coords[0][0] > ours().coords[1][0])
        {
            return "left";
        }
        if (ours().coords[0][0] < ours().coords[1][0])
        {
            return "right";
        }
        if (ours().coords[0][1] > ours().coords[1][1])
        {
            return "up";
        }
        return "down";
    }

    private String curDir()
    {
        if (reverseDir().equals("left"))
        {
            return "right";
        }
        if (reverseDir().equals("right"))
        {
            return "left";
        }
        if (reverseDir().equals("up"))
        {
            return "down";
        }
        return "up";
    }

    private void parseStart(Map<String, Object> requestBody)
    {
        Board.game_id = requestBody.get("game_id").toString();
        Board.width = (Integer) requestBody.get("width");
        Board.height = (Integer) requestBody.get("width");
        Board.board = new BoardTile[Board.width][Board.height];
        Board.distanceMap = new int[Board.width][Board.height];
        for (int i = 0; i < Board.width; i++)
        {
            for (int j = 0; j < Board.height; j++)
            {
                Board.board[i][j] = new BoardTile();
            }
        }
    }

    private void parseBoard(Map<String, Object> requestBody)
    {
        Board.game_id = requestBody.get("game_id").toString();
        Board.turn = (Integer) requestBody.get("turn");
        parseBoardTiles((ArrayList<ArrayList<Object>>) requestBody.get("board"));
        parseSnakes((ArrayList<Map<String, Object>>) requestBody.get("snakes"));
        Board.food = toDoubleIntArray((ArrayList<ArrayList<Integer>>) requestBody.get("food"));
        
        Board.invalidCoords.clear();
    }

    private void parseBoardTiles(ArrayList<ArrayList<Object>> tiles)
    {

        for (int i = 0; i < Board.width; i++)
        {
            for (int j = 0; j < Board.height; j++)
            {
                Map<String, String> q = (Map<String, String>) tiles.get(i).get(j);
                ((BoardTile) Board.board[i][j]).state = BoardTile.State.getState(q.get("state"));
            }
        }
    }
    
    private void resetDistanceMap()
    {

        for (int i = 0; i < Board.width; i++)
        {
            for (int j = 0; j < Board.height; j++)
            {
                if (Board.board[i][j].state == BoardTile.State.BODY || Board.board[i][j].state == BoardTile.State.HEAD || Board.invalidCoords.contains(toIntArray(i, j)))
                {
                    Board.distanceMap[i][j] = -1;
                } else
                {
                    Board.distanceMap[i][j] = Board.width * Board.height;
                }
            }
        }
    }

    private void parseSnakes(ArrayList<Map<String, Object>> snakes)
    {
        for (Map<String, Object> m : snakes)
        {
            String snake = m.get("name").toString();
            if (!Board.snakes.containsKey(snake))
            {
                Board.snakes.put(snake, new Snake(snake));
            }

            Board.snakes.get(snake).coords = toDoubleIntArray((ArrayList<ArrayList<Integer>>) m.get("coords"));
        }
    }

    private int[][] toDoubleIntArray(ArrayList<ArrayList<Integer>> t)
    {
        int[][] c = new int[t.size()][2];
        for (int i = 0; i < t.size(); i++)
        {
            c[i][0] = t.get(i).get(0);
            c[i][1] = t.get(i).get(1);
        }
        return c;
    }

    private class Snake
    {

        public String name;
        public String state;
        public int[][] coords;
        public int score;
        public int color;
        public String head_url;
        public String taunt;

        public Snake(String s)
        {
            name = s;
        }
    }

    private static class Board
    {
        public static int[][] distanceMap;
        public static ArrayList<int[]> invalidCoords = new ArrayList<int[]>();

        public static int width;
        public static int height;

        public static String game_id;
        public static int turn;
        public static BoardTile[][] board;
        public static int[][] checkMap;
        public static Map<String, Snake> snakes = new HashMap<String, Snake>();
        public static int[][] food;
    }

    public static class BoardTile
    {

        public enum State
        {

            HEAD, BODY, FOOD, EMPTY;

            public static State getState(String s)
            {
                if (s.equals("head"))
                {
                    return HEAD;
                }
                if (s.equals("body"))
                {
                    return BODY;
                }
                if (s.equals("food"))
                {
                    return FOOD;
                }
                if (s.equals("empty"))
                {
                    return EMPTY;
                }
                return null;
            }
        }

        public State state = State.EMPTY;
        public Snake snake = null;
    }
}

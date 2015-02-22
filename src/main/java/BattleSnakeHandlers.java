
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class BattleSnakeHandlers
{

    public static String NAME = "Retro-Fire";

    public Object handleStart(Map<String, Object> requestBody)
    {
        // Dummy Response
        parseStart(requestBody);
        Map<String, Object> responseObject = new HashMap<String, Object>();
        responseObject.put("name", NAME);
        responseObject.put("color", "#80F700");
        responseObject.put("head_url", "");
        responseObject.put("taunt", "Get Shreked");
        return responseObject;
    }

    public Object handleMove(Map<String, Object> requestBody)
    {
        this.parseBoard(requestBody);
        // Dummy Response
        Map<String, Object> responseObject = new HashMap<String, Object>();

        responseObject.put("move", getMove());
        responseObject.put("taunt", "Get Shreked");
        return responseObject;
    }

    public Object handleEnd(Map<String, Object> requestBody)
    {
        // No response required
        Map<String, Object> responseObject = new HashMap<String, Object>();
        return responseObject;
    }

    private String getMove()
    {
        return foodDirection();
    }

    private String foodDirection()
    {
        findFoodDistances();
        for (int i = 0; i < Board.width; i++)
        {
            for (int j = 0; j < Board.height; j++)
            {
                System.err.print("[" + Board.distanceMap[i][j] + "]");
            }
            System.err.println();
        }
        int[] t = findShortestFoodCoord();
        return coordToDir(shortestPath(t[0], t[1]));
    }

    private String coordToDir(int[] c)
    {
        if (getNextCoords("left") == c)
        {
            return "left";
        }
        if (getNextCoords("right") == c)
        {
            return "right";
        }
        if (getNextCoords("up") == c)
        {
            return "up";
        }
        return "down";

    }

    private int[] shortestPath(int x, int y)
    {
        if (ourCoords()[0] == x && ourCoords()[1] == y)
        {
            return toIntArray(x, y);
        }

        if (Board.distanceMap[x - 1][y] < Board.distanceMap[x + 1][y])          // Competing x-1, x+1, y-1, y+1
        {
            if (Board.distanceMap[x - 1][y] < Board.distanceMap[x][y - 1])      // Competing x-1, y-1, y+1
            {
                if (Board.distanceMap[x - 1][y] < Board.distanceMap[x][y + 1])  // Competing x-1, y+1    
                {
                    return shortestPath(x - 1, y);                              // x-1 Won
                } else
                {
                    return shortestPath(x, y + 1);                              // y+1 Won
                }
            } else if (Board.distanceMap[x][y - 1] < Board.distanceMap[x][y + 1])// Competing y-1, y+1
            {
                return shortestPath(x, y - 1);                                  // y-1 Won
            } else
            {
                return shortestPath(x, y + 1);                                  // y+1 Won
            }
        } else if (Board.distanceMap[x + 1][y] < Board.distanceMap[x][y - 1])     // Competing x+1, y-1, y+1
        {
            if (Board.distanceMap[x + 1][y] < Board.distanceMap[x][y + 1])         // Competing x+1, y+1
            {
                return shortestPath(x + 1, y);                                  // x+1 Won
            } else
            {
                return shortestPath(x, y + 1);                                    // y+1 Won
            }
        } else if (Board.distanceMap[x][y - 1] < Board.distanceMap[x][y + 1])      // Competing y-1, y+1
        {
            return shortestPath(x, y - 1);                                    // y-1 Won
        } else
        {
            return shortestPath(x, y + 1);                                    // y+1 Won
        }
    }

    private int[] toIntArray(int x, int y)
    {
        int[] t =
        {
            x, y
        };
        return t;
    }

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

    private void findFoodDistances()
    {
        for (int i = 0; i < Board.food.length; i++)
        {
            calcDist(Board.food[i][0], Board.food[i][1]);
        }
    }

    private int calcDist(int x, int y)
    {
        if (x == ourCoords()[0] && y == ourCoords()[1])
        {
            return 0;
        }

        int maxDist = Board.width * Board.height;
        int bestDist = maxDist;

        if (x - 1 > 0 && Board.distanceMap[x - 1][y] != -1)
        {
            if (Board.distanceMap[x - 1][y] == maxDist)
            {
                Board.distanceMap[x - 1][y] -= 1;
                int calc = calcDist(x - 1, y);
                Board.distanceMap[x - 1][y] += 1;
                return calc;
            }
            bestDist = Board.distanceMap[x - 1][y];
        }

        if (x + 1 <= Board.width && Board.distanceMap[x + 1][y] != -1)
        {
            if (Board.distanceMap[x + 1][y] == maxDist)
            {
                Board.distanceMap[x + 1][y] -= 1;
                int calc = calcDist(x + 1, y);
                Board.distanceMap[x + 1][y] += 1;
                return calc;
            }
            if (Board.distanceMap[x + 1][y] < bestDist)
            {
                bestDist = Board.distanceMap[x + 1][y];
            }
        }

        if (y - 1 > 0 && Board.distanceMap[x][y - 1] != -1)
        {
            if (Board.distanceMap[x][y - 1] == maxDist)
            {
                Board.distanceMap[x][y - 1] -= 1;
                int calc = calcDist(x, y - 1);
                Board.distanceMap[x][y - 1] += 1;
                return calc;
            }
            if (Board.distanceMap[x][y - 1] < bestDist)
            {
                bestDist = Board.distanceMap[x][y - 1];
            }
        }

        if (y <= Board.height && Board.distanceMap[x][y + 1] != -1)
        {
            if (Board.distanceMap[x][y + 1] == maxDist)
            {
                Board.distanceMap[x][y + 1] -= 1;
                int calc = calcDist(x, y + 1);
                Board.distanceMap[x][y + 1] += 1;
                return calc;
            }
            if (Board.distanceMap[x][y + 1] < bestDist)
            {
                bestDist = Board.distanceMap[x][y + 1];
            }
        }

        if (bestDist + 1 < Board.distanceMap[x][y])
        {
            Board.distanceMap[x][y] = bestDist + 1;
        }
        return Board.distanceMap[x][y];
    }

    /*public boolean mvleft()
     {
     return safeMove(-1, 0);
     }

     public boolean mvup()
     {
     return safeMove(0, -1);
     }

     public boolean mvright()
     {
     return safeMove(1, 0);
     }

     public boolean mvdown()
     {
     return safeMove(0, 1);
     }*/
    private int[] getNextCoords(String dir)
    {
        if (dir.equals("left"))
        {
            return ourCoords(-1, 0);
        }
        if (dir.equals("right"))
        {
            return ourCoords(1, 0);
        }
        if (dir.equals("up"))
        {
            return ourCoords(0, -1);
        }
        return ourCoords(0, 1);
    }

    private int[] ourCoords(int x, int y)
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

    /* private boolean safeMove2(int x, int y)
     {
     return isSafeMove2(ourCoords(x, y));
     }

     private boolean isSafeMove2(int[] c)
     {
     System.err.println("CHECK SAFE MOVE: " + c[0] + ":" + c[1]);
     if (c[0] > 1 && c[0] < Board.width && c[1] > 1 && c[1] < Board.height)
     {
     return coordToTile(c).state != BoardTile.State.HEAD && coordToTile(c).state != BoardTile.State.BODY;
     }
     return false;
     }*/
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
    }

    private void parseBoardTiles(ArrayList<ArrayList<Object>> tiles)
    {

        for (int i = 0; i < Board.width; i++)
        {
            for (int j = 0; j < Board.height; j++)
            {
                ((BoardTile) Board.board[i][j]).state = BoardTile.State.getState(tiles.get(i).get(j).toString());
                if (Board.board[i][j].state == BoardTile.State.BODY || Board.board[i][j].state == BoardTile.State.HEAD)
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

        public static int width;
        public static int height;

        public static String game_id;
        public static int turn;
        public static BoardTile[][] board;
        public static int[][] distanceMap;
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

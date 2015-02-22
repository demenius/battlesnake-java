
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class BattleSnakeHandlers
{

    public Object handleStart(Map<String, Object> requestBody)
    {
        // Dummy Response
        parseStart(requestBody);
        Map<String, Object> responseObject = new HashMap<String, Object>();
        responseObject.put("name", "Inland-Taipans");
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
        
        String dir = curDir();
        int[] f = ourCoords();
        int[] t = getCoords(dir);
        if(safeMove(t[0]-f[0], t[1]-f[1]))
            responseObject.put("move", dir);
        else
            responseObject.put("move", dfooddist());
        responseObject.put("taunt", "Get Shreked");
        return responseObject;
    }

    public Object handleEnd(Map<String, Object> requestBody)
    {
        // No response required
        Map<String, Object> responseObject = new HashMap<String, Object>();
        return responseObject;
    }

    //--------------EVANS CODE------------------------
    public void stateSelect(Map<String, Object> board, Object mySnake)
    {
        /*if(checkSafety == -1)Survival(board, mysnake);
         else if(mySnake.score < Val) Hungry(board, mySnake);
         else if(mySnake.life < 50) Hungry(board, mySnake);
         else Aggressive(board, mySnake);*/

    }

    public void Aggressive(Map<String, Object> board, Object mySnake)
    {
        /*Object enemy = checkEnemyDistanceLength(); // should choose a target
         attack(enemy); // moves toward enemy*/
    }

    public void attack(Map<String, Object> board, Object mySnake, Object enemy)
    {
        // Move toward enemy, block, or collide
        //if()
    }

    public void Hungry(Map<String, Object> board, Object mySnake)
    {
        // Move toward food
        //dfooddist(board, );

    }

    public void Survival(Map<String, Object> board, Object mysnake)
    {
        //playSafe();
    }

    //-------------TIMS CODE--------------------------
    public String dfooddist()
    {
        int[][] myboard = new int[Board.board.length][Board.board[0].length];
        for (int i = 0; i < myboard.length; i++)
        {
            for (int j = 0; j < myboard[i].length; j++)
            {
                myboard[i][j] = -1;
            }
        }
        myboard[ours().coords[1][0]][ours().coords[1][1]] = 0;
        Queue<int[]> q = new LinkedList<int[]>();

        int dist = 0;
        if (mvleft())
        {
            q.add(ourCoords(-1,0));
            return "left";
        }
        if (mvup())
        {
            q.add(ourCoords(0,-1));
            return "up";
        }
        if (mvright())
        {
            q.add(ourCoords(1,0));
            return "right";
        }
        if (mvdown())
        {
            q.add(ourCoords(0,1));
            return "down";
        }
        return null;
    }

    public boolean mvleft()
    {
        return safeMove(-1, 0);
    }

    public boolean mvup()
    {
        return safeMove(0,-1);
    }

    public boolean mvright()
    {
        return safeMove(1,0);
    }

    public boolean mvdown()
    {
        return safeMove(0,1);
    }

    public int absval(int n)
    {
        if (n < 0)
        {
            n *= -1;
        }
        return n;
    }
    
    private int[] getCoords(String dir)
    {
        if(reverseDir().equals("left"))
            return ourCoords(-1, 0);
        if(reverseDir().equals("right"))
            return ourCoords(1, 0);
        if(reverseDir().equals("up"))
            return ourCoords(0, -1);
        return ourCoords(0, 1);
    }
    
    private int[] ourCoords(int x, int y)
    {
        int[] c = ourCoords();
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
        return Board.snakes.get("Inland-Taipans");
    }
    
    private BoardTile coordToTile(int[] t)
    {
        return Board.board[t[0]][t[1]];
    }
    
    private String checkShute()
    {
        String dir = "up";
        
        
        
        return dir;
    }
    
    private String reverseDir()
    {
        if(ours().coords[0][0] < ours().coords[1][0])
            return "left";
        if(ours().coords[0][0] > ours().coords[1][0])
            return "right";
        if(ours().coords[0][1] < ours().coords[1][1])
            return "up";
        return "down";
    }
    
    private String curDir()
    {
        if(reverseDir().equals("left"))
            return "right";
        if(reverseDir().equals("right"))
            return "left";
        if(reverseDir().equals("up"))
            return "down";
        return "up";
    }

    private boolean safeMove(int x, int y)
    {
        if(ourCoords(x,y)[0] < 0 || ourCoords(x,y)[0] >= Board.width || ourCoords(x,y)[1] < 0 || ourCoords(x,y)[1] >= Board.height)
            return coordToTile(ourCoords(x,y)).state != BoardTile.State.HEAD && coordToTile(ourCoords(x,y)).state != BoardTile.State.BODY;
        return false;
    }
    
    private void parseStart(Map<String, Object> requestBody)
    {
        Board.game_id = requestBody.get("game_id").toString();
        Board.width = (Integer) requestBody.get("width");
        Board.height = (Integer) requestBody.get("width");
        Board.board = new BoardTile[Board.width][Board.height];
        for (int i = 0; i < Board.width; i++)
        {
            for(int j = 0; j < Board.height; j++)
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
        parseSnakes((ArrayList<Object>[]) requestBody.get("snakes"));
        Board.food = (int[][]) (requestBody.get("food"));
    }

    private void parseBoardTiles(ArrayList<ArrayList<Object>> tiles)
    {
        
        for (int i = 0; i < Board.width; i++)
        {
            for(int j = 0; j < Board.height; j++)
            {
                ((BoardTile) Board.board[i][j]).state = BoardTile.State.getState(tiles.get(i).get(j).toString());
            }
        }
    }

    private void parseSnakes(ArrayList<Object>[] snakes)
    {
        for (int j = 0; j < snakes.length; j++)
        {
            for(Object o : snakes[j])
            {
                String snake = "";
                if(o.toString().equals("name"))
                {
                    snake = o.toString();
                    if (!Board.snakes.containsKey(snake))
                    {
                        Board.snakes.put(snake, new Snake(snake));
                    }
                } else if(o.toString().equals("state"))
                {
                    Board.snakes.get(snake).state = o.toString();
                } else if(o.toString().equals("coords"))
                {
                    Board.snakes.get(snake).coords = (int[][])o;
                } else if(o.toString().equals("score"))
                {
                    Board.snakes.get(snake).score = (Integer) o;
                } else if(o.toString().equals("color"))
                {
                    Board.snakes.get(snake).color = (Integer) o;
                } else if(o.toString().equals("head_url"))
                {
                    Board.snakes.get(snake).head_url = o.toString();
                } else if(o.toString().equals("taunt"))
                {
                    Board.snakes.get(snake).taunt = o.toString();
                }
                
            }
            


        }
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
        public static BoardTile[][] board = new BoardTile[width][height];
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


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
        responseObject.put("taunt", "Get Shreked: " + Board.width + ":" + Board.height);
        return responseObject;
    }

    public Object handleMove(Map<String, Object> requestBody)
    {
        // Dummy Response
        Map<String, Object> responseObject = new HashMap<String, Object>();
        responseObject.put("move", dfooddist());
        responseObject.put("taunt", "Get Shreked: " + Board.width + ":" + Board.height);
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
        myboard[Board.ours.coords[1][0]][Board.ours.coords[1][1]] = 0;
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
        if (Board.ours.coords[1][0] == 0)
        {
            return false;
        }
        return coordToTile(ourCoords(-1,0)).state != BoardTile.State.HEAD && coordToTile(ourCoords(-1,0)).state != BoardTile.State.BODY;
    }

    public boolean mvup()
    {
        if (Board.ours.coords[1][1] == 0)
        {
            return false;
        }
        return coordToTile(ourCoords(0,-1)).state != BoardTile.State.HEAD && coordToTile(ourCoords(0,-1)).state != BoardTile.State.BODY;
    }

    public boolean mvright()
    {
        if (Board.ours.coords[1][0] == Board.board.length - 1)
        {
            return false;
        }
        return coordToTile(ourCoords(1,0)).state != BoardTile.State.HEAD && coordToTile(ourCoords(1,0)).state != BoardTile.State.BODY;
    }

    public boolean mvdown()
    {
        if (Board.ours.coords[0][0] == Board.board.length - 1) // Check For Bottom Wall
        {
            return false;
        }
        
        return coordToTile(ourCoords(0,1)).state != BoardTile.State.HEAD && coordToTile(ourCoords(0,1)).state != BoardTile.State.BODY;
    }

    public int absval(int n)
    {
        if (n < 0)
        {
            n *= -1;
        }
        return n;
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
        return Board.ours.coords[0];
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
        if(Board.ours.coords[0][0] < Board.ours.coords[1][0])
            return "left";
        if(Board.ours.coords[0][0] > Board.ours.coords[1][0])
            return "right";
        if(Board.ours.coords[0][1] < Board.ours.coords[1][1])
            return "up";
        return "down";
    }

    private boolean safeMove()
    {
        return false;
    }
    
    private void parseStart(Map<String, Object> requestBody)
    {
        Board.game_id = requestBody.get("game_id").toString();
        Board.width = (Integer) requestBody.get("width");
        Board.height = (Integer) requestBody.get("width");
        Board.ours = new Snake("Inland-Taipans");
        Board.snakes.put(Board.ours.name, Board.ours);
    }

    private void parseBoard(Map<String, Object> requestBody)
    {
        Board.game_id = requestBody.get("game_id").toString();
        Board.turn = (Integer) requestBody.get("turn");
        parseBoardTiles((Map<String, String>[][]) requestBody.get("board"));
        parseSnakes((Map<String, Object>[]) requestBody.get("snakes"));
        Board.food = (int[][]) (requestBody.get("food"));
    }

    private void parseBoardTiles(Map<String, String>[][] tiles)
    {
        for (int i = 0; i < Board.width; i++)
        {
            for (int j = 0; j < Board.height; j++)
            {
                ((BoardTile) Board.board[i][j]).state = BoardTile.State.getState(tiles[i][j].get("state"));
                String snake = tiles[i][j].get("snake");
                ((BoardTile) Board.board[i][j]).snake = Board.snakes.get(snake);
            }
        }
    }

    private void parseSnakes(Map<String, Object>[] snakes)
    {
        for (int j = 0; j < snakes.length; j++)
        {
            String snake = snakes[j].get("name").toString();
            if (!Board.snakes.containsKey(snake))
            {
                Board.snakes.put(snake, new Snake(snake));
            }

            Board.snakes.get(snake).state = snakes[j].get("state").toString();
            Board.snakes.get(snake).coords = (int[][]) snakes[j].get("coords");
            Board.snakes.get(snake).score = (Integer) snakes[j].get("score");
            Board.snakes.get(snake).color = (Integer) snakes[j].get("color");
            Board.snakes.get(snake).head_url = snakes[j].get("head_url").toString();
            Board.snakes.get(snake).taunt = snakes[j].get("taunt").toString();

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
        public static Snake ours;
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

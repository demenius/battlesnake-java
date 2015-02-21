

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BattleSnakeHandlers {
    
    public Object handleStart(Map<String, Object> requestBody) {
        // Dummy Response
        parseStart(requestBody);
        Map<String, Object> responseObject = new HashMap<String, Object>();
        responseObject.put("name", "Inland-Taipans");
        responseObject.put("color", "#80F700");
        responseObject.put("head_url", "https://img.4plebs.org/boards/s4s/image/1390/48/1390481001892.png");
        responseObject.put("taunt", "Get Shreked: " + Board.width + ":" + Board.height);
        return responseObject;
    }
    
    public Object handleMove(Map<String, Object> requestBody) {
        // Dummy Response
        Map<String, Object> responseObject = new HashMap<String, Object>();
        responseObject.put("move", "down");
        responseObject.put("taunt", "Get Shreked: " + Board.width + ":" + Board.height);
        return responseObject;
    }
    
    public Object handleEnd(Map<String, Object> requestBody) {
        // No response required
        Map<String, Object> responseObject = new HashMap<String, Object>();
        return responseObject;
    }

    private void parseStart(Map<String, Object> requestBody)
    {
        Board.game_id = requestBody.get("game_id").toString();
        Board.width = (Integer)requestBody.get("width");
        Board.height = (Integer)requestBody.get("width");
    }
    
    private void parseBoard(Map<String, Object> requestBody)
    {
        Board.game_id = requestBody.get("game_id").toString();
        Board.turn = (Integer)requestBody.get("turn");
        parseBoardTiles((Map<String,String>[][])requestBody.get("board"));
        parseSnakes((Map<String,Object>[])requestBody.get("snakes"));
        Board.food = (int[][])(requestBody.get("food"));
    }
    
    private void parseBoardTiles(Map<String,String>[][] tiles)
    {
        for(int i = 0; i < Board.width; i++)
        {
            for(int j = 0; j < Board.height; j++)
            {
                ((BoardTile)Board.board[i][j]).state = BoardTile.State.getState(tiles[i][j].get("state"));
                String snake = tiles[i][j].get("snake");
                ((BoardTile)Board.board[i][j]).snake = Board.snakes.get(snake);
            }
        }
    }
    
    private void parseSnakes(Map<String,Object>[] snakes)
    {
        for(int j = 0; j < snakes.length; j++)
        {
            String snake = snakes[j].get("name").toString();
            if(!Board.snakes.containsKey(snake))
                Board.snakes.put(snake, new Snake(snake));
            
            Board.snakes.get(snake).state = snakes[j].get("state").toString();
            Board.snakes.get(snake).coords = (int[][])snakes[j].get("coords");
            Board.snakes.get(snake).score = (Integer)snakes[j].get("score");
            Board.snakes.get(snake).color = (Integer)snakes[j].get("color");
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
        
        public Snake(String s) {name = s;}
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
                if(s.equals("head")) return HEAD;
                if(s.equals("body")) return BODY;
                if(s.equals("food")) return FOOD;
                if(s.equals("empty")) return EMPTY;
                return null;
            }
        }
        
        public State state = State.EMPTY;
        public Snake snake = null;
    }
}

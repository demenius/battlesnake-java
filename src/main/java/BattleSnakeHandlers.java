

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;


public class BattleSnakeHandlers {
    
    public Object handleStart(Map<String, Object> requestBody) {
        // Dummy Response
        parseStart(requestBody);
        Map<String, Object> responseObject = new HashMap<String, Object>();
        responseObject.put("name", "Inland-Taipans");
        responseObject.put("color", "#80F700");
        responseObject.put("head_url", "https://inland-taipans.herokuapp.com/");
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

    private void parseStart(JSONObject obj)
    {
        Board.game_id = obj.getJSONObject("game_id");
        Board.width = obj.getJSONObject("width");
        Board.height = obj.getJSONObject("width");
    }
    
    private void parseBoard(JSONObject obj)
    {
        Board.game_id = obj.getJSONObject("game_id");
        Board.turn = obj.getJSONObject("turn");
        Board.board = parseBoardTiles(obj.getJSONObject("board"));
        Board.snakes = parseSnakes(obj.getJSONObject("snakes"));
        Board.food = obj.getJSONObject("food");
    }
    
    
    private static class Snake
    {
        public static String name;
        public static String state;
        public static int[][] coords;
        public static int score;
        public static int color;
        public static String head_url;
        public static String taunt;
    }
    
    private static class Board
    {
        public static int width;
        public static int height;
        
        public static String game_id;
        public static int turn;
        public static BoardTile[][] board;
        public static Snake[] snakes;
        public static int[][] food;
    }
    
    private static class BoardTile
    {
        public enum State {HEAD, BODY, FOOD, EMPTY}
        public static State state;
        public static Snake snake;
    }
}

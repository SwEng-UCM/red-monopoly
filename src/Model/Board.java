package Model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Tile> _tiles;

    public Board() {
        _tiles = new ArrayList<>();
        initBoard();
    }

    /*
    * //TODO: here we would add our configuration on the board, as its immutable, and the TYPE of tile
    * */
    private void initBoard(){
        //usage example asiopudadopnfu
        _tiles.add(new PropertyTile("Property 1", 1));
        _tiles.add(new CommunityChestTile("Community Chest 1", 2));
        _tiles.add(new PropertyTile("Property 2", 3));
        _tiles.add(new TaxTile("Tax 1", 4));
        _tiles.add(new PropertyTile("Property 3", 5));
        _tiles.add(new RailroadTile("Railroad 1", 6));
        _tiles.add(new PropertyTile("Property 4", 7));
        _tiles.add(new ChanceTile("Chance 1", 8));
        _tiles.add(new PropertyTile("Property 5", 9));
        _tiles.add(new JailTile("Jail", 10));
        _tiles.add(new PropertyTile("Property 6", 11));
        _tiles.add(new UtilityTile("Utility 1", 12));
        _tiles.add(new PropertyTile("Property 7", 13));
        _tiles.add(new PropertyTile("Property 8", 14));
        _tiles.add(new RailroadTile("Railroad 2", 15));
        _tiles.add(new PropertyTile("Property 9", 16));
        _tiles.add(new CommunityChestTile("Community Chest 2", 17));
        _tiles.add(new PropertyTile("Property 10", 18));
        _tiles.add(new PropertyTile("Property 11", 19));
        _tiles.add(new FreeParkingTile("Free Parking", 20));
        _tiles.add(new PropertyTile("Property 12", 21));
        _tiles.add(new ChanceTile("Chance 2", 22));
        _tiles.add(new PropertyTile("Property 13", 23));
        _tiles.add(new PropertyTile("Property 14", 24));
        _tiles.add(new RailroadTile("Railroad 3", 25));
        _tiles.add(new PropertyTile("Property 15", 26));
        _tiles.add(new PropertyTile("Property 16", 27));
        _tiles.add(new UtilityTile("Utility 2", 28));
        _tiles.add(new PropertyTile("Property 17", 29));
        _tiles.add(new GoToJailTile("Go To Jail", 30));
        _tiles.add(new PropertyTile("Property 18", 31));
        _tiles.add(new PropertyTile("Property 19", 32));
        _tiles.add(new CommunityChestTile("Community Chest 3", 33));
        _tiles.add(new PropertyTile("Property 20", 34));
        _tiles.add(new RailroadTile("Railroad 4", 35));
        _tiles.add(new ChanceTile("Chance 3", 36));
        _tiles.add(new PropertyTile("Property 21", 37));
        _tiles.add(new TaxTile("Tax 2", 38));
        _tiles.add(new PropertyTile("Property 22", 39));
        _tiles.add(new GoTile("Go", 0));
    }
   

    public Tile getTile(int position){
        return _tiles.get(position);
    }

    public int getTilePosition(Tile tile){
        return _tiles.indexOf(tile);
    }

    public int getSize(){
        return _tiles.size();
    }

}

package Model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Tile> _tiles;

    public Board() {
        _tiles = new ArrayList<>();
        initBoard();
    }

    private void initBoard(){
        //usage example
        _tiles.add(new PropertyTile("Property 1", 1, 50));
        _tiles.add(new CommunityChestTile("Community Chest 1", 2));
        _tiles.add(new PropertyTile("Property 2", 3, 100));
        _tiles.add(new TaxTile("Tax 1", 4));
        _tiles.add(new PropertyTile("Property 3", 5, 150));
        _tiles.add(new RailroadTile("Railroad 1", 6));
        _tiles.add(new PropertyTile("Property 4", 7, 200));
        _tiles.add(new ChanceTile("Chance 1", 8));
        _tiles.add(new PropertyTile("Property 5", 9, 250));
        _tiles.add(new JailTile("Jail", 10));
        _tiles.add(new PropertyTile("Property 6", 11, 300));
        _tiles.add(new UtilityTile("Utility 1", 12));
        _tiles.add(new PropertyTile("Property 7", 13, 350));
        _tiles.add(new PropertyTile("Property 8", 14, 400));
        _tiles.add(new RailroadTile("Railroad 2", 15));
        _tiles.add(new PropertyTile("Property 9", 16, 450));
        _tiles.add(new CommunityChestTile("Community Chest 2", 17));
        _tiles.add(new PropertyTile("Property 10", 18, 500));
        _tiles.add(new PropertyTile("Property 11", 19, 550));
        _tiles.add(new FreeParkingTile("Free Parking", 20));
        _tiles.add(new PropertyTile("Property 12", 21, 600));
        _tiles.add(new ChanceTile("Chance 2", 22));
        _tiles.add(new PropertyTile("Property 13", 23, 650));
        _tiles.add(new PropertyTile("Property 14", 24, 700));
        _tiles.add(new RailroadTile("Railroad 3", 25));
        _tiles.add(new PropertyTile("Property 15", 26, 750));
        _tiles.add(new PropertyTile("Property 16", 27, 800));
        _tiles.add(new UtilityTile("Utility 2", 28));
        _tiles.add(new PropertyTile("Property 17", 29, 850));
        _tiles.add(new GoToJailTile("Go To Jail", 30));
        _tiles.add(new PropertyTile("Property 18", 31, 900));
        _tiles.add(new PropertyTile("Property 19", 32, 950));
        _tiles.add(new CommunityChestTile("Community Chest 3", 33));
        _tiles.add(new PropertyTile("Property 20", 34, 1000));
        _tiles.add(new RailroadTile("Railroad 4", 35));
        _tiles.add(new ChanceTile("Chance 3", 36));
        _tiles.add(new PropertyTile("Property 21", 37, 1050));
        _tiles.add(new TaxTile("Tax 2", 38));
        _tiles.add(new PropertyTile("Property 22", 39, 1100));
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
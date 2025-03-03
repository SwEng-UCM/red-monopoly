package Model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Tile> _tiles;

    public Board() {
        _tiles = new ArrayList<>();
        initBoard();
    }

    private void initBoard() {
        // Usage example with price added
        _tiles.add(new GoTile("Go", 0));
        _tiles.add(new PropertyTile("Nizhny Novgorod", 1, 500, 2000)); // Rent: 50, Price: 200
        _tiles.add(new CommunityChestTile("Community Chest 1", 2));
        _tiles.add(new PropertyTile("Magnitogorsk", 3, 100, 300)); // Rent: 100, Price: 300
        _tiles.add(new TaxTile("Tax 1", 4));
        _tiles.add(new PropertyTile("Norilsk", 5, 150, 400)); // Rent: 150, Price: 400
        _tiles.add(new RailroadTile("Trans-Siberian Railway", 6));
        _tiles.add(new PropertyTile("Property 4", 7, 200, 500)); // Rent: 200, Price: 500
        _tiles.add(new ChanceTile("Chance", 8));
        _tiles.add(new ChanceTile("Chance", 11));
        _tiles.add(new PropertyTile("Property 5", 9, 250, 600)); // Rent: 250, Price: 600
        _tiles.add(new JailTile("Gulag", 10));
        // Add more properties with their respective rent and price values...
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

    public List<Tile> getTiles() {
        return _tiles;
    }

}
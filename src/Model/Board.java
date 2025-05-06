package Model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Tile> _tiles;
    private static Board instance;

    public Board() {
        _tiles = new ArrayList<>();
        initBoard();
    }

    private void initBoard() {
        _tiles.add(new GoTile("Go (+200₽)", 0));
    
        // Brown
        _tiles.add(new PropertyTile("Norilsk", 1, 2, 60)); 
        _tiles.add(new CommunityChestTile("Community Chest", 2));
        _tiles.add(new PropertyTile("Yakutsk", 3, 4, 60)); 
    
        // Tax
        _tiles.add(new TaxTile("Income Tax", 4, 10));
    
        // Railroad #1
        _tiles.add(new RailroadTile("Trans-Siberian Railway", 5, 25, 200));
    
        // Light Blue
        _tiles.add(new PropertyTile("Magnitogorsk", 6, 6, 100));
        _tiles.add(new ChanceTile("Chance", 7));
        _tiles.add(new PropertyTile("Orenburg", 8, 6, 100));
        _tiles.add(new PropertyTile("Tomsk", 9, 8, 120));
    
        // Jail
        _tiles.add(new JailTile("Gulag", 10));


        //TURNED LEFTside


        // Pink
        _tiles.add(new PropertyTile("Vladivostok", 11, 10, 140));
        _tiles.add(new UtilityTile("Electric Company", 12, 150));
        _tiles.add(new PropertyTile("Khabarovsk", 13, 10, 140));
        _tiles.add(new PropertyTile("Irkutsk", 14, 12, 160));
    
        // Railroad #2
        _tiles.add(new RailroadTile("Baikal-Amur Mainline", 15, 25, 200));
    
        // Orange
        _tiles.add(new PropertyTile("Barnaul", 16, 14, 180));
        _tiles.add(new CommunityChestTile("Community Chest", 17));
        _tiles.add(new PropertyTile("Tyumen", 18, 14, 180));
        _tiles.add(new PropertyTile("Saratov", 19, 16, 200));
    
        // Free Parking
        _tiles.add(new FreeParkingTile("Free Parking", 20));


        //TURNED DOWNSIDE

        // Red
        _tiles.add(new PropertyTile("Perm", 21, 18, 220));
        _tiles.add(new ChanceTile("Chance", 22));
        _tiles.add(new PropertyTile("Krasnoyarsk", 23, 18, 220));
        _tiles.add(new PropertyTile("Omsk", 24, 20, 240));
    
        // Railroad #3
        _tiles.add(new RailroadTile("Circum-Baikal Railway", 25, 25, 200));
    
        // Yellow
        _tiles.add(new PropertyTile("Samara", 26, 22, 260));
        _tiles.add(new PropertyTile("Chelyabinsk", 27, 22, 260));
        _tiles.add(new UtilityTile("Water Works", 28, 150));
        _tiles.add(new PropertyTile("Kazan", 29, 24, 280));
    
        // Go To Jail
        _tiles.add(new GoToJailTile("Go to Gulag", 30));


        //TURNED RIGHTSIDE

    
        // Green
        _tiles.add(new PropertyTile("Nizhny Novgorod", 31, 26, 300));
        _tiles.add(new PropertyTile("Yekaterinburg", 32, 26, 300));
        _tiles.add(new CommunityChestTile("Community Chest", 33));
        _tiles.add(new PropertyTile("Novosibirsk", 34, 28, 320));
    
        // Railroad #4
        _tiles.add(new RailroadTile("Ural Railway", 35, 25, 200));
    
        // Chance
        _tiles.add(new ChanceTile("Chance", 36));
    
        // Dark Blue
        _tiles.add(new PropertyTile("Minsk", 37, 35, 350));
    
        // Luxury Tax
        _tiles.add(new TaxTile("Luxury Tax", 38, 20));
    
        // Dark Blue
        _tiles.add(new PropertyTile("Moscow", 39, 50, 400));
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

    public static Board getInstance() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }

    public void setTiles(List<Tile> tiles) {
        this._tiles = tiles;
    }
}
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
        // Usage example with price added
        _tiles.add(new GoTile("Go", 0));
        _tiles.add(new PropertyTile("Nizhny Novgorod", 1, 500, 2000)); // Rent: 50, Price: 200
        _tiles.add(new CommunityChestTile("Community Chest 1", 2));
        _tiles.add(new PropertyTile("Magnitogorsk", 3, 100, 300)); // Rent: 100, Price: 300
        _tiles.add(new TaxTile("Tax 1", 4));
        _tiles.add(new PropertyTile("Norilsk", 5, 150, 400)); // Rent: 150, Price: 400
        _tiles.add(new RailroadTile("Trans-Siberian Railway", 6));
        _tiles.add(new PropertyTile("Minsk", 7, 200, 500)); // Rent: 200, Price: 500
        _tiles.add(new ChanceTile("Chance", 8));
        _tiles.add(new ChanceTile("Chance", 11));
        _tiles.add(new PropertyTile("Novosibirsk", 9, 250, 600)); // Rent: 250, Price: 600
        _tiles.add(new JailTile("Gulag", 10));
        _tiles.add(new PropertyTile("Kazan", 11, 300, 700)); // Rent: 300, Price: 700
        _tiles.add(new UtilityTile("Electric Company", 12));
        _tiles.add(new PropertyTile("Vladivostok", 13, 350, 800)); // Rent: 350, Price: 800
        _tiles.add(new PropertyTile("Khabarovsk", 14, 400, 900)); // Rent: 400, Price: 900
        _tiles.add(new RailroadTile("Baikal-Amur Mainline", 15));
        _tiles.add(new PropertyTile("Yakutsk", 16, 450, 1000)); // Rent: 450, Price: 1000
        _tiles.add(new CommunityChestTile("Community Chest 2", 17));
        _tiles.add(new PropertyTile("Irkutsk", 18, 500, 1100)); // Rent: 500, Price: 1100
        _tiles.add(new FreeParkingTile("Free Parking", 19));
        _tiles.add(new PropertyTile("Krasnoyarsk", 20, 550, 1200)); // Rent: 550, Price: 1200
        _tiles.add(new ChanceTile("Chance", 21));
        _tiles.add(new PropertyTile("Omsk", 22, 600, 1300)); // Rent: 600, Price: 1300
        _tiles.add(new PropertyTile("Tomsk", 23, 650, 1400)); // Rent: 650, Price: 1400
        _tiles.add(new RailroadTile("Circum-Baikal Railway", 24));
        _tiles.add(new PropertyTile("Barnaul", 25, 700, 1500)); // Rent: 700, Price: 1500
        _tiles.add(new PropertyTile("Chelyabinsk", 26, 750, 1600)); // Rent: 750, Price: 1600
        _tiles.add(new UtilityTile("Water Works", 27));
        _tiles.add(new PropertyTile("Perm", 28, 800, 1700)); // Rent: 800, Price: 1700
        _tiles.add(new GoToJailTile("Go to Gulag", 29));
        _tiles.add(new PropertyTile("Yekaterinburg", 30, 850, 1800)); // Rent: 850, Price: 1800
        _tiles.add(new PropertyTile("Tyumen", 31, 900,
                1900)); // Rent: 900, Price: 1900
        _tiles.add(new CommunityChestTile("Community Chest 3", 32));
        _tiles.add(new PropertyTile("Kurgan", 33, 950, 2000)); // Rent: 950, Price: 2000
        _tiles.add(new RailroadTile("Ural Railway", 34));
        _tiles.add(new ChanceTile("Chance", 35));
        _tiles.add(new PropertyTile("Orenburg", 36, 1000, 2100)); // Rent: 1000, Price: 2100
        _tiles.add(new TaxTile("Tax 2", 37));
        _tiles.add(new PropertyTile("Samara", 38, 1050, 2200)); // Rent: 1050, Price: 2200
        //_tiles.add(new PropertyTile("Saratov", 39, 1100, 2300)); // Rent: 1100, Price: 2300


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

    public static Board getInstance() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }
}
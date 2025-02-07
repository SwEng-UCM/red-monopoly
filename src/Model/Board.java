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
        //usage example
        _tiles.add(new PropertyTile("Tverskaya Street", 8));
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

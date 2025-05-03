package Model;

public abstract class Tile { //abstract class that will represent all the different tiles on the board
    protected String name;
    protected int position;

    public Tile(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public abstract String action(Player player);
}

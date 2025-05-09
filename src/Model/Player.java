package Model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Player {
    private String name;
    private int position;
    private int money;
    private boolean inJail;
    private int jailTurnCount = 0;

    /**
     * Stores the classpath resource path for the avatar, e.g. "/players/player3.png"
     * Default is the first avatar.
     */
    @SerializedName("avatarPath")
    private String avatarPath = "/players/player1.png";

    private List<PropertyTile> ownedProperties = new ArrayList<>();
    private List<RailroadTile> ownedRailroads = new ArrayList<>();

    // New fields for turn management
    private boolean skipTurn = false;
    private boolean extraTurn = false;

    protected String type;

    public Player(String name) {
        this.name = name;
        this.position = 0;
        this.money = 1500; // Default starting money
        this.inJail = false;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void addMoney(int amount) {
        money += amount;
        System.out.println(name + " now has " + money + " ₽");
    }

    public void deductMoney(int amount) {
        money -= amount;
        System.out.println(name + " now has " + money + " ₽");
    }

    public boolean isInJail() {
        return inJail;
    }

    public void setInJail(boolean inJail) {
        this.inJail = inJail;
    }

    public int getJailTurnCount() {
        return jailTurnCount;
    }

    public void incrementJailTurn() {
        jailTurnCount++;
    }

    public void resetJailTurn() {
        jailTurnCount = 0;
    }

    // Methods for skipping a turn
    public void skipNextTurn() {
        skipTurn = true;
        System.out.println(name + " will skip their next turn.");
    }

    public boolean shouldSkipTurn() {
        return skipTurn;
    }

    public void resetSkipTurn() {
        skipTurn = false;
    }

    // Methods for extra turn
    public void setExtraTurn(boolean extraTurn) {
        this.extraTurn = extraTurn;
        if (extraTurn) {
            System.out.println(name + " gets an extra turn!");
        }
    }

    public boolean hasExtraTurn() {
        return extraTurn;
    }

    public void resetExtraTurn() {
        extraTurn = false;
    }

    // Example placeholder for going to jail
    public void goToJail() {
        setInJail(true);
        resetJailTurn();
        System.out.println(name + " has been sent to jail!");
    }

    // Property ownership
    public List<PropertyTile> getOwnedProperties() {
        return ownedProperties;
    }

    public void setOwnedProperties(List<PropertyTile> properties) {
        this.ownedProperties = properties;
    }

    public void addProperty(PropertyTile property) {
        ownedProperties.add(property);
    }

    // Railroad ownership
    public List<RailroadTile> getOwnedRailroads() {
        return ownedRailroads;
    }

    public void setOwnedRailroads(List<RailroadTile> railroads) {
        this.ownedRailroads = railroads;
    }

    public void addRailroad(RailroadTile railroad) {
        ownedRailroads.add(railroad);
    }

    /**
     * Returns the classpath resource path for the avatar (leading slash).
     */
    public String getAvatarPath() {
        return avatarPath;
    }

    /**
     * Normalizes and sets the avatar resource path.
     * Accepts either a resource-style path ("/players/...png")
     * or a file-style path ("resources/players/...").
     */
    public void setAvatarPath(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank()) {
            this.avatarPath = "/players/player1.png";
            return;
        }
        String path = avatarPath;
        if (path.startsWith("resources/")) {
            path = path.substring("resources".length());
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        this.avatarPath = path;
    }

    @SerializedName("type")
    public String getType() {
        return "Human";
    }

    public void setJailTurnCount(int oldJailTurns) {
        this.jailTurnCount = oldJailTurns;
    }
}

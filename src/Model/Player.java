package Model;

public class Player {
    private String name;
    private int position;
    private int money;
    private boolean inJail;

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

    private int jailTurnCount = 0;

    public int getJailTurnCount() {
        return jailTurnCount;
    }

    public void incrementJailTurn() {
        jailTurnCount++;
    }

    public void resetJailTurn() {
        jailTurnCount = 0;
    }

}

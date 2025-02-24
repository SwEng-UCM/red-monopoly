package Model;

public class Player {
    private String name;
    private int position;
    private int money;

    public Player(String name) {
        this.name = name;
        this.position = 0;
        this.money = 1500; // Default starting money
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
        System.out.println(name + " now has $" + money);
    }

    public void deductMoney(int amount) {
        money -= amount;
        System.out.println(name + " now has $" + money);
    }
}
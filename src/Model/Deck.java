package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {

    private List<ChanceCard> chanceCards, usedChanceCards;
    private List<CommunityCard> communityCards, usedCommunityCards;
    private Random rand;

    public Deck(){
        rand = new Random();
        chanceCards = new ArrayList<>();
        usedChanceCards = new ArrayList<>();
        communityCards = new ArrayList<>();
        usedCommunityCards = new ArrayList<>();

        for(int i = 0; i < 16; i++){
            chanceCards.add(new ChanceCard(i));
            communityCards.add(new CommunityCard(i));
        }
    }

    public int dealChance() {
        // If no chance cards are left, recycle the used cards back into the deck.
        if (chanceCards.isEmpty()) {
            chanceCards.addAll(usedChanceCards);
            usedChanceCards.clear();
        }

        // Choose a random chance card.
        int index = rand.nextInt(chanceCards.size());
        ChanceCard drawnCard = chanceCards.remove(index);
        usedChanceCards.add(drawnCard);

        // Here, we simply return the card's ID.
        // In a complete game, you might instead return the card itself or execute its action.
        return drawnCard.getId();
    }

    public int dealCommunity() {
        if (communityCards.isEmpty()) {
            communityCards.addAll(usedCommunityCards);
            usedCommunityCards.clear();
        }

        int index = rand.nextInt(communityCards.size());
        CommunityCard drawnCard = communityCards.remove(index);
        usedCommunityCards.add(drawnCard);

        return drawnCard.getId();
    }

}

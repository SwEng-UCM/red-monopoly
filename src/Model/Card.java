package Model;

public abstract class Card {

        int id;

        /**
        * Constructor for the Card class
        * @param id used to set id field
        */
        public Card(int id) {
            this.id = id;
        }
        /**
        * getter for the id field
        */
        public int getId() {
            return id;
        }

        /**
        * setter for the id field
        * @param id to replace current state.
        * Method added as a convention. Not used in the app
        */
        public void setId(int id) {
            this.id = id;
        }
}

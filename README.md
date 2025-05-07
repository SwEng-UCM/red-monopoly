# Red Monopoly

A digital twist on the classic Monopoly board game, set in the USSR. Buy, trade, and build your way across the Soviet Union—from Moscow to Vladivostok—while managing five-year plans, factories, and political intrigue.

---

## Table of Contents

1. [User Manual & How to Play](#user-manual--how-to-play)  
2. [Project Configuration](#project-configuration)  
3. [Running the Game](#running-the-game)  
4. [Known Limitations](#known-limitations)  
5. [Contributing](#contributing)  
6. [License](#license)  

---

## User Manual & How to Play

Below you will find the rules and features of Red Monopoly. Jump straight to any section:

- [Start a New Game](#start-a-new-game)  
- [Character Selection](#character-selection)  
- [Turn Sequence](#turn-sequence)  
- [Winning Conditions](#winning-conditions)  
- [Automatic Player](#automatic-player)  
- [It Is Not Your Game, It Is OUR Game!](#it-is-not-your-game-it-is-our-game)  
- [Additional Features](#additional-features)  

---

### Start a New Game

1. From the main menu, click **New Game**.  
2. Choose number of players (2–8) and an avatar per player.  
3. Click **OK** to place tokens on Red Square.

**Screenshot**:  
![Start a New Game](docs/screenshots/RM1.png)

---

### Character Selection

1. After naming your player, you’ll see 8 Soviet-themed avatars.  
2. Click an avatar to select.  
3. Confirm to return to the main menu.

**Screenshot**:  
![Character Selection](docs/screenshots/CHARACTERSCREEN.png)

---

### Turn Sequence

1. **Roll Dice**: Click the **DICE** button.  
2. **Move Token**: Token advances automatically.  
3. **Resolve Space**:  
   - Buy property (if unowned).  
   - Pay rent (if owned).  
   - Draw a **Card** for special events.
   - If a player is Jailed, they cannot move unless they roll 2 dices that are the same value (E.G. 5 and 5). If they do not accomplish this in 3 turns, they will pay a Jail Fee and proceed normally.

**Screenshot**:  
![Turn Sequence](docs/screenshots/RMGame.png)

---

### Winning Conditions

- **Elimination**: Bankrupt all other players.  

---

### Automatic Player

1. When prompted for a name, type `AI` to add a computer player.  
2. Set difficulty: `easy`, `medium`, or `hard`.  
3. The AI will take its turn automatically.

**Screenshot**:  
![Automatic Player](docs/screenshots/RMAI.png)

---

### It Is Not Your Game, It Is OUR Game!

– **Multiplayer Mode**:  
  - Do you want to play against your friends, or simply want bragging rights? Now you can do that in Red Monopoly with our multiplayer feature!
  - Host-Client architecture. One player hosts, others join via IP.  
  - Supports 2–8 players over LAN.

---

### Additional Features

- **Player Info Box**: Click the icon to view owned properties.
  

![Player Info Box](docs/screenshots/PlayerInfo.png)  

- **Tile Hints**: Hover over any tile for description and price.

![Tile Hints](docs/screenshots/RMMouseOver.png)  

- **Undo Move**: Click **Undo** to revert your last action.

![Undo Button](docs/screenshots/playerinfobutton.png)

---
## Project Configuration

1. **Clone the Repository**  
   ```bash
   git clone https://github.com/SwEng-UCM/red-monopoly.git
   cd red-monopoly

2. **Run the Game**
3. 

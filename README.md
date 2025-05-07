# Monopoly: Soviet Edition

A digital twist on the classic Monopoly board game, set in the USSR. Buy, trade, and build your way across the Soviet Union—from Moscow to Vladivostok—while managing five-year plans, factories, and political intrigue.

---

## Table of Contents

1. [User Manual & How to Play](#user-manual--how-to-play)  
2. [Project Configuration](#project-configuration)  
3. [Compilation Instructions](#compilation-instructions)  
4. [Deployment Instructions](#deployment-instructions)  
5. [Running the Game](#running-the-game)  
6. [Screenshots](#screenshots)  
7. [Known Limitations](#known-limitations)  
8. [Contributing](#contributing)  
9. [License](#license)  

---

## User Manual & How to Play

For full rules, examples, and advanced strategies, see the [User Manual (PDF)](docs/User_Manual.pdf).

### Getting Started

1. **Start a New Game**  
   - From the main menu, click **New Game**.  
   - Choose number of players (2–6) and starting rubles per player.  
   - Click **Begin** to place tokens on Red Square.

2. **Turn Sequence**  
   - **Roll Dice**: Click “Roll” or press **R**.  
   - **Move Token**: Your token advances the total pip count.  
   - **Resolve Space**:  
     - Buy property if unowned (pay the Bank in rubles).  
     - Pay rent if owned by another player.  
     - Draw a “Commissar Card” for special events.  
   - **Manage Five-Year Plan**:  
     - Access your dashboard sidebar.  
     - Allocate resources to factories, infrastructure, or research.  
     - Complete tasks to earn bonuses.

3. **Winning Conditions**  
   - **Economic Victory**: Achieve a net worth of 10,000 rubles.  
   - **Industrial Victory**: Complete the “Grand Industrialization” five-year plan first.  
   - **Elimination**: Bankrupt all other players.

---

## Project Configuration

1. **Clone the Repository**  
   ```bash
   git clone https://github.com/SwEng-UCM/red-monopoly.git
   cd red-monopoly

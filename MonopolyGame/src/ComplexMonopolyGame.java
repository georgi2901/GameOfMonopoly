import java.util.Random;
import java.util.Scanner;

public class ComplexMonopolyGame {
    static class Player {
        String name;
        int position;
        int money;
        int propertiesOwned;
        private boolean skipTurn = false;

        public Player(String name) {
            this.name = name;
            this.position = 0;
            this.money = 1000;
            this.propertiesOwned = 0;
        }

        public boolean isSkipTurn() {
            return skipTurn;
        }

        public void setSkipTurn(boolean skipTurn) {
            this.skipTurn = skipTurn;
        }
    }

    static class Property {
        String name;
        int cost;
        int baseRent;
        int houseCost;
        int hotelCost;
        int rentWithHouse;
        int rentWithHotel;
        int houses;
        boolean hotel;
        boolean isJail;

        public Property(String name, int cost, int baseRent, int houseCost, int hotelCost, boolean isJail) {
            this.name = name;
            this.cost = cost;
            this.baseRent = baseRent;
            this.houseCost = houseCost;
            this.hotelCost = hotelCost;
            this.rentWithHouse = baseRent;
            this.rentWithHotel = baseRent * 2;
            this.houses = 0;
            this.hotel = false;
            this.isJail = isJail;
        }
    }

    static final int BOARD_SIZE = 10;
    static final int GO_REWARD = 200;
    static final int MAX_HOUSES = 4;

    static Player[] players;
    static Property[] board;
    static Random random = new Random();

    static int numPlayers;

    public static void main(String[] args) {
        initializeGame();
        playGame();
    }

    static void initializeGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of players: ");
        int numPlayers = scanner.nextInt();
        players = new Player[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for Player " + (i + 1) + ": ");
            String playerName = scanner.next();
            players[i] = new Player(playerName);
        }

        board = new Property[BOARD_SIZE];
        board[0] = new Property("GO", 0, 0, 0, 0, false);
        board[1] = new Property("Property 1", 100, 10, 50, 100, false);
        board[2] = new Property("Jail", 0, 0, 0, 0, true);
        board[3] = new Property("Brown 2", 60, 4, 50, 100, false);
        board[4] = new Property("Jail", 0, 0, 0, 0, true);
        board[5] = new Property("Railroad 1", 200, 25, 0, 0, false);
        board[6] = new Property("Jail", 0, 0, 0, 0, true);
        board[7] = new Property("Chance", 0, 0, 0, 0, false);
        board[8] = new Property("Jail", 0, 0, 0, 0, true);
        board[9] = new Property("Light Blue 3", 120, 8, 50, 100, false);

        System.out.println("Game initialized with " + numPlayers + " players.");
    }

    static void playGame() {
        int currentPlayerIndex = 0;
        boolean gameWon = false;

        while (!gameWon) {
            Player currentPlayer = players[currentPlayerIndex];

            if(!currentPlayer.isSkipTurn()) {
                System.out.println("\n" + currentPlayer.name + "'s turn.");
                System.out.println("Position: " + currentPlayer.position);
                System.out.println("Money: $" + currentPlayer.money);
                System.out.println("Properties owned: " + currentPlayer.propertiesOwned);
                System.out.println("Type 'roll' to roll the dice: ");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.next();

                if (input.equalsIgnoreCase("roll")) {
                    int diceRoll = random.nextInt(6) + 1;
                    System.out.println("You rolled a " + diceRoll);

                    int newPosition = (currentPlayer.position + diceRoll) % BOARD_SIZE;

                    if (newPosition < currentPlayer.position) {
                        currentPlayer.money += GO_REWARD;
                        System.out.println(currentPlayer.name + " completed a full circle! Collect $" + GO_REWARD + ".");
                    }

                    currentPlayer.position = newPosition;

                    executeAction(currentPlayer);

                    currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
                }

                if (numPlayers == 1) {
                    gameWon = true;
                    System.out.println("Congratulations, " + players[0].name + "! You won!");
                }
            }else {
                currentPlayer.setSkipTurn(false);
                System.out.println(currentPlayer.name + " is in jail. Skips a turn!");
                if (currentPlayerIndex==players.length-1){
                    currentPlayerIndex=0;
                }else {
                    currentPlayerIndex++;
                }
            }
        }
    }

    static void executeAction(Player player) {
        Property currentProperty = board[player.position];

        if (currentProperty != null) {
            System.out.println("Landed on: " + currentProperty.name);

            if (currentProperty.isJail) {
                System.out.println("You are in jail! Miss your next turn.");
                player.setSkipTurn(true);
            }

            if (currentProperty.cost > 0 && !currentProperty.hotel) {
                System.out.println("Do you want to buy " + currentProperty.name + " for $" + currentProperty.cost + "? (yes/no)");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.next();

                if (input.equalsIgnoreCase("yes") && player.money >= currentProperty.cost) {
                    player.money -= currentProperty.cost;
                    player.propertiesOwned++;
                    System.out.println("Congratulations! You now own " + currentProperty.name + ".");
                } else {
                    System.out.println("You chose not to buy " + currentProperty.name + ".");
                }
            } else {
                System.out.println("This space has no action.");
            }

            if (checkColorSet(player)) {
                System.out.println("Congratulations! You own the entire color set. You can now build houses/hotels.");
                buildHousesHotels(player, currentProperty);
            }
        }
    }

    static boolean checkColorSet(Player player) {
        return player.propertiesOwned >= 3;
    }


    static void buildHousesHotels(Player player, Property currentProperty) {
        System.out.println("Do you want to build houses/hotels? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();

        if (input.equalsIgnoreCase("yes")) {
            System.out.println("Do you want to build houses (h) or hotels (ht)?");
            String buildOption = scanner.next();

            if (buildOption.equalsIgnoreCase("h") && player.money >= currentProperty.houseCost && currentProperty.houses < MAX_HOUSES) {
                player.money -= currentProperty.houseCost;
                currentProperty.houses++;
                System.out.println("You built a house on " + currentProperty.name + ".");
            } else if (buildOption.equalsIgnoreCase("ht") && player.money >= currentProperty.hotelCost && !currentProperty.hotel) {
                player.money -= currentProperty.hotelCost;
                currentProperty.hotel = true;
                System.out.println("You built a hotel on " + currentProperty.name + ".");
            } else {
                System.out.println("You cannot build more houses/hotels on " + currentProperty.name + ".");
            }
        }
    }
}
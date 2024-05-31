import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class User {
    String username;
    String password;
    boolean isFirstPurchase;

    User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isFirstPurchase = true; // Indicates that the user has not made a purchase yet
    }
}

class Drink {
    String name;
    double price;
    int purchaseCount;
    HashMap<String, Integer> toppings; // To track available toppings and their counts

    Drink(String name, double price) {
        this.name = name;
        this.price = price;
        this.purchaseCount = 0;
        this.toppings = new HashMap<>();
    }

    void addTopping(String topping) {
        toppings.putIfAbsent(topping, 0);
    }

    void addPurchase(String topping) {
        purchaseCount++;
        toppings.put(topping, toppings.getOrDefault(topping, 0) + 1);
    }

    HashMap<String, Integer> getSortedToppings() {
        HashMap<String, Integer> sortedToppings = new HashMap<>(toppings);
        return sortedToppings;
    }

    String getMostPopularTopping() {
        return toppings.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("None");
    }
}

class CartItem {
    Drink drink;
    String topping;
    double finalPrice;

    CartItem(Drink drink, String topping, double finalPrice) {
        this.drink = drink;
        this.topping = topping;
        this.finalPrice = finalPrice;
    }
}

public class Main {
    private static HashMap<String, User> users = new HashMap<>();
    private static HashMap<String, Drink> drinks = new HashMap<>();
    private static HashMap<Integer, CartItem> cart = new HashMap<>();
    private static int cartId = 1;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Initialize drinks menu
        Drink coke = new Drink("Coke", 1.50);
        coke.addTopping("Lemon");
        coke.addTopping("Ice");

        Drink pepsi = new Drink("Pepsi", 1.45);
        pepsi.addTopping("Lemon");
        pepsi.addTopping("Ice");

        Drink sprite = new Drink("Sprite", 1.40);
        sprite.addTopping("Mint");
        sprite.addTopping("Ice");

        Drink fanta = new Drink("Fanta", 1.55);
        fanta.addTopping("Orange Slice");
        fanta.addTopping("Ice");

        drinks.put(coke.name, coke);
        drinks.put(pepsi.name, pepsi);
        drinks.put(sprite.name, sprite);
        drinks.put(fanta.name, fanta);

        while (true) {
            System.out.println("Welcome! Please choose an option:");
            System.out.println("1. Sign In");
            System.out.println("2. Sign Up");
            System.out.println("3. Continue as Guest");
            System.out.println("4. Quit");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    signIn();
                    break;
                case 2:
                    signUp();
                    break;
                case 3:
                    showMenu(null);
                    break;
                case 4:
                    System.out.println("Thank you for visiting. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void signIn() {
        System.out.println("Sign In:");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = users.get(username);
        if (user != null && user.password.equals(password)) {
            System.out.println("Sign In successful! Welcome " + username);
            showMenu(user);
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    private static void signUp() {
        System.out.println("Sign Up:");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        users.put(username, new User(username, password));
        System.out.println("Sign Up successful! You can now sign in.");
    }

    private static void showMenu(User user) {
        while (true) {
            System.out.println("Menu:");
            int index = 1;
            for (Drink drink : drinks.values()) {
                System.out.print(index++ + ". " + drink.name + " - $" + drink.price +
                        " (Purchased: " + drink.purchaseCount + ", Most popular topping: " + drink.getMostPopularTopping() + ")");
                System.out.print(" Toppings: ");
                for (Map.Entry<String, Integer> entry : drink.getSortedToppings().entrySet()) {
                    System.out.print(entry.getKey() + " (" + entry.getValue() + ") ");
                }
                System.out.println();
            }
            System.out.println("0. View Cart and Checkout");
            System.out.println("Please choose a drink by number (or 0 to view cart and checkout):");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            if (choice == 0) {
                viewCartAndCheckout(user);
                break;
            }

            if (choice < 1 || choice > drinks.size()) {
                System.out.println("Invalid choice. Please try again.");
            } else {
                Drink selectedDrink = (Drink) drinks.values().toArray()[choice - 1];
                System.out.println("Available toppings for " + selectedDrink.name + ":");
                int toppingIndex = 1;
                for (String topping : selectedDrink.toppings.keySet()) {
                    System.out.println(toppingIndex++ + ". " + topping);
                }
                System.out.print("Enter the number of a topping (or 0 for none): ");
                int toppingChoice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                String topping = "None";
                double toppingPrice = 0;
                if (toppingChoice > 0 && toppingChoice <= selectedDrink.toppings.size()) {
                    topping = (String) selectedDrink.toppings.keySet().toArray()[toppingChoice - 1];
                    toppingPrice = 1.0; // Each topping costs $1
                }

                double finalPrice = selectedDrink.price + toppingPrice;
                cart.put(cartId++, new CartItem(selectedDrink, topping, finalPrice));
                System.out.println("Added to cart!");
            }
        }
    }

    private static void viewCartAndCheckout(User user) {
        System.out.println("Cart:");
        double total = 0;
        for (CartItem item : cart.values()) {
            System.out.println(item.drink.name + " with " + item.topping + " - $" + String.format("%.2f", item.finalPrice));
            total += item.finalPrice;
        }

        double discount = 0;
        if (user != null) {
            if (user.isFirstPurchase) {
                discount = 0.69; // 69% discount for the first purchase
                user.isFirstPurchase = false;
            } else {
                discount = 0.06; // 6% discount for subsequent purchases
            }
        }

        double discountedTotal = total * (1 - discount);
        System.out.println("Original Total: $" + String.format("%.2f", total));
        System.out.println("Discount applied: " + (discount * 100) + "%");
        System.out.println("Final Total: $" + String.format("%.2f", discountedTotal));
        System.out.println("Do you want to checkout? (yes/no)");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("yes")) {
            for (CartItem item : cart.values()) {
                item.drink.addPurchase(item.topping);
            }
            cart.clear();
            System.out.println("Thank you for your purchase!");
        } else {
            System.out.println("Returning to menu.");
        }
    }
}

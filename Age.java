import java.util.Scanner;
import java.util.InputMismatchException;

class Age {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int age1 = 0;
        int age2 = 0;
        int combinedAge = 0;
        int guessedAge = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.print("Enter your age: ");
                age1 = scanner.nextInt();
                validInput = true; // If we get here, input was valid               
            } catch (InputMismatchException e) {
                System.out.println("Error: That's not a valid age. Please try again, and only use integers.");
                scanner.next(); // Clear the invalid input from scanner
            }
        }
        System.out.println("\"" + age1 + "\"");
        validInput = false;

        while (!validInput) {
            try {
                System.out.print("Enter your friend's age: ");
                age2 = scanner.nextInt();
                validInput = true; // If we get here, input was valid                
            } catch (InputMismatchException e) {
                System.out.println("Error: That's not a valid age. Please try again, and only use integers.");
                scanner.next(); // Clear the invalid input from scanner
            }
        }
        System.out.println("\"" + age2 + "\"");
        validInput = false;

        combinedAge = age1 + age2;
        System.out.println("Huh. I just noticed that when you add the two ages together you get a nice number: " + combinedAge);

        while (!validInput) {
            try {
                System.out.print("Enter Spike's favorite number: ");
                guessedAge = scanner.nextInt();
                validInput = true; // Set to true since we got a valid integer
        
                if (guessedAge == combinedAge) {
                    System.out.println("That's right! " + combinedAge + " is my favorite number!");
                    System.out.println("You win!");
                    scanner.nextLine(); // Clear the newline left by nextInt()
                    scanner.close();
                    return;
                } else {
                    if(guessedAge == -42) return;
                    for(int i=0;i<Math.abs(guessedAge);i++) {
                        System.out.println("Wrong! Try again.");
                    }
                    validInput = false; // Reset to false to continue the loop
                }

            }  catch (InputMismatchException e) {
                System.out.println("Wrong! Please enter a valid number.");
                scanner.next(); // Clear the invalid input from scanner 
            }
        }

        
    }
}
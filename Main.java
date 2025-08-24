import java.util.Scanner;

class Main {

    public static void main(String args[]) {
        System.out.println("Hello World, I exist.");
        Scanner scanner = new Scanner(System.in);

        House steveHouse = new House(3);
        House bobHouse = new House(896541651);

        System.out.println(steveHouse);
        System.out.println(bobHouse);

        Resident steve = new Resident("Steve", "McGee", "Maytember 32nd", true);
        steveHouse.setResident(steve);

        cameronString("Changed steve house resident");

        int num1, num2;
        num1 = 6;
        num2 = 7;

        System.out.println(steveHouse);
        System.out.println(bobHouse);

        System.out.println("Hello World, I exist." +"6" + "7 and then finally " );
        System.out.println(num1+num2);
        
        String newString = whatsUp();
        System.out.println(newString);
        String name = scanner.nextLine();
        System.out.println("You typed: " + name);
    }

    private static void cameronString(String thing) {
        System.out.println("This is a string: " + thing);
    }
   
   private static String whatsUp(){
     return "Not much";
   }

   
}
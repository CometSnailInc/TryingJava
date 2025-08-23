
class Main {

    public static void main(String args[]) {
        House steveHouse = new House(3);
        House bobHouse = new House(896541651);

        System.out.println(steveHouse);
        System.out.println(bobHouse);

        Resident steve = new Resident("Steve", "McGee", "Maytember 32nd", true);
        steveHouse.setResident(steve);

        cameronString("Changed steve house resident");

        System.out.println(steveHouse);
        System.out.println(bobHouse);
    }

    private static void cameronString(String thing) {
        System.out.println("This is a string: " + thing);
    }
}
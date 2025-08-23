class Resident {
    String firstName;
    String lastName;
    String birthday;
    boolean isChicken;

    public Resident(String firstName, String lastName, String birthday, boolean isChicken) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.isChicken = isChicken;
    }

    public String toString() {
        return firstName + " " + lastName + " born " + birthday;
    }
}
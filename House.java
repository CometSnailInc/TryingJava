class House {
    int houseNumber;
    Resident resident;

    public House(int houseNumber) {
        this.houseNumber = houseNumber;
        this.resident = null;
    }

    public String toString() {
        return "House #" + houseNumber + " with resident: " + resident;
    }

    public void setResident(Resident newResident) {
        resident = newResident;
    }
}
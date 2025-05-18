package model;

public class Person {
    private String lastName;
    private String firstName;
    private String birthday;

    public Person(String lastName, String firstName, String birthday) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    // Getters
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
}

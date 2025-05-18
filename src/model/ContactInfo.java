package model;

public class ContactInfo {
    private String address;
    private String phoneNumber;

    public ContactInfo(String address, String phoneNumber) {
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}


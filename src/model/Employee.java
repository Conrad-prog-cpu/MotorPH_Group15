package model;

public class Employee {
    private String employeeNumber;
    private Person person;
    private ContactInfo contactInfo;
    private String status;
    private GovernmentID governmentID;
    private Job job;
    private String sssNumber;
    private String philHealthNumber;
    private String pagibigNumber;
    private String tinNumber;

    public Employee(String employeeNumber, Person person, ContactInfo contactInfo, 
                    String status, GovernmentID governmentID, Job job) {
        this.employeeNumber = employeeNumber;
        this.person = person;
        this.contactInfo = contactInfo;
        this.status = status;
        this.governmentID = governmentID;
        this.job = job;
    }

    public Employee(String token, Person person, ContactInfo contact, String token0, Job job, GovernmentID govID) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // Getters
    public String getEmployeeNumber() { return employeeNumber; }
    public Person getPerson() { return person; }
    public ContactInfo getContactInfo() { return contactInfo; }
    public String getStatus() { return status; }
    public GovernmentID getGovernmentID() { return governmentID; }
    public Job getJob() { return job; }
    
    public String getSssNumber() {
    return sssNumber;
}

public String getPhilHealthNumber() {
    return philHealthNumber;
}

public String getPagibigNumber() {
    return pagibigNumber;
}

public String getTinNumber() {
    return tinNumber;
}


    public String getBirthday() {
        return person.getBirthday();
    }

    public String getFirstName() {
        
        return person.getFirstName();
        
    }

    public String getLastName() {
        return person.getLastName();
    }

    public double getHourlyRate() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

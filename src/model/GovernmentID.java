package model;

public class GovernmentID {
    private String sss;
    private String philHealth;
    private String tin;
    private String pagIbig;

    public GovernmentID(String sss, String philHealth, String tin, String pagIbig) {
        this.sss = sss;
        this.philHealth = philHealth;
        this.tin = tin;
        this.pagIbig = pagIbig;
    }

    public String getSSS() {
        return sss;
    }

    public String getPhilHealth() {
        return philHealth;
    }

    public String getTIN() {
        return tin;
    }

    public String getPagIbig() {
        return pagIbig;
    }
}


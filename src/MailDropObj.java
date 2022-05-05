public class MailDropObj {
    private String mailDropID;
    private String businessName;
    private String address;
    private String notes;
    private boolean passedToSheets;

    public MailDropObj() {
    }

    public String getMailDropID() {
        return this.mailDropID;
    }

    public void setMailDropID(String mailDropID) {
        this.mailDropID = mailDropID;
    }

    public String getMailDropBusinessName() {
        return this.businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getMailDropAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isPassedToSheets() {
        return this.passedToSheets;
    }

    public void setPassedToSheets(boolean passedToSheets) {
        this.passedToSheets = passedToSheets;
    }
}

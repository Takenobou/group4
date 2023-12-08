package group4.passwordmanager.model;

public class Master {

    private String masterPassword;

    public Master(){}

    public Master(String masterPassword){
        this.masterPassword = masterPassword;
    }

    public String getMasterPassword(){return masterPassword;}

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

}

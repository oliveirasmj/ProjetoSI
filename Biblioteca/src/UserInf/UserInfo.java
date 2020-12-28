/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserInf;




public class UserInfo {

    private String name, email,nic;
    public UserCrypto uc;


    public UserInfo(){
        this.uc =  new UserCrypto();
        setInfoFromCertificate();
    }
    
    public UserInfo(String name, String email, String nic) {
        this.name = name;
        this.email = email;
        this.nic = nic;
        this.uc = new UserCrypto();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }
    
    /**
     * function used to set the currentUser information from his certificate
     */
    private void setInfoFromCertificate(){
        String[] s = uc.getPublicCertificate().toString().split(",");

        this.setNic(s[1].split("=")[1]);
        this.setName(s[2].split("=")[1] + " " + s[3].split("=")[1]);
        s = null;
    }
  
    
    @Override
	public String toString() {
		return "UserInfo [name=" + name + ", email=" + email + ", nic=" + nic + ", uc=" + uc + "]";
	}
    
    
    
}

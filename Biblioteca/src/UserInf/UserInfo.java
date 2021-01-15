package UserInf;

import static Helpers.globalMethods.bytesEncodeBase64;

import java.io.File;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo {

    private String name, email,nic;
    public FunctionCC uc;


    //chamanda em verifica se existe
    public UserInfo(){
        this.uc =  new FunctionCC();
        setInfoFromCertificate();
    }
    
    //chamada para gerar licenca
    public UserInfo(String email){
    	this.email = email;
        this.uc =  new FunctionCC();
        setInfoFromCertificate();
    }
    
    //chamada para ler licenca valida e construir user
    public UserInfo(String name, String email, String nic) {
        this.name = name;
        this.email = email;
        this.nic = nic;
        this.uc = new FunctionCC();
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
     * funcao utilizada para obter dados do utilizador atual
     */
    private void setInfoFromCertificate(){
        String[] s = uc.getPublicCertificate().toString().split(",");
        this.setNic(s[1].split("=BI")[1]);
        this.setName(s[0].split("=")[1]);
        s = null;
    }
    
    public JSONObject userJson() throws JSONException, CertificateEncodingException {
    	
      	//UserObj
    	JSONObject UserObj = new JSONObject();
    	UserObj.put("name", this.getName());
    	UserObj.put("email", this.getEmail());
    	UserObj.put("nic", this.getNic());
    	
    	 byte[] certificate = uc.getPublicCertificate().getEncoded();
    	 

        
         //verificar o certificado do utilizador
         if(uc.verificarCertificado(certificate))
        	 UserObj.put("certificate", bytesEncodeBase64(certificate));


    	return UserObj;
    }
  
    
    @Override
	public String toString() {
		return "UserInfo [name=" + name + ", email=" + email + ", nic=" + nic + ", uc=" + uc + "]";
	}
    
    
    
}

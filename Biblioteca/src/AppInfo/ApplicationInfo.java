package AppInfo;

import java.security.MessageDigest;

import org.json.*;

import Helpers.globalMethods;
import static Helpers.globalMethods.*;


public class ApplicationInfo {
    private String appName;
    private double version;
    private String hash;
    private String appPubKey;

 
    public ApplicationInfo(String FileName, double version, String algorithm,String pubKey) {
        String[] getAppName = FileName.split("/");
        this.appName = getAppName[getAppName.length-1];
        this.version = version;
        this.hash = generateHash(FileName,algorithm);
        this.appPubKey =  pubKey;
    }
    

    public ApplicationInfo(String appName, String hash,double version,String pubKey) {
        this.appName = appName;
        this.version = version;
        this.hash = hash;
        this.appPubKey = pubKey ;
    }
    
    

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getHash() {
        return hash;
    }
    

    public JSONObject toJSON() throws JSONException{
        JSONObject obj = new JSONObject();
        obj.put("appName", this.appName);
        obj.put("version", this.version);
        obj.put("hash", this.hash);
        obj.put("appPubKey", this.appPubKey);
        
        return obj;
    }
    
    
 
    
    @Override
	public String toString() {
		return "ApplicationInfo [appName=" + appName + ", version=" + version + ", hash=" + hash + ", appPubKey="
				+ appPubKey + "]";
	}


    private String generateHash(String filename, String algorithm){
        
        if(!algorithm.equals("MD5") && !algorithm.equals("SHA-1") && !algorithm.equals("SHA-256")){
            System.out.println("invalide algorithm");
            return "";
        }
        
        try{
            byte[] fileToHash = globalMethods.readFromFile(filename);
            
            MessageDigest digest = MessageDigest.getInstance(algorithm);

            byte[] hashed = digest.digest(fileToHash);

            return BytesToHexString(hashed);
        } catch (Exception ex) {
            return "";
        }
    }
    

    private String BytesToHexString(byte[] arrayBytes) {
    StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
    return stringBuffer.toString();
}
    

    
}

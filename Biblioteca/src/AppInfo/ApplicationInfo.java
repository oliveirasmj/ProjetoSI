/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    /**
     * used when this class is set by a license
     * @param appName
     * @param hash
     * @param version 
     */
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
    
    /**
     * function used to return the ApplicationInfo object as a JSON object
     * @return JSONObject
     * @throws JSONException 
     */
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

	/**
     * function used to generate the hash of the application
     * @param filename name of the file to hash
     * @param algorithm algorithm used to hash
     * @return String of the hash
     */
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
    
    /**
     * function used to convert hash bytes to its hex value
     * @param arrayBytes hash bytes
     * @return String of the hash bytes
     */
    private String BytesToHexString(byte[] arrayBytes) {
    StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
    return stringBuffer.toString();
}
    

    
}

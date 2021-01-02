/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class MotherboardSerial {
    
    //change
    /** 
     * Method for get System MotherboardSerial Serial Number
     * @return  MAC Address
     */
    public static String getSystemMotherBoard_SerialNumber(){
        try{
            String OSName=  System.getProperty("os.name");
            if(OSName.contains("Windows")){
                return (getWindowsMotherboard_SerialNumber());
            }
            else{
                return (GetLinuxMotherBoard_serialNumber());
            }
        }
        catch(Exception E){
            return "";
        }
    }
    
    //change
    /**
     * Method for get Windows Machine MotherBoard Serial Number
     * @return 
     */
    private static String getWindowsMotherboard_SerialNumber() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto",".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs =
            "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
              + "Set colItems = objWMIService.ExecQuery _ \n"
              + "   (\"Select * from Win32_BaseBoard\") \n"
              + "For Each objItem in colItems \n"
              + "    Wscript.Echo objItem.SerialNumber \n"
              + "    exit for  ' do the first cpu only! \n"
              + "Next \n";

            fw.write(vbs);
            fw.close();

            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
               result += line;
            }
            input.close();
        }
        catch(Exception E){
             return "";
        }
        return result.trim();
    } 
    
    //change
    /**
     * Method for get Linux Machine MotherBoard Serial Number
     * @return 
     */
    private static String GetLinuxMotherBoard_serialNumber() {
        String command = "dmidecode -s baseboard-serial-number";
        String sNum = null; 
        try {   
            Process SerNumProcess = Runtime.getRuntime().exec(command);
            BufferedReader sNumReader = new BufferedReader(new InputStreamReader(SerNumProcess.getInputStream()));
            sNum = sNumReader.readLine().trim();
            SerNumProcess.waitFor();
            sNumReader.close();
        }
        catch (Exception ex) {
            sNum ="";
        }
        return sNum; 
    }
    
    
    
    public static Boolean compareMotherBoardSerial(String LicenseMotherBoardSerial){
        String currentMotherBoardSerial = getSystemMotherBoard_SerialNumber();
        if(!currentMotherBoardSerial.equals(LicenseMotherBoardSerial))
            return false;
        return true;
    }
}

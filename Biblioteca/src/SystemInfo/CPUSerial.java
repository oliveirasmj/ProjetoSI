/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class CPUSerial {
    public final static String getCPUSerial() throws IOException {
        String os = System.getProperty("os.name");
        try {
            if (os.startsWith("Windows")) {
                return getCPUSerialWindows();
            } else if (os.startsWith("Linux")) {
                return getCPUSerialLinux();
            } else {
                throw new IOException("unknown operating system: " + os);
            }
        } catch (Exception ex) {
            return "";
        }
    }
    
     /*
     * Captura serial da CPU no WINDOWS, atraves da execucao de script visual
     * basic
     */
    private static String getCPUSerialWindows() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "On Error Resume Next \r\n\r\n"
                    + "strComputer = \".\"  \r\n"
                    + "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n"
                    + "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n"
                    + "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n "
                    + "For Each objItem in colItems\r\n "
                    + "    Wscript.Echo objItem.ProcessorId  \r\n "
                    + "    exit for  ' do the first cpu only! \r\n"
                    + "Next                    ";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
        }
        if (result.trim().length() < 1 || result == null) {
            result = "";
        }
        return result.trim();
    }

    /*
     * Captura serial de CPU em sistemas Linux, atraves da execucao de comandos
     * em shell.
     */
    private static String getCPUSerialLinux() {
        String result = "";
        try {
            String[] args = {"bash", "-c", "lshw -class processor | grep serial"};
            Process p = Runtime.getRuntime().exec(args);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
        }
        if (result.trim().length() < 1 || result == null) {
            result = "";
        }
        return result;
    }
    
    public static Boolean compareCPUSerial(String LicenseCPUSerial) throws IOException{
        String currentCPUSerial = getCPUSerial();
        if(!currentCPUSerial.equals(LicenseCPUSerial))
            return false;
        return true;
    }

}

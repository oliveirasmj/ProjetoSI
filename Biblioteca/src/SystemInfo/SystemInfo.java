package SystemInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

public class SystemInfo {

	private String macAdress, motherBoardSerial, userName, hostName, cpuSerial;

	/**
	 * Construtor do system info
	 * 
	 * @throws IOException
	 */
	public SystemInfo() throws IOException {
		this.macAdress = getMac();
		this.motherBoardSerial = MotherboardSerial.getSystemMotherBoard_SerialNumber();
		this.userName = System.getProperty("user.name");
		this.hostName = getSystemName();
		this.cpuSerial = CPUSerial.getCPUSerial();
	}
	
    public SystemInfo(String macAdress, String motherboardserial, String jsonUserName, String jsonHostName, String jsonCpuSerial) throws IOException {
    	
    	boolean systemInfoIntegra = true;
    	
    	if(!macAdress.equals(getMac())) 
    		systemInfoIntegra=false;
    	
    	
    	if(!motherboardserial.equals(MotherboardSerial.getSystemMotherBoard_SerialNumber())) 
    		systemInfoIntegra=false;
    	
    
    	if(!jsonUserName.equals(System.getProperty("user.name"))) 
    		systemInfoIntegra=false;
    	
    	if(!jsonHostName.equals(getSystemName())) 
    		systemInfoIntegra=false;
    	
    	if(!jsonCpuSerial.equals(CPUSerial.getCPUSerial())) 
    		systemInfoIntegra=false;
    	
    	
    		if(systemInfoIntegra) {
    			this.macAdress = macAdress;
    			this.motherBoardSerial = motherboardserial;
    	        this.userName = jsonUserName;
    	        this.hostName = jsonHostName;
    	        this.cpuSerial = jsonCpuSerial;
    		}else {
    			System.out.println("Os dados da licenca não corresponderem ao computador atual");
    		}
    		
    }

	/**
	 * Obtem o nome do sistema
	 * 
	 * @return String nome do sistema
	 */
	public static String getSystemName() {
		try {
			InetAddress inetaddress = InetAddress.getLocalHost();
			return inetaddress.getHostName();
		} catch (Exception E) {
			return "";
		}
	}

	/**
	 * Getter do mac do pc
	 * 
	 * @return String mac
	 */
	public String getMac() {
		InetAddress ip;
		StringBuilder sb = new StringBuilder();
		try {

			ip = InetAddress.getLocalHost();
			// System.out.println("Current IP address : " + ip.getHostAddress());

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);

			byte[] mac = network.getHardwareAddress();

			// System.out.print("Current MAC address : ");

			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			// System.out.println(sb.toString());
			// return sb.toString();
		} catch (UnknownHostException e) {

			e.printStackTrace();

		} catch (SocketException e) {

			e.printStackTrace();

		}

		return sb.toString();

	}
	
	

	public String getMacAdress() {
		return macAdress;
	}

	public void setMacAdress(String macAdress) {
		this.macAdress = macAdress;
	}

	public String getMotherBoardSerial() {
		return motherBoardSerial;
	}

	public void setMotherBoardSerial(String motherBoardSerial) {
		this.motherBoardSerial = motherBoardSerial;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getCpuSerial() {
		return cpuSerial;
	}

	public void setCpuSerial(String cpuSerial) {
		this.cpuSerial = cpuSerial;
	}
	
	
	public JSONObject systemJSON() throws JSONException {
		
		JSONObject SystemObj = new JSONObject();
    	SystemObj.put("macAdress", this.getMac());
    	SystemObj.put("motherBoardSerial", this.getMotherBoardSerial());
    	SystemObj.put("userName", this.getUserName());
    	SystemObj.put("hostName", this.getHostName());
    	SystemObj.put("cpuSerial", this.getCpuSerial());
		
		return SystemObj;
	}
	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.macAdress + " " + this.userName + " " + this.hostName + " " + this.motherBoardSerial + " "
				+ this.cpuSerial;
	}

}

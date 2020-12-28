package com.main;

import java.io.IOException;
import org.json.simple.JSONObject;
import Helpers.WriteToFile;
import SystemInfo.SystemInfo;
import UserInf.UserInfo;

public class BibControlo {
	// private UserInfo currentUser;
	public BibControlo() {
	}

	// se programa não encontra a licenca
	// cria uma
	public boolean criaLicenca() {
		return true;
	}

	public static void main(String[] args) throws IOException {
		obterDadosSO();
	}

	@SuppressWarnings("unchecked")
	public static void obterDadosSO() throws IOException {
		// instanciar objetos
		SystemInfo sy = new SystemInfo();
		UserInfo currentUser = new UserInfo();

		System.out.println("utilizador criado através de  cartao de cidadao: " + currentUser);

		//SystemObj
    	JSONObject SystemObj = new JSONObject();
    	SystemObj.put("macAdress", sy.getMac());
    	SystemObj.put("motherBoardSerial", sy.getMotherBoardSerial());
    	SystemObj.put("userName", sy.getUserName());
    	SystemObj.put("hostName", sy.getHostName());
    	SystemObj.put("cpuSerial", sy.getCpuSerial());
    	
    	//UserObj
    	JSONObject UserObj = new JSONObject();
    	UserObj.put("name", currentUser.getName());
    	UserObj.put("email", currentUser.getEmail());
    	UserObj.put("nic", currentUser.getNic());
    	
    	//Ambos
    	JSONObject detailsList = new JSONObject();
    	detailsList.put("system", SystemObj);
    	detailsList.put("user", UserObj);
    	
    	
//    	//Write JSON file
//    	try (FileWriter file = new FileWriter("./licenca.json")) {
//
//            file.write(detailsList.toJSONString());
//            file.flush();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    	WriteToFile fw = new WriteToFile();
    	fw.writeFile(detailsList, "./licenca.json");

	}

}
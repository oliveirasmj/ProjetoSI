package com.main;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
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

	public static void main(String[] args) throws IOException, JSONException {
		obterDadosSO();
	}

	public static void obterDadosSO() throws IOException, JSONException {
		// instanciar objetos
		SystemInfo sy = new SystemInfo();
		Gson gson = new Gson();
		JSONObject jso1 = new JSONObject();
		JSONObject jso2 = new JSONObject();
		
		  
		 

		WriteToFile fw = new WriteToFile();
		UserInfo currentUser = new UserInfo();
		
		// ESTE BLOCO DE CODIGO NAO DÁ MAS É ALGO ASSIM PARA JUJNTAR OS 2 objectos USER E SISTEMA
		// AMOBOS OBJETOS ESTAO CRIADO BASTA CORRERES O FICHEIRO BIB 
		
		//
		/*
		 * 
		JSONObject Obj1 = (JSONObject) jso1.get(sy.toString()); 
		  JSONObject Obj2 =(JSONObject) jso2.get(currentUser.toString()); 
		  JSONObject combined = new JSONObject();
		  combined.put("Systema", Obj1); combined.put("User", Obj2);
*/
		System.out.println("utilizador criado através de  cartao de cidadao: " + currentUser);
		// Criar ficheiro
		// converte objetos Java para JSON e retorna JSON como Strin
		
		//TODO: String de user com gson nao da
		//String json = gson.toJson(currentUser);

		// variaveis locais
			String caminhoFicheiroJson = "./licenca.json";

		// converte objetos Java para JSON e retorna JSON como String
		 String json = gson.toJson(sy);
		 
		 //cria ficheiro de licenca, chama funcao em package HELPER
		 fw.writeFile(json, caminhoFicheiroJson);

	}

}

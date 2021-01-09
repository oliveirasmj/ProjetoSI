/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helpers;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.spec.IvParameterSpec;


public class globalMethods {
    /**
     * function used to read a file a retrive its bytes
     * @param fileName name/path of the file to read
     * @return byte array of the content of the file
     */
    public static byte[] readFromFile(String fileName) throws IOException{
        try{
            FileInputStream fis = new FileInputStream(fileName);
            byte[] fileBytes = new byte[fis.available()];
            fis.read(fileBytes);
           // fis.close();
            return fileBytes;
        } catch (Exception e) {
            System.out.println("erro a ler ficheiro: " + e);
            throw new IOException();

        }
        
    }
    
    /**
     * function used to write to or a new file
     * @param nomeFicheiro name of the file to write
     * @param contiudo content of the file to be written
     */
    public static void writeToFile(String nomeFicheiro, byte[] contiudo) throws IOException{
        try {
            FileOutputStream fos = new FileOutputStream(nomeFicheiro);
            fos.write(contiudo);
            fos.close();
        } catch (Exception e) {
            System.out.println("erro a escrever ficheiro: " + e);
            throw new IOException();
        }
        
    }
    
    /**
     * function used mostly for debugging to be able to turn bytes into a string (not necessarily human readable)
     * @param bytes bytes array that we want to turn into string
     * @return String that represents the bytes
     */
    public static String bytesToStringPrint(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Erro em bytesToString");
            return "";
        }
    }
    
    /**
     * function used to encode a byte array into base64 string
     * @param bytes byte array of content to be transformed
     * @return Strinf that represents the bytes in base 64
     */
    public static String bytesEncodeBase64(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }
    /**
     * function used to decode a base64 string into a byte array
     * @param base64String String encoded in base64
     * @return byte array of the decoded string
     */
    public static byte[] stringDecodeBase64(String base64String){
        return Base64.getDecoder().decode(base64String);
    }

    
}

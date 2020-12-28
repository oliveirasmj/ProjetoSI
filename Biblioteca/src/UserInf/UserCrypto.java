/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserInf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static globalMethods.globalMethods.bytesToStringPrint;
import static globalMethods.globalMethods.stringDecodeBase64;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;


public class UserCrypto {
    
    private Provider ccProvider; 
    private KeyStore ks;
    private Certificate licenseCertificate; //used only when using for imported lincese

    public UserCrypto() {
        try {
           this.ccProvider = Security.getProvider("SunPKCS11-CartaoCidadao");
            this.ks = KeyStore.getInstance("PKCS11",ccProvider);
            this.ks.load(null,null);
          
        } catch (KeyStoreException ex ) {
            Logger.getLogger(UserCrypto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserCrypto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserCrypto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(UserCrypto.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
    
    /**
     * function used to get the signature byes of a data
     * @param bytesToSign bytes of data to be signed
     * @return byte array 
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     * @throws InvalidKeyException
     * @throws SignatureException 
     */
    public byte[] getSignatureOfData(byte[] bytesToSign) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, SignatureException{
        PrivateKey pk = (PrivateKey) ks.getKey("CITIZEN AUTHENTICATION CERTIFICATE", null);
        Signature sig = Signature.getInstance("SHA256withRSA",this.ccProvider);
        
        sig.initSign(pk); //chave privada
        sig.update(bytesToSign); //texto a assinar
        return sig.sign(); // assinar (é a assinatura)
    }
    
    /**
     * function that gets ths user certificate from the CC
     * @return Certificate
     */
    public Certificate getPublicCertificate () {
        try {
            return this.ks.getCertificate("CITIZEN AUTHENTICATION CERTIFICATE");
        }catch (KeyStoreException ex) {
            Logger.getLogger(UserCrypto.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
        
    /**
     * function used to get the public key from a given certificate
     * @param cer certificate to extract public key
     * @return PublicKey
     */
    public PublicKey getPublicKey (Certificate cer){
        return cer.getPublicKey();
    }

    /**
     * function used to set a certificate from the license
     * @param certificateEncodedString string encoded in base64 that representes the certificate
     * @throws CertificateException 
     */
    public void setLicenseCertificate(String certificateEncodedString) throws CertificateException {
        InputStream is = new ByteArrayInputStream(stringDecodeBase64(certificateEncodedString));
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) fact.generateCertificate(is);
        this.licenseCertificate = cert;
        System.out.println("license certificate inserted");
        verificarCertificado(cert.getEncoded());
    }
    
    
    public Certificate getLicenseCertificate() {
        return licenseCertificate;
    }
    
    /**
     * function used to verify if the currentUser is the valide user
     * @param licenseCertificate certificate of the license 
     * @return true if the user is valide, otherwise returns false
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws InvalidKeyException
     * @throws SignatureException 
     */
    public boolean VerifyUserByCC(Certificate licenseCertificate) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, InvalidKeyException, SignatureException{
        
        byte[] randomBytes = new byte[20];
        //SecureRandom.getInstanceStrong().nextBytes(randomBytes);
        new Random().nextBytes(randomBytes);
        //System.out.println("random bytes : " + bytesToStringPrint(randomBytes));

        byte[] userSignatureBytes = getSignatureOfData(randomBytes);
        Signature userSignature = Signature.getInstance("SHA256withRSA");
        
        userSignature.initVerify(licenseCertificate);
        userSignature.update(randomBytes);
        
        return userSignature.verify(userSignatureBytes);
        
    }
    
    /**
     * function used to verify if a given certificate is valid
     * @param certificateBytes certificate bytes
     * @return true if the certificate is valid, otherwise return false
     */
    public boolean verificarCertificado(byte[] certificateBytes){
        try {
            InputStream is = new ByteArrayInputStream(certificateBytes);
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) fact.generateCertificate(is);
            cert.checkValidity();
            System.out.println("certificado valido");
            return true;
        } catch (Exception e) {
            System.out.println("certificado inválido");
            return false;
        }
    }
    
    
    public static void main(String[] args) {
        try {
            System.out.println("---A gerar ficheiros");
            String texto = "isto é um texto e tal";
            writeToFile("texto", texto.getBytes());
            
            
            UserCrypto uc =  new UserCrypto();
            
            writeToFile("AssinaturaFicheiro", uc.getSignatureOfData(texto.getBytes()));
            
            Certificate cer = uc.getPublicCertificate();
            writeToFile("certificadoChavePublica", cer.getEncoded());
            
            PublicKey pk = uc.getPublicKey(cer);
            writeToFile("chavePublica", pk.getEncoded());
            
            System.out.println("Gerado com sucesso");
            
            
            
            
            
            
            System.out.println("---A validar ficheiros");
            
            //certificate
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            FileInputStream is = new FileInputStream ("src/testes/certificadoChavePublica");
            X509Certificate certificado = (X509Certificate) fact.generateCertificate(is);
            verificarCertificado(certificado);
            
            
            //signature
            byte[] mysignatureread = readFromFile("AssinaturaFicheiro");
            Signature mysignature = Signature.getInstance("SHA256withRSA");
            mysignature.initVerify(certificado);
            
            
            //data
            byte[] data = readFromFile("texto");
            mysignature.update(data);
            
            boolean verifies = mysignature.verify(mysignatureread);
            
            System.out.println("signature verifies: " + verifies);
            System.out.println("data : " + bytesToStringPrint(data));
            
        } catch (Exception ex) {
            Logger.getLogger(UserCrypto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static void writeToFile(String nomeFicheiro, byte[] conteudo){
        try {
            FileOutputStream fos = new FileOutputStream("src/testes/"+nomeFicheiro);
            fos.write(conteudo);
            fos.close();
        } catch (Exception e) {
            System.out.println("erro a escrever ficheiro: " + e);
        }
        
    }
    
    public static byte[] readFromFile(String fileName){
        try{
            FileInputStream fis = new FileInputStream("src/testes/"+fileName);
            byte[] fileBytes = new byte[fis.available()];
            fis.read(fileBytes);
            return fileBytes;
        } catch (Exception e) {
            System.out.println("erro a ler ficheiro: " + e);
        }
        return null;
        
    }
    
    public static void verificarCertificado(X509Certificate cer){
        try {
            //cer.checkValidity(new Date(1990, 1, 1)); //data que permite fazer com que o certificado fique inválido
            cer.checkValidity();
            System.out.println("certificado valido");
        } catch (Exception e) {
            System.out.println("certificado inválido");
        }
    }
    
    
    
    
}

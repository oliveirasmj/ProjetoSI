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

import static Helpers.globalMethods.bytesToStringPrint;
import static Helpers.globalMethods.stringDecodeBase64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;


public class FunctionCC {
    
    private Provider ccProvider; 
    private KeyStore ks;
    private Certificate licenseCertificate; 

    public FunctionCC() {
        try {
           this.ccProvider = Security.getProvider("SunPKCS11-CartaoCidadao");
            this.ks = KeyStore.getInstance("PKCS11",ccProvider);
            this.ks.load(null,null);
          
        } catch (KeyStoreException ex ) {
            Logger.getLogger(FunctionCC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FunctionCC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FunctionCC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(FunctionCC.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
    
    /**
     * funcao utilizada para obter bytes de signature
     * @param array de bytes a serem assinados
     * @return Array de bytes
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
        return sig.sign(); // assinar (Ã© a assinatura)
    }
    
    /**
     * funcao para obter certeficado do cc
     * @return Certificate
     */
    public Certificate getPublicCertificate () {
        try {
            return this.ks.getCertificate("CITIZEN AUTHENTICATION CERTIFICATE");
        }catch (KeyStoreException ex) {
            Logger.getLogger(FunctionCC.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
        
    /**
     * funcao utilizada para obter a chave publica do cc 
     * @param cer certeficado utilizado para obter a chave publica
     * @return chave publica
     */
    public PublicKey getPublicKey (Certificate cer){
        return cer.getPublicKey();
    }

    /**
     * funcao utilizada para obter o certeficado da licença
     * @param certificateEncodedString string do certeficado encoded em 64bits
     * @throws CertificateException 
     */
    public void setLicenseCertificate(String certificateEncodedString) throws CertificateException {
        InputStream is = new ByteArrayInputStream(stringDecodeBase64(certificateEncodedString));
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) fact.generateCertificate(is);
        this.licenseCertificate = cert;
        System.out.println("Certeficado da licença inserido.");
        verificarCertificado(cert.getEncoded());
    }
    
    
    public Certificate getLicenseCertificate() {
        return licenseCertificate;
    }
    
    /**
     * funcao utilizada para verificar se um utilizado é valido
     * @param licenseCertificate certeficado em licenca
     * @return true se utilizador valido
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws InvalidKeyException
     * @throws SignatureException 
     */
    public boolean VerifyUserByCC(Certificate licenseCertificate) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, InvalidKeyException, SignatureException{
        
        byte[] randomBytes = new byte[20];

        new Random().nextBytes(randomBytes);
       
        byte[] userSignatureBytes = getSignatureOfData(randomBytes);
        Signature userSignature = Signature.getInstance("SHA256withRSA");
        
        userSignature.initVerify(licenseCertificate);
        userSignature.update(randomBytes);
        
        return userSignature.verify(userSignatureBytes);
        
    }
    
    /**
     *  funcao para verificar se certeficado é válido
     * @param certificateBytes bytes de certeficado
     * @return tru se certeficado válido
     */
    public boolean verificarCertificado(byte[] certificateBytes){
        try {
            InputStream is = new ByteArrayInputStream(certificateBytes);
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) fact.generateCertificate(is);
            //verifica se certificado obtido atraves dos bytes do json lido é valido
            cert.checkValidity();
            System.out.println("Certificado valido");
            return true;
        } catch (Exception e) {
            System.out.println("Certificado inválido");
            return false;
        }
    }
    
    public static X509Certificate getClientCertificate(byte[] certificateBytes) throws CertificateException {
		InputStream is = new ByteArrayInputStream(certificateBytes);
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		return (X509Certificate) fact.generateCertificate(is);
	}
    
    
    
  
    
    
    
    
}

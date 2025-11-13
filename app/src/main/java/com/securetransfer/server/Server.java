package com.securetransfer.server;

import com.securetransfer.crypto.RSAUtils;

public class Server {

    public static void main(String[] args){
        System.out.println("|===================|");
        System.out.println("| Iniciando Servidor|");
        System.out.println("|===================|");
        
        try {
            RSAUtils rsa = new RSAUtils();
            rsa.generateKeyPair();

            String mensajeOriginal = "Clave AES temporal";
            System.out.println("Texto original: " + mensajeOriginal);

            String cifrado = rsa.encryptWithPublic(mensajeOriginal);
            System.out.println("Texto cifrado (Base64): " + cifrado);

            String descifrado = rsa.decryptWithPrivate(cifrado);
            System.out.println("Texto descifrado: " + descifrado);

            System.out.println("Prueba RSA completada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

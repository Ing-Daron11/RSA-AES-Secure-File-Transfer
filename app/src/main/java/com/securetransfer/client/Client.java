package com.securetransfer.client;

import com.securetransfer.crypto.AESUtil;
import com.securetransfer.crypto.RSAUtils;
import com.securetransfer.crypto.HashUtils;
import com.securetransfer.utils.FileUtils;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

public class Client {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9090;
    private static final String INPUT_FILE = "file_to_send.txt";

    public static void main(String[] args) {
        new Client().start();
    }

    public void start() {
        // Código ANSI para color azul
        String BLUE = "\u001B[34m";
        String RESET = "\u001B[0m";
        
        System.out.println(BLUE + "                _____\r\n" + //
                        "             ,-\"     \"-.\r\n" + //
                        "            / o       o \\\r\n" + //
                        "           /   \\     /   \\\r\n" + //
                        "          /     )-\"-(     \\\r\n" + //
                        "         /     ( 6 6 )     \\\r\n" + //
                        "        /       \\ \" /       \\\r\n" + //
                        "       /         )=(         \\\r\n" + //
                        "      /   o   .--\"-\"--.   o   \\\r\n" + //
                        "     /    I  /  -   -  \\  I    \\\r\n" + //
                        " .--(    (_}y/\\       /\\y{_)    )--.\r\n" + //
                        "(    \".___l\\/__\\_____/__\\/l___,\"    )\r\n" + //
                        " \\                                 /\r\n" + //
                        "  \"-._      o O o O o O o      _,-\"\r\n" + //
                        "      `--Y--.___________.--Y--'\r\n" + //
                        "         |==.___________.==| hjw\r\n" + //
                        "         `==.___________.==' `97\r\n" + //
                        "" + RESET);

        System.out.println("Cliente iniciando \n" + 
                            "Conectando al servidor en " + SERVER_HOST + ":" + SERVER_PORT + "...");

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            System.out.println("Conectado al servidor.");

            // Recibir clave pública RSA del servidor
            int publicKeyLength = in.readInt();
            byte[] publicKeyBytes = new byte[publicKeyLength];
            in.readFully(publicKeyBytes); //Aquí esta recibiendo la clave pública RSA

            //Aquí se está reconstruyendo la clave pública RSA
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey serverPublicKey = keyFactory.generatePublic(spec);
            System.out.println("Clave publica RSA del servidor recibida.");

            //Generar clave AES
            SecretKey aesKey = AESUtil.generateAESKey();
            System.out.println("Clave AES generada.");

            //Cifrar clave AES con clave pública del servidor y enviar
            byte[] encryptedAESKey = RSAUtils.encryptRSA(aesKey.getEncoded(), serverPublicKey);
            out.writeInt(encryptedAESKey.length);
            out.write(encryptedAESKey);
            out.flush();
            System.out.println("Clave AES cifrada y enviada al servidor.");

            // Buscar archivo en múltiples ubicaciones
            String fileToUse = findInputFile();
            
            //Leer archivo a enviar
            byte[] fileData = FileUtils.readFile(fileToUse);

            //Cifrar archivo con AES y enviar
            byte[] encryptedFile = AESUtil.encrypt(fileData, aesKey);
            out.writeInt(encryptedFile.length);
            out.write(encryptedFile);
            out.flush();
            System.out.println("Archivo cifrado y enviado al servidor.");

            //Calcular hash del archivo original y enviar
            byte[] hash = HashUtils.sha256(fileData);
            out.writeInt(hash.length);
            out.write(hash);
            out.flush();
            System.out.println("Hash SHA-256 del archivo enviado.");

            //Recibir respuesta del servidor
            String response = in.readUTF();
            if ("OK".equals(response)) {
                System.out.println("Servidor confirma: integridad verificada correctamente.");
            } else {
                System.out.println("Servidor reporta: error de integridad.");
            }

            socket.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error: No se encontró el archivo '" + INPUT_FILE + "'");
            System.err.println("Por favor, crea el archivo antes de ejecutar el cliente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Busca el archivo de entrada en la carpeta del cliente
     */
    private String findInputFile() throws FileNotFoundException {
        // Buscar en la carpeta del cliente (src/main/java/com/securetransfer/client/)
        String clientDir = "app/src/main/java/com/securetransfer/client/";
        String filePath = clientDir + INPUT_FILE;
        
        File file = new File(filePath);
        if (file.exists()) {
            //System.out.println("Archivo encontrado en: " + file.getAbsolutePath());
            return filePath;
        }
        
        // Si no existe, intentar desde la carpeta actual
        file = new File(INPUT_FILE);
        if (file.exists()) {
            //System.out.println("Archivo encontrado en: " + file.getAbsolutePath());
            return INPUT_FILE;
        }
        
        throw new FileNotFoundException("No se encontró '" + INPUT_FILE + "' en " + clientDir);
    }
}

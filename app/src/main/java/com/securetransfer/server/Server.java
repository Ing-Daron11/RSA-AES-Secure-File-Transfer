package com.securetransfer.server;

import com.securetransfer.crypto.AESUtil;
import com.securetransfer.crypto.RSAUtils;
import com.securetransfer.crypto.HashUtils;
import com.securetransfer.utils.FileUtils;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class Server {

    private static final int PORT = 9090;
    private static final String OUTPUT_FILE = "received/received_file.txt";

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        System.out.println("Servidor iniciado. Esperando conexión en el puerto " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Socket socket = serverSocket.accept();
            System.out.println("Cliente conectado desde " + socket.getInetAddress());

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            //Generar par de claves RSA
            KeyPair keyPair = RSAUtils.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            //Enviar clave pública al cliente
            byte[] publicKeyBytes = publicKey.getEncoded();
            out.writeInt(publicKeyBytes.length);
            out.write(publicKeyBytes);
            out.flush();
            System.out.println("Clave pública RSA enviada al cliente.");

            //Recibir clave AES cifrada
            int encryptedKeyLength = in.readInt();
            byte[] encryptedAESKey = new byte[encryptedKeyLength];
            in.readFully(encryptedAESKey);

            byte[] aesKeyBytes = RSAUtils.decryptRSA(encryptedAESKey, privateKey);
            SecretKey aesKey = AESUtil.rebuildKey(aesKeyBytes);
            System.out.println("Clave AES recibida y descifrada correctamente.");

            //Recibir archivo cifrado
            int encryptedFileLength = in.readInt();
            byte[] encryptedFile = new byte[encryptedFileLength];
            in.readFully(encryptedFile);
            System.out.println("Archivo cifrado recibido.");

            //Descifrar archivo y guardar
            byte[] decryptedFile = AESUtil.decrypt(encryptedFile, aesKey);
            FileUtils.saveFile(OUTPUT_FILE, decryptedFile);
            System.out.println("Archivo descifrado y guardado en " + OUTPUT_FILE);

            //|Recibir hash enviado por el cliente
            int hashLength = in.readInt();
            byte[] receivedHash = new byte[hashLength];
            in.readFully(receivedHash);

            //Calcular hash del archivo recibido
            byte[] localHash = HashUtils.sha256(decryptedFile);

            //Comparar hashes
            if (Arrays.equals(localHash, receivedHash)) {
                System.out.println("Integridad verificada: el archivo se transfirió correctamente.");
                out.writeUTF("OK");
            } else {
                System.out.println("Error de integridad: los hashes no coinciden.");
                out.writeUTF("FAIL");
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

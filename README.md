# RSA-AES Secure File Transfer

> Sistema de transferencia segura de archivos utilizando criptografía híbrida (RSA + AES-256)

<div align="center">

![Status](https://img.shields.io/badge/status-active-success.svg)
![Java](https://img.shields.io/badge/java-17-blue.svg)
![Gradle](https://img.shields.io/badge/gradle-9.1.0-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

</div>

---

## Tabla de Contenidos
- [Integrantes](#integrantes)
- [Descripción General](#descripción-general)
- [Diagrama de Flujo](#diagrama-de-flujo)
- [Instalación](#instalación)
- [Ejecución del Proyecto](#ejecución-del-proyecto)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Problemas Encontrados y Soluciones](#problemas-encontrados-y-soluciones)
- [Próximos Pasos](#próximos-pasos)

---

## Integrantes

| Nombre | GitHub |
|--------|--------|
| **Miguel** | [@Miguel-23-ing](https://github.com/Miguel-23-ing) 
| **Daron** | [@Ing-Daron11](https://github.com/Ing-Daron11) 

## Descripción General

Este proyecto implementa un sistema de transferencia segura de archivos entre un cliente y un servidor usando **criptografía híbrida**. Combina RSA (asimétrica) para el intercambio seguro de claves y AES-256 (simétrica) para cifrar los datos del archivo. Además, implementa verificación de integridad mediante hashes SHA-256.

**Caso de Uso:** Transferir archivos sensibles a través de una red insegura garantizando confidencialidad, integridad y autenticidad.


## Diagrama de Flujo

![alt text](<images/Diagrama de flujo.png>)


## Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Ing-Daron11/RSA-AES-Secure-File-Transfer.git
cd RSA-AES-Secure-File-Transfer
```

### 2. Compilar el Proyecto

```powershell
# Windows (PowerShell o CMD)
.\gradlew.bat build

# macOS / Linux
./gradlew build
```

## Ejecución del Proyecto

### Usando Gradle

**Paso 1: Abre DOS terminales**

**Terminal 1 - Ejecutar el Servidor**
```powershell
.\gradlew.bat runServer
```

**Terminal 2 - Ejecutar el Cliente (después de que el servidor esté listo)**
```powershell
.\gradlew.bat runClient
```

### Salida Esperada

**Servidor:**
```
Servidor iniciado. Esperando conexion en el puerto 9090...
Cliente conectado desde /127.0.0.1
Clave publica RSA enviada al cliente.
Clave AES recibida y descifrada correctamente.
Archivo cifrado recibido.
Archivo descifrado y guardado en received/received_file.txt
Integridad verificada: el archivo se transfirió correctamente.
```

**Cliente:**
```
[ASCII ART del extraterrestre en AZUL]
Cliente iniciando
Conectando al servidor en localhost:9090...
Conectado al servidor.
Clave publica RSA del servidor recibida.
Clave AES generada.
Clave AES cifrada y enviada al servidor.
Archivo leído: ... (X bytes)
Archivo cifrado y enviado al servidor.
Hash SHA-256 del archivo enviado.
Servidor confirma: integridad verificada correctamente.
```

---

## Estructura del Proyecto

```
com.securetransfer/
│
├── client/
│   └── Client.java
│       • Conecta con el servidor
│       • Genera clave AES
│       • Cifra y envía el archivo
│       • Verifica confirmación del servidor
│
├── server/
│   └── Server.java
│       • Escucha conexiones entrantes
│       • Genera par de claves RSA
│       • Descifra clave AES
│       • Descifra y guarda el archivo
│       • Verifica integridad
│
├── crypto/
│   ├── RSAUtils.java
│   │   • generateKeyPair()
│   │   • encryptRSA() / decryptRSA()
│   │
│   ├── AESUtil.java
│   │   • generateAESKey()
│   │   • encrypt() / decrypt()
│   │   • rebuildKey()
│   │
│   └── HashUtils.java
│       • sha256() - retorna bytes
│       • sha256Hex() - retorna string hex
│
└── utils/
    └── FileUtils.java
        • saveFile()
        • readFile()
        • fileExists()
        • deleteFile()
```


---

## Problemas Encontrados y Soluciones

### 1. **Gradle no estaba instalado**
- **Problema**: `gradlew` no se reconocía como comando en Windows
- **Causa**: Gradle no estaba instalado globalmente
- **Solución**: 
  - Instalar Gradle vía Chocolatey: `choco install gradle -y` (con privilegios de administrador)
  - Alternativa: Descargar Gradle manualmente y agregar al PATH

### 2. **Archivo `file_to_send.txt` no encontrado**
- **Problema**: Cliente lanzaba `FileNotFoundException`
- **Causa**: La ruta relativa del archivo era inconsistente según el working directory de Gradle
- **Solución**: 
  - Implementar búsqueda de archivo en múltiples rutas
  - Configurar `workingDir = rootDir` en la tarea `runClient` en `build.gradle`
  - Asegurarse de que el archivo esté en `app/src/main/java/com/securetransfer/client/`

### 3. **EOFException en el servidor durante la primera ejecución**
- **Problema**: Servidor esperaba datos que nunca llegaban
- **Causa**: Cliente se desconectaba antes de enviar el archivo (por el problema anterior)
- **Solución**: Una vez resuelto el problema 2, este se resolvió automáticamente

### 4. **Métodos inexistentes en RSAUtils**
- **Problema**: Métodos llamados no coincidían con la implementación original
- **Causa**: Métodos eran de instancia en lugar de estáticos
- **Solución**: 
  - Refactorizar RSAUtils para métodos estáticos
  - Añadir métodos `encryptRSA()` y `decryptRSA()` que trabajen con bytes
  - Actualizar Client.java y Server.java para usar los nuevos métodos

---

## Próximos Pasos

### Mejoras Planeadas

| Característica | Descripción |
|---|---|
| **Autenticación Mutua** | Implementar certificados digitales y autenticar servidor en cliente |
| **Compresión de Datos** | Añadir compresión GZIP antes de cifrar para reducir tamaño de transferencia |
| **Indicador de Progreso** | Mostrar barra de progreso durante transferencia con información de velocidad y tiempo estimado |
| **Transferencia Múltiple** | Permitir transferencias consecutivas sin reconectar; reutilizar o regenerar clave AES |
| **GUI (Interfaz Gráfica)** | Interfaz Swing o JavaFX con selección de archivos mediante explorador |

---

## Licencia

Este proyecto está bajo licencia **MIT**. Consulta el archivo `LICENSE` para más detalles.

---

## Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## Referencias

- [Java Cryptography Architecture (JCA)](https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html)
- [RFC 3394 - AES Key Wrap Algorithm](https://tools.ietf.org/html/rfc3394)
- [NIST Guidelines for Key Management](https://nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-57p1.pdf)
- [Gradle Documentation](https://docs.gradle.org/)

---

## Contacto

Para preguntas o sugerencias:
- **Daron**: [@Ing-Daron11](https://github.com/Ing-Daron11)
- **Miguel**: [@Miguel-23-ing](https://github.com/Miguel-23-ing)


---

<div align="center">

**Hecho con ❤️ para la seguridad de datos**

⭐ Si este proyecto te fue útil, considera darle una estrella en GitHub

</div>

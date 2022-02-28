package easyoa.filemanager.client;

import easyoa.filemanager.exception.InfinItException;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Properties;

@Slf4j
public class SftpClient {

    private JSch jsch = null;
    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;

    private SftpClient(){

    }

    private static volatile SftpClient INSTANCE = null;

    public static SftpClient getINSTANCE(){
        if(INSTANCE == null){
          synchronized (SftpClient.class){
            if(INSTANCE == null){
                return new SftpClient();
            }
          }
        }
        return INSTANCE;
    }
    /**
     * Connects to the server and does some commands.
     */
    public void connect(String server, int port, String login, String password) {
        try {
            log.debug("Initializing jsch server");
            jsch = new JSch();
            session = jsch.getSession(login, server, port);

            // Java 6 version
            session.setPassword(password.getBytes(Charset.forName("ISO-8859-1")));
            
            // Java 5 version
            // session.setPassword(password.getBytes("ISO-8859-1"));

            log.debug("Jsch set to StrictHostKeyChecking=no");
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            log.info("Connecting to " + server + ":" + port);
            session.connect();
            log.info("server Connected !");

            // Initializing a channel
            log.debug("Opening a channel ...");
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            log.debug("Channel sftp opened");


        } catch (JSchException e) {
            log.error("", e);
        }
    }

    /**
     * Uploads a file to the sftp server
     * @param sourceFile String path to sourceFile
     * @param destinationFile String path on the remote server
     * @throws InfinItException if connection and channel are not available or if an error occurs during upload.
     */
    public void uploadFile(String sourceFile, String destinationFile,String rootPath,String targetFilePath) throws InfinItException {
        if (channelSftp == null || session == null || !session.isConnected() || !channelSftp.isConnected()) {
            throw new InfinItException("Connection to server is closed. Open it first.");
        }
        try {
            log.debug("Uploading file to server");
            try {
                log.info("root path :{},target path:{}",rootPath,targetFilePath);
                channelSftp.cd(rootPath+targetFilePath);
            }catch (Exception e){
                log.info("没有目录，创建目录。root {},target {}",rootPath,targetFilePath);
                createDir(rootPath,targetFilePath);
            }
            log.info("创建目录成功，进入目录，src{},dest {}",sourceFile,rootPath+targetFilePath+destinationFile);
            channelSftp.put(sourceFile, rootPath+targetFilePath+destinationFile);
            log.info("file is Uploaded successfully.");
        } catch (SftpException e) {
            throw new InfinItException(e);
        }
    }

    /**
     * Retrieves a file from the sftp server
     * @param destinationFile String path to the remote file on the server
     * @param sourceFile String path on the local fileSystem
     * @throws InfinItException if connection and channel are not available or if an error occurs during download.
     */
    public void retrieveFile(String sourceFile, String destinationFile) throws InfinItException {
        if (channelSftp == null || session == null || !session.isConnected() || !channelSftp.isConnected()) {
            throw new InfinItException("Connection to server is closed. Open it first.");
        }

        try {
            log.debug("Downloading file to server");
            channelSftp.get(sourceFile, destinationFile);
            log.info("Download successfull.");
        } catch (SftpException e) {
            throw new InfinItException(e.getMessage(), e);
        }
    }
    
    public void disconnect() {
        if (channelSftp != null) {
            log.debug("Disconnecting sftp channel");
            channelSftp.disconnect();
        }
        if (channel != null) {
            log.debug("Disconnecting channel");
            channel.disconnect();
        }
        if (session != null) {
            log.debug("Disconnecting session");
            session.disconnect();
        }
    }

    public void createDir(String root,String targetPath) throws SftpException {
        channelSftp.cd(root);

        String[] folders = targetPath.split( "/" );
        for ( String folder : folders ) {
            if ( folder.length() > 0 ) {
                try {
                    channelSftp.cd( folder );
                }
                catch ( SftpException e ) {
                    channelSftp.mkdir( folder );
                    channelSftp.cd( folder );
                }
            }
        }
    }
}

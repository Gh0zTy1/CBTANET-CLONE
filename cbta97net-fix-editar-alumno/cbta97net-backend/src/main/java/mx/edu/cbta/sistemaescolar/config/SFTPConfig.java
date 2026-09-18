package mx.edu.cbta.sistemaescolar.config;

import org.springframework.beans.factory.annotation.Value;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;

@Configuration
public class SFTPConfig {

    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port:2222}")
    private int port;

    @Value("${sftp.user}")
    private String user;

    @Value("${sftp.password}")
    private String password;

    @Bean
    public SessionFactory<SftpClient.DirEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUser(user);
        factory.setPassword(password);
        factory.setAllowUnknownKeys(true);

        // Pool de sesiones (reduce a 5 o 1 si el servidor es muy estricto)
        CachingSessionFactory<SftpClient.DirEntry> cachingFactory = new CachingSessionFactory<>(factory, 5);
        cachingFactory.setSessionWaitTimeout(5000);
        return cachingFactory;
    }

    @Bean
    public SftpRemoteFileTemplate sftpRemoteFileTemplate(SessionFactory<SftpClient.DirEntry> sftpSessionFactory) {
        return new SftpRemoteFileTemplate(sftpSessionFactory);
    }
}

package org.ai.service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.ai.config.ApplicationConfig;
import org.ai.integration.types.HistoryFormat;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HistoryService {

    private final ApplicationConfig applicationConfig;


    public void writeToFileInContainer(String host, int port, String user, String password, String filePath, String content) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            String command = "echo \"" + content + "\" >> " + filePath;

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            channel.connect();
            channel.disconnect();
            session.disconnect();

            System.out.println("File written successfully in the container.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to write to file in container.");
        }
    }

//    public List<HistoryFormat> getHistory() {
//
//    }
}

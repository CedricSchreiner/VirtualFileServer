package services.classes;

import models.classes.Command;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

public class NotifyService extends Thread {
    private List<String> gli_ipList;
    private Command gob_command;

    public NotifyService(List<String> ili_ip , Command iob_command) {
        this.gli_ipList = ili_ip;
        this.gob_command = iob_command;
    }

    @Override
    public void run() {

        for (String lva_ip : gli_ipList) {
            try (Socket socket = new Socket(lva_ip, 32001)) {
                BufferedWriter lob_bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                lob_bufferedWriter.append(gob_command.toString());
                lob_bufferedWriter.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

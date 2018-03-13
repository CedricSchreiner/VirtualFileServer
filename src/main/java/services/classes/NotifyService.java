package services.classes;

import builder.ServiceObjectBuilder;
import cache.Cache;
import models.classes.Command;
import models.classes.SharedDirectory;
import models.classes.User;
import services.interfaces.SharedDirectoryService;
import services.interfaces.UserService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            System.out.println("Send " + gob_command.getCommand() + " to: " + lva_ip);
            try (Socket socket = new Socket(lva_ip, 32001)) {
                BufferedWriter lob_bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                lob_bufferedWriter.append(gob_command.toString());
                lob_bufferedWriter.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void notifyClients(String iva_relativeFilePath, User iob_user, String iva_command, int iva_directoryId, String iva_ipAddress, String... iar_information) {
        List<String> lli_ipList;
        Command lob_command;

        lli_ipList = collectIps(iva_relativeFilePath, iob_user, iva_directoryId, iva_ipAddress);
        lob_command = new Command(iva_relativeFilePath, iva_command, iva_directoryId, iar_information);
        NotifyService notifyService = new NotifyService(lli_ipList, lob_command);
        notifyService.setName("NotifyService");
        notifyService.start();
    }

    public static void notifyClients(String iva_relativeFilePath, String iva_command, int iva_directoryId, List<String> ili_ipList, String... iar_information) {
        Command lob_command;

        lob_command = new Command(iva_relativeFilePath, iva_command, iva_directoryId, iar_information);
        NotifyService notifyService = new NotifyService(ili_ipList, lob_command);
        notifyService.setName("NotifyService");
        notifyService.start();
    }

    public static void notifyClient(String iva_relativeFilePath, User iob_user, String iva_command, int iva_directoryId, String iva_ipAddress, String... iar_information) {
        List<String> lli_ipList = new ArrayList<>();
        Command lob_command;

        lli_ipList = addIpAddressFromUser(iob_user, iob_user, lli_ipList, iva_ipAddress);

        lob_command = new Command(iva_relativeFilePath, iva_command, iva_directoryId, iar_information);
        NotifyService notifyService = new NotifyService(lli_ipList, lob_command);
        notifyService.setName("NotifyService");
        notifyService.start();
    }

    public static List<String> collectIps(String iva_relativeFilePath, User iob_user, int iva_directoryId, String iva_ipAddress) {
        List<String> lli_ipList = new ArrayList<>();
        SharedDirectoryService lob_sharedDirectoryService;
        SharedDirectory lob_sharedDirectory;

        if (iva_directoryId < 0) {
            lli_ipList = addIpAddressFromUser(iob_user, iob_user, lli_ipList, iva_ipAddress);
        }

        if (iva_directoryId == 0) {
            lli_ipList = getPublicIpAddresses(iob_user, iva_ipAddress);
        }

        if (iva_directoryId > 0) {
            lob_sharedDirectoryService = ServiceObjectBuilder.getSharedDirectoryServiceObject();
            lob_sharedDirectory = lob_sharedDirectoryService.getSharedDirectoryById(iva_directoryId);

            if (lob_sharedDirectory.getMembers().size() > 0) {
                for (User lob_member : lob_sharedDirectory.getMembers()) {
                    lli_ipList = addIpAddressFromUser(iob_user, lob_member, lli_ipList, iva_ipAddress);
                }
            }

            lli_ipList = addIpAddressFromUser(iob_user, lob_sharedDirectory.getOwner(), lli_ipList, iva_ipAddress);
        }

        return lli_ipList.stream().distinct().collect(Collectors.toList());
    }

    private static List<String> getPublicIpAddresses(User iob_user, String iva_ipAddress) {
        UserService lob_userService;
        lob_userService = ServiceObjectBuilder.getUserServiceObject();
        List<String> rli_ipList = new ArrayList<>();

        for (User lob_user : lob_userService.getAllUser()) {
            rli_ipList = addIpAddressFromUser(iob_user, lob_user, rli_ipList, iva_ipAddress);
        }

        return addIpAddressFromUser(iob_user, iob_user, rli_ipList, iva_ipAddress);
    }

    private static List<String> addIpAddressFromUser(User iob_client, User iob_user, List<String> ili_list, String iva_ipAddress) {
        Cache lob_cache = Cache.getIpCache();
        List<String> lob_ipList;

        if (!iob_user.getEmail().equals(iob_client.getEmail())) {
            lob_ipList = lob_cache.get(iob_user.getEmail());

            if (lob_ipList != null) {
                ili_list.addAll(lob_cache.get(iob_user.getEmail()));
            }
        } else {
            lob_ipList = lob_cache.get(iob_user.getEmail());

            if (lob_ipList != null) {
                for (String lva_ip : lob_cache.get(iob_user.getEmail())) {
                    if (!lva_ip.equals(iva_ipAddress)) {
                        ili_list.add(lva_ip);
                    }
                }
            }
        }

        return ili_list;
    }
}

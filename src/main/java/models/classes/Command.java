package models.classes;
import utilities.Utils;

public class Command {
    private String gob_relativeFilePath;
    private String gva_command;
    private int gva_directoryId;
    private String[] gar_information;

    public Command(String lva_relativePath, String iva_command, int iva_directoryId, String... iar_information) {
        this.gob_relativeFilePath = lva_relativePath;
        this.gva_command = iva_command;
        this.gva_directoryId = iva_directoryId;

        if (iar_information.length != 0) {
            this.gar_information = iar_information;
        } else {
            this.gar_information = null;
        }
    }

    public String getRelativePath() {
        return this.gob_relativeFilePath;
    }

    public String getCommand() {
        return this.gva_command;
    }

    public String[] getInformation() {
        return this.gar_information;
    }

    public int getDirectoryId() {
        return this.gva_directoryId;
    }

    @Override
    public String toString() {
        StringBuilder rob_toString = new StringBuilder(gva_command + "_" + gob_relativeFilePath);

        if (gar_information != null) {
            for (String lva_information : gar_information) {
                rob_toString.append("_").append(lva_information);
            }
        }

        rob_toString.append("_").append(gva_directoryId);

        return rob_toString.toString();
    }
}

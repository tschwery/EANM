package ch.inf3.eveonline;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingFile extends File{
    public static final String CHAR_PREFIX = "core_char_";
    public static final String USER_PREFIX = "core_user_";
    public static final String PROFILE_PREFIX = "settings_";

    private long id;
    private String profile;
    private String datasource;

    public SettingFile(String s) {
        super(s);
        this.id = Long.valueOf("0" + this.getName().replaceAll("\\D", ""));

        String profileDirectoryName = this.getParentFile().getName();
        this.profile = profileDirectoryName.replace(PROFILE_PREFIX, "");
        
        String datasourceDirectoryName = this.getParentFile().getParentFile().getName();
        this.datasource = datasourceDirectoryName.substring(datasourceDirectoryName.lastIndexOf("_") + 1);
    }

    public SettingFile(File f) {
        this(f.getPath());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(32);

        if (!"Default".equals(profile)) {
            sb.append("(").append(profile).append(") ");
        }

        if (!"tranquility".equals(datasource)) {
            sb.append("(").append(datasource).append(") ");
        }

        sb.append(Long.toString(id));

        if (isCharFile()) {
            sb.append(" - ");
            sb.append(getCharName());
        }

        sb.append(" - ");
        sb.append("Last connection on ").append(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date(super.lastModified())));

        return sb.toString();
    }

    public long getId() {
        return id;
    }

    public String getCharName() {
        return CharacterInformation.getInfos(id, datasource).getName();
    }

    public boolean isCharFile() {
        return getName().startsWith(CHAR_PREFIX) && !getName().startsWith(CHAR_PREFIX + "_");
    }

    public boolean isUserFile() {
        return getName().startsWith(USER_PREFIX) && !getName().startsWith(USER_PREFIX + "_");
    }
}
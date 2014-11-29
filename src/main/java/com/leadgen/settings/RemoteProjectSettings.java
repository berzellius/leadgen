package com.leadgen.settings;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by berz on 22.11.14.
 */
public class RemoteProjectSettings extends CommonProjectSettings {
    @Override
    public String getPathToUploads() {
        return "/opt/tomcat/tomcat7/uploads/";
    }

    @Override
    public HashMap<String, String> getDatabaseConnectionConfig() {
        HashMap<String, String> dbConnect = new LinkedHashMap<String, String>();
        dbConnect.put("path","jdbc:postgresql://localhost:5432/leadgen");
        dbConnect.put("database", "postgres");
        dbConnect.put("password", "xrhysna5p78yig25and");

        return dbConnect;
    }
}

package com.applitools.eyes.visualGridClient.model;

import com.applitools.eyes.Logger;
import com.applitools.utils.GeneralUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;

public class FileDebugResourceWriter implements IDebugResourceWriter {

    private static final String DEFAULT_PREFIX = "resource_";
    private static final String DEFAULT_PATH = "";

    private Logger logger;
    private String path;
    private String prefix;
    private String filter;

    public FileDebugResourceWriter(Logger logger, String path, String prefix, String filter) {
        this.logger = logger;
        this.setPath(path);
        this.setPrefix(prefix);
        this.filter = filter;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? DEFAULT_PREFIX : prefix;
    }

    public void setPath(String path) {
        if (path != null) {
            path = path.endsWith("/") ? path : path + '/';
        } else {
            path = DEFAULT_PATH;
        }

        this.path = path;
    }

    @Override
    public void write(RGridResource resource) {
        String url = resource.getUrl();
        if (filter == null || filter.isEmpty() || url.toUpperCase().contains(filter.toUpperCase())) {
            try {
                String substring = url.substring(url.lastIndexOf("/") + 1);
                substring = substring.replaceAll("\\?","_");
                FileUtils.writeByteArrayToFile(new File(path + prefix + substring), ArrayUtils.toPrimitive(resource.getContent()));
            } catch (IOException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }
}

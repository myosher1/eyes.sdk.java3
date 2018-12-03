package com.applitools.eyes.visualGridClient.data;

import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RGridDom {

    public static final String CONTENT_TYPE = "x-applitools-html/cdt";
    @JsonIgnore
    private List domNodes = null;

    @JsonIgnore
    private Map<String, RGridResource> resources = null;

    @JsonIgnore
    private String sha256;

    @JsonInclude
    private String hashFormat = "sha256";

    public void addResource(RGridResource resource) {
        if (this.resources == null) {
            this.resources = new HashMap<>();
        }
        this.resources.put(resource.getUrl(), resource);
    }

    public List getDomNodes() {
        return domNodes;
    }

    public void setDomNodes(List domNodes) {
        this.domNodes = domNodes;
    }

    public Map<String, RGridResource> getResources() {
        return resources;
    }

    public void setResources(Map<String, RGridResource> resources) {
        this.resources = resources;
    }

    @JsonProperty("hash")
    public String getSha256() {
        if (this.sha256 == null) {
            sha256 = GeneralUtils.getSha256hash(ArrayUtils.toObject(getStringObjectMap().getBytes()));
        }
        return sha256;
    }

    private String getStringObjectMap() {

        Map<String, Object> map = new HashMap<>();
        map.put("domNodes", this.domNodes);
        map.put("resources", this.resources);


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);

        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getHashFormat() {
        return hashFormat;
    }

    public RGridResource asResource() {

        RGridResource gridResource = null;
            gridResource = new RGridResource(null, CONTENT_TYPE, ArrayUtils.toObject(getStringObjectMap().getBytes()));

        return gridResource;
    }
}

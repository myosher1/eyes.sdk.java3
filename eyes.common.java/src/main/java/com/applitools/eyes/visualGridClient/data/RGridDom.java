package com.applitools.eyes.visualGridClient.data;

import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

public class RGridDom {

    @JsonIgnore
    private String domNodes = null;

    @JsonIgnore
    private Map<String, RGridResource> resources = null;

    @JsonIgnore
    private String sha256;

    @JsonIgnore
    private String cdt = null;

    @JsonInclude
    private String hashFormat = "sha256";

    public void addResource(RGridResource resource) {
        if (this.resources == null) {
            this.resources = new HashMap<>();
        }
        this.resources.put(resource.getUrl(), resource);
    }

    public String getDomNodes() {
        return domNodes;
    }

    public void setDomNodes(String domNodes) {
        this.domNodes = domNodes;
    }

    public Map<String, RGridResource> getResources() {
        return resources;
    }

    public void setResources(Map<String, RGridResource> resources) {
        this.resources = resources;
    }

    @JsonProperty("hash")
    public String getSha256() throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("cdt", cdt);
        map.put("resources", this.resources);
        if (this.sha256 == null) {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);

            sha256 = objectMapper.writeValueAsString(map);
            sha256 =  GeneralUtils.getSha256hash(ArrayUtils.toObject(sha256.getBytes()));
        }
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getCdt() {
        return cdt;
    }

    public void setCdt(String cdt) {
        this.cdt = cdt;
    }

    public String getHashFormat() {
        return hashFormat;
    }
}

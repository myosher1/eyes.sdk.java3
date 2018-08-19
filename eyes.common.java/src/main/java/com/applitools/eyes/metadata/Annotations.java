
package com.applitools.eyes.metadata;

import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.RegionF;
import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "floating",
    "ignore",
    "strict",
    "content",
    "layout"
})
@JsonIgnoreProperties({"remarks", "mismatching"})
public class Annotations {

    @JsonProperty("floating")
    private FloatingMatchSettings[] floating = null;
    @JsonProperty("ignore")
    private RegionF[] ignore = null;
    @JsonProperty("strict")
    private RegionF[] strict = null;
    @JsonProperty("content")
    private RegionF[] content = null;
    @JsonProperty("layout")
    private RegionF[] layout = null;

    @JsonProperty("floating")
    public FloatingMatchSettings[] getFloating() {
        return floating;
    }

    @JsonProperty("floating")
    public void setFloating(FloatingMatchSettings[] floating) {
        this.floating = floating;
    }

    @JsonProperty("ignore")
    public RegionF[] getIgnore() {
        return ignore;
    }

    @JsonProperty("ignore")
    public void setIgnore(RegionF[] ignore) {
        this.ignore = ignore;
    }

    @JsonProperty("strict")
    public RegionF[] getStrict() {
        return strict;
    }

    @JsonProperty("strict")
    public void setStrict(RegionF[] strict) {
        this.strict = strict;
    }

    @JsonProperty("content")
    public RegionF[] getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(RegionF[] content) {
        this.content = content;
    }

    @JsonProperty("layout")
    public RegionF[] getLayout() {
        return layout;
    }

    @JsonProperty("layout")
    public void setLayout(RegionF[] layout) {
        this.layout = layout;
    }
}

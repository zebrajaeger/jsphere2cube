package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.zebrajaeger.sphere2cube.Stringable;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ViewerConfigBase extends Stringable {
    @JsonProperty
    private boolean enabled = true;
    @JsonProperty("title")
    private String pageTitle;
    @JsonProperty
    private String target;
    @JsonProperty("css")
    private List<URL> cssFiles = new ArrayList<>();
    @JsonProperty("js")
    private List<URL> jsFiles = new ArrayList<>();

    public ViewerConfigBase(String pageTitle, String target) {
        this.pageTitle = pageTitle;
        this.target = target;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<URL> getCssFiles() {
        return cssFiles;
    }

    public void setCssFiles(List<URL> cssFiles) {
        this.cssFiles = cssFiles;
    }

    public List<URL> getJsFiles() {
        return jsFiles;
    }

    public void setJsFiles(List<URL> jsFiles) {
        this.jsFiles = jsFiles;
    }

}

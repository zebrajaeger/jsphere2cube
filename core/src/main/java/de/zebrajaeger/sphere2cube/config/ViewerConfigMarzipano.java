package de.zebrajaeger.sphere2cube.config;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ViewerConfigMarzipano {
    private boolean enabled = true;
    private String pageTitle = "cube2sphere - pannellum";
    private String target = "index.p.html";

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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

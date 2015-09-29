package com.janssen.connectforlife.callflows.contract;

import java.util.Objects;

/**
 * Renderer Contract, maps directly to the Renderer domain
 *
 * @author bramak09
 * @see com.janssen.connectforlife.callflows.domain.Renderer
 */
public class RendererContract {

    /**
     * Name of the renderer, Eg: VoiceXML, Kookoo, CCXML, etc
     */
    private String name;

    /**
     * The Mime Type to use for this renderer sent in HTTP responses
     */
    private String mimeType;

    /**
     * A template string that can be interpreted in client-side Javascript land to generate the output of each node
     * The format of this template is specific to the javascript library that is used
     */
    private String template;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RendererContract)) {
            return false;
        }
        final RendererContract other = (RendererContract) o;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}


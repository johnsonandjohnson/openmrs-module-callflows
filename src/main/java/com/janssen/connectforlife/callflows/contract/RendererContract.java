package com.janssen.connectforlife.callflows.contract;

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
}


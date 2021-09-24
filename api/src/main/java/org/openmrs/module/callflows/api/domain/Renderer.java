package org.openmrs.module.callflows.api.domain;

/**
 * Renderer configuration for a output format like VoiceXML, kookoo, etc
 * <p>
 * A renderer has a template that generates the relevant format (voice-xml, kookoo, proprietary) based on the flow data structure
 * The renderers are used on the client-side to generate the output format files
 * The use of client-side renderers are in order to support complex use-cases where manual editing of the output format after render
 * as well as pasting wholly adhoc output files can be performed
 *
 * @author bramak09
 */
public class Renderer {

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

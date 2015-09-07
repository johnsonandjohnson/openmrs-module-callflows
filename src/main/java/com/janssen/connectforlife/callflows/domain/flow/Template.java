package com.janssen.connectforlife.callflows.domain.flow;

/**
 * The template class
 *
 * @author bramak09
 */
public class Template {

    /**
     * The template's content. This is the content for a velocity template and is interpreted before the content can be retrieved
     */
    private String content;

    /**
     * Some template contents are auto-generated. Some are edited manually. This field captures whether a call flow designer has
     * manually edited the content of this template
     */
    private boolean isDirty;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setIsDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }
}

package org.openmrs.module.callflows.api.domain.flow;

/**
 * A text element
 *
 * @author bramak09
 */
public class TextElement extends Element {

    /**
     * The actual text of the element. can be used for labels, prompts etc
     */
    private String txt;

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    @Override
    public String toString() {
        return "TextElement{" +
                "txt='" + txt + '\'' +
                '}';
    }
}

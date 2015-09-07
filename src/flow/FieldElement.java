package com.janssen.connectforlife.callflows.domain.flow;

/**
 * FieldElement class
 *
 * @author bramak09
 * @see com.janssen.connectforlife.callflows.domain.flow.Element
 */
public class FieldElement extends Element {

    /**
     * The type of the field
     */
    private String fieldType;

    /**
     * A grammar associated with this field
     */
    private String grammar;

    /**
     * Each field can have a prompt or label text associated with it
     */
    private String txt;

    /**
     * Whether during input of this field, a user has to hear the whole of txt or can barge in
     */
    private boolean bargeIn;

    /**
     * The text to read out if there was no input within a acceptable period of time
     */
    private String noInput;

    /**
     * The text to read out if there was no match w.r.t the grammar
     */
    private String noMatch;

    /**
     * Whether re-prompt should be supported in case we encounter a no input or a no match event
     */
    private boolean reprompt;

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getGrammar() {
        return grammar;
    }

    public void setGrammar(String grammar) {
        this.grammar = grammar;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public boolean isBargeIn() {
        return bargeIn;
    }

    public void setBargeIn(boolean bargeIn) {
        this.bargeIn = bargeIn;
    }

    public String getNoInput() {
        return noInput;
    }

    public void setNoInput(String noInput) {
        this.noInput = noInput;
    }

    public String getNoMatch() {
        return noMatch;
    }

    public void setNoMatch(String noMatch) {
        this.noMatch = noMatch;
    }

    public boolean isReprompt() {
        return reprompt;
    }

    public void setReprompt(boolean reprompt) {
        this.reprompt = reprompt;
    }
}

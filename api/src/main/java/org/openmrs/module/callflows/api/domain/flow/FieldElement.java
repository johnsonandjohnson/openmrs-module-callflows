package org.openmrs.module.callflows.api.domain.flow;

/**
 * FieldElement class
 *
 * @author bramak09
 * @see org.openmrs.module.callflows.api.domain.flow.Element
 */
public class FieldElement extends Element {

    /**
     * The type of the field
     */
    private String fieldType;

    /**
     * Any meta of the field, like for digits, a minLength and a maxLength or a length can be supported
     */
    private String fieldMeta;

    /**
     * Each field can have a prompt or label text associated with it
     */
    private String txt;

    /**
     * Whether during input of this field, a user has to hear the whole of txt or can barge in
     */
    private boolean bargeIn;

    /**
     * Whether dual tone multi frequency (DTMF) input is enabled for this field
     * This is often used in conjunction with the other mode - voice
     */
    private boolean dtmf;

    /**
     * Whether voice mode is enabled for this field
     * This is often used in conjunction with the other mode - DTMF
     */
    private boolean voice;

    /**
     * The text to read out if there was no input within a acceptable period of time
     */
    private String noInput;

    /**
     * The text to read out if there was no match w.r.t the grammar
     */
    private String noMatch;

    /**
     * The goodBye message. If set control terminates if noInput and noMatch are encountered
     */
    private String goodBye;

    /**
     * Number of times reprompt is supported
     */
    private int reprompt;

    /**
     * DTMF Grammar, a comma separated list of allowed key presses for this field
     */
    private String dtmfGrammar;

    /**
     * Voice Grammar, a comma seperated list of allowed utterances that can be used for this field
     */
    private String voiceGrammar;

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
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

    public boolean isDtmf() {
        return dtmf;
    }

    public void setDtmf(boolean dtmf) {
        this.dtmf = dtmf;
    }

    public boolean isVoice() {
        return voice;
    }

    public void setVoice(boolean voice) {
        this.voice = voice;
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

    public String getGoodBye() {
        return goodBye;
    }

    public void setGoodBye(String goodBye) {
        this.goodBye = goodBye;
    }

    public int getReprompt() {
        return reprompt;
    }

    public void setReprompt(int reprompt) {
        this.reprompt = reprompt;
    }

    public String getFieldMeta() {
        return fieldMeta;
    }

    public void setFieldMeta(String fieldMeta) {
        this.fieldMeta = fieldMeta;
    }

    public String getDtmfGrammar() {
        return dtmfGrammar;
    }

    public void setDtmfGrammar(String dtmfGrammar) {
        this.dtmfGrammar = dtmfGrammar;
    }

    public String getVoiceGrammar() {
        return voiceGrammar;
    }

    public void setVoiceGrammar(String voiceGrammar) {
        this.voiceGrammar = voiceGrammar;
    }
}

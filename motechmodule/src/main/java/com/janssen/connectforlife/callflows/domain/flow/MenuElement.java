package com.janssen.connectforlife.callflows.domain.flow;

/**
 * Menu element identifying for which input, where to jump to
 *
 * @author bramak09
 */
public class MenuElement extends Element {

    /**
     * The input of the menu
     */
    private String input;

    /**
     * The next jump point
     */
    private String next;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}

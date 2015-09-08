package com.janssen.connectforlife.callflows;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import java.io.IOException;

/**
 * All unit tests must extend this
 *
 * @author bramak09
 */
public class BaseTest {


    @Rule
    // Warning from check style need to be ignored as the Junit require ExpectedException to be public
    // CHECKSTYLE IGNORE check FOR NEXT 1 LINES
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Use this method at the beginning of your test case in your //Given part
     * to indicate that you expect the tested method to throw an Exception.
     * <p/>
     * <b>Important :</b> The @Test annotation should NOT contain the expected property for the exception
     *
     * @param clazz the class of the expected exception.
     */
    protected <E extends Exception> void expectException(Class<E> clazz) {
        expectedException.expect(clazz);
    }

    /**
     * Converts the provided object into a JSON string
     * This is used in controller unit tests, but is generic to be used elsewhere as well
     *
     * @param obj to convert
     * @return a json string representation of the object
     * @throws IOException if not able to convert
     */
    protected String json(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    /**
     * Converts the provided object into a UTF-8 encoded JSON byte array
     * This is used in controller unit tests, but is generic to be used elsewhere as well
     *
     * @param obj to convert
     * @return a json string representation of the object
     * @throws IOException if not able to convert
     */
    protected byte[] jsonBytes(Object obj) throws IOException {
        return new ObjectMapper().writeValueAsString(obj).getBytes("UTF-8");
    }
}

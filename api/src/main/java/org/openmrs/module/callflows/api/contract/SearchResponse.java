package org.openmrs.module.callflows.api.contract;

import java.util.List;

/**
 * Generic Search Response
 *
 * @author bramak09
 */
public class SearchResponse<T> {

    /**
     * The list of response objects
     */
    private List<T> results;

    /**
     * Indicates if there are more results
     */
    private boolean more;

    public SearchResponse(List<T> results) {
        this.results = results;
        this.more = false;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }
}

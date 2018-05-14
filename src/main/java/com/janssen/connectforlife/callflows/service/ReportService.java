package com.janssen.connectforlife.callflows.service;

import com.janssen.connectforlife.callflows.domain.Call;

import org.motechproject.mds.query.QueryParams;

import java.util.List;

/**
 * Service Interface that define APIs to retrieve and read call Details
 *
 * @author hsingh36
 */
public interface ReportService {
    /**
     * Fetch call details based on query params by utilizing motech data service 'retrieveAll' function
     * Limitation of 'retrieveAll()' - Throws heap memory issue for 25000 records, but works fine with 20000 records at a time.
     *
     * @param queryParams
     * @return list of calls
     */
    List<Call> findCalls(QueryParams queryParams);

    /**
     * Fetch the count of call records present in db
     *
     * @return total number of call records present in db
     */
    long retrieveCount();
}

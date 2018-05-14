package com.janssen.connectforlife.callflows.service.impl;

import com.janssen.connectforlife.callflows.domain.Call;
import com.janssen.connectforlife.callflows.repository.CallDataService;
import com.janssen.connectforlife.callflows.service.ReportService;

import org.motechproject.mds.query.QueryParams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * ReportService Implementation class
 *
 * @author hsingh36
 */
@Service("reportService")
public class ReportServiceImpl implements ReportService {

    @Autowired
    private CallDataService callDataService;

    @Override
    public List<Call> findCalls(QueryParams queryParams) {
        return callDataService.retrieveAll(queryParams);
    }

    @Override
    public long retrieveCount() {
        return callDataService.count();
    }
}

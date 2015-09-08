package com.janssen.connectforlife.callflows.service.impl;

import com.janssen.connectforlife.callflows.domain.CallFlow;
import com.janssen.connectforlife.callflows.exception.CallFlowAlreadyExistsException;
import com.janssen.connectforlife.callflows.repository.CallFlowDataService;
import com.janssen.connectforlife.callflows.service.CallFlowService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.regex.Pattern;

/**
 * Call Flow service implementation
 *
 * @author bramak09
 */
@Service("callFlowService")
public class CallFlowServiceImpl implements CallFlowService {

    private static final Pattern ALPHA_NUMERIC = Pattern.compile("^[a-zA-Z0-9]+$");

    @Autowired
    private CallFlowDataService callFlowDataService;

    @Override
    @Transactional
    public CallFlow create(CallFlow callflow) throws CallFlowAlreadyExistsException {
        if (StringUtils.isEmpty(callflow.getName()) || !ALPHA_NUMERIC.matcher(callflow.getName()).matches()) {
            throw new IllegalArgumentException(
                    "Callflow name is required and must contain only alphanumeric characters : " + callflow.getName());
        }
        // check for duplicates in database
        if (null != callFlowDataService.findByName(callflow.getName())) {
            throw new CallFlowAlreadyExistsException("CallFlow already exists! : " + callflow.getName());
        }
        return callFlowDataService.create(callflow);
    }

}


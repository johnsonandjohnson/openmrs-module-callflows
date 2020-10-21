package org.openmrs.module.callflows.api.helper;

import org.openmrs.module.callflows.Constants;
import org.openmrs.module.callflows.api.contract.ConfigContract;
import org.openmrs.module.callflows.api.domain.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration Helper
 *
 * @author bramak09
 */
public final class ConfigHelper {

    private static final String EMPTY = "";

    // private, hence can't be constructed
    private ConfigHelper() {
    }

    public static List<Config> createConfigs() {

        // configuration
        List<Config> configs = new ArrayList<>();

        Config voxeo = new Config();
        voxeo.setName(Constants.CONFIG_VOXEO);
        voxeo.setOutgoingCallUriTemplate(Constants.CONFIG_VOXEO_OUT_TEMPLATE);
        voxeo.setOutgoingCallMethod(Constants.CONFIG_VOXEO_METHOD);
        voxeo.setHasAuthRequired(Boolean.FALSE);
        voxeo.setAuthToken(EMPTY);
        voxeo.setOutgoingCallPostHeadersMap(new HashMap<String, String>());
        voxeo.setOutgoingCallPostParams("");
        voxeo.setOutboundCallLimit(Constants.CONFIG_VOXEO_OUTBOUND_CALL_LIMIT);
        voxeo.setOutboundCallRetryAttempts(Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_ATTEMPTS);
        voxeo.setOutboundCallRetrySeconds(Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_SECONDS);
        voxeo.setCallAllowed(Constants.CONFIG_VOXEO_CAN_PLACE_OUTBOUND_CALL);
        voxeo.setServicesMap(GenericHelper.buildServicesMap());
        voxeo.setTestUsersMap(
                GenericHelper.buildTestUsersMap(Constants.CONFIG_VOXEO_USER, Constants.CONFIG_VOXEO_USER_URL));

        Config yo = new Config();
        yo.setName(Constants.CONFIG_YO);
        yo.setOutgoingCallUriTemplate(Constants.CONFIG_YO_OUT_TEMPLATE);
        yo.setOutgoingCallMethod(Constants.CONFIG_YO_METHOD);
        yo.setHasAuthRequired(Boolean.FALSE);
        yo.setAuthToken("");
        yo.setOutgoingCallPostHeadersMap(new HashMap<String, String>());
        yo.setOutgoingCallPostParams("");
        yo.setOutboundCallLimit(Constants.CONFIG_YO_OUTBOUND_CALL_LIMIT);
        yo.setOutboundCallRetryAttempts(Constants.CONFIG_YO_OUTBOUND_CALL_RETRY_ATTEMPTS);
        yo.setOutboundCallRetrySeconds(Constants.CONFIG_YO_OUTBOUND_CALL_RETRY_SECONDS);
        yo.setCallAllowed(Constants.CONFIG_YO_CAN_PLACE_OUTBOUND_CALL);
        yo.setServicesMap(GenericHelper.buildServicesMap());
        yo.setTestUsersMap(GenericHelper.buildTestUsersMap(Constants.CONFIG_YO_USER, Constants.CONFIG_YO_USER_URL));

        Config imiMobile = new Config();
        imiMobile.setName(Constants.CONFIG_IMI_MOBILE);
        imiMobile.setOutgoingCallUriTemplate(Constants.CONFIG_IMI_OUT_TEMPLATE);
        imiMobile.setOutgoingCallMethod(Constants.CONFIG_IMI_METHOD);
        imiMobile.setAuthToken("");

        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Key", "ccb2b7b2-3205-44ba-9e06-4844be3c298f");
        headersMap.put("Content-Type", "application/x-www-form-urlencoded");
        imiMobile.setOutgoingCallPostHeadersMap(headersMap);
        imiMobile.setOutgoingCallPostParams("address=[phone]" +
                "&mode=Callflow" +
                "&schedule_datetime=" +
                "&sendername=4039996210" +
                "&patternId=&callflow_id=3894" +
                "&menu=/openhouse/app/dvp/callflow/callflow_3894/menu_text/funct_cfldemo.txt" +
                "&externalHeaders=x-imi-ivrs-jumpTo:[internal.jumpTo];x-imi-ivrs-callId:[internal.callId]");
        imiMobile.setOutboundCallLimit(Constants.CONFIG_IMI_OUTBOUND_CALL_LIMIT);
        imiMobile.setOutboundCallRetryAttempts(Constants.CONFIG_IMI_OUTBOUND_CALL_RETRY_ATTEMPTS);
        imiMobile.setOutboundCallRetrySeconds(Constants.CONFIG_IMI_OUTBOUND_CALL_RETRY_SECONDS);
        imiMobile.setCallAllowed(Constants.CONFIG_IMI_CAN_PLACE_OUTBOUND_CALL);
        imiMobile.setServicesMap(GenericHelper.buildServicesMap());
        imiMobile.setTestUsersMap(
                GenericHelper.buildTestUsersMap(Constants.CONFIG_IMI_USER, Constants.CONFIG_IMI_USER_URL));

        configs.add(voxeo);
        configs.add(yo);
        configs.add(imiMobile);
        return configs;
    }

    public static List<ConfigContract> createConfigContracts() {

        // configuration
        List<ConfigContract> contracts = new ArrayList<>();

        ConfigContract voxeo = new ConfigContract();
        voxeo.setName(Constants.CONFIG_VOXEO);
        voxeo.setOutgoingCallUriTemplate(Constants.CONFIG_VOXEO_OUT_TEMPLATE);
        voxeo.setOutgoingCallMethod(Constants.CONFIG_VOXEO_METHOD);
        voxeo.setHasAuthRequired(Boolean.FALSE);
        voxeo.setOutboundCallLimit(Constants.CONFIG_VOXEO_OUTBOUND_CALL_LIMIT);
        voxeo.setOutboundCallRetryAttempts(Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_ATTEMPTS);
        voxeo.setOutboundCallRetrySeconds(Constants.CONFIG_VOXEO_OUTBOUND_CALL_RETRY_SECONDS);
        voxeo.setOutgoingCallPostHeadersMap(new HashMap<String, String>());
        voxeo.setOutgoingCallPostParams("");
        voxeo.setCallAllowed(Constants.CONFIG_VOXEO_CAN_PLACE_OUTBOUND_CALL);
        voxeo.setServicesMap(GenericHelper.buildServicesMap());
        voxeo.setTestUsersMap(
                GenericHelper.buildTestUsersMap(Constants.CONFIG_VOXEO_USER, Constants.CONFIG_VOXEO_USER_URL));

        ConfigContract yo = new ConfigContract();
        yo.setName(Constants.CONFIG_YO);
        yo.setOutgoingCallUriTemplate(Constants.CONFIG_YO_OUT_TEMPLATE);
        yo.setOutgoingCallMethod(Constants.CONFIG_YO_METHOD);
        yo.setHasAuthRequired(Boolean.FALSE);
        yo.setOutboundCallLimit(Constants.CONFIG_YO_OUTBOUND_CALL_LIMIT);
        yo.setOutboundCallRetryAttempts(Constants.CONFIG_YO_OUTBOUND_CALL_RETRY_ATTEMPTS);
        yo.setOutboundCallRetrySeconds(Constants.CONFIG_YO_OUTBOUND_CALL_RETRY_SECONDS);
        yo.setOutgoingCallPostHeadersMap(new HashMap<String, String>());
        yo.setOutgoingCallPostParams("");
        yo.setCallAllowed(Constants.CONFIG_YO_CAN_PLACE_OUTBOUND_CALL);
        yo.setServicesMap(GenericHelper.buildServicesMap());
        yo.setTestUsersMap(GenericHelper.buildTestUsersMap(Constants.CONFIG_YO_USER, Constants.CONFIG_YO_USER_URL));

        ConfigContract imiMobile = new ConfigContract();
        imiMobile.setName(Constants.CONFIG_IMI_MOBILE);
        imiMobile.setOutgoingCallUriTemplate(Constants.CONFIG_IMI_OUT_TEMPLATE);
        imiMobile.setOutgoingCallMethod(Constants.CONFIG_IMI_METHOD);
        imiMobile.setHasAuthRequired(Boolean.FALSE);
        imiMobile.setOutboundCallLimit(Constants.CONFIG_IMI_OUTBOUND_CALL_LIMIT);
        imiMobile.setOutboundCallRetryAttempts(Constants.CONFIG_IMI_OUTBOUND_CALL_RETRY_ATTEMPTS);
        imiMobile.setOutboundCallRetrySeconds(Constants.CONFIG_IMI_OUTBOUND_CALL_RETRY_SECONDS);
        imiMobile.setCallAllowed(Constants.CONFIG_IMI_CAN_PLACE_OUTBOUND_CALL);
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Key", "ccb2b7b2-3205-44ba-9e06-4844be3c298f");
        headersMap.put("Content-Type", "application/x-www-form-urlencoded");
        imiMobile.setOutgoingCallPostHeadersMap(headersMap);
        imiMobile.setOutgoingCallPostParams("address=[phone]" +
                "&mode=Callflow" +
                "&schedule_datetime=" +
                "&sendername=4039996210" +
                "&patternId=&callflow_id=3894" +
                "&menu=/openhouse/app/dvp/callflow/callflow_3894/menu_text/funct_cfldemo.txt" +
                "&externalHeaders=x-imi-ivrs-jumpTo:[internal.jumpTo];x-imi-ivrs-callId:[internal.callId]");
        imiMobile.setServicesMap(GenericHelper.buildServicesMap());
        imiMobile.setTestUsersMap(
                GenericHelper.buildTestUsersMap(Constants.CONFIG_IMI_USER, Constants.CONFIG_IMI_USER_URL));
        contracts.add(voxeo);
        contracts.add(yo);
        contracts.add(imiMobile);
        return contracts;
    }

}

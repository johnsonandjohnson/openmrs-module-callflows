package org.openmrs.module.callflows.api.util;

public final class CallFlowTaskUtil {

    private static final String SUFFIX_SMS_TASK = "-runOnce";

    public static String generateTaskName(String subject, String jobId) {
        return String.format("%s-%s%s", subject, jobId, SUFFIX_SMS_TASK);
    }

    private CallFlowTaskUtil() {
    }
}

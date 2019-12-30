package org.openmrs.module.callflows.api.evaluation;

import org.openmrs.module.DaemonToken;

import java.io.IOException;

public interface EvaluationCommand {

    void setDaemonToken(DaemonToken daemonToken);

    DaemonToken getDaemonToken();

    String execute(EvaluationContext context) throws IOException;
}

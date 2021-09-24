package org.openmrs.module.callflows.api.evaluation;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.DaemonToken;

import java.io.IOException;

public interface EvaluationCommand extends OpenmrsService {

    void setDaemonToken(DaemonToken daemonToken);

    DaemonToken getDaemonToken();

    String execute(EvaluationContext context) throws IOException;
}

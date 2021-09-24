package org.openmrs.module.callflows.api.evaluation;

import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.ModuleFactory;

import java.io.IOException;

public class EvalNodeDaemon extends Daemon {

    public static String evalNode(EvaluationCommand command, EvaluationContext context) throws IOException {
        if (!ModuleFactory.isTokenValid(command.getDaemonToken())) {
            throw new ContextAuthenticationException("Invalid token " + command.getDaemonToken());
        }
        String result = null;
        try {
            isDaemonThread.set(true);
            result = command.execute(context);
        } finally {
            isDaemonThread.set(false);
        }
        return result;
    }
}

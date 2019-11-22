package org.openmrs.module.callflows.api.validate.validator;

import static org.openmrs.module.callflows.ValidationMessages.CALL_FLOW_NAME_BLANK_OR_NON_ALFA_NUMERIC;
import static org.openmrs.module.callflows.ValidationMessages.CALL_FLOW_NAME_DUPLICATION;
import static org.openmrs.module.callflows.ValidationMessages.CALL_FLOW_NODES_NULL;
import static org.openmrs.module.callflows.ValidationMessages.CALL_FLOW_NODE_NAME_BLANK_OR_NON_ALFA_NUMERIC;

import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.callflows.api.dao.CallFlowDao;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;
import org.openmrs.module.callflows.api.domain.flow.UserNode;
import org.openmrs.module.callflows.api.service.FlowService;
import org.openmrs.module.callflows.api.validate.annotation.ValidCallFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallFlowValidator implements ConstraintValidator<ValidCallFlow, CallFlow> {

    private static final String NAME_PATH = "callflow.name";
    private static final String RAW_NAME_PATH = "callflow.raw.name";
    private static final String RAW_NODES_PATH = "callflow.raw.nodes";
    private static final Pattern ALPHA_NUMERIC = Pattern.compile("^[a-zA-Z0-9]+$");

    @Autowired
    private CallFlowDao callFlowDao;

    @Autowired
    private FlowService flowService;

    /**
     * Validates the name uniqueness.
     *
     * @param callflow object which wraps configs to validate
     * @param ctx      validation context
     * @return the validation result
     */
    @Override
    public boolean isValid(CallFlow callflow, ConstraintValidatorContext ctx) {
        boolean isValid = true;

        isValid = isValid && isNameValid(callflow, ctx);

        isValid = isValid && isRawFlowValid(callflow, ctx);

        return isValid;
    }

    @Override
    public void initialize(ValidCallFlow parameters) {

    }

    private boolean isNameValid(CallFlow callflow, ConstraintValidatorContext ctx) {
        boolean isValid = true;
        if (StringUtils.isEmpty(callflow.getName()) || !ALPHA_NUMERIC.matcher(callflow.getName()).matches()) {
            addErrorToContext(ctx, NAME_PATH, CALL_FLOW_NAME_BLANK_OR_NON_ALFA_NUMERIC);
            isValid = false;
        }
        CallFlow existing = callFlowDao.findByName(callflow.getName());
        boolean nameAlreadyExists = existing != null;
        boolean isTheSameInstance = nameAlreadyExists && existing.getId() == callflow.getId();
        boolean isNew = callflow.getId() == null;
        if (nameAlreadyExists) {
            if (isNew || !isTheSameInstance) {
                addErrorToContext(ctx, NAME_PATH, String.format(CALL_FLOW_NAME_DUPLICATION,
                    callflow.getName()));
                isValid = false;
            }
        }
        return isValid;
    }

    private boolean isRawFlowValid(CallFlow callflow, ConstraintValidatorContext ctx) {
        boolean isValid = true;

        Flow flow = flowService.loadByJson(callflow.getRaw());

        if (StringUtils.isEmpty(flow.getName()) || !ALPHA_NUMERIC.matcher(flow.getName()).matches()) {
            addErrorToContext(ctx, RAW_NAME_PATH, String.format(CALL_FLOW_NAME_DUPLICATION,
                flow.getName()));
            isValid = false;
        }

        if (flow.getNodes() == null) {
            addErrorToContext(ctx, RAW_NODES_PATH, CALL_FLOW_NODES_NULL);
            isValid = false;
        } else {
            for (Node node : flow.getNodes()) {
                if (node instanceof UserNode && StringUtils.isEmpty(node.getStep())) {
                    addErrorToContext(ctx, RAW_NODES_PATH, CALL_FLOW_NODE_NAME_BLANK_OR_NON_ALFA_NUMERIC);
                    isValid = false;
                }
            }
        }
        return isValid;
    }

    private void addErrorToContext(ConstraintValidatorContext context, String path,
                                   String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
            .addNode(path)
            .addConstraintViolation();
    }
}

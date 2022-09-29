/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.validate.validator;

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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import static org.openmrs.module.callflows.ValidationMessageConstants.CALL_FLOW_NAME_BLANK_OR_NON_ALFA_NUMERIC;
import static org.openmrs.module.callflows.ValidationMessageConstants.CALL_FLOW_NAME_DUPLICATION;
import static org.openmrs.module.callflows.ValidationMessageConstants.CALL_FLOW_NODES_NULL;
import static org.openmrs.module.callflows.ValidationMessageConstants.CALL_FLOW_NODE_NAME_BLANK_OR_NON_ALFA_NUMERIC;

@Component
public class CallFlowValidator implements ConstraintValidator<ValidCallFlow, CallFlow> {

    private static final String NAME_PATH = "callflow.name";
    private static final String RAW_PATH = "callflow.raw";
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
        ctx.disableDefaultConstraintViolation();

        boolean isValid = true;

        isValid = isValid && isNameValid(callflow, ctx);

        isValid = isValid && isRawFlowValid(callflow, ctx);

        return isValid;
    }

    @Override
    public void initialize(ValidCallFlow parameters) {
        // shouldn't any specific action be performed
    }

    /**
     * Validates if the name is Valid or not.
     *
     * @param callflow object which wraps configs to validate
     * @param ctx      validation context
     * @return the validation result
     */
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
        if (nameAlreadyExists && (isNew || !isTheSameInstance)) {
            addErrorToContext(ctx, NAME_PATH, String.format(CALL_FLOW_NAME_DUPLICATION,
                    callflow.getName()));
            isValid = false;
        }
        return isValid;
    }

    /**
     * Validates if the raw name is Valid or not.
     *
     * @param callflow object which wraps configs to validate
     * @param ctx      validation context
     * @return the validation result
     */
    private boolean isRawFlowValid(CallFlow callflow, ConstraintValidatorContext ctx) {
        boolean isValid = true;
        Flow flow;
        try {
            flow = flowService.loadByJson(callflow.getRaw());
        } catch (Exception ex) {
            addErrorToContext(ctx, RAW_PATH, ex.getMessage());
            return false;
        }

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
        context.buildConstraintViolationWithTemplate(message)
                .addNode(path)
                .addConstraintViolation();
    }
}

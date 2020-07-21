/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

// Messages displayed on the frontend

export const FIELD_REQUIRED = 'This field is required';

// Breadcrumbs
export const PROVIDERS_BREADCRUMB = 'Providers';
export const RENDERERS_BREADCRUMB = 'Renderers';
export const SYSTEM_ADMINISTRATION_BREADCRUMB = 'System Administration';
export const MODULE_NAME = 'Call flows';

// Generic messages
export const GENERIC_FAILURE = 'An error occurred.';
export const GENERIC_PROCESSING = 'Processing...';
export const GENERIC_SUCCESS = 'Success.';
export const GENERIC_INVALID_FORM = 'Form is invalid. Check fields and send it again.';
export const GENERIC_LOADING = 'Loading...'

// Designer flow test
export const DESIGNER_FLOW_TEST_SECTION_DESCRIPTION='The data you provide here are not saved in the Call Flow and they are only used for testing.'
export const DESIGNER_TEST_CALL_LABEL = 'Initiate Call';
export const DESIGNER_TEST_CALL_GENERAL_DESCRIPTION = 'Start a call and test it end-to-end using configured provider.';
export const DESIGNER_TEST_CALL_CONFIGURATION_LABEL = 'Configuration';
export const DESIGNER_TEST_CALL_EXTENSION_LABEL = 'Extension';
export const DESIGNER_TEST_CALL_PHONE_NUMBER_LABEL = 'Phone Number';
export const DESIGNER_TEST_CALL_INITIATION_SUCCESS = 'Successfully initiated the test call.';
export const DESIGNER_TEST_CALL_INITIATION_FAILURE = 'The test call initiation failed.';
export const DESIGNER_TEST_FLOW_LABEL = 'Test Call Flow';
export const DESIGNER_TEST_FLOW_GENERAL_DESCRIPTION = 'Test the callflow without using configured provider.';
export const DESIGNER_FLOW_UPDATE_SUCCESS = 'Successfully updated the flow.';
export const DESIGNER_FLOW_UPDATE_FAILURE = 'Failed to update the flow.';
export const DESIGNER_FLOW_CREATE_SUCCESS = 'Successfully created the flow.';
export const DESIGNER_FLOW_CREATE_FAILURE = 'Failed to create the flow.';
export const DESIGNER_FLOW_TEST_TITLE = 'Designer';
export const DESIGNER_FLOW_TEST_CREATE_BTN = 'Create';
export const DESIGNER_NEW_FLOW_BREADCRUMB = 'Designer';
export const DESIGNER_NEW_FLOW_BREADCRUMB_NEW = 'New';
export const INIT_CALL_MODAL_TITLE = 'Initiate Test Call';
export const INIT_CALL_MODAL_TEXT = 'Are you sure you want to initiate a test call?';

// Config form
export const CONFIG_FORM_NAME_HEADER = 'Name';
export const CONFIG_FORM_TEMPLATE_HEADER = 'Outgoing call URI template (optional)';
export const CONFIG_FORM_TEMPLATE_NOTE = 'Type HTTP request. Use values provided in query params ' +
  '(eg. foo=bar) instead of sections between square brackets (eg. [foo])';
export const CONFIG_FORM_METHOD_HEADER = 'Outgoing call HTTP method';
export const CONFIG_FORM_METHOD_RADIO_POST = 'POST';
export const CONFIG_FORM_METHOD_RADIO_GET = 'GET';
export const CONFIG_FORM_HEADERS_HEADER = 'POST header parameters';
export const CONFIG_FORM_HEADERS_NOTE = 'Use header1: value1, header 2: value 2, ' +
  'format to create a map with HTTP POST request header parameters';
export const CONFIG_FORM_TYPE_HEADER = 'POST parameters';
export const CONFIG_FORM_TYPE_NOTE = 'Type HTTP POST parameters';
export const CONFIG_FORM_QUEUE_HEADER = 'Outbound Call Queue Configuration';
export const CONFIG_FORM_QUEUE_LIMIT = 'Call Limit';
export const CONFIG_FORM_QUEUE_SEC = 'Retry Sec';
export const CONFIG_FORM_QUEUE_ATTEMPTS = 'Retry Attempts';
export const CONFIG_FORM_QUEUE_CALL = 'Call after all Retry Attempts?';
export const CONFIG_FORM_USERS_HEADER = 'Test users (optional)';
export const CONFIG_FORM_USERS_NOTE = 'Add test users for testing with simulation programs. ' +
  'The provided Outbound URLs will over-ride the above Outgoing call URI template for those users\' phone numbers';
export const CONFIG_FORM_USERS_KEY_LABEL = 'Phone number';
export const CONFIG_FORM_USERS_VALUE_LABEL = 'Outbound URL';
export const CONFIG_FORM_SAVE_BUTTON = 'Save';
export const CONFIG_FORM_SERVICE_MAP_HEADER = 'Injected services map';
export const CONFIG_FORM_SERVICE_MAP_NOTE = 'Map of services that can be injected in IVR templates. ' +
  'Key is the name used in Velocity, Value is the id of Spring Bean';
export const CONFIG_FORM_NAME_IS_NOT_UNIQUE = 'Each config must have their unique name';

// Flow form
export const FLOW_NAME_LABEL = 'Name';
export const FLOW_REPROMPT_LABEL = 'Repeat/Reprompt count';
export const FLOW_FIELDTYPE_LABEL = 'Type';
export const FLOW_RANGE_LABEL = 'Range';
export const FLOW_BARGEIN_LABEL = 'Barge';
export const FLOW_DTMF_LABEL = 'DTMF';
export const FLOW_VOICE_LABEL = 'Voice';
export const FLOW_NO_INPUT_LABEL = 'No Input';
export const FLOW_NO_MATCH_LABEL = 'No Match';
export const FLOW_EXIT_LABEL = 'Exit';
export const FLOW_DTMFGRAMMAR_LABEL = 'DTMF Grammar';
export const FLOW_VOICEGRAMMAR_LABEL = 'Voice Grammar';
export const DIRTY_LABEL = 'Content can be safely edited manually without losing changes as renderer is OFF.';
export const NON_DIRTY_LABEL = 'Auto generation via renderer template is ON';
export const MISSING_TEMPLATE = 'Missing template for the following renderer: ';
export const FLOW_RANGE_HELP = 'x..y or x or ..y or x..';
export const FLOW_REPROMPT_HELP = 'No. of times to repeat/reprompt if no input/match';
export const DELETE_INTERACTION_NODE = 'Delete interaction node.';

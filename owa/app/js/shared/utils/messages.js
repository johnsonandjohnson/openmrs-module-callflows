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

// Generic messages
export const GENERIC_FAILURE = 'An error occurred.';
export const GENERIC_PROCESSING = 'Processing...';
export const GENERIC_SUCCESS = 'Success.';

// Designer flow test
export const DESIGNER_FLOW_TEST_CONFIGURATION_LABEL = 'Configuration';
export const DESIGNER_FLOW_TEST_EXTENSION_LABEL = 'Extension';
export const DESIGNER_FLOW_TEST_PHONE_NUMBER_LABEL = 'Phone Number';

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
export const CONFIG_FORM_SAVE_BUTTON = 'SAVE';
export const CONFIG_FORM_SERVICE_MAP_HEADER = 'Injected services map';
export const CONFIG_FORM_SERVICE_MAP_NOTE = 'Map of services that can be injected in IVR templates. ' +
  'Key is the name used in Velocity, Value is the class of OSGi service';

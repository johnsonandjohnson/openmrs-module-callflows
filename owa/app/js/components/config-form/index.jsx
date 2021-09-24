/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import {
  Button,
  Checkbox,
  Col,
  ControlLabel,
  Form,
  FormControl,
  FormGroup,
  HelpBlock,
  Radio,
  Row
} from 'react-bootstrap';
import PropTypes from 'prop-types';
import _ from 'lodash';

import ConfigUI from './config-ui';
import * as Default from '../../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import MapFields from '../MapFields';
import ErrorDesc from '@bit/soldevelo-omrs.cfl-components.error-description';
import { validateField } from '../../shared/utils/validation-util';

const ConfigForm = (props) => {
  const { validationSchema } = props;
  const { errors } = props.config;

  const fieldClass = 'form-control';
  const errorFieldClass = fieldClass + ' error-field';

  const validateAndUpdate = (fieldName) => {
    validateField(props.config, fieldName, validationSchema)
      .then(() => {
        props.config.errors && delete props.config.errors[fieldName];
        props.updateValues(props);
      })
      .catch((errors) => {
        props.config.errors = _.merge({}, props.config.errors, errors)
        props.updateValues(props);
      });
  }

  const handleChange = (event) => {
    const fieldName = event.target.name;
    props.config[fieldName] = event.target.value;
    validateAndUpdate(fieldName);
  };

  const handleChecboxChange = (event) => {
    const fieldName = event.target.name;
    props.config[fieldName] = event.target.checked;
    validateAndUpdate(fieldName);
  };

  const handleArrayChange = (fieldName, list) => {
    props.config[fieldName] = list;
    validateAndUpdate(fieldName);
  };

  const renderError = (fieldName) => {
    if (errors) {
      return <ErrorDesc field={errors[fieldName]} />
    }
  };

  return (
    <Form className="form" onSubmit={e => e.preventDefault()}>
      <FormGroup controlId={`name_${props.localId}`}>
        <ControlLabel>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_NAME_HEADER', defaultMessage: Default.CONFIG_FORM_NAME_HEADER })}</ControlLabel>
        <FormControl type="text"
          name="name"
          value={props.config.name}
          onChange={handleChange}
          className={errors && errors.name ? errorFieldClass : fieldClass} />
        {renderError("name")}
      </FormGroup>
      <FormGroup controlId={`outgoingCallUriTemplate_${props.localId}`}>
        <ControlLabel>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_TEMPLATE_HEADER', defaultMessage: Default.CONFIG_FORM_TEMPLATE_HEADER })}</ControlLabel>
        <HelpBlock>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_TEMPLATE_NOTE', defaultMessage: Default.CONFIG_FORM_TEMPLATE_NOTE })}</HelpBlock>
        <FormControl type="text"
          componentClass="textarea"
          name="outgoingCallUriTemplate"
          value={props.config.outgoingCallUriTemplate}
          onChange={handleChange} />
      </FormGroup>
      <FormGroup controlId={`outgoingCallMethod_${props.localId}`}>
        <ControlLabel>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_METHOD_HEADER', defaultMessage: Default.CONFIG_FORM_METHOD_HEADER })}</ControlLabel>
        <Radio name="outgoingCallMethod"
          title="POST"
          value="POST"
          onChange={handleChange}
          checked={'POST' === props.config.outgoingCallMethod}>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_METHOD_RADIO_POST', defaultMessage: Default.CONFIG_FORM_METHOD_RADIO_POST })}</Radio>
        <Radio name="outgoingCallMethod"
          title="GET"
          value="GET"
          onChange={handleChange}
          checked={'GET' === props.config.outgoingCallMethod}>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_METHOD_RADIO_GET', defaultMessage: Default.CONFIG_FORM_METHOD_RADIO_GET })}</Radio>
      </FormGroup>

      {'POST' === props.config.outgoingCallMethod &&
        <div>
          <FormGroup controlId={`outgoingCallPostHeadersMap_${props.localId}`}>
            <ControlLabel>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_HEADERS_HEADER', defaultMessage: Default.CONFIG_FORM_HEADERS_HEADER })}</ControlLabel>
            <HelpBlock>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_HEADERS_NOTE', defaultMessage: Default.CONFIG_FORM_HEADERS_NOTE })}</HelpBlock>
            <MapFields
              entries={props.config.outgoingCallPostHeadersMap}
              fieldName="outgoingCallPostHeadersMap"
              updateValues={handleArrayChange} />
          </FormGroup>
          <FormGroup controlId={`outgoingCallPostParams_${props.localId}`}>
            <ControlLabel>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_TYPE_HEADER', defaultMessage: Default.CONFIG_FORM_TYPE_HEADER })}</ControlLabel>
            <HelpBlock>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_TYPE_NOTE', defaultMessage: Default.CONFIG_FORM_TYPE_NOTE })}</HelpBlock>
            <MapFields
              entries={props.config.outgoingCallPostParams}
              fieldName="outgoingCallPostParams"
              updateValues={handleArrayChange} />
          </FormGroup>
        </div>
      }

      <FormGroup controlId={`queue_${props.localId}`}>
        <ControlLabel>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_QUEUE_HEADER', defaultMessage: Default.CONFIG_FORM_QUEUE_HEADER })}</ControlLabel>
        <Row>
          <Col componentClass={HelpBlock} sm={2}>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_QUEUE_LIMIT', defaultMessage: Default.CONFIG_FORM_QUEUE_LIMIT })}</Col>
          <Col componentClass={HelpBlock} sm={2}>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_QUEUE_SEC', defaultMessage: Default.CONFIG_FORM_QUEUE_SEC })}</Col>
          <Col componentClass={HelpBlock} sm={2}>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_QUEUE_ATTEMPTS', defaultMessage: Default.CONFIG_FORM_QUEUE_ATTEMPTS })}</Col>
          <Col componentClass={HelpBlock} xs={2} sm={4}>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_QUEUE_CALL', defaultMessage: Default.CONFIG_FORM_QUEUE_CALL })}</Col>
        </Row>
        <Row>
          <Col sm={2}>
            <FormControl type="number"
              name="outboundCallLimit"
              value={props.config.outboundCallLimit}
              onChange={handleChange} />
          </Col>
          <Col sm={2}>
            <FormControl type="number"
              name="outboundCallRetrySeconds"
              value={props.config.outboundCallRetrySeconds}
              onChange={handleChange} />
          </Col>
          <Col sm={2}>
            <FormControl type="number"
              name="outboundCallRetryAttempts"
              value={props.config.outboundCallRetryAttempts}
              onChange={handleChange} />
          </Col>
          <Col xs={2} sm={4}>
            <Checkbox name="callAllowed"
              onChange={handleChecboxChange}
              checked={props.config.callAllowed} />
          </Col>
        </Row>
      </FormGroup>
      <FormGroup controlId={`servicesMap_${props.localId}`}>
        <ControlLabel>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_SERVICE_MAP_HEADER', defaultMessage: Default.CONFIG_FORM_SERVICE_MAP_HEADER })}</ControlLabel>
        <HelpBlock>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_SERVICE_MAP_NOTE', defaultMessage: Default.CONFIG_FORM_SERVICE_MAP_NOTE })}</HelpBlock>
        <FormControl type="text"
          componentClass="textarea"
          name="servicesMap"
          value={props.config.servicesMap}
          onChange={handleChange} />
      </FormGroup>
      <FormGroup controlId={`testUsersMap_${props.localId}`}>
        <ControlLabel>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_USERS_HEADER', defaultMessage: Default.CONFIG_FORM_USERS_HEADER })}</ControlLabel>
        <HelpBlock>{getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_USERS_NOTE', defaultMessage: Default.CONFIG_FORM_USERS_NOTE })}</HelpBlock>
        <MapFields
          entries={props.config.testUsersMap}
          fieldName="testUsersMap"
          updateValues={handleArrayChange}
          keyLabel={getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_USERS_KEY_LABEL', defaultMessage: Default.CONFIG_FORM_USERS_KEY_LABEL })}
          valueLabel={getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_USERS_VALUE_LABEL', defaultMessage: Default.CONFIG_FORM_USERS_VALUE_LABEL })} />
      </FormGroup>
    </Form>
  );
};

ConfigForm.propTypes = {
  config: PropTypes.instanceOf(ConfigUI).isRequired,
  localId: PropTypes.string.isRequired,
  isOpenOnInit: PropTypes.bool.isRequired,
  updateValues: PropTypes.func.isRequired,
  validationSchema: PropTypes.object.isRequired
};

export default ConfigForm;

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
import * as Msg from '../../shared/utils/messages';
import MapFields from '../MapFields';

const ConfigForm = (props) => {

  const handleChange = (event) => {
    props.config[event.target.name] = event.target.value;
    props.updateValues(props);
  };

  const handleChecboxChange = (event) => {
    props.config[event.target.name] = event.target.checked;
    props.updateValues(props);
  };

  const handleArrayChange = (fieldName, list) => {
    props.config[fieldName] = list;
    props.updateValues(props);
  };

  return (
    <Form className="form" onSubmit={e => e.preventDefault()}>
      <FormGroup controlId={`name_${props.localId}`}>
        <ControlLabel>{Msg.CONFIG_FORM_NAME_HEADER}</ControlLabel>
        <FormControl type="text"
          name="name"
          value={props.config.name}
          onChange={handleChange} />
      </FormGroup>
      <FormGroup controlId={`outgoingCallUriTemplate_${props.localId}`}>
        <ControlLabel>{Msg.CONFIG_FORM_TEMPLATE_HEADER}</ControlLabel>
        <HelpBlock>{Msg.CONFIG_FORM_TEMPLATE_NOTE}</HelpBlock>
        <FormControl type="text"
          componentClass="textarea"
          name="outgoingCallUriTemplate"
          value={props.config.outgoingCallUriTemplate}
          onChange={handleChange} />
      </FormGroup>
      <FormGroup controlId={`outgoingCallMethod_${props.localId}`}>
        <ControlLabel>{Msg.CONFIG_FORM_METHOD_HEADER}</ControlLabel>
        <Radio name="outgoingCallMethod"
          title="POST"
          value="POST"
          onChange={handleChange}
          checked={'POST' === props.config.outgoingCallMethod}>{Msg.CONFIG_FORM_METHOD_RADIO_POST}</Radio>
        <Radio name="outgoingCallMethod"
          title="GET"
          value="GET"
          onChange={handleChange}
          checked={'GET' === props.config.outgoingCallMethod}>{Msg.CONFIG_FORM_METHOD_RADIO_GET}</Radio>
      </FormGroup>
      <FormGroup controlId={`outgoingCallPostHeadersMap_${props.localId}`}>
        <ControlLabel>{Msg.CONFIG_FORM_HEADERS_HEADER}</ControlLabel>
        <HelpBlock>{Msg.CONFIG_FORM_HEADERS_NOTE}</HelpBlock>
        <MapFields
          entries={props.config.outgoingCallPostHeadersMap}
          fieldName="outgoingCallPostHeadersMap"
          updateValues={handleArrayChange} />
      </FormGroup>
      <FormGroup controlId={`outgoingCallPostParams_${props.localId}`}>
        <ControlLabel>{Msg.CONFIG_FORM_TYPE_HEADER}</ControlLabel>
        <HelpBlock>{Msg.CONFIG_FORM_TYPE_NOTE}</HelpBlock>
        <MapFields
          entries={props.config.outgoingCallPostParams}
          fieldName="outgoingCallPostParams"
          updateValues={handleArrayChange} />
      </FormGroup>
      <FormGroup controlId={`queue_${props.localId}`}>
        <ControlLabel>{Msg.CONFIG_FORM_QUEUE_HEADER}</ControlLabel>
        <Row>
          <Col componentClass={HelpBlock} sm={2}>{Msg.CONFIG_FORM_QUEUE_LIMIT}</Col>
          <Col componentClass={HelpBlock} sm={2}>{Msg.CONFIG_FORM_QUEUE_SEC}</Col>
          <Col componentClass={HelpBlock} sm={2}>{Msg.CONFIG_FORM_QUEUE_ATTEMPTS}</Col>
          <Col componentClass={HelpBlock} xs={2} sm={4}>{Msg.CONFIG_FORM_QUEUE_CALL}</Col>
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
        <ControlLabel>{Msg.CONFIG_FORM_SERVICE_MAP_HEADER}</ControlLabel>
        <HelpBlock>{Msg.CONFIG_FORM_SERVICE_MAP_NOTE}</HelpBlock>
        <FormControl type="text"
          componentClass="textarea"
          name="servicesMap"
          value={props.config.servicesMap}
          onChange={handleChange} />
      </FormGroup>
      <FormGroup controlId={`testUsersMap_${props.localId}`}>
        <ControlLabel>{Msg.CONFIG_FORM_USERS_HEADER}</ControlLabel>
        <HelpBlock>{Msg.CONFIG_FORM_USERS_NOTE}</HelpBlock>
        <MapFields
          entries={props.config.testUsersMap}
          fieldName="testUsersMap"
          updateValues={handleArrayChange}
          keyLabel={Msg.CONFIG_FORM_USERS_KEY_LABEL}
          valueLabel={Msg.CONFIG_FORM_USERS_VALUE_LABEL} />
      </FormGroup>
      <Button className="btn confirm btn-xs" onClick={props.submit}>{Msg.CONFIG_FORM_SAVE_BUTTON}</Button>
    </Form>
  );
};

ConfigForm.propTypes = {
  config: PropTypes.instanceOf(ConfigUI).isRequired,
  localId: PropTypes.string.isRequired,
  isOpen: PropTypes.bool.isRequired,
  updateValues: PropTypes.func.isRequired,
  submit: PropTypes.func.isRequired
};

export default ConfigForm;

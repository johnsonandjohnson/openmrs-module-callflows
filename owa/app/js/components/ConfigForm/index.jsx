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

import ConfigUI from './ConfigUI';
import MapFields from '../MapFields';
import ParamsField from '../ParamsField';

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

  const handleDelete = (event) => {
  };

  const handleAdd = () => {
  }

  return (
    <Form className="form" onSubmit={e => e.preventDefault()}>
      <FormGroup controlId={`name_${props.localId}`}>
        <ControlLabel>Name:</ControlLabel>
        <FormControl type="text"
          name="name"
          value={props.config.name}
          onChange={handleChange} />
      </FormGroup>
      <FormGroup controlId={`outgoingCallUriTemplate_${props.localId}`}>
        <ControlLabel><b>Outgoing call URI template</b> (optional)</ControlLabel>
        <HelpBlock>We'll never share your email with anyone else.</HelpBlock>
        <FormControl type="text"
          componentClass="textarea"
          name="outgoingCallUriTemplate"
          value={props.config.outgoingCallUriTemplate}
          onChange={handleChange} />
      </FormGroup>
      <FormGroup controlId={`outgoingCallMethod_${props.localId}`}>
        <ControlLabel><b>Outgoing call HTTP method</b></ControlLabel>
        <Radio name="outgoingCallMethod"
          title="POST"
          value="POST"
          onChange={handleChange}
          checked={'POST' === props.config.outgoingCallMethod}>POST</Radio>
        <Radio name="outgoingCallMethod"
          title="GET"
          value="GET"
          onChange={handleChange}
          checked={'GET' === props.config.outgoingCallMethod}>GET</Radio>
      </FormGroup>
      <FormGroup controlId={`outgoingCallPostHeadersMap_${props.localId}`}>
        <ControlLabel><b>POST header parameters</b></ControlLabel>
        <MapFields
          entries={props.config.outgoingCallPostHeadersMap}
          fieldName="outgoingCallPostHeadersMap"
          updateValues={handleArrayChange} />
      </FormGroup>
      <FormGroup controlId={`outgoingCallPostParams_${props.localId}`}>
        <ControlLabel><b>POST parameters</b></ControlLabel>
        <HelpBlock>Type HTTP POST parameters</HelpBlock>
        <MapFields
          entries={props.config.outgoingCallPostParams}
          fieldName="outgoingCallPostParams"
          updateValues={handleArrayChange} />
      </FormGroup>
      <FormGroup controlId={`queue_${props.localId}`}>
        <ControlLabel><b>Outbound Call Queue Configuration</b></ControlLabel>
        <Row>
          <Col componentClass={HelpBlock} sm={2}>Call Limit</Col>
          <Col componentClass={HelpBlock} sm={2}>Retry Sec</Col>
          <Col componentClass={HelpBlock} sm={2}>Retry Attempts</Col>
          <Col componentClass={HelpBlock} xs={2} sm={4}>Call after all Retry Attempts?</Col>
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
      <ParamsField
        fieldName="servicesMap"
        updateValues={handleArrayChange}
        labels="Injected services map"
        params={props.config.servicesMap} />
      <FormGroup controlId={`testUsersMap_${props.localId}`}>
        <ControlLabel><b>Test users</b> (optional)</ControlLabel>
        <HelpBlock>Add test users for testing with simulation programs. The provided Outbound URLs will over-ride the above
          Outgoing call URI template for those users' phone numbers.</HelpBlock>
        <MapFields
          entries={props.config.testUsersMap}
          fieldName="testUsersMap"
          updateValues={handleArrayChange}
          keyLabel="Phone number"
          valueLabel="Outbound URL" />
      </FormGroup>
      <Button className="btn confirm btn-xs" onClick={props.submit}>SAVE</Button>
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

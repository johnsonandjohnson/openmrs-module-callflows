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

import AddButton from '../AddButton';
import ConfigUI from './ConfigUI';


const ConfigForm = (props) => {

  const handleChange = (event) => {
    props.config[event.target.name] = event.target.value;
    props.updateValues(props);
  };

  const handleChecboxChange = (event) => {
    props.config[event.target.name] = event.target.checked;
    props.updateValues(props);
  };

  const handleArrayChange = (event) => {
    console.log(event);
    const { key, value } = event.target.value;
    props.config[event.target.name].map(item => {
      console.log(item);
      if (item.key === key) {
        item.value = value;
      }
    });
    props.updateValues(props);
  };

  const handleSubmit = (event) => {
    //ToDo
    event.preventDefault();
  };

  const handleDelete = (event) => {
    //ToDo
  };

  const genereateElement = (keyLabel, key, valueLabel, value) => {
    return (
      <FormGroup key={_.uniqueId(`${props.localId}_`)} controlId={`queue_${props.localId}`}>
        <Row>
          <Col componentClass={HelpBlock} sm={4}>{keyLabel}</Col>
          <Col componentClass={HelpBlock} sm={6}>{valueLabel}</Col>
        </Row>
        <Row>
          <Col sm={4}>
            <FormControl type="text"
              name="servicesMap"
              value={key}
              onChange={handleArrayChange} />
          </Col>
          <Col sm={6}>
            <FormControl type="text"
              name="servicesMap"
              value={value}
              onChange={handleArrayChange} />
          </Col>
        </Row>
      </FormGroup>);
  }

  const handleAddService = () => {
    props.config.servicesMap.push({ key: '', value: '' });
    //handleArrayChange
  };

  const renderMap = (list, keyLebel = 'Key', valLebel = 'Value') => {
    let items = list.map(item => { return genereateElement(keyLebel, item.key, valLebel, item.value); });
    items.push(genereateElement(keyLebel, '', valLebel, ''));
    return items;
  };

  const handleAddTester = () => {
    props.config.testUsersMap.push({ key: '', value: '' });
    //handleArrayChange
  }

  return (
    <Form className="form" onSubmit={handleSubmit}>
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
        <HelpBlock>Use header1: value1, header 2: value 2, format to create a map with HTTP POST request header parameters</HelpBlock>
        {renderMap(props.config.outgoingCallPostHeadersMap)}
        <Row>
          <Col sm={2}>
            <AddButton handleAdd={handleAddService} txt='Add more' />
          </Col>
        </Row>
      </FormGroup>
      <FormGroup controlId={`outgoingCallPostParams_${props.localId}`}>
        <ControlLabel><b>POST parameters</b></ControlLabel>
        <HelpBlock>Type HTTP POST parameters</HelpBlock>
        {renderMap(props.config.outgoingCallPostParams)}
        <Row>
          <Col sm={2}>
            <AddButton handleAdd={handleAddService} txt='Add more' />
          </Col>
        </Row>
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
      <FormGroup controlId={`servicesMap_${props.localId}`}>
        <ControlLabel><b>Injected services map</b></ControlLabel>
        <FormControl type="text"
          componentClass="textarea"
          name="servicesMap"
          value={props.config.servicesMap}
          onChange={handleChange} />
      </FormGroup>
      <FormGroup controlId={`testUsersMap_${props.localId}`}>
        <ControlLabel><b>Test users</b> (optional)</ControlLabel>
        <HelpBlock>Add test users for testing with simulation programs. The provided Outbound URLs will over-ride the above
          Outgoing call URI template for those users' phone numbers.</HelpBlock>
        {renderMap(props.config.testUsersMap, 'Phone number', 'Outbound URL')}
        <Row>
          <Col sm={2}>
            <AddButton handleAdd={handleAddTester} txt='Add more' />
          </Col>
        </Row>
      </FormGroup>
      <Button className="btn confirm btn-xs" onClick={props.save}>SAVE</Button>
    </Form>
  );
};

ConfigForm.propTypes = {
  config: PropTypes.instanceOf(ConfigUI).isRequired,
  localId: PropTypes.string.isRequired,
  isOpen: PropTypes.bool.isRequired,
  updateValues: PropTypes.func.isRequired,
  save: PropTypes.func.isRequired
};



export default ConfigForm;

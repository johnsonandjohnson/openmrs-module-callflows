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
import uuid from 'uuid';

import AddButton from './AddButton';
import ConfigModel from '../shared/model/Config.model';


const ConfigForm = (props) => {

  const handleChange = (event) => {
    props.config[event.target.name] = event.target.value;
    props.parentUpdater(props);
  };

  const handleSubmit = (event) => {
    //ToDo
    event.preventDefault();
  };

  const handleDelete = (event) => {
    //ToDo
  };

  const genereateElement = () => {
    return (
      <FormGroup controlId={`queue${props.localId}`}>
        <Row>
          <Col componentClass={HelpBlock} sm={4}>Key</Col>
          <Col componentClass={HelpBlock} sm={6}>Value</Col>
        </Row>
        <Row>
          <Col sm={4}><FormControl type="text" /></Col>
          <Col sm={6}><FormControl type="text" /></Col>
        </Row>
      </FormGroup>);
  }

  const renderServices = (event) => {
    //ToDo
    let list = props.config.servicesMap.map(item => { return genereateElement(); });
    list.push(genereateElement())
    return list;
  };

  const handleAddServices = (event) => {
    //ToDo
    let list = props.config.servicesMap.map(item => { return genereateElement(); });
    console.log(list);
    list.push(genereateElement())
    console.log(list);
    return list;
  };

  return (
    <Form className="form" onSubmit={handleSubmit}>
      <FormGroup controlId={`name${props.localId}`}>
        <ControlLabel>Name:</ControlLabel>
        <FormControl type="text"
          name="name"
          value={props.config.name}
          onChange={handleChange} />
      </FormGroup>
      <FormGroup controlId={`urlTemplate${props.localId}`}>
        <ControlLabel><b>Outgoing call URI template</b> (optional)</ControlLabel>
        <HelpBlock>We'll never share your email with anyone else.</HelpBlock>
        <FormControl type="text"
          componentClass="textarea"
          name="name"
          value={props.config.name}
          onChange={handleChange} />
      </FormGroup>
      <FormGroup controlId={`methodType${props.localId}`}>
        <ControlLabel><b>Outgoing call HTTP method</b></ControlLabel>
        <Radio name="radioGroup">POST</Radio>
        <Radio name="radioGroup">GET</Radio>
      </FormGroup>
      <FormGroup controlId={`headers${props.localId}`}>
        <ControlLabel><b>POST header parameters</b></ControlLabel>
        <HelpBlock>Use header1: value1, header 2: value 2, format to create a map with HTTP POST request header parameters</HelpBlock>
        <FormControl type="text"
          componentClass="textarea"
          name="name"
          value={props.config.name}
          onChange={handleChange} />
      </FormGroup>
      <FormGroup controlId={`params${props.localId}`}>
        <ControlLabel><b>POST parameters</b></ControlLabel>
        <HelpBlock>Type HTTP POST parameters</HelpBlock>
        <FormControl type="text"
          componentClass="textarea"
          name="name"
          value={props.config.name}
          onChange={handleChange} />
      </FormGroup>
      <FormGroup controlId={`queue${props.localId}`}>
        <ControlLabel><b>Outbound Call Queue Configuration</b></ControlLabel>
        <Row>
          <Col componentClass={HelpBlock} sm={2}>Call Limit</Col>
          <Col componentClass={HelpBlock} sm={2}>Retry Sec</Col>
          <Col componentClass={HelpBlock} sm={2}>Retry Attempts</Col>
          <Col componentClass={HelpBlock} xs={2} sm={4}>Call after all Retry Attempts?</Col>
        </Row>
        <Row>
          <Col sm={2}><FormControl type="number" placeholder="0" /></Col>
          <Col sm={2}><FormControl type="number" placeholder="0" /></Col>
          <Col sm={2}><FormControl type="number" placeholder="0" /></Col>
          <Col xs={2} sm={4}><Checkbox /></Col>
        </Row>
      </FormGroup>

      <FormGroup controlId={`queue${props.localId}`}>
        <ControlLabel><b>Injected services map</b></ControlLabel>
        {renderServices()}
        <Row>
          <Col sm={2}>
            <AddButton handleAdd={handleAddServices} txt='Add more' />
          </Col>
        </Row>
      </FormGroup>
      <Button className="btn cancel btn-xs">CANCEL</Button>
      <Button className="btn confirm btn-xs">SAVE</Button>
    </Form>
  );
};

ConfigForm.propTypes = {
  config: PropTypes.instanceOf(ConfigModel).isRequired,
  localId: PropTypes.string.isRequired,
  isOpen: PropTypes.bool.isRequired,
  parentUpdater: PropTypes.func.isRequired
};

export class ConfigFormData {
  constructor(fetched) {
    this.config = new ConfigModel(fetched);
    this.localId = uuid.v4();
    this.isOpen = !fetched;
  }
}

export default ConfigForm;

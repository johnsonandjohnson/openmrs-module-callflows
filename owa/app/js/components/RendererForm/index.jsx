import React from 'react';
import {
    Button,
    ControlLabel,
    Form,
    FormControl,
    FormGroup,
  } from 'react-bootstrap';
  import PropTypes from 'prop-types';
  import _ from 'lodash';

  import RendererUI from './RendererUI';
  
  const RendererForm = (props) => {

    const handleChange = (event) => {
        props.renderer[event.target.name] = event.target.value;
        props.updateValues(props);
    };

      return (
          <div>
              <Form className="form" onSubmit={e => e.preventDefault()}>
                <FormGroup controlId={`name_${props.localId}`}>
                    <ControlLabel>Name:</ControlLabel>
                    <FormControl type="text"
                    name="name"
                    value={props.renderer.name}
                    onChange={handleChange} />
                </FormGroup>

                <FormGroup controlId={`mimeType_${props.localId}`}>
                    <ControlLabel>MIME type:</ControlLabel>
                    <FormControl type="text"
                    name="mimeType"
                    value={props.renderer.mimeType}
                    onChange={handleChange} />
                </FormGroup>

                <FormGroup controlId={`template_${props.localId}`}>
                    <ControlLabel>Underscore JS template:</ControlLabel>
                    <FormControl
                    componentClass="textarea"
                    name="template"
                    value={props.renderer.template}
                    onChange={handleChange} />
                </FormGroup>

                <Button className="btn confirm btn-xs" onClick={props.submit}>SAVE</Button>
              </Form>
          </div>

      );
  };

  RendererForm.propTypes = {
      renderer: PropTypes.instanceOf(RendererUI).isRequired,
      localId: PropTypes.string.isRequired,
      isOpen: PropTypes.bool.isRequired,
      updateValues: PropTypes.func.isRequired,
      submit: PropTypes.func.isRequired
  };

  export default RendererForm;
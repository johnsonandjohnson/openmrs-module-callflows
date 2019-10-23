import React from 'react';
import {
  Form,
  Button,
  FormGroup,
  ControlLabel,
  FormControl
} from 'react-bootstrap';

const Renderer = (props) => {

  const handleChange = (event) => {
    let fieldName = event.target.name;
    let fieldVal = event.target.value;
    props.renderer[fieldName] = fieldVal;
    props.onChange(props.renderer);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    if (props.renderer.id) {
      props.updateRenderer(props.renderer);
    } else {
      props.createRenderer(props.renderer);
    }
  };

  const handleDelete = (event) => {
  
  }
  


  return (
    <div>
      <Form className="form" onSubmit={handleSubmit}>
        <FormGroup controlId={"formName" + props.renderer.uiLocalUuid}>
          <ControlLabel>Renderer Name:</ControlLabel>
          <p className="form-tooltip">It is usually a file extension, eg. vxml, ccxml</p>
          <FormControl type="text"
            name='name'
            value={props.renderer.name}
            onChange={handleChange} />
        </FormGroup>

        <FormGroup controlId={"formMimeType" + props.renderer.uiLocalUuid}>
          <ControlLabel>MIME type:</ControlLabel>
          <p className="form-tooltip">Provide the MIME type for given file extension</p>
          <FormControl type="text"
            name='mimeType'
            value={props.renderer.mimeType}
            onChange={handleChange} />
        </FormGroup>

        <FormGroup controlId={"formTemplate" + props.renderer.uiLocalUuid}>
          <ControlLabel>Underscore JS template:</ControlLabel>
          <FormControl type="text"
            name='template'
            value={props.renderer.template}
            onChange={handleChange} />
        </FormGroup>

        <Button className="btn btn-danger btn-md" onClick={handleDelete}>Delete</Button>
        <Button className="btn btn-success btn-md" type="submit">Save</Button>
      </Form>
    </div>
  );
};

export default Renderer;
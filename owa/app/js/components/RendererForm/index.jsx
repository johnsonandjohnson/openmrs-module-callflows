/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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
import * as Default from '../../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
  
const RendererForm = (props) => {

  const handleChange = (event) => {
    props.renderer[event.target.name] = event.target.value;
    props.updateValues(props);
  };

    return (
      <div>
        <Form className="form" onSubmit={e => e.preventDefault()}>
          <FormGroup controlId={`name_${props.localId}`}>
            <ControlLabel>Renderer Name:</ControlLabel>
            <p className="form-tooltip">It is usually a file extension, eg. vxml, ccxml</p>
            <FormControl type="text"
            name="name"
            value={props.renderer.name}
            onChange={handleChange} />
          </FormGroup>

          <FormGroup controlId={`mimeType_${props.localId}`}>
            <ControlLabel>MIME type:</ControlLabel>
            <p className="form-tooltip">Provide the MIME type for given file extension</p>
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

          <Button className="btn confirm btn-xs" onClick={props.submit}>
            {getIntl().formatMessage({ id: 'CALLFLOW_CONFIG_FORM_SAVE_BUTTON', defaultMessage: Default.CONFIG_FORM_SAVE_BUTTON })}
          </Button>
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
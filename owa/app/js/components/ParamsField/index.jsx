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
  FormControl,
  FormGroup,
  ControlLabel
} from 'react-bootstrap';
import PropTypes from 'prop-types';
import _ from 'lodash';

import MapEntry from '../../shared/utils/MapEntry';

const ParamsField = (props) => {
  const handleChange = (event) => {
    const input = event.target;
    const params = MapEntry.paramsToArray(input.value);
    props.updateValues(props.fieldName, params);
  }
  const { fieldName, params, label } = props;
  const strParams = MapEntry.arrayToParams(params);
  return (
    <FormGroup controlId={`${fieldName}_${props.localId}`}>
      <ControlLabel><b>{label}</b></ControlLabel>
      <FormControl type="text"
        componentClass="textarea"
        name={fieldName}
        value={strParams}
        onChange={handleChange} />
    </FormGroup>);
}

ParamsField.defaultProps = {
  labels: 'Map',
  params: []
}

ParamsField.propTypes = {
  fieldName: PropTypes.string.isRequired,
  updateValues: PropTypes.func.isRequired,
  labels: PropTypes.string,
  params: PropTypes.array
};

export default ParamsField;

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
  Col,
  FormControl,
  FormGroup,
  HelpBlock,
  Row
} from 'react-bootstrap';
import PropTypes from 'prop-types';
import _ from 'lodash';

import MapEntry from '../../shared/utils/MapEntry';
import RemoveButton from '../RemoveButton';
import './index.scss';

const MapField = (props) => {
  const { fieldName, handleChange, keyLabel, valueLabel, entry, columnSizes } = props;
  return (
    <FormGroup controlId={`${fieldName}_${entry.id}`}>
      <Row>
        <Col componentClass={HelpBlock} sm={columnSizes.key}>{keyLabel}</Col>
        <Col componentClass={HelpBlock} sm={columnSizes.value}>{valueLabel}</Col>
        {(columnSizes.button === 0)
          ? null
          : <Col componentClass={HelpBlock} sm={columnSizes.button} />}
      </Row>
      <Row>
        <Col sm={columnSizes.key}
          className="map-field-left">
          <FormControl type="text"
            name="key"
            value={entry.key}
            onChange={handleChange} />
        </Col>
        <Col sm={columnSizes.value}
          className="map-field">
          <FormControl type="text"
            name="value"
            value={entry.value}
            onChange={handleChange} />
        </Col>
        {(columnSizes.button === 0)
          ? null
          : <Col sm={columnSizes.button}
            className="map-field">
            <RemoveButton
              buttonClass="col-remove-button"
              handleRemove={props.handleRemove}
              localId={entry.id} />
          </Col>}
      </Row>
    </FormGroup>
  );
};

export const defaultColumnSizes = {
  key: 4,
  value: 7,
  button: 1
};

export const columnSizesType = PropTypes.shape({
  key: PropTypes.number,
  value: PropTypes.number,
  button: PropTypes.number
});

MapField.defaultProps = {
  keyLabel: 'Key',
  valueLabel: 'Value',
  entry: new MapEntry('', ''),
  columnSizes: defaultColumnSizes
};

MapField.propTypes = {
  fieldName: PropTypes.string.isRequired,
  handleChange: PropTypes.func.isRequired,
  handleRemove: PropTypes.func.isRequired,
  keyLabel: PropTypes.string,
  valueLabel: PropTypes.string,
  entry: PropTypes.instanceOf(MapEntry),
  columnSizes: columnSizesType
};

export default MapField;

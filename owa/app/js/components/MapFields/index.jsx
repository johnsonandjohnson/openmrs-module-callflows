/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { Col, Row } from 'react-bootstrap';
import PropTypes from 'prop-types';
import _ from 'lodash';

import MapField, {
  columnSizesType,
  defaultColumnSizes
} from './MapField';
import AddButton from '../add-button';
import MapEntry from '../../shared/utils/map-entry-util';
import './index.scss';

const MapFields = (props) => {

  const { fieldName, entries, columnSizes,
    keyLabel, valueLabel, addLabel, removeable } = props;

  const handleChange = (event) => {
    const input = event.target;
    const fieldName = _.split(input.id, '_')[0];
    const entryId = _.split(input.id, '_')[1];
    let entries = getUpdatedEntry(input, entryId);
    props.updateValues(fieldName, entries);
  };

  const handleAdd = () => {
    props.entries.push(new MapEntry('', ''));
    props.updateValues(fieldName, entries);
  };

  const handleRemove = (event) => {
    const input = event.target;
    if (_.size(entries) > 1) {
      _.pull(props.entries, find(input.id));
    } else {
      props.entries[0] = new MapEntry('', '');
    }

    props.updateValues(fieldName, entries);
  };

  const find = (id) => {
    let result = null;
    props.entries.map(entry => {
      if (entry.id === id) {
        result = entry;
      }
    });
    return result;
  };

  const getUpdatedEntry = (input, entryId) => {
    return props.entries.map(entry => {
      if (entry.id === entryId) {
        entry[input.name] = input.value;
      }
      return entry;
    });
  };

  const setRemove = () => {
    return {
      key: columnSizes.key,
      value: removeable ? columnSizes.value :  columnSizes.button + columnSizes.value,
      button: removeable ? columnSizes.button : 0
    };
  };

  const sizes = setRemove();
  return (
    <div>
      {entries.map(entry => {
        return <MapField
          key={entry.id}
          fieldName={fieldName}
          handleChange={handleChange}
          handleRemove={handleRemove}
          entry={entry}
          columnSizes={sizes}
          keyLabel={keyLabel}
          valueLabel={valueLabel} />;
      })}
      <Row>
        <Col sm={sizes.key}>
          <AddButton handleAdd={handleAdd} txt={addLabel} />
        </Col>
      </Row>
    </div>
  );
};

MapFields.defaultProps = {
  entries: [],
  columnSizes: defaultColumnSizes,
  addLabel: 'Add more',
  removeable: true
};

MapFields.propTypes = {
  fieldName: PropTypes.string.isRequired,
  updateValues: PropTypes.func.isRequired,
  entries: PropTypes.array,
  columnSizes: columnSizesType,
  keyLabel: PropTypes.string,
  valueLabel: PropTypes.string,
  addLabel: PropTypes.string,
  removeable: PropTypes.bool
};

export default MapFields;

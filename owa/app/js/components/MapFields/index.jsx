/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';

import MapField, {
  columnSizesType,
  defaultColumnSizes
} from './MapField';

const MapFields = (props) => {

  const handleChange = (event) => {
    const input = event.target;
    const fieldName = _.split(input.id, '_')[0];
    const entryId = _.split(input.id, '_')[1];
    let entries = getUpdatedEntry(input, entryId);
    props.updateValues(fieldName, entries);
  }

  const getUpdatedEntry = (input, entryId) => {
    return props.entries.map(entry => {
      if (entry.id === entryId) {
        entry[input.name] = input.value;
      }
      return entry;
    });
  }

  const { fieldName, entries, columnSizes, keyLabel, valueLabel } = props;
  return (
    <div>
      {entries.map(entry => {
        return <MapField
          key={entry.id}
          fieldName={fieldName}
          handleChange={handleChange}
          entry={entry}
          columnSizes={columnSizes}
          keyLabel={keyLabel}
          valueLabel={valueLabel} />;
      })}
    </div>
  );
}


MapFields.defaultProps = {
  entries: [],
  columnSizes: defaultColumnSizes
}

MapFields.propTypes = {
  fieldName: PropTypes.string.isRequired,
  updateValues: PropTypes.func.isRequired,
  entries: PropTypes.array,
  columnSizes: columnSizesType,
  keyLabel: PropTypes.string,
  valueLabel: PropTypes.string
};

export default MapFields;

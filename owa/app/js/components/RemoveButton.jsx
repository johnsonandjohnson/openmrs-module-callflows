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

const RemoveButton = (props) => {
  const { localId, handleRemove, tooltip } = props;
  return (
    <i className="medium icon-remove delete-action"
      id={localId}
      onClick={handleRemove}
      title={tooltip} />);
};

RemoveButton.defaultProps = {
  tooltip: null
};

RemoveButton.propTypes = {
  handleRemove: PropTypes.func.isRequired,
  localId: PropTypes.string.isRequired,
  tooltip: PropTypes.string
};

export default RemoveButton;

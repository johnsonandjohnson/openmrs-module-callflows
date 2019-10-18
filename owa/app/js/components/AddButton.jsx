/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Button } from 'react-bootstrap';
import PropTypes from 'prop-types';
import _ from 'lodash';

const AddButton = (props) => {
  const { handleAdd, txt } = props;
  return (
    <Button className="btn confirm btn-xs" onClick={handleAdd} >
      <FontAwesomeIcon  size="1x" icon={['fas', 'plus']} />
      { (!!txt) ? ' ' + txt : null }
  </Button>);
}


AddButton.propTypes = {
  handleAdd: PropTypes.func.isRequired,
  txt: PropTypes.string
};

export default AddButton;
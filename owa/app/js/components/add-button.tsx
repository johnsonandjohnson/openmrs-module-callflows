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
import _ from 'lodash';


interface IAddButton {
  handleAdd: Function,
  buttonClass?: string,
  txt?: string
}

const AddButton = (props: IAddButton) => {
  const { handleAdd, txt, buttonClass } = props;
  const clazz = buttonClass + ' add-button';
  return (
      <Button className={clazz} onClick={handleAdd} >
        <FontAwesomeIcon size="1x" icon={['fas', 'plus']} />
        {(txt === null) ? null : ' ' + txt}
      </Button>);
}

AddButton.defaultProps = {
  buttonClass: 'button',
  txt: null
}

export default AddButton;
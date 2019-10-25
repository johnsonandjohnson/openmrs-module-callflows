
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
  Modal,
  Button
} from 'react-bootstrap';
import PropTypes from 'prop-types';
import _ from 'lodash';

const OpenMRSModal = (props) => {
  const { title, txt, confirmLabel, cancelLabel } = props;
  return (
    <Modal show={props.show} onHide={props.handleClose}>
      <Modal.Header>
        <Modal.Title>{title}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <p>{txt}</p>
        <Button bsClass="button confirm right" onClick={props.confirm}>{confirmLabel}</Button>
        <Button bsClass="button cancel" onClick={props.deny}>{cancelLabel}</Button>
      </Modal.Body>
    </Modal>
  );
};

OpenMRSModal.defaultProps = {
  title: 'Confirm',
  txt: 'Are you sure?',
  confirmLabel: 'YES',
  cancelLabel: 'NO'
};

OpenMRSModal.propTypes = {
  title: PropTypes.string,
  txt: PropTypes.string,
  confirmLabel: PropTypes.string,
  cancelLabel: PropTypes.string,
  deny: PropTypes.func.isRequired,
  confirm: PropTypes.func.isRequired,
  show: PropTypes.bool.isRequired
};

export default OpenMRSModal;

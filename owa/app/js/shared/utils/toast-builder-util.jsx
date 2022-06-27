/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

// Component used for building toasts (notification) with OpenMRS style guideline matching

export const ToastStatusContent = ({ message, type }) => {
  var customClass = "toast-item-image toast-item-image-" + type;
  return (
    <div className="callflow-toast-item-wrapper">
      <div className={customClass}></div>
      <p>{message}</p>
    </div>
  );
}

export const CloseButton = (props) => (
  <div className="toast-item-close" onClick={props.closeToast} />
);

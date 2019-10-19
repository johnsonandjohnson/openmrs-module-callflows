/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { connect } from 'react-redux';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Accordion } from '@openmrs/react-components';
import { Button } from 'react-bootstrap';

import Renderer from './Renderer';

export class Providers extends React.Component {

  handleAdd = () => {
    return null;
  }

  render() {
      return (
        <div className="body-wrapper">
          <h1>Call flows Renderers</h1>
          <Button className="btn btn-success btn-md" onClick={this.handleAdd}><i className="fa fa-plus"></i> Add Renderer</Button>


        </div>
      );
  }
}
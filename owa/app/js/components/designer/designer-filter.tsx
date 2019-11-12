/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import moment from 'moment';
import React from 'react';
import {
  Col,
  Row
} from 'react-bootstrap';



export interface IDesignerFiltersProps {
  filtersChangedCallback?(filters: {}): void;
};

export interface IDesignerFiltersState {
  flowName?: string
}

export default class DesignerFilters extends React.PureComponent<IDesignerFiltersProps, IDesignerFiltersState> {
  constructor(props) {
    super(props);
    this.state = {
      flowName: ''
    };
  }

  onFlowNameFilterChange = (event) => {
    this.setState({ flowName: event.target.value })
    this.props.filtersChangedCallback && this.props.filtersChangedCallback({ flowName: event.target.value });
  }

  render = () =>
    <Row>
      <Col sm="7">
        <Row sm="8">
          <Col sm="6">
            Search by flow name:
          </Col>
        </Row>
        <Row className="u-mb-10" sm="8">
          <Col sm="6">
            <input className="u-fill-col" type="text" onChange={this.onFlowNameFilterChange} value={this.state.flowName} />
          </Col>
        </Row>
      </Col>
    </Row>;
}

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

import AddButton from './AddButton';
import { reset, getConfigs, postConfig } from '../reducers/providers.reducer';

export class Providers extends React.Component {

  handleAdd = () => {
    return null;
  }

  render() {
    const buttonLabel = 'Add Provider';
    const title = 'Providers';
    return (
      <div className="body-wrapper">
        <div className="row">
          <div className="col-md-12 col-xs-12">
            <h2>{title}</h2>
          </div>
        </div>
        <div className="row">
          <AddButton handleAdd={this.handleAdd} txt={buttonLabel} />
        </div>
        <div className="panel-body">
          <Accordion title="test" border="true">
            <div>form</div>
          </Accordion>
        </div>
        <Button className="btn cancel btn-xs" onClick={this.props.reset} >CANCEL</Button>
        <Button className="btn confirm btn-xs" onClick={this.props.postConfig} >SAVE</Button>
      </div>
    );
  }
}

export const mapStateToProps = state => ({
  configs: state.providersReducer.configs
});

const mapDispatchToProps = {
  reset,
  getConfigs,
  postConfig
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Providers);
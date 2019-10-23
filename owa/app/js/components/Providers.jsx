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
import { reset, getConfigs, postConfigs, updateConfigForm } from '../reducers/providers.reducer';
import ConfigForm from './ConfigForm';

export class Providers extends React.Component {

  componentDidMount = () => {
    this.props.getConfigs();
  }

  handleAdd = () => {
    return this.props.postConfigs();
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
        <div className="panel-body">
        <div className="row">
          <div className="col-md-12 col-xs-12">
            <AddButton handleAdd={this.handleAdd} txt={buttonLabel} buttonClass='confirm' />
          </div>
        </div>
          {this.props.configForms.map(item => {
            return (
              <Accordion title={item.config.name}
                border={true}
                key={item.localId}
                open={item.isOpen}>
                <ConfigForm config={item.config}
                  isOpen={item.isOpen}
                  localId={item.localId}
                  parentUpdater={this.props.updateConfigForm} />
              </Accordion>
            );
          })}
        </div>
      </div>
    );
  }
}

export const mapStateToProps = state => ({
  configForms: state.providersReducer.configForms
});

const mapDispatchToProps = {
  reset,
  getConfigs,
  postConfigs,
  updateConfigForm
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Providers);
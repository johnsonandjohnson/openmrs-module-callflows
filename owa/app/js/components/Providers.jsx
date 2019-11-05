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
import {
  Col,
  Row
} from 'react-bootstrap';
import { Accordion } from '@openmrs/react-components';
import _ from 'lodash';

import AddButton from './AddButton';
import {
  reset,
  getConfigs,
  postConfigs,
  updateConfigForm,
  addNewForm,
  removeForm,
  openModal,
  closeModal
} from '../reducers/providers.reducer';
import RemoveButton from './RemoveButton';
import ConfigForm from './ConfigForm';
import OpenMRSModal from './OpenMRSModal';

export class Providers extends React.Component {

  constructor(props) {
    super(props);
    this.focusRef = null;
  }

  componentDidMount = () => {
    this.props.getConfigs();
    this.focusDiv();
  }

  componentDidUpdate = (prev) => {
    if (prev.configForms.length > this.props.configForms.length) {
      this.props.postConfigs(this.props.configForms);
    }
    this.focusDiv();
  }

  submitConfigs = () => {
    this.props.postConfigs(this.props.configForms);
  }

  handleRemove = (event) => {
    this.props.openModal(event.target.id);
  }

  handleClose = () => {
    this.props.closeModal();
  }

  handleConfirm = () => {
    this.props.removeForm(this.props.toDeleteId, this.props.configForms);
  }

  getOffsetTop(element) {
    let offsetTop = 0;
    while (element) {
      offsetTop += element.offsetTop;
      element = element.offsetParent;
    }
    return offsetTop;
  }

  focusDiv() {
    if (!_.isEmpty(this.focusRef)) {
      window.scrollTo({ left: 0, top: this.getOffsetTop(this.focusRef), behavior: 'smooth' });
      this.focusRef = null;
    }
  }

  render() {
    const buttonLabel = 'Add Provider';
    const title = 'Providers';
    return (
      <div className="body-wrapper">
        <OpenMRSModal
          deny={this.handleClose}
          confirm={this.handleConfirm}
          show={this.props.showModal}
          title="Delete Provider"
          txt="Are you sure you want to delete this Provider?" />
        <div className="row">
          <div className="col-md-12 col-xs-12">
            <h2>{title}</h2>
          </div>
        </div>
        <div className="panel-body">
          <div className="row">
            <div className="col-md-12 col-xs-12">
              <AddButton
                handleAdd={this.props.addNewForm}
                txt={buttonLabel}
                buttonClass="confirm" />
            </div>
          </div>
          {this.props.configForms.map(item => {
            return (
              <Row key={item.localId}>
                <Col sm={11}
                  className="cfl-col-field-left">
                  <Accordion title={item.config.name}
                    border={true}
                    open={item.isOpen}>
                    <div ref={(div) => {
                      if (item.localId === this.props.newEntry) {
                        this.focusRef = div;
                      }
                    }}>
                      <ConfigForm
                        config={item.config}
                        isOpen={item.isOpen}
                        localId={item.localId}
                        updateValues={this.props.updateConfigForm}
                        submit={this.submitConfigs} />
                    </div>
                  </Accordion>
                </Col>
                <Col sm={1}
                  className="cfl-col-field">
                  <RemoveButton
                    buttonClass="col-remove-button"
                    handleRemove={this.handleRemove}
                    localId={item.localId}
                    tooltip="Delete Provider" />
                </Col>
              </Row>
            );
          })}
        </div>
      </div>
    );
  }
}

export const mapStateToProps = state => ({
  configForms: state.providersReducer.configForms,
  showModal: state.providersReducer.showModal,
  toDeleteId: state.providersReducer.toDeleteId,
  newEntry: state.providersReducer.newEntry
});

const mapDispatchToProps = {
  reset,
  getConfigs,
  postConfigs,
  updateConfigForm,
  addNewForm,
  removeForm,
  openModal,
  closeModal
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Providers);

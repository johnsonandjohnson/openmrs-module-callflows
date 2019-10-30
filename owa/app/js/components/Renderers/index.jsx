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
import { Accordion } from '@openmrs/react-components';
import { Col, Row } from 'react-bootstrap';


import AddButton from '../AddButton';
import {
  reset,
  getRenderers,
  postRenderers,
  updateRendererForm,
  addNewForm,
  removeForm,
  openModal,
  closeModal
} from '../../reducers/renderersReducer';

import RendererForm from '../RendererForm';
import RemoveButton from '../RemoveButton';
import OpenMRSModal from '../OpenMRSModal';

export class Renderers extends React.Component {

  componentDidMount = () => {
    this.props.getRenderers();
  }

  componentDidUpdate = (prev) => {
    if (prev.rendererForms.length > this.props.rendererForms.length) {
        this.props.postRenderers(this.props.rendererForms);
    }
  }

  submitRenderers = () => {
    this.props.postRenderers(this.props.rendererForms);
  }

  handleRemove = (event) => {
    this.props.openModal(event.target.id);
  }
  handleClose = () => {
    this.props.closeModal();
  }

  handleConfirm = () => {
    this.props.removeForm(this.props.toDeleteId, this.props.rendererForms);
  }


  render() {
    const buttonLabel = 'Add Renderer';
    const title = 'Renderers';
    return (
      <div className="body-wrapper">
        <OpenMRSModal
          deny={this.handleClose}
          confirm={this.handleConfirm}
          show={this.props.showModal}
          title="Delete Renderer"
          txt="Are you sure you want to delete this Renderer?" />
        <div className="row">
          <div className="col-md-12 col-xs-12">
              <h2>{title}</h2>
          </div>
        </div>
        <div className="panel-body">
          <div className="row">
            <div className="col-md-12 col-xs-12">
              <AddButton handleAdd={this.props.addNewForm} txt={buttonLabel} buttonClass='confirm' />
            </div>
          </div>
          {this.props.rendererForms.map(item => {
          return (
            <Row key={item.localId}>
              <Col sm={11}>
                <Accordion title={item.renderer.name}
                  border={true}
                  open={item.isOpen}>
                  <RendererForm renderer = {item.renderer}
                    isOpen = {item.isOpen}
                    localId = {item.localId}
                    updateValues={this.props.updateRendererForm}
                    submit={this.submitRenderers}/>
                </Accordion>
              </Col>
              <Col sm={1}>
                <RemoveButton
                  handleRemove={this.handleRemove}
                  localId={item.localId}
                  tooltip="Delete Renderer" />
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
  rendererForms: state.renderersReducer.rendererForms,
  showModal: state.renderersReducer.showModal,
  toDeleteId: state.renderersReducer.toDeleteId
});

const mapDispatchToProps = {
  reset,
  getRenderers,
  postRenderers,
  updateRendererForm,
  addNewForm,
  removeForm,
  openModal,
  closeModal
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Renderers);
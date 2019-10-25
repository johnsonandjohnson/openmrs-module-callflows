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
import { Button, Col, Row } from 'react-bootstrap';


import AddButton from '../AddButton';
import {
    reset,
    getRenderers,
    postRenderers,
    updateRendererForm,
    addNewForm,
    removeForm
} from '../../reducers/renderersReducer';
import RendererForm from '../RendererForm';

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
    this.props.removeForm(event.target.id, this.props.rendererForms);
}


  render() {
      const buttonLabel = 'Add Renderer';
      const title = 'Renderers';
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
                    submit={this.submitRenderers}
                 />
            </Accordion>
             </Col>
             <Col sm={1}>
               <i className="medium icon-remove" id={item.localId} onClick={this.handleRemove} />
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
  rendererForms: state.renderersReducer.rendererForms
});

const mapDispatchToProps = {
  reset,
  getRenderers,
  postRenderers,
  updateRendererForm,
  addNewForm,
  removeForm
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Renderers);
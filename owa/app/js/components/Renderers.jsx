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

import {reset, getRenderers, createRenderer, addNew, changeRenderer, updateRenderer} from '../reducers/renderersReducer';
import Renderer from './Renderer';

export class Renderers extends React.Component {

  constructor(props) {
    super(props);
    this.handleAdd = this.handleAdd.bind(this);
    this.newEntry = null;
  }

  handleAdd(e) {
    e.preventDefault();
    this.props.addNew();
  }

  componentDidMount() {
    this.props.getRenderers();
    this.focusDiv();
  }

  componentDidUpdate = () => {
    this.focusDiv();
  }

  getOffsetTop(element) {
    let offsetTop = 0;
    while(element) {
      offsetTop += element.offsetTop;
      element = element.offsetParent;
    }
    return offsetTop;
  }


  focusDiv() {
    if (this.newEntry) {
      window.scrollTo({left: 0, top: this.getOffsetTop(this.newEntry), behavior: 'smooth'});
      this.newEntry = null;
    }
  }
   

  
  render() {
    return (
        <div className="body-wrapper">
          <h1>Renderers</h1>
          <Button className="btn btn-success btn-md" onClick={this.handleAdd}><i className="fa fa-plus"></i> Add Renderer</Button>
        
          {this.props.renderers.map(item => {
          return (
            <Accordion title={`Renderer: ${item.name ? item.name : 'not saved'}`} border={true} open={item.isOpen} key={item.uiLocalUuid} >
              <div ref={(div) => {
                if (item.isOpen) {
                  item.isOpen = null;
                  this.newEntry = div;
                }
              }}>
                  <Renderer renderer={item} onChange={this.props.changeRenderer} 
                    createRenderer={this.props.createRenderer}
                    getRenderers={this.props.getRenderers}
                    updateRenderer={this.props.updateRenderer}
                 />
              </div>
            </Accordion>
          );
        })}
        </div>
    );
  }
}

export const mapStateToProps = state => ({
  renderers: state.renderersReducer.renderers
});

const mapDispatchToProps = {
  reset,
  getRenderers,
  createRenderer,
  addNew,
  changeRenderer,
  updateRenderer,
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Renderers);
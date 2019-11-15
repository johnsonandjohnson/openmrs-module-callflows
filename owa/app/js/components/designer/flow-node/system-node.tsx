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
import { RouteComponentProps } from 'react-router-dom';
import {
  reset,
  getConfigs,
  postConfigs,
  getFlows,
  getFlow,
  updateNode
} from '../../../reducers/designer.reducer';
import { IRootState } from '../../../reducers';
import { Controlled as CodeMirror } from 'react-codemirror2';
import { ISystemNode } from '../../../shared/model/system-node.model';

export interface ISystemNodeProps extends StateProps, DispatchProps, RouteComponentProps<{ flowName: string }> {
  node: ISystemNode,
  nodeIndex: number
};

export interface ISystemNodeState {
  nodeValue: any;
};

export class SystemNode extends React.PureComponent<ISystemNodeProps, ISystemNodeState> {
  constructor(props) {
    super(props);
    this.state = {
      nodeValue: this.props.node.templates.velocity.content
    };
  }

  options = {
    lineNumbers: true,
    lineWrapping: true,
    addModeClass: true,
    tabSize: 2,
    mode: 'velocity', //may be: javascript,xml,sql,velocity
    theme: 'default'
    // extraKeys: {
    //     "F11": fullscreenOn,
    //     "Esc": fullScreenOff
    // } //TODO OCALL-73: Add support
  }

  componentDidMount = () => {
  }

  handleNodeValueChange = (editor, data, value) => {
    let node = this.props.node;
    node.templates.velocity.content = value;
    this.props.updateNode(node, this.props.nodeIndex);
  }

  handleNodeNameChange = (event: any) => {
    let node = this.props.node;
    node.step = event.target.value;
    this.props.updateNode(node, this.props.nodeIndex);
  }

  render() {
    const systemNodeClass = 'system-node';
    return (
      <div className={systemNodeClass}>
        <div className="input-group">
          <span className="input-group-addon"> Step </span>
          <input
            className="form-control"
            value={this.props.node.step}
            onChange={this.handleNodeNameChange}
          />
        </div>
        <CodeMirror
          value={this.state.nodeValue}
          options={this.options}
          onBeforeChange={(editor, data, value) => {
            this.setState({ nodeValue: value });
          }}
          onChange={this.handleNodeValueChange}
        />
      </div>
    );
  }
}

export const mapStateToProps = ({ designerReducer }: IRootState) => (designerReducer);

const mapDispatchToProps = ({
  reset,
  getConfigs,
  postConfigs,
  getFlows,
  getFlow,
  updateNode
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SystemNode);

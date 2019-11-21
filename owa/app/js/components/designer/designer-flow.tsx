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
  putFlow,
  updateFlow,
  postFlow
} from '../../reducers/designer.reducer';
import { IRootState } from '../../reducers';
import DesignerCallTest from './test-call/designer-call-test';
import {
  Form,
  Button,
  FormGroup,
  FormControl
} from 'react-bootstrap';
import _ from 'lodash';
import { Accordion, Tabs, Tab } from '@openmrs/react-components';
import SystemNode from './flow-node/system-node';
import UserNode from './flow-node/user-node';
import { ISystemNode } from '../../shared/model/system-node.model';
import { NodeType } from '../../shared/model/node-type.model';
import { INode } from '../../shared/model/node.model';
import DesignerFlowTest from './test-flow/designer-flow-test';
import { IFlow } from '../../shared/model/flow.model';
import { IUserNode } from '../../shared/model/user-node.model';
import * as Msg from '../../shared/utils/messages';
import Tooltip from '../tooltip';

export interface IDesignerFlowProps extends StateProps, DispatchProps, RouteComponentProps<{ flowName: string }> {
};

export interface IDesignerFlowState {
  isNew: boolean;
  nodesExpansion: {};
};

export class DesignerFlow extends React.PureComponent<IDesignerFlowProps, IDesignerFlowState> {
  constructor(props) {
    super(props);
    this.state = {
      isNew: !this.props.match.params || !this.props.match.params.flowName,
      nodesExpansion: {}
    };
  }

  componentDidMount = () => {
    if (!!this.state.isNew) {
      this.props.reset();
    } else {
      const { flowName } = this.props.match.params;
      this.props.getFlow(flowName);
    }
  }

  componentWillUpdate(nextProps, nextState) {
    if (this.props.flowLoaded === false && nextProps.flowLoaded === true) {
      if (!!this.props.nodes) {
        const initialExpansion = {};
        try {
          this.props.nodes.forEach((node) => {
            initialExpansion[node.step] = false;
          });
        } finally {
          this.setState({
            nodesExpansion: initialExpansion
          });
        }
      }
    }
  }

  handleNameChange = (event) => {
    const fieldName: string = event.target.name;
    const value: string = event.target.value;
    let flow: IFlow = _.cloneDeep(this.props.flow);
    flow[fieldName] = value;
    this.props.updateFlow(flow);
  };

  handleSave = () => {
    if (!!this.props.flow.id) {
      this.props.putFlow(this.props.flow, this.props.nodes);
    } else {
      this.props.postFlow(this.props.flow, this.props.nodes);
    }
  }

  renderSystemNode = (node: ISystemNode, nodeIndex: number) => {
    return <SystemNode node={node} nodeIndex={nodeIndex} />
  }

  renderUserNode = (node: IUserNode, index: number) => {
    return <UserNode node={node} nodeIndex={index} />
  };

  setExpansionAll = (val: boolean) => {
    let clone = {};
    this.props.nodes.forEach((node) => {
      clone[node.step] = val;
    });
    this.setState({
      nodesExpansion: clone
    });
  }

  collapseAll = () => this.setExpansionAll(false);

  expandAll = () => this.setExpansionAll(true);

  renderSteps = () => {
    let { flow } = this.props;
    if (!!flow.raw) {
      try {
        return this.props.nodes.map((node: INode, index: number) => {
          return (
            <Accordion
              title={node.step}
              border={true}
              open={this.state.nodesExpansion[node.step]}
              key={`node${index}-${this.state.nodesExpansion[node.step] ? 'true' : 'false'}`}>
              {node.nodeType === NodeType.SYSTEM ?
                this.renderSystemNode(node as ISystemNode, index) :
                this.renderUserNode(node as IUserNode, index)
              }
            </Accordion>
          );
        });
      } catch (ex) {
        console.error(ex);
        return (
          <div>Unable to parse flow steps</div>
        );
      }
    } else return null;
  }

  render() {
    const { flow } = this.props;
    const formClass = 'form-control';
    return (
      <div className="body-wrapper">
        <div className="panel-body">
          <Form>
            <FormGroup>
                <FormControl type="text"
                  name="name"
                  value={flow.name}
                  onChange={this.handleNameChange}
                  className={formClass}
                />
            </FormGroup>
            <Button className="btn btn-success btn-md" onClick={() => alert('Not implemented yet.')}>Add interaction</Button>
            <Button className="btn btn-secondary btn-md" onClick={() => this.expandAll()}>Expand</Button>
            <Button className="btn btn-secondary btn-md" onClick={() => this.collapseAll()}>Collapse</Button>
          </Form>
        </div>
        <div className="panel-body">
          {this.renderSteps()}
        </div>
        <div className="body-wrapper">
          <div className="panel-body">
            <Button className="btn btn-success btn-md" onClick={this.handleSave}>Save</Button>
          </div>
          <hr />
          <div className="panel-body">
            <h2>Test Flow</h2>
            <Tooltip message={Msg.DESIGNER_FLOW_TEST_SECTION_DESCRIPTION} />
            <Tabs>
              <Tab label={Msg.DESIGNER_TEST_CALL_LABEL}>
                <DesignerCallTest />
              </Tab>
              <Tab label={Msg.DESIGNER_TEST_FLOW_LABEL}>
                <DesignerFlowTest />
              </Tab>
            </Tabs>
          </div>
        </div>
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
  putFlow,
  updateFlow,
  postFlow
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(DesignerFlow);

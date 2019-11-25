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
  postFlow,
  deleteInteractionNode,
  addEmptyInteractionNode
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
import { Tabs } from '@openmrs/react-components';
import Accordion from '../accordion';
import SystemNode from './flow-node/system-node';
import UserNode from './flow-node/user-node';
import { NodeType } from '../../shared/model/node-type.model';
import { INode } from '../../shared/model/node.model';
import DesignerFlowTest from './test-flow/designer-flow-test';
import { IFlow } from '../../shared/model/flow.model';
import * as Msg from '../../shared/utils/messages';
import Tooltip from '../tooltip';
import { getRenderers } from '../../reducers/renderersReducer';
import { TabWrapper } from '../tab-wrapper';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IconProp } from '@fortawesome/fontawesome-svg-core';
import './designer.scss';
import { NodeUI } from '../../shared/model/node-ui';
import { UserNodeUI } from '../../shared/model/user-node-ui';
import { SystemNodeUI } from '../../shared/model/system-node-ui';

export interface IDesignerFlowProps extends StateProps, DispatchProps, RouteComponentProps<{ flowName: string }> {
};

export interface IDesignerFlowState {
  isNew: boolean;
  nodesExpansion: {};
};

export class DesignerFlow extends React.PureComponent<IDesignerFlowProps, IDesignerFlowState> {

  trashIcon: IconProp = ['far', 'trash-alt'];

  constructor(props) {
    super(props);
    this.state = {
      isNew: !this.props.match.params || !this.props.match.params.flowName,
      nodesExpansion: {}
    };
  }

  componentDidMount = () => {
    this.props.getRenderers();
    if (!!this.state.isNew) {
      this.props.reset();
    } else {
      const { flowName } = this.props.match.params;
      this.props.getFlow(flowName);
    }
  }

  componentWillUpdate(nextProps: IDesignerFlowProps, nextState: IDesignerFlowState) {
    const flowLoaded = this.props.flowLoaded === false && nextProps.flowLoaded === true;
    if (flowLoaded) {
      if (!!this.props.nodes) {
        const initialExpansion = {};
        try {
          nextProps.nodes.forEach((node, i) => {
            initialExpansion[i] = false;
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

  renderSystemNode = (node: SystemNodeUI, nodeIndex: number) => {
    return <SystemNode node={node} nodeIndex={nodeIndex} />
  }

  renderUserNode = (node: UserNodeUI, index: number) => {
    return <UserNode
      initialNode={node}
      nodeIndex={index}
      renderers={this.props.rendererForms}
      currentFlow={this.props.flow} />
  };

  setExpansionAll = (val: boolean) => {
    let clone = {};
    this.props.nodes.forEach((node, id) => {
      clone[id] = val;
    });
    this.setState({
      nodesExpansion: clone
    });
  }

  addInteraction = () => {
    this.props.addEmptyInteractionNode();

    const nodesExpansion = _.clone(this.state.nodesExpansion);
    const length = _.keys(nodesExpansion).length;
    nodesExpansion[length] = true;
    nodesExpansion[length + 1] = true;
    this.setState({
      nodesExpansion
    });
  }

  collapseAll = () => this.setExpansionAll(false);

  expandAll = () => this.setExpansionAll(true);

  toggleExpansion = (index: number) => {
    this.setState((prevState) => {
      const newValues = { ...prevState.nodesExpansion };
      newValues[index] = !prevState.nodesExpansion[index];
      return ({
        nodesExpansion: newValues
      })
    });
  }

  isNotFirstInterationNode(id) {
    return id >= 2;
  }

  isRemovedInteractionNode(id, deletedNodeId) {
    return id === deletedNodeId || id === deletedNodeId + 1;
  }

  shiftNodesExpansion = (deletedNodeId: number) => {
    const { nodesExpansion } = this.state;
    const newNodesExpansion = {};
    Object.keys(nodesExpansion).forEach((key) => {
      const id = parseInt(key);
      if (this.isRemovedInteractionNode(id, deletedNodeId)) {
        newNodesExpansion[id] = false;
        newNodesExpansion[id + 1] = false;
      } else if (id < deletedNodeId) {
        newNodesExpansion[id] = nodesExpansion[id];
      } else if (this.isNotFirstInterationNode(id)) {
        newNodesExpansion[id - 2] = nodesExpansion[id];
      }
    });
    return newNodesExpansion;
  }

  handleRemoveInteraction = (event, index) => {
    event.stopPropagation();
    event.preventDefault();
    const nodesExpansion = this.shiftNodesExpansion(index);
    this.setState({
      nodesExpansion
    }, () => this.props.deleteInteractionNode(index));


  };

  createTitle = (node, index) => {
    return (
      <span>
        {(node.step ? node.step : '')}
        {this.state.nodesExpansion[index] && (node.nodeType === NodeType.USER ?
          <span
            onClick={(event) => this.handleRemoveInteraction(event, index)}
            className="interaction-trash-button"
            title={Msg.DELETE_INTERACTION_NODE}>
            <FontAwesomeIcon
              size="1x"
              icon={this.trashIcon} />
          </span>
          : '')}
      </span>
    );
  }

  renderSteps = () => {
    let { flowLoaded } = this.props;
    if (flowLoaded) {
      try {
        return this.props.nodes.map((node: NodeUI, index: number) => {
          return (
            <div key={`node-${index}`}>
              <Accordion
                handleClick={() => this.toggleExpansion(index)}
                title={this.createTitle(node, index)}
                border={true}
                open={this.state.nodesExpansion[index]}>
                {node.nodeType === NodeType.SYSTEM ?
                  this.renderSystemNode(node as SystemNodeUI, index) :
                  this.renderUserNode(node as UserNodeUI, index)
                }
              </Accordion>
            </div>
          );
        });
      } catch (ex) {
        console.error(ex);
        return (
          <div>Unable to parse flow steps</div>
        );
      }
    } else return Msg.GENERIC_LOADING;
  }

  render() {
    const { flow, loading, flowLoaded } = this.props;
    const formClass = 'form-control';
    const ready = !loading && flowLoaded;
    return (
      <div className="body-wrapper">
        <div className="panel-body">
          <Form>
            <FormGroup controlId="name">
              <FormControl type="text"
                name="name"
                value={flow.name}
                onChange={this.handleNameChange}
                className={formClass}
              />
            </FormGroup>
            <Button className="btn btn-success btn-md" disabled={!ready} onClick={() => this.addInteraction()}>Add interaction</Button>
            <Button className="btn btn-secondary btn-md" disabled={!ready} onClick={() => this.expandAll()}>Expand</Button>
            <Button className="btn btn-secondary btn-md" disabled={!ready} onClick={() => this.collapseAll()}>Collapse</Button>
          </Form>
        </div>
        <div className="panel-body">
          {this.renderSteps()}
        </div>
        <div className="body-wrapper">
          <div className="panel-body">
            <Button className="btn btn-success btn-md" disabled={!ready} onClick={this.handleSave}>Save</Button>
          </div>
          <hr />
          <div className="panel-body">
            <h2>Test Flow</h2>
            <Tooltip message={Msg.DESIGNER_FLOW_TEST_SECTION_DESCRIPTION} />
            <Tabs>
              <TabWrapper key="callTest" label={Msg.DESIGNER_TEST_CALL_LABEL} >
                <DesignerCallTest />
              </TabWrapper>
              <TabWrapper key="flowTest" label={Msg.DESIGNER_TEST_FLOW_LABEL} >
                <DesignerFlowTest />
              </TabWrapper>
            </Tabs>
          </div>
        </div>
      </div>
    );
  }
}

export const mapStateToProps = state => ({
  flow: state.designerReducer.flow,
  flowLoaded: state.designerReducer.flowLoaded,
  nodes: state.designerReducer.nodes,
  loading: state.designerReducer.loading,
  rendererForms: state.renderersReducer.rendererForms
});

const mapDispatchToProps = ({
  reset,
  getConfigs,
  postConfigs,
  getFlows,
  getFlow,
  putFlow,
  updateFlow,
  postFlow,
  getRenderers,
  deleteInteractionNode,
  addEmptyInteractionNode
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(DesignerFlow);

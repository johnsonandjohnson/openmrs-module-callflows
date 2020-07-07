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
import DesignerCallTest from './test-call/designer-call-test';
import {
  Form,
  Button,
  FormGroup,
  FormControl,
  Row,
  Col
} from 'react-bootstrap';
import _ from 'lodash';
import { Tabs } from '@openmrs/react-components';
import Accordion from '../accordion';
import SystemNode from './flow-node/system-node';
import UserNode from './flow-node/user-node';
import { NodeType } from '../../shared/model/node-type.model';
import DesignerFlowTest from './test-flow/designer-flow-test';
import { IFlow } from '../../shared/model/flow.model';
import * as Msg from '../../shared/utils/messages';
import Tooltip from '../tooltip';
import { getRenderers } from '../../reducers/renderersReducer';
import { TabWrapper } from '../tab-wrapper';
import './designer.scss';
import { NodeUI } from '../../shared/model/node-ui';
import { IUserNode } from '../../shared/model/user-node.model';
import { ISystemNode } from '../../shared/model/system-node.model';

export interface IDesignerFlowProps extends StateProps, DispatchProps, RouteComponentProps<{ flowName: string }> {
};

export interface IDesignerFlowState {
  isNew: boolean;
  nodesExpansion: {};
};

export class DesignerFlow extends React.PureComponent<IDesignerFlowProps, IDesignerFlowState> {
  private readonly componentPath = '/designer/edit/';

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
    if (this.state.isNew && !this.props.flow.id && !!nextProps.flow.id) {
      this.props.history.push(this.componentPath + nextProps.flow.name);
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
      this.props.putFlow(this.props.flow, this.props.nodes, () => {
        this.props.history.push(this.componentPath + this.props.flow.name);
      });
    } else {
      this.props.postFlow(this.props.flow, this.props.nodes);
    }
  }

  renderSystemNode = (nodeUI: NodeUI, nodeIndex: number) => {
    return <SystemNode nodeUI={nodeUI} nodeIndex={nodeIndex} />
  }

  renderUserNode = (nodeUI: NodeUI, index: number) => {
    return <UserNode
      initialNodeUI={nodeUI}
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

  changeAccordionTheme = (darkTheme) => {
    darkTheme++;
    if ( darkTheme > 4) {
      darkTheme = 1;
    }
    return darkTheme;
  }

  renderSteps = () => {
    const { flowLoaded } = this.props;
    const { isNew } = this.state;
    if (flowLoaded  || isNew) {
      try {
        let darkTheme = 0;
        return this.props.nodes.map((nodeUI: NodeUI, index: number) => {
          darkTheme = this.changeAccordionTheme(darkTheme);
          const node = nodeUI.model;
          return (
            <Row key={`node-${index}`} className={darkTheme < 3 ? 'darkAccordion' : ''}>
              <Col sm={11}>
                <Accordion
                  handleClick={() => this.toggleExpansion(index)}
                  title={node.step ? node.step : ''}
                  border={true}
                  otherClassNames={node.nodeType === NodeType.SYSTEM ? 'system-node' : 'user-node'}
                  open={this.state.nodesExpansion[index]}>
                  {node.nodeType === NodeType.SYSTEM ?
                    this.renderSystemNode(nodeUI, index) :
                    this.renderUserNode(nodeUI, index)
                  }
                </Accordion>
              </Col>
              {(node.nodeType === NodeType.USER ?
                <Col sm={1}>
                  <i className="medium icon-remove delete-action interaction-trash-button"
                    onClick={(event) => this.handleRemoveInteraction(event, index)}
                    title={Msg.DELETE_INTERACTION_NODE} />
                </Col>
                : '')}
            </Row>
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
    const { isNew } = this.state;
    const formClass = 'form-control';
    const ready = !loading && (flowLoaded || isNew);
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
            <Button className="btn btn-success btn-md add-btn" disabled={!ready} onClick={() => this.addInteraction()}>Add interaction</Button>
            <Button className="btn btn-secondary btn-md sec-btn" disabled={!ready} onClick={() => this.expandAll()}>Expand</Button>
            <Button className="btn btn-secondary btn-md sec-btn" disabled={!ready} onClick={() => this.collapseAll()}>Collapse</Button>
          </Form>
        </div>
        <div className="panel-body">
          {this.renderSteps()}
        </div>
        <div className="body-wrapper">
          <div className="panel-body">
            <Button className="btn btn-success btn-md confirm" disabled={!ready} onClick={this.handleSave}>Save</Button>
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

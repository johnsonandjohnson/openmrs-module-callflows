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
  updateFlow
} from '../../reducers/designer.reducer';
import { IRootState } from '../../reducers';
import DesignerFlowTest from './designer-flow-test';
import {
  Form,
  Button,
  FormGroup,
  FormControl
} from 'react-bootstrap';

import { Accordion } from '@openmrs/react-components';
import SystemNode from './flow-node/system-node';
import { ISystemNode } from '../../shared/model/system-node.model';
import { NodeType } from '../../shared/model/node-type.model';
import { INode } from '../../shared/model/node.model';

export interface IDesignerFlowProps extends StateProps, DispatchProps, RouteComponentProps<{ flowName: string }> {
};

export interface IDesignerFlowState {
  isNew: boolean;
};

export class DesignerFlow extends React.PureComponent<IDesignerFlowProps, IDesignerFlowState> {
  constructor(props) {
    super(props);
    this.state = {
      isNew: !this.props.match.params || !this.props.match.params.flowName
    };
  }

  componentDidMount = () => {
    if (!this.state.isNew) {
      const { flowName } = this.props.match.params;
      this.props.getFlow(flowName);
    }
  }

  handleNameChange = () => {
    alert('Not implemented yet.');
    //TODO: OCALL-50: Add name changing via redux
  }

  handleSave = () => {
    this.props.updateFlow(this.props.flow, this.props.nodes);
  }

  renderStepElement = (node: any, elementName: string, label: string) => {
    //TODO: OCALL-70: Refactor or remove it, it's unused
    if (!!node.templates[elementName]) {
      return (
        <div>
          <label>{label}:</label>
          <p>{node.templates[elementName].content}</p>
        </div>
      );
    } else return null;
  }

  renderSystemNode = (node: ISystemNode, nodeIndex: number) => {
    return <SystemNode node={node} nodeIndex={nodeIndex} />
  }

  renderSteps = () => {
    let { flow } = this.props;
    if (!!flow.raw) {
      try {
        return this.props.nodes.map((node: INode, index: number) => {
          return (
            <div>
              <Accordion
                key={`node${index}`}
                title={node.step}
                border={true}
                open={false}>
                {node.nodeType === NodeType.SYSTEM ? this.renderSystemNode(node as ISystemNode, index) : ''}
              </Accordion>
            </div>
          );
        });
      } catch (ex) {
        return (
          <div>Unable to parse flow steps</div>
        );
      }
    } else return null;
  }

  render() {
    const { flow } = this.props;
    const formClass = 'form-control';
    const errorFormClass = formClass + ' error-field';
    return (
      <div className="body-wrapper">
        <div className="panel-body">
          <h2>Test Call Flow</h2>
          <DesignerFlowTest />
        </div>
        <hr />
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
            <Button className="btn btn-success btn-md" onSubmit={() => alert('Not implemented yet.')}>Add interaction</Button>
            <Button className="btn btn-secondary btn-md" >Expand</Button>
            <Button className="btn btn-secondary btn-md" >Collapse</Button>
          </Form>
        </div>
        <hr />
        <div className="panel-body">
          {this.renderSteps()}
        </div>
        <div className="body-wrapper">
          <div className="panel-body">
          <Button className="btn btn-success btn-md" onClick={this.handleSave}>Save</Button>
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
  updateFlow
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(DesignerFlow);

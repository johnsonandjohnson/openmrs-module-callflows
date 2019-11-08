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
  getFlow
} from '../../reducers/designer.reducer';
import { IRootState } from '../../reducers';
import DesignerTable from './designer-table';
import DesignerFlowTest from './designer-flow-test';
import {
  Form,
  Button,
  FormGroup,
  FormControl
} from 'react-bootstrap';

import { Accordion } from '@openmrs/react-components';

export interface IDesignerFlowProps extends StateProps, DispatchProps, RouteComponentProps<{ flowName: string }> {
};
export interface IDesignerFlowState {

};

export class DesignerFlow extends React.PureComponent<IDesignerFlowProps, IDesignerFlowState> {

  componentDidMount = () => {
    const { flowName } = this.props.match.params;
    this.props.getFlow(flowName);
  }

  handleNameChange = () => {
    alert('Not implemented yet.');
    //TODO: Add name changing via redux
  }


  render() {
    const title = 'Designer';
    const { flow } = this.props;

    const formClass = 'form-control';
    const errorFormClass = formClass + ' error-field';
    return (
      <div className="body-wrapper">
        <div className="panel-body">
          <h2>Test Call Flow</h2>
          <DesignerFlowTest />
        </div>
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
          {JSON.parse(flow.raw).nodes.map((node: any) => {
            <Accordion title={node.step}
              border={true}
              open={false}>
              {node}
            </Accordion>
          })}

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
  getFlow
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(DesignerFlow);


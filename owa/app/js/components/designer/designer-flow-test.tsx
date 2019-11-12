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

} from '../../reducers/designer.reducer';
import { IRootState } from '../../reducers';
import {
  Form,
  Button,
  FormGroup,
  FormControl
} from 'react-bootstrap';
import TextLabel from '../text-label';
import * as Msg from '../../shared/utils/messages';
import { validateForm, validateField } from '../../shared/utils/validation-util'
import { IFlowTestError, validationSchema } from '../../shared/model/flow-test.model';
import ErrorDesc from '../error-desc';

export interface IDesignerFlowTestProps extends StateProps, DispatchProps, RouteComponentProps<{ flowName: string }> {
  flowName?: string
};

export interface IDesignerFlowTestState {
  configuration: string, //TODO: move to form
  extension: string,
  phoneNumber: string
  errors?: IFlowTestError,
};

export class DesignerFlowTest extends React.PureComponent<IDesignerFlowTestProps, IDesignerFlowTestState> {
  constructor(props) {
    super(props);
    this.state = {
      configuration: '',
      extension: '',
      phoneNumber: '',
      errors: undefined
    }
  }

  componentDidMount = () => {
    if (!!this.props.flowName) { //by default this.props.flow from parent is used
      this.props.getFlow(this.props.flowName);
    }
  }

  handleConfigurationChange = (e) => {
    const newValue = e.target.value;
    const form = {
      configuration: newValue
    };
    validateField(form, 'configuration', validationSchema)
      .then(() => {
        this.setState({
          ...this.state,
          configuration: newValue,
          errors: undefined
        });
      })
      .catch((errors) => {
        this.setState({
          ...this.state,
          configuration: newValue,
          errors
        });
      });
  }

  handleExtensionChange = (e) => {
    const newValue = e.target.value;
    const form = {
      extension: newValue
    };
    validateField(form, 'extension', validationSchema)
      .then(() => {
        this.setState({
          ...this.state,
          extension: newValue,
          errors: undefined
        });
      })
      .catch((errors) => {
        this.setState({
          ...this.state,
          extension: newValue,
          errors
        });
      });
  }

  handlePhoneNumberChange = (e) => {
    const newValue = e.target.value;
    const form = {
      phoneNumber: newValue
    };
    validateField(form, 'phoneNumber', validationSchema)
      .then(() => {
        this.setState({
          ...this.state,
          phoneNumber: newValue,
          errors: undefined
        });
      })
      .catch((errors) => {
        this.setState({
          ...this.state,
          phoneNumber: newValue,
          errors
        });
      });
  }

  handleSubmit = (e) => {
    e.preventDefault();
    validateForm(this.state, validationSchema)
      .then(() => {
        //TODO: make AXIOS request in reducer
      })
      .catch((errors) => {
        this.setState({
          ...this.state,
          errors
        });
      });
  }

  renderError(fieldName: string) {
    if (this.state.errors) {
      return <ErrorDesc field={this.state.errors[fieldName]} />
    }
  }

  render() {
    const formClass = 'form-control';
    const errorFormClass = formClass + ' error-field';
    const { errors } = this.state;

    return (
      <Form className="form" onSubmit={this.handleSubmit}>
        <FormGroup controlId={"formName"}>
          <TextLabel text={Msg.DESIGNER_FLOW_TEST_CONFIGURATION_LABEL} isMandatory={false} isWithColon={true} />
          {/* <Tooltip message={Msg.MAPPING_NAME_DESC} /> */}
          <FormControl type="text"
            name='name'
            value={this.state.configuration}
            onChange={this.handleConfigurationChange}
            className={errors && errors.configuration ? errorFormClass : formClass} />
          {this.renderError('configuration')}
        </FormGroup>
        <FormGroup controlId={"formName"}>
          <TextLabel text={Msg.DESIGNER_FLOW_TEST_EXTENSION_LABEL} isMandatory={false} isWithColon={true} />
          {/* <Tooltip message={Msg.MAPPING_NAME_DESC} /> */}
          <FormControl type="text"
            name='name'
            value={this.state.extension}
            onChange={this.handleExtensionChange}
            className={errors && errors.extension ? errorFormClass : formClass} />
          {this.renderError('extension')}
        </FormGroup>
        <FormGroup controlId={"formName"}>
          <TextLabel text={Msg.DESIGNER_FLOW_TEST_PHONE_NUMBER_LABEL} isMandatory={false} isWithColon={true} />
          {/* <Tooltip message={Msg.MAPPING_NAME_DESC} /> */}
          <FormControl type="text"
            name='name'
            value={this.state.phoneNumber}
            onChange={this.handlePhoneNumberChange}
            className={errors && errors.phoneNumber ? errorFormClass : formClass} />
          {this.renderError('phoneNumber')}
        </FormGroup>
        <Button className="btn btn-primary btn-md" type="submit">Test Call Flow</Button>
      </Form>
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
)(DesignerFlowTest);

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
  getConfigs,
  getFlow,
  makeTestCall
} from '../../../reducers/designer.reducer';
import { IRootState } from '../../../reducers';
import {
  Form,
  Button,
  FormGroup,
  FormControl
} from 'react-bootstrap';
import TextLabel from '../../text-label';
import Tooltip from '../../tooltip';
import * as Msg from '../../../shared/utils/messages';
import { validateForm, validateField } from '../../../shared/utils/validation-util'
import { IFlowCallError, validationSchema } from '../../../shared/model/flow-test.model';
import ErrorDesc from '@bit/soldevelo-omrs.cfl-components.error-description';
import { CONFIG_EXTENSIONS } from '../../../constants';
import { handleCarret } from '../../../shared/utils/form-handling-util';
import 'react-chat-elements/dist/main.css';

export interface IDesignerCallTestProps extends StateProps, DispatchProps, RouteComponentProps<{ flowName: string }> {
  flowName?: string
};

export interface IDesignerCallTestState {
  configuration: string,
  extension: string,
  phoneNumber: string
  errors?: IFlowCallError,
};

export class DesignerCallTest extends React.PureComponent<IDesignerCallTestProps, IDesignerCallTestState> {
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
    this.props.getConfigs();
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

  handlePhoneNumberChange = (e: any) => {
    handleCarret(e);
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
        const flowName = this.props.flowName ? this.props.flowName : this.props.flow.name as string;
        this.props.makeTestCall(
          this.state.configuration,
          flowName,
          this.state.phoneNumber,
          this.state.extension
        );
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
        <FormGroup controlId="general-description">
          <Tooltip message={Msg.DESIGNER_TEST_CALL_GENERAL_DESCRIPTION} />
        </FormGroup>
        <FormGroup controlId="configuration">
          <TextLabel text={Msg.DESIGNER_TEST_CALL_CONFIGURATION_LABEL} isMandatory={false} isWithColon={true} />
          <FormControl componentClass="select" name="configuration"
            value={this.state.configuration}
            onChange={this.handleConfigurationChange}
            className={errors && errors.configuration ? errorFormClass : formClass}>
            <option hidden />
            {this.props.configForms.map(item => {
              return (<option value={item.config.name} key={item.config.name}>{item.config.name}</option>);
            })}
          </FormControl>
          {this.renderError('configuration')}
        </FormGroup>
        <FormGroup controlId="extension">
          <TextLabel text={Msg.DESIGNER_TEST_CALL_EXTENSION_LABEL} isMandatory={false} isWithColon={true} />
          <FormControl componentClass="select" name="extension"
            value={this.state.extension}
            onChange={this.handleExtensionChange}
            className={errors && errors.extension ? errorFormClass : formClass}>
            <option hidden />
            {CONFIG_EXTENSIONS.map(extension => {
              return (<option value={extension} key={extension}>{extension}</option>);
            })}
          </FormControl>
          {this.renderError('extension')}
        </FormGroup>
        <FormGroup controlId="phoneNumber">
          <TextLabel text={Msg.DESIGNER_TEST_CALL_PHONE_NUMBER_LABEL} isMandatory={false} isWithColon={true} />
          <FormControl type="text"
            name="phoneNumber"
            value={this.state.phoneNumber}
            onChange={this.handlePhoneNumberChange}
            className={errors && errors.phoneNumber ? errorFormClass : formClass} />
          {this.renderError('phoneNumber')}
        </FormGroup>
        <Button className="btn btn-primary btn-md" type="submit">Initiate Test Call</Button>
      </Form>
    );
  }
}

export const mapStateToProps = ({ designerReducer }: IRootState) => (designerReducer);

const mapDispatchToProps = ({
  getConfigs,
  getFlow,
  makeTestCall
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(DesignerCallTest);

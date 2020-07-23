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
  resetTestRunner,
  sendMessage,
  initiateTestFlow
} from '../../../reducers/designer.reducer';
import { IRootState } from '../../../reducers';
import {
  Button
} from 'react-bootstrap';
import { MessageList } from 'react-chat-elements';
import 'react-chat-elements/dist/main.css';
import { Input } from 'react-chat-elements';
import UserMessage from '../../../shared/model/user-message.model';
import * as Default from '../../../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import Tooltip from '../../tooltip';

export interface IDesignerFlowTestProps extends StateProps, DispatchProps, RouteComponentProps<{ flowName: string }> {
  flowName?: string,
};

export interface IDesignerFlowTestState {
  configuration: string,
  shouldInitiate: boolean
};

export class DesignerFlowTest extends React.PureComponent<IDesignerFlowTestProps, IDesignerFlowTestState> {
  constructor(props) {
    super(props);
    this.state = {
      configuration: '',
      shouldInitiate: true
    }
  }

  componentDidMount = () => {
    this.props.resetTestRunner();
    if (!!this.props.flowName) {
      this.props.getFlow(this.props.flowName);
    }
    this.props.getConfigs();
  }

  componentWillReceiveProps(nextProps: IDesignerFlowTestProps) {
    const configs = nextProps.configForms;
    const flowName = !!nextProps.flowName ? nextProps.flowName : nextProps.flow.name;
    const isReadyToInitiate = this.state.shouldInitiate && configs.length > 0 && !!flowName;
    if (isReadyToInitiate) {
      this.props.initiateTestFlow(configs[0].config.name, flowName!);
      this.setState({
        ...this.state,
        shouldInitiate: false
      });
    }
  }

  sendMessage = () => {
    let input: any = this.refs.messageInput;
    let message = input.input.value;
    this.props.sendMessage(new UserMessage('You', message, new Date()));
    input.clear();
  }

  renderInputFields = () => {
    let isInputExpected = this.props.continueFieldProps.currentNodeFields.length;
    if (isInputExpected) {
      return (
        <Input
          ref="messageInput"
          placeholder="Type here..."
          rightButtons={
            <Button className="btn btn-primary btn-md" onClick={this.sendMessage}>Send</Button>
          } />
      );
    } else {
      return (
        <Input
          className="disabled"
          rightButtons={
            <Button className="btn btn-secondary btn-md" >Send</Button>
          } />
      );
    }
  }

  render() {
    return (
      <div>
        <Tooltip message={getIntl().formatMessage({ id: 'CALLFLOW_DESIGNER_TEST_FLOW_GENERAL_DESCRIPTION', defaultMessage: Default.DESIGNER_TEST_FLOW_GENERAL_DESCRIPTION })} />
        <div className="flow-test-container">
          <MessageList
            className="message-list"
            lockable={true}
            toBottomHeight={'100%'}
            dataSource={this.props.messages} />
          {this.renderInputFields()}
        </div>
      </div>
    );
  }
}

export const mapStateToProps = ({ designerReducer }: IRootState) => (designerReducer);

const mapDispatchToProps = ({
  getConfigs,
  getFlow,
  sendMessage,
  initiateTestFlow,
  resetTestRunner
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(DesignerFlowTest);

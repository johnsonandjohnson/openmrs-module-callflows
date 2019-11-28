import React, { isValidElement } from 'react';
import { IElement, ElementType, FieldType } from '../../../shared/model/element.model';
import { Controlled as CodeMirror } from 'react-codemirror2';
import { connect } from 'react-redux';
import { Row, Col, FormGroup, FormControl, ControlLabel, HelpBlock } from 'react-bootstrap';
import { Tabs } from '@openmrs/react-components';
import * as Msg from '../../../shared/utils/messages';
import FormSectionField from './form-section-field';
import FormSectionCheckbox from './form-section-checkbox';
import TabWrapper from '../../tab-wrapper';
import './flow-node.scss';

interface IProps {
  element: IElement;
  elementIndex: number;
  update: (element: IElement) => void;
}

interface IState {
  txtValue: string;
  noInputValue?: string;
  noMatchValue?: string;
  exitValue?: string;
}

export class FormSection extends React.Component<IProps, IState> {

  constructor(props: IProps) {
    super(props);
    this.state = {
      txtValue: props.element.txt,
      noInputValue: props.element.noInput,
      noMatchValue: props.element.noMatch,
      exitValue: props.element.goodBye
    }
  }

  componentDidUpdate = (prevProps: IProps) => {
    if (prevProps.element.txt !== this.props.element.txt) {
      this.setState({
        txtValue: this.props.element.txt
      });
    }
  };

  options = {
    lineNumbers: true,
    lineWrapping: true,
    addModeClass: true,
    tabSize: 2,
    mode: 'velocity', // may be: javascript,xml,sql,velocity
    theme: 'default',
    extraKeys: {
      'F11': function (cm) {
        cm.setOption('fullScreen', !cm.getOption('fullScreen'));
      },
      'Esc': function (cm) {
        if (cm.getOption('fullScreen')) cm.setOption('fullScreen', false);
      }
    }
  };

  handleCodeChange = (editor, data, value, fieldName: string) => this.handleChange(value, fieldName);

  handleChange = (value, fieldName: string) => {
    const element = this.props.element;
    element[fieldName] = value;
    this.props.update(element);
  }

  renderField = () => {
    const { elementIndex, element } = this.props;
    const handleChange = this.handleChange;

    return (
      <>
        <Row>
          <FormSectionField {...{ elementIndex, handleChange }} name="name" value={element.name} />
          <FormSectionField {...{ elementIndex, handleChange }}
            name="fieldType"
            value={element.fieldType}
            componentClass="select" >
            {Object.keys(FieldType).map(k => <option key={k} value={FieldType[k]}>{FieldType[k]}</option>)}
          </FormSectionField>
        </Row>
        <Row>
          <FormSectionField {...{ elementIndex, handleChange }} name="reprompt" value={element.reprompt} help={Msg.FLOW_REPROMPT_HELP} />
          {element.fieldType == FieldType.DIGITS &&
            <FormSectionField {...{ elementIndex, handleChange }}
              name="fieldMeta"
              value={element.fieldMeta}
              label={Msg.FLOW_RANGE_LABEL}
              help={Msg.FLOW_RANGE_HELP} />
          }
        </Row>
        <Row>
          <Col md={6}>
            <Row className="checkbox-container left-code-wrapper">
              <FormSectionCheckbox {...{ elementIndex, handleChange }} name="bargeIn" value={element.bargeIn} />
              <FormSectionCheckbox {...{ elementIndex, handleChange }} name="dtmf" value={element.dtmf} />
              <FormSectionCheckbox {...{ elementIndex, handleChange }} name="voice" value={element.voice} />
            </Row>
            <Row className="left-code-wrapper">
              <CodeMirror
                value={this.state.txtValue}
                options={this.options}
                onBeforeChange={(editor, data, value) => this.setState({ txtValue: value })}
                onChange={(editor, data, value) => this.handleCodeChange(editor, data, value, 'txt')}
              />
            </Row>
            <Row>
              <FormSectionField md={12} {...{ elementIndex, handleChange }} name="dtmfGrammar" value={element.dtmfGrammar} />
            </Row>
          </Col>
          <Col md={6}>
            <Row className="right-code-wrapper">
              <Tabs>
                <TabWrapper key="noInput" label={Msg.FLOW_NO_INPUT_LABEL}>
                  <CodeMirror
                    value={this.state.noInputValue ? this.state.noInputValue : ''}
                    options={this.options}
                    onBeforeChange={(editor, data, value) => this.setState({ noInputValue: value })}
                    onChange={(editor, data, value) => this.handleCodeChange(editor, data, value, 'noInput')}
                  />
                </TabWrapper>
                <TabWrapper key="noMatch" label={Msg.FLOW_NO_MATCH_LABEL}>
                  <CodeMirror
                    value={this.state.noMatchValue ? this.state.noMatchValue : ''}
                    options={this.options}
                    onBeforeChange={(editor, data, value) => this.setState({ noMatchValue: value })}
                    onChange={(editor, data, value) => this.handleCodeChange(editor, data, value, 'noMatch')}
                  />
                </TabWrapper>
                <TabWrapper key="exit" label={Msg.FLOW_EXIT_LABEL}>
                  <CodeMirror
                    value={this.state.exitValue ? this.state.exitValue : ''}
                    options={this.options}
                    onBeforeChange={(editor, data, value) => this.setState({ exitValue: value })}
                    onChange={(editor, data, value) => this.handleCodeChange(editor, data, value, 'exit')}
                  />
                </TabWrapper>
              </Tabs>
            </Row>
            <Row>
              <FormSectionField md={12} {...{ elementIndex, handleChange }} name="voiceGrammar" value={element.voiceGrammar} />
            </Row>
          </Col>
        </Row>
      </>
    );
  };

  renderTxt = () =>
    <CodeMirror
      value={this.state.txtValue}
      options={this.options}
      onBeforeChange={(editor, data, value) => this.setState({ txtValue: value })}
      onChange={(editor, data, value) => this.handleCodeChange(editor, data, value, 'txt')}
    />

  render() {
    const { element } = this.props;
    return element.type === ElementType.FIELD ?
      this.renderField() : this.renderTxt();
  };
}

export default connect()(FormSection);

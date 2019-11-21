import React from 'react';
import { IElement, ElementType } from '../../../shared/model/element.model';
import { Controlled as CodeMirror } from 'react-codemirror2';
import { connect } from 'react-redux';
import { Row, Col, FormGroup, FormControl, ControlLabel, Checkbox, HelpBlock } from 'react-bootstrap';
import { Tabs } from '@openmrs/react-components';
import * as Msg from '../../../shared/utils/messages';

interface IProps {
  name: string;
  elementIndex: number;
  md?: number;
  value?: boolean;
  handleChange: (value, fieldName: string) => void;
}

export class FormSectionCheckbox extends React.Component<IProps> {
  public static defaultProps = {
    md: 2
  };

  render = () =>
    <Col md={this.props.md}>
      <FormGroup controlId={`element_name_${this.props.name}-${this.props.elementIndex}`}>
        <Checkbox name={this.props.name} checked={this.props.value}
          onChange={e => this.props.handleChange(e.target.checked, this.props.name)}>
          {Msg[`FLOW_${this.props.name.toUpperCase()}_LABEL`]}
        </Checkbox>
      </FormGroup>
    </Col>
}

export default connect()(FormSectionCheckbox);

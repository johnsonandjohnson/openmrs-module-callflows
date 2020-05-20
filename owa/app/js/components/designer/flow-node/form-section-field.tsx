import React from 'react';
import { connect } from 'react-redux';
import { Col, FormGroup, FormControl, ControlLabel, HelpBlock } from 'react-bootstrap';
import * as Msg from '../../../shared/utils/messages';

interface IProps {
  name: string;
  elementIndex: number;
  type?: string;
  md?: number;
  label?: string;
  help?: string;
  componentClass?: React.ReactType;
  value?: string;
  handleChange: (value, fieldName: string) => void;
}

export class FormSectionField extends React.Component<IProps> {
  public static defaultProps = {
    type: "text",
    md: 6
  };

  render = () =>
    <Col md={this.props.md}>
      <FormGroup controlId={`element_name_${this.props.name}-${this.props.elementIndex}`}>
        <ControlLabel>{`${this.props.label ? this.props.label : Msg[`FLOW_${this.props.name.toUpperCase()}_LABEL`]}:`}</ControlLabel>
        {this.props.help && <HelpBlock>{this.props.help}</HelpBlock>}
        <FormControl
          componentClass={this.props.componentClass}
          type={this.props.type}
          name={this.props.name}
          value={this.props.value}
          onChange={(e: any) => this.props.handleChange(e.target.value, this.props.name)}
        >
          {this.props.children}
        </FormControl>
      </FormGroup>
    </Col>
}

export default connect()(FormSectionField);

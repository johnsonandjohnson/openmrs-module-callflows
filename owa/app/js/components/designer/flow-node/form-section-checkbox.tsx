import React from 'react';
import { connect } from 'react-redux';
import { Col, FormGroup, Checkbox } from 'react-bootstrap';
import * as Default from '../../../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";

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
          onChange={(e: any) => this.props.handleChange(e.target.checked, this.props.name)}>
          {getIntl().formatMessage({ id: 'CALLFLOW_' + `FLOW_${this.props.name.toUpperCase()}_LABEL`, defaultMessage: Default[`FLOW_${this.props.name.toUpperCase()}_LABEL`] })}
        </Checkbox>
      </FormGroup>
    </Col>
}

export default connect()(FormSectionCheckbox);

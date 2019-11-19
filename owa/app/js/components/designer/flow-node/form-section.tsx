import React from 'react';
import { IElement, ElementType } from '../../../shared/model/element.model';
import { Controlled as CodeMirror } from 'react-codemirror2';
import { connect } from 'react-redux';

interface IProps {
  element: IElement;
  elementIndex: number;
  update: (element: IElement) => void;
}

interface IState {
  txtValue: string;
}

export class FormSection extends React.PureComponent<IProps, IState> {

  constructor(props: IProps) {
    super(props);
    this.state = {
      txtValue: props.element.txt
    }
  }

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

  handleCodeMirrorValueChange = (editor, data, value, fieldName: string) => this.handleFieldValuesChange(value, fieldName);

  handleFieldValuesChange = (value, fieldName: string) => {
    const element = this.props.element;
    element[fieldName] = value;
    this.props.update(element);
  }

  renderTxt = () =>
    <CodeMirror
      value={this.state.txtValue}
      options={this.options}
      onBeforeChange={(editor, data, value) => this.setState({ txtValue: value })}
      onChange={(editor, data, value) => this.handleCodeMirrorValueChange(editor, data, value, 'txt')}
    />

  render = () => {
    const { element } = this.props;
    return element.type === ElementType.FIELD ?
      null : this.renderTxt(); // todo OCALL-71: Extend form with 'Filed' entry type
  };
}

export default connect()(FormSection);
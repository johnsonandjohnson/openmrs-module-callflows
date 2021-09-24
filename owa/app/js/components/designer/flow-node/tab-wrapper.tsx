import React from 'react';
import { connect } from 'react-redux';

interface IProps {
  label: string;
}

export class TabWrapper extends React.Component<IProps> {
  render = () => <React.Fragment />
}

export default connect()(TabWrapper);

import React from 'react';
import '@openmrs/react-components/assets/css/accordion.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IconProp } from '@fortawesome/fontawesome-svg-core';

interface IProps {
  title: React.ReactNode;
  border?: boolean;
  open?: boolean;
  default?: boolean;
  handleClick: () => void
}

interface IState {
  visible?: boolean;
  hovering: boolean;
}

export default class Accordion extends React.PureComponent<IProps, IState> {

  defaultIcon: IconProp = ['fas', 'star'];
  nonDefaultIcon: IconProp = ['far', 'star'];

  constructor(props: IProps) {
    super(props);
    this.state = {
      visible: this.props.open === undefined ? false : this.props.open,
      hovering: false
    };
  }

  componentDidUpdate = (prevProps, prevState) => {
    if (this.props.open != prevState.open) {
      this.setState({
        visible: this.props.open
      });
    }
  }

  handleMouseHover = () => this.setState((prevState: IState) => ({
    ...prevState,
    hovering: !prevState.hovering
  }));

  defaultHandleClick = () => {
    this.setState((prevState: IState) => ({ visible: !prevState.visible }));
  }

  handleClick = () => {
    if (!!this.props.handleClick) {
      this.props.handleClick();
    } else {
      this.defaultHandleClick();
    }
  }

  render = () => {
    return (
      <div className={`accordion ${this.props.border ? 'border' : ''}`}>
        <div
          className="header"
          onClick={this.handleClick}
          role="button"
          tabIndex={0}
          onMouseEnter={() => this.handleMouseHover()}
          onMouseLeave={() => this.handleMouseHover()}
        >
          <a>
            <span>
              <FontAwesomeIcon
                className={`${this.state.visible ? 'rotate90' : ''}`}
                size="1x"
                icon={['fas', 'chevron-right',]} />
            </span>
            &nbsp;&nbsp;
					{this.props.title}
          </a>
        </div>
        <div className={`content ${!this.state.visible ? 'close' : 'open'}`}>
          {this.props.children}
        </div>
      </div>
    );
  }
}

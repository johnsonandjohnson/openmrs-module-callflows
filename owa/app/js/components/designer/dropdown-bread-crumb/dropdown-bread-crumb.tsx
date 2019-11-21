import React from 'react';

import './dropdown-bread-crumb.scss';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Navbar, Nav, NavItem, NavDropdown, MenuItem } from 'react-bootstrap';

import { IBlock } from '../../../shared/model/block.model';
import { IElement } from '../../../shared/model/element.model';

interface IProps {
  blocks: ReadonlyArray<IBlock>;
  selectedBlock?: IBlock;
  selectedElement?: IElement;
  blockChangedCallback: (index: number) => void;
  blockRemovedCallback: (index: number) => void;
  elementChangedCallback: (index: number) => void;
  elementRemovedCallback: (index: number) => void;
}

export class DropdownBreadCrumb extends React.Component<IProps> {

  onBlockChange = (selectedIndex: number) => this.props.blockChangedCallback(selectedIndex);

  onElementChange = (selectedIndex: number) => {
    const { selectedBlock } = this.props;
    selectedBlock && this.props.elementChangedCallback(selectedIndex);
  };

  onBlockRemove = (index: number, event) => {
    event.preventDefault();
    event.stopPropagation();
    this.props.blockRemovedCallback(index);
  };

  onElementRemove = (index: number, event) => {
    event.preventDefault();
    event.stopPropagation();
    this.props.elementRemovedCallback(index);
  };

  renderHomeCrumb = () =>
    <a className="breadcrumb-link-item">
      <FontAwesomeIcon icon={['fas', 'home']} />
    </a>

  renderDelimiter = () =>
    <span className="breadcrumb-link-item">
      <FontAwesomeIcon size="xs" icon={['fas', 'chevron-right']} />
    </span>

  renderMenuItem = (text: string, index: number, onChange:(index: number) => void, onRemove: (index: number, event) => void) =>
    <MenuItem key={`breadcrumb-menu-item-${index}`} className="breadcrumb-menu-item" onClick={e => onChange(index)}>
      {text}
      <span className="breadcrumb-menu-item-icon" onClick={e => onRemove(index, e)}>
        <FontAwesomeIcon size="xs" icon={['fas', 'times']} />
      </span>
    </MenuItem>

  render = () => {
    const { selectedBlock, selectedElement } = this.props;
    return (
      <Navbar className="breadcrumb">
        <Nav>
          <NavItem className="breadcrumb-link-item">
            <FontAwesomeIcon icon={['fas', 'home']} />
          </NavItem>
          {selectedBlock && (
            <>
              <NavDropdown id={selectedElement && selectedElement.name} title={selectedBlock.name} className="breadcrumb-link-item">
                {this.props.blocks.map((block: IBlock, index: number) => 
                  this.renderMenuItem(block.name, index, this.onBlockChange, this.onBlockRemove))}
              </NavDropdown>
              {selectedElement && (
                <>
                  <NavItem className="breadcrumb-link-item">
                    <FontAwesomeIcon size="xs" icon={['fas', 'chevron-right']} />
                  </NavItem>
                  <NavDropdown id={selectedElement && selectedElement.name} title={selectedElement.name} className="breadcrumb-link-item">
                    {selectedBlock.elements && selectedBlock.elements
                      .map((element: IElement, index: number) =>
                        this.renderMenuItem(element.name, index, this.onElementChange, this.onElementRemove))}
                  </NavDropdown>
                </>
              )}
            </>
          )}
        </Nav>
      </Navbar>
    );
  };
}
import React from 'react';
import { connect } from 'react-redux';
import { updateNode } from '../../../reducers/designer.reducer';
import { IUserNode } from '../../../shared/model/user-node.model';
import { RouteComponentProps } from 'react-router';
import { IBlock } from '../../../shared/model/block.model';
import { IElement, defaultTxtValue, defaultFieldValue } from '../../../shared/model/element.model';
import { Row, Col, FormGroup, FormControl, Checkbox, Form } from 'react-bootstrap';
import { DropdownBreadCrumb } from '../dropdown-bread-crumb/dropdown-bread-crumb';
import AddButton from '../../add-button';
import { FormSection } from './form-section';
import { RenderedSections } from './rendered-section/rendered-sections';

interface IProps extends DispatchProps, RouteComponentProps<{ flowName: string }> {
  node: IUserNode;
  nodeIndex: number;
}

interface IState {
  selectedBlock?: IBlock;
  selectedBlockIndex: number;
  selectedElement?: IElement;
  selectedElementIndex: number;
  nodeStep: string;
}

class UserNode extends React.Component<IProps, IState> {

  constructor(props: IProps) {
    super(props);
    const selectedBlock = this.getFirstBlockFromNode(props.node);
    const selectedElement = this.getFirstElementFromBlock(selectedBlock);
    this.state = {
      selectedBlock,
      selectedBlockIndex: 0,
      selectedElement,
      selectedElementIndex: 0,
      nodeStep: props.node.step
    }
  }

  getFirstBlockFromNode = (node: IUserNode) => node.blocks && node.blocks.length > 0 ? node.blocks[0] : undefined;

  getFirstElementFromBlock = (block: IBlock | undefined) => block && block.elements.length > 0 ?
    block.elements[0] : undefined

  onSelectedBlockChange = (selectedBlockIndex: number) => {
    const selectedBlock = this.props.node.blocks[selectedBlockIndex];
    const selectedElement = this.getFirstElementFromBlock(selectedBlock);
    this.setState({
      selectedBlock,
      selectedBlockIndex,
      selectedElement,
      selectedElementIndex: 0
    });
  };

  onSelectedElementChange = (selectedElementIndex: number) =>
    this.setState((prevState: IState) => ({
      selectedElement: prevState.selectedBlock ? prevState.selectedBlock.elements[selectedElementIndex] : undefined,
      selectedElementIndex
    }));

  onBlockRemove = (indexToRemove: number) => {
    const { node } = this.props;
    node.blocks.splice(indexToRemove, 1);
    const selectedBlock = this.getFirstBlockFromNode(node);
    this.setState({
      selectedBlock,
      selectedBlockIndex: 0,
      selectedElement: this.getFirstElementFromBlock(selectedBlock),
      selectedElementIndex: 0
    }, () => this.props.updateNode(node, this.props.nodeIndex));
  };

  onElementRemove = (indexToRemove: number) => {
    const { node } = this.props;
    node.blocks[this.state.selectedBlockIndex].elements.splice(indexToRemove, 1);
    const updatedElements = node.blocks[this.state.selectedBlockIndex].elements;
    this.setState({
      selectedElement: updatedElements.length > 0 ? updatedElements[0] : undefined,
      selectedElementIndex: 0
    }, () => this.props.updateNode(node, this.props.nodeIndex));
  };

  updateNode = (element: IElement) => {
    const { selectedBlock } = this.state;
    if (selectedBlock && this.state.selectedElement) {
      selectedBlock.elements[this.state.selectedElementIndex] = element;
      const { node } = this.props;
      node.blocks[this.state.selectedBlockIndex] = selectedBlock;
      this.setState({
        selectedBlock,
        selectedElement: element
      }, () => this.props.updateNode(node, this.props.nodeIndex));
    }
  };

  onNodeStepChange = event => {
    const { node } = this.props;
    const nodeStep = event.target.value;
    node.step = nodeStep;
    this.setState({
      nodeStep
    }, () => this.props.updateNode(node, this.props.nodeIndex));
  };

  addBlock = () => {
    const { node } = this.props;
    const block: IBlock = {
      name: 'Form',
      type: 'form',
      elements: []
    };
    node.blocks.push(block);
    this.setState({
      selectedBlock: block,
      selectedElementIndex: 0
    }, () => this.props.updateNode(node, this.props.nodeIndex));
  }

  addElement = (element: IElement) => {
    const { selectedBlock } = this.state;
    if (selectedBlock) {
      const node = this.props.node;
      selectedBlock.elements.push(element);
      node.blocks[this.state.selectedBlockIndex] = selectedBlock;
      this.setState({
        selectedElement: element,
        selectedElementIndex: selectedBlock.elements.length - 1
      }, () => this.props.updateNode(node, this.props.nodeIndex));
    }
  };

  addTTS = () => {
    this.addElement({ ...defaultTxtValue });
  }

  addField = () => {
    this.addElement({ ...defaultFieldValue });
  }

  render = () => {
    const { node } = this.props;
    const { selectedBlock, selectedElement, selectedElementIndex } = this.state;
    return (
      <Form className="form dropdown-container">
        <Row>
          <Col md={7}>
            <FormGroup controlId={this.props.nodeIndex.toString()}>
              <FormControl
                type="text"
                name="name"
                value={this.state.nodeStep}
                onChange={this.onNodeStepChange}
              />
            </FormGroup>
          </Col>
          <Col md={5}>
            <AddButton handleAdd={this.addBlock} txt="Form" />
            <AddButton handleAdd={this.addField} txt="Field" />
            <AddButton handleAdd={this.addTTS} txt="TTS" />
          </Col>
        </Row>
        <Row>
          <Col md={12}>
            <FormGroup controlId="callAllowed">
              <Checkbox name="callAllowed" checked={false}>
                Check this if you want to submit to the server even if no fields are configured
              </Checkbox>
            </FormGroup>
          </Col>
        </Row>
        <DropdownBreadCrumb
          blocks={node.blocks}
          selectedBlock={selectedBlock}
          selectedElement={selectedElement}
          blockChangedCallback={this.onSelectedBlockChange}
          elementChangedCallback={this.onSelectedElementChange}
          blockRemovedCallback={this.onBlockRemove}
          elementRemovedCallback={this.onElementRemove}
        />
        {selectedElement && (
          <FormSection
            key={selectedElementIndex}
            element={selectedElement}
            elementIndex={selectedElementIndex}
            update={this.updateNode}
          />
        )}
        {<RenderedSections 
          key={this.props.nodeIndex + '-rendered-section'} 
          node={node} 
          nodeIndex={this.props.nodeIndex} 
          updateNode={this.props.updateNode} />}
      </Form>
    );
  };
}

const mapDispatchToProps = ({
  updateNode
});

type DispatchProps = typeof mapDispatchToProps;

export default connect(
  undefined,
  mapDispatchToProps
)(UserNode);
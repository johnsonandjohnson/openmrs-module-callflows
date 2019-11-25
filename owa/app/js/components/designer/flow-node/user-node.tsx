import React from 'react';
import { connect } from 'react-redux';
import { updateNode } from '../../../reducers/designer.reducer';
import { RouteComponentProps } from 'react-router';
import { IBlock } from '../../../shared/model/block.model';
import { IElement, defaultTxtValue, defaultFieldValue } from '../../../shared/model/element.model';
import { Row, Col, FormGroup, FormControl, Checkbox, Form } from 'react-bootstrap';
import { DropdownBreadCrumb } from '../dropdown-bread-crumb/dropdown-bread-crumb';
import AddButton from '../../add-button';
import { FormSection } from './form-section';
import { RenderedSections } from './rendered-section/rendered-sections';
import * as _u from 'underscore';
import { IFlow } from '../../../shared/model/flow.model';
import RendererModel from '../../../shared/model/Renderer.model';
import { IUserNodeTemplate } from '../../../shared/model/user-node-template.model';
import _ from 'lodash';
import { UserNodeUI } from '../../../shared/model/user-node-ui';

interface IProps extends DispatchProps, RouteComponentProps<{ flowName: string }> {
  initialNode: UserNodeUI;
  nodeIndex: number;
  renderers: Array<any>;
  currentFlow: IFlow;
}

interface IState {
  selectedBlock?: IBlock;
  selectedBlockIndex: number;
  selectedElement?: IElement | null;
  selectedElementIndex: number;
  nodeStep: string;
  node: UserNodeUI;
}

class UserNode extends React.Component<IProps, IState> {

  constructor(props: IProps) {
    super(props);
    const selectedBlock = this.getFirstBlockFromNode(props.initialNode);
    const selectedElement = this.getFirstElementFromBlock(selectedBlock);
    this.state = {
      selectedBlock,
      selectedBlockIndex: 0,
      selectedElement,
      selectedElementIndex: 0,
      nodeStep: props.initialNode.step,
      node: props.initialNode
    }
  }

  componentDidUpdate = (prevProps, prevState) => {
    if (this.props.initialNode !== prevState.node) {
      const selectedBlock = this.getFirstBlockFromNode(this.props.initialNode);
      const selectedElement = this.getFirstElementFromBlock(selectedBlock);
      this.setState({
        selectedBlock,
        selectedBlockIndex: 0,
        selectedElement,
        selectedElementIndex: 0,
        nodeStep: this.props.initialNode.step,
        node: this.props.initialNode
      });
    }
  }

  componentDidMount = () => {
    const { node } = this.state;
    node.templates = this.updateRenderedSection();
    this.setState({
      node
    }, () => this.props.updateNode(node, this.props.nodeIndex));
  }

  getFirstBlockFromNode = (node: UserNodeUI) => node.blocks && node.blocks.length > 0 ? node.blocks[0] : undefined;

  getFirstElementFromBlock = (block: IBlock | undefined) => block && block.elements.length > 0 ?
    block.elements[0] : undefined

  onSelectedBlockChange = (selectedBlockIndex: number) => {
    const selectedBlock = this.state.node.blocks[selectedBlockIndex];
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

  getExistingTemplateOrCreate = (templateKey: string): IUserNodeTemplate => {
    let template: IUserNodeTemplate = this.state.node.templates[templateKey];
    if (!template) {
      template = {dirty: false} as IUserNodeTemplate;
    }
    return template;
  }

  updateRenderedSection = (): Map<string, IUserNodeTemplate> => {
    let newTemplates = {} as Map<string, IUserNodeTemplate>;
    this.props.renderers.map((rendererUi) => {
      const renderer: RendererModel = rendererUi.renderer;
      if (!!renderer) {
        const template = this.getExistingTemplateOrCreate(renderer.name);
        if (template.dirty) {
          newTemplates[renderer.name] = template;
        } else {
          const compiledTpl = _u.template(renderer.template);
          template.content = compiledTpl({node: this.state.node, flow: this.props.currentFlow});
          newTemplates[renderer.name] = template;
        }
      }
    });
    return newTemplates;
  };

  onBlockRemove = (indexToRemove: number) => {
    const { node } = this.state;
    node.blocks.splice(indexToRemove, 1);
    const selectedBlock = this.getFirstBlockFromNode(node);
    this.setState({
      selectedBlock,
      selectedBlockIndex: 0,
      selectedElement: this.getFirstElementFromBlock(selectedBlock),
      selectedElementIndex: 0,
      node
    }, () => this.props.updateNode(node, this.props.nodeIndex));
  };

  onElementRemove = (indexToRemove: number) => {
    const { node } = this.state;
    node.blocks[this.state.selectedBlockIndex].elements.splice(indexToRemove, 1);
    const updatedElements = node.blocks[this.state.selectedBlockIndex].elements;
    this.setState({
      selectedElement: updatedElements.length > 0 ? updatedElements[0] : undefined,
      selectedElementIndex: 0,
      node
    }, () => this.props.updateNode(node, this.props.nodeIndex));
  };

  updateTemplateContent = (templateKey: string, value: string) => {
    const { node } = this.state;
    node.templates[templateKey].content = value;
    this.setState({
      node
    }, () => this.props.updateNode(node, this.props.nodeIndex));
  };

  updateTemplateDirtyStatus = (templateKey: string, dirty: boolean) => {
    const { node } = this.state;
    node.templates[templateKey].dirty = dirty;
    if (!dirty) {
      node.templates = this.updateRenderedSection();
    }
    this.setState({
      node
    }, () => this.props.updateNode(node, this.props.nodeIndex));
  };

  updateNode = (element: IElement) => {
    const { selectedBlock } = this.state;
    if (selectedBlock && this.state.selectedElement) {
      selectedBlock.elements[this.state.selectedElementIndex] = element;
      let { node } = this.state;
      const newTemplates = this.updateRenderedSection();
      node.templates= newTemplates;
      node.blocks[this.state.selectedBlockIndex] = selectedBlock;
      this.setState({
        selectedBlock,
        selectedElement: element,
        node
      }, () => this.props.updateNode(node, this.props.nodeIndex));
    }
  };


  onNodeStepChange = event => {
    const { node } = this.state;
    const nodeStep = event.target.value;
    node.step = nodeStep;
    this.setState({
      nodeStep,
      node
    }, () => this.props.updateNode(node, this.props.nodeIndex));
  };

  addBlock = () => {
    const { node } = this.state;
    const block: IBlock = {
      name: 'Form',
      type: 'form',
      elements: []
    };
    node.blocks.push(block);
    this.setState({
      selectedBlock: block,
      selectedElement: null,
      node
    }, () => this.props.updateNode(node, this.props.nodeIndex));
  }

  addElement = (element: IElement) => {
    const { selectedBlock } = this.state;
    if (selectedBlock) {
      const node = this.state.node;
      selectedBlock.elements.push(element);
      node.blocks[this.state.selectedBlockIndex] = selectedBlock;
      this.setState({
        selectedElement: element,
        selectedElementIndex: selectedBlock.elements.length - 1,
        node
      }, () => this.props.updateNode(node, this.props.nodeIndex));
    }
  };

  addTTS = () => {
    this.addElement({ ...defaultTxtValue });
  }

  addField = () => {
    this.addElement({ ...defaultFieldValue });
  }

  toggleContinueNode = () => {
    const { node } = this.state;
    const selectedBlock = this.state.selectedBlock;
    if (selectedBlock) {
      selectedBlock.continueNode = !selectedBlock.continueNode;
      this.setState({selectedBlock}, () => {
        node.blocks[this.state.selectedBlockIndex] = selectedBlock;
        this.props.updateNode(node, this.props.nodeIndex);
      });
    }
  }

  render = () => {
    const { node } = this.state;
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
              <Checkbox name="callAllowed" checked={selectedBlock && selectedBlock.continueNode}
                onChange={this.toggleContinueNode}>
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
          templates={node.templates}
          nodeIndex={this.props.nodeIndex}
          updateTemplateContent={this.updateTemplateContent}
          updateTemplateDirtyStatus={this.updateTemplateDirtyStatus} />}
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
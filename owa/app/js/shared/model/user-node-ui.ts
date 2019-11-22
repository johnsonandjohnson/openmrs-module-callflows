import _ from 'lodash'
import { NodeType } from './node-type.model';
import { NodeUI } from './node-ui';
import { IBlock } from './block.model';
import { IUserNodeTemplate } from './user-node-template.model';
import { INode } from './node.model';

export class UserNodeUI extends NodeUI {
  nodeType: NodeType.USER;
  blocks: Array<IBlock>;

  constructor() {
    super()
    this.nodeType = NodeType.USER;
    this.blocks = [] as Array<IBlock>;
  }
}

export const getUI = (node: INode): UserNodeUI => {
  const nodeUI = new UserNodeUI();

  if (!!node.step) {
    nodeUI.step = node.step;
  }
  if (!!node.templates) {
    nodeUI.templates = node.templates;
  }
  if (!!node.blocks) {
    nodeUI.blocks = node.blocks;
  }

  return nodeUI;
}

export const getNewUI = (): UserNodeUI => {
  const nodeUI: UserNodeUI = getUI(_.cloneDeep(defaultValue));
  nodeUI.isNew = true;
  return nodeUI;
}

const defaultValue: Readonly<INode> = {
  nodeType: NodeType.USER,
  templates: {} as Map<string, IUserNodeTemplate>,
  blocks: [] as Array<IBlock>,
  step: ''
};

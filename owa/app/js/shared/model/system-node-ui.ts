import _ from 'lodash'
import { NodeType } from './node-type.model';
import { NodeUI } from './node-ui';
import { INode } from './node.model';

export class SystemNodeUI extends NodeUI {
  nodeType: NodeType.SYSTEM;

  constructor() {
    super();
    this.nodeType = NodeType.SYSTEM;
  }
}

export const getUI = (node: INode): SystemNodeUI => {
  const nodeUI = new SystemNodeUI();

  if (!!node.step) {
    nodeUI.step = node.step;
  }
  if (!!node.templates) {
    nodeUI.templates = node.templates;
  }

  return nodeUI;
}

export const getNewUI = (): SystemNodeUI => {
  const nodeUI = getUI(_.cloneDeep(defaultValue));
  nodeUI.isNew = true;
  return nodeUI;
}

const defaultValue: Readonly<INode> = {
  nodeType: NodeType.SYSTEM,
  templates: {
    velocity: {
      content: '',
      dirty: false
    }
  },
  step: '',
  blocks: null
};

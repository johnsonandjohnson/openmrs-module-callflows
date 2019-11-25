import { ObjectUI } from './object-ui';
import { INode } from './node.model';
import { NodeType } from './node-type.model';
import { ISystemNode, defaultValue as defaultSystemNodeValue } from './system-node.model';
import { IUserNode, defaultValue as defaultUserNodeValue } from './user-node.model';

export class NodeUI extends ObjectUI<INode> {
  isNew: boolean;

  constructor(model: INode) {
    super();
    this.isNew = false;
    this.set(model);
  }

  set(model: INode) {
    if (!model) {
      throw 'Model not provided'
    }
    if (model.nodeType === NodeType.SYSTEM) {
      this.model = model as ISystemNode;
    } else if (model.nodeType === NodeType.USER) {
      this.model = model as IUserNode;
    } else {
      throw `No support for '${model.nodeType}' nodeType`;
    }
  }
}

export const getNewUserNode = (): NodeUI => {
  const nodeUI = new NodeUI(defaultUserNodeValue);
  nodeUI.isNew = true;
  return nodeUI;
};

export const getNewSystemNode = (): NodeUI => {
  const nodeUI = new NodeUI(defaultSystemNodeValue);
  nodeUI.isNew = true;
  return nodeUI;
};

export const toModel = (nodeUI: NodeUI): INode => nodeUI.getModel();

export const toUI = (node: INode): NodeUI => new NodeUI(node);
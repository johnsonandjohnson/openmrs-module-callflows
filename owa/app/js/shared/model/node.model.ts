import _ from 'lodash';

import { IBlock } from './block.model';
import { NodeType } from './node-type.model';
import { NodeUI } from './node-ui';
import { getUI as getSystemNodeUI, SystemNodeUI } from './system-node-ui';
import { getUI as getUserNodeUI, UserNodeUI } from './user-node-ui';

export interface INode {
  nodeType: NodeType | null;
  templates: any;
  step: string;
  blocks: Array<IBlock> | null;
}

export const getUI = (node: INode): NodeUI => {
  if (node.nodeType === NodeType.SYSTEM) {
    return getSystemNodeUI(node)
  } else if (node.nodeType === NodeType.USER) {
    return getUserNodeUI(node);
  } else {
    throw `No support for '${node.nodeType}' nodeType`;
  }
}

export const getModel = (nodeUI: SystemNodeUI & UserNodeUI): INode => {
  const model: INode = _.clone(defaultValue);

  if (!nodeUI.nodeType) {
    throw "nodeType is not set"
  }
  model.nodeType = nodeUI.nodeType;

  if (!!nodeUI.step) {
    model.step = nodeUI.step;
  }
  if (!!nodeUI.templates) {
    model.templates = nodeUI.templates;
  }
  if (!!nodeUI.blocks) {
    model.blocks = nodeUI.blocks;
  }

  return model;
}

const defaultValue: Readonly<INode> = {
  nodeType: null,
  templates: [],
  step: '',
  blocks: null
};
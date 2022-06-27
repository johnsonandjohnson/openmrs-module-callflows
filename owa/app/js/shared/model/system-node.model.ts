import _ from 'lodash'
import { NodeType } from './node-type.model';
import { INode } from './node.model';

export interface ISystemNode extends INode {
  nodeType: NodeType.SYSTEM;
  templates: {
    velocity: {
      content: any,
      dirty: boolean
    }
  };
  step: string;
}

export const defaultValue: Readonly<ISystemNode> = {
  nodeType: NodeType.SYSTEM,
  templates: {
    velocity: {
      content: '',
      dirty: false
    }
  },
  step: ''
};

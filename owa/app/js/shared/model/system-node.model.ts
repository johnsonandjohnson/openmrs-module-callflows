import { NodeType } from './node-type.model';

export interface ISystemNode {
  nodeType: NodeType.SYSTEM
  templates: {
    velocity: {
      content: any,
      dirty: boolean
    }
  },
  step: string
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

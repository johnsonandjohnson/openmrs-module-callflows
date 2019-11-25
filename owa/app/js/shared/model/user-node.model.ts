import _ from 'lodash'
import { NodeType } from './node-type.model';
import { IBlock } from './block.model';
import { IUserNodeTemplate } from './user-node-template.model';

export interface IUserNode {
  nodeType: NodeType.USER;
  templates: Map<string, IUserNodeTemplate>;
  blocks: Array<IBlock>;
  step: string;
}

export const defaultValue: Readonly<IUserNode> = {
  nodeType: NodeType.USER,
  templates: {} as Map<string, IUserNodeTemplate>,
  blocks: [] as Array<IBlock>,
  step: ''
};

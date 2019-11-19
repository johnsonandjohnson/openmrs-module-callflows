import { IBlock } from './block.model';
import { NodeType } from './node-type.model';

export interface INode {
  nodeType: NodeType;
  templates: any;
  blocks: ReadonlyArray<IBlock>;
  step: string;
}

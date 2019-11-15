import { IBlock } from './block.model';

export interface INode {
  nodeType: string; // todo maybe we can define an enum
  templates: any;
  blocks: ReadonlyArray<IBlock>;
}
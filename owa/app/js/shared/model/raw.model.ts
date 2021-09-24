import { INode } from './node.model';

export interface IRaw {
  nodes: ReadonlyArray<INode>;
  name: string;
  audio: any;
}
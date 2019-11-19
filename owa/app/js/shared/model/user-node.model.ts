import { NodeType } from './node-type.model';
import { IBlock } from './block.model';

export interface IUserNode {
  nodeType: NodeType.USER;
  templates: {
    vxml: {
      content: any;
      dirty: boolean;
    };
    ccxml: {
      content: any;
      dirty: boolean;
    }
  };
  blocks: Array<IBlock>;
  step: string;
}
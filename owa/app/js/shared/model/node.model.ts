import _ from 'lodash';

import { NodeType } from './node-type.model';

export interface INode {
  nodeType: NodeType;
  templates: any;
  step: string;
}

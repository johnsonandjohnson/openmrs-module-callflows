import uuid from 'uuid';
import { NodeType } from './node-type.model';

export class NodeUI {
  step: string;
  nodeType: NodeType;
  templates: any;
  isNew: boolean;
  localId: string;

  constructor() {
    this.localId = uuid.v4();
  }
}

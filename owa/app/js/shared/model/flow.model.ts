import { FlowStatus } from './flow-status.model'
export interface IFlow {
  id?: number;
  name?: string;
  raw?: any;
  status?: FlowStatus;
  description?: string | null;
}

export const defaultValue: Readonly<IFlow> = {
  id: 0,
  name: '',
  raw: '{}',
  status: FlowStatus.Draft,
  description: null
};

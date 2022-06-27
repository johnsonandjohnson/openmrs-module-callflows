import { FlowStatus } from './flow-status.model'
export interface IFlow {
  id?: number | null;
  name?: string;
  raw?: any;
  status?: FlowStatus;
  description?: string | null;
}

export const defaultValue: Readonly<IFlow> = {
  id: null,
  name: '',
  raw: '{}',
  status: FlowStatus.Draft,
  description: null
};

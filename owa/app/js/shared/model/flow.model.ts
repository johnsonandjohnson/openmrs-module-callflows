export interface IFlow {
  id?: number;
  name?: string;
  raw?: any;
  status?: string;
  description?: string;
}

export const defaultValue: Readonly<IFlow> = {};

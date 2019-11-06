export interface IFlow {
  id?: number;
  name?: string;
  raw?: string;
  status?: string;
  description?: string;
}

export const defaultValue: Readonly<IFlow> = {};

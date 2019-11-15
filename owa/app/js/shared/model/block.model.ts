import { IElement } from './element.model';

export interface IBlock {
  name: string;
  type: string; // todo maybe we can define enum
  elements: ReadonlyArray<IElement>;
}
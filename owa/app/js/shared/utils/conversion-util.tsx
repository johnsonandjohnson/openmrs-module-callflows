import React from 'react';

export const convertToType = (variable: string, typeName: string ) => {
  switch (typeName) {
    case 'string':
      return String.bind(null, variable)();
    case 'number':
      return Number.bind(null, variable)();
    case 'boolean':
      return variable == 'true' ? true : false;
    default:
      throw 'Unsupported type';
  }
}

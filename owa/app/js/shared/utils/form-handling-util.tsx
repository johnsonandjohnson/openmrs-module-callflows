import React from 'react';

export const handleCarret = (event: any) => {
  const caret = event.target.selectionStart;
  const element: any = event.target;
  window.requestAnimationFrame(() => {
    element.selectionStart = caret;
    element.selectionEnd = caret;
  });
}

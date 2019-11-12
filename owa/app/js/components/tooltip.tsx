import React from 'react';

const Tooltip = (props) => {
  const { message } = props;

  return (!!message && (
    <p className="form-tooltip">
      {message}
    </p>
  ))
};

export default Tooltip;

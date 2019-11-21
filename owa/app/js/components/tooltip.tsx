import React from 'react';

const Tooltip = (props: { message: string }) => {
  const { message } = props;

  if (!!message) {
    return (
      <p className="form-tooltip">
        {message}
      </p>
    );
  } else return null;
};

export default Tooltip;

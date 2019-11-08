import React from 'react';
import PropTypes from 'prop-types';

const ErrorDesc = (props) => {
  const { field } = props;

  if (!!field) {
    return (
      <div className="error-description">
        {field}
      </div>
    );
  } else return null;
};

export default ErrorDesc;

ErrorDesc.propTypes = {
  field: PropTypes.string
};

import React from 'react';
import PropTypes from 'prop-types';

const RemoveButton = (props) => {
  const { localId, handleRemove, tooltip } = props;
  return (
    <i className="medium icon-remove delete-action"
      id={localId}
      onClick={handleRemove}
      title={tooltip} />);
};

RemoveButton.defaultProps = {
  tooltip: null
};

RemoveButton.propTypes = {
  handleRemove: PropTypes.func.isRequired,
  localId: PropTypes.string.isRequired,
  tooltip: PropTypes.string
};

export default RemoveButton;

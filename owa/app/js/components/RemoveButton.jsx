import React from 'react';
import PropTypes from 'prop-types';

const DEFAULT_CLASS = 'medium icon-remove delete-action';

const RemoveButton = (props) => {
  const { localId, handleRemove, tooltip, buttonClass } = props;
  const clazz = buttonClass + ' ' + DEFAULT_CLASS;
  return (
    <i className={clazz}
      id={localId}
      onClick={handleRemove}
      title={tooltip} />);
};

RemoveButton.defaultProps = {
  tooltip: null,
  buttonClass: DEFAULT_CLASS
};

RemoveButton.propTypes = {
  handleRemove: PropTypes.func.isRequired,
  localId: PropTypes.string.isRequired,
  tooltip: PropTypes.string,
  buttonClass: PropTypes.string
};

export default RemoveButton;

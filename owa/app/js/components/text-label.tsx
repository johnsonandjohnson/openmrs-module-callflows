import React from 'react';
import { ControlLabel } from 'react-bootstrap';

const renderMandatoryField = () => {
  return (
    <i> (obligatory) </i>
  )
}

const renderColon = () => {
  return (
    <span>:</span>
  )
}

const TextLabel = (props) => {
  const { text, isMandatory, isWithColon } = props;

  if (!!text) {
    return (
      <ControlLabel>
        {text}
        {isMandatory && renderMandatoryField()}
        {isWithColon && renderColon()}
      </ControlLabel>
    );
  } else return null;
};

export default TextLabel;

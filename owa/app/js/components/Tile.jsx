import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import _ from 'lodash';

const Tile = (props) => {
  const { name, href, icon } = props;
  return (
    <a href={href} className="button app big" >
      {_.isEmpty(icon) ?
        <i className='icon-align-justify big' /> :
        <FontAwesomeIcon size='3x' icon={icon} />}
      <h5>{name}</h5>
    </a>
  );
}

Tile.defaultProps = {
  name: 'New App',
  href: '#/',
  icon: []
}

export default Tile;
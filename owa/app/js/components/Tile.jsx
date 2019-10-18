/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import _ from 'lodash';

function renderIcons(icons) {
  if (_.isArray(icons) && !(_.isEmpty(icons))) {
    if (_.size(icons) === 1) {
      return <FontAwesomeIcon size='3x' icon={icons[0].icon} />;
    } else {
      return (
        <div>
          {icons.map(model => {
            let size = (!!model.size) ? model.size : '3x';
            return <FontAwesomeIcon key={model.icon} size={size} icon={model.icon} />;
            })}
        </div>);
    }
  } else {
    return <i className='icon-align-justify big' />;
  }
}

const Tile = (props) => {
  const { name, href, icons } = props;
  return (
    <a href={href} className="button app big" >
      {renderIcons(icons)}
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
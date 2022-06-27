import PropTypes from 'prop-types';

export default class IconModel {
  constructor(icon, size) {
    this.icon = icon;
    this.size = size;
  }
}

IconModel.propTypes = {
  icon: PropTypes.array.isRequired,
  size: PropTypes.string
};
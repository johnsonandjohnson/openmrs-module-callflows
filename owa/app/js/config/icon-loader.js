import { 
  faHome,
  faChevronRight,
  faPhoneAlt,
  faGlobe,
  faPlus,
  faRandom,
  faMagic,
  faPencilAlt,
  faTimes,
  faStar
} from '@fortawesome/free-solid-svg-icons';
import {
  faStar as farStar, 
  faTrashAlt
} from '@fortawesome/free-regular-svg-icons';
import { library } from '@fortawesome/fontawesome-svg-core';


export const loadIcons = () => {
  library.add(
    faHome,
    faChevronRight,
    faPhoneAlt,
    faGlobe,
    faPlus,
    faRandom,
    faMagic,
    faPencilAlt,
    faTimes,
    faStar,
    faTrashAlt,
    farStar
  );
};

import { 
  faHome,
  faChevronRight,
  faPhoneAlt,
  faGlobe,
  faPlus,
  faRandom,
  faMagic,
  faPencilAlt,
  faTimes
} from '@fortawesome/free-solid-svg-icons';
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
    faTimes
  );
};

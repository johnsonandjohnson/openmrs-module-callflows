import axiosInstance from '@bit/soldevelo-omrs.cfl-components.shared/axios'
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';
import _ from 'lodash';

import RendererFormData from '../components/RendererForm/RendererFormData';
import * as Default from '../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import { handleRequest } from '../shared/utils/request-status-util';

export const ACTION_TYPES = {
  RESET: 'renderersReducer/RESET',
  UPDATE_RENDERER_FORMS: 'renderersReducer/UPDATE_RENDERER_FORMS',
  FETCH_RENDERERS: 'renderersReducer/FETCH_RENDERERS',
  POST_RENDERER: 'renderersReducer/POST_RENDERER',
  ADD_NEW_FORM: 'renderersReducer/ADD_NEW_FORM',
  REMOVE_FORM: 'renderersReducer/REMOVE_FORM',
  OPEN_MODAL: 'renderersReducer/OPEN_MODAL',
  CLOSE_MODAL: 'renderersReducer/CLOSE_MODAL',
  CLEAR_FOCUS: 'renderersReducer/CLEAR_FOCUS'
};

const initialState = {
  rendererForms: [] as ReadonlyArray<any>,
  showModal: false,
  toDeleteId: null,
  focusEntry: null
};

export type RenderersState = Readonly<typeof initialState>;

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.POST_RENDERER):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.POST_RENDERER):
      return {
        ...state
      }
    case SUCCESS(ACTION_TYPES.POST_RENDERER):
      return {
        ...state,
        rendererForms: action.payload.data.map((fetched) => {
          return new RendererFormData(fetched);
        }),
        focusEntry: null
      };
    case REQUEST(ACTION_TYPES.FETCH_RENDERERS):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.FETCH_RENDERERS):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.FETCH_RENDERERS):
      return {
        ...state,
        rendererForms: action.payload.data.map((fetched) => {
          return new RendererFormData(fetched);
        })
      };
    case ACTION_TYPES.UPDATE_RENDERER_FORMS: {
      return {
        ...state,
        rendererForms: updateRendererForms(state.rendererForms, action.payload)
      };
    }
    case ACTION_TYPES.ADD_NEW_FORM: {
      let rendererForms = Array.from(state.rendererForms);
      let form = new RendererFormData();

      rendererForms.push(form);

      return {
        ...state,
        rendererForms,
        focusEntry: form.localId
      };
    }
    case ACTION_TYPES.REMOVE_FORM: {
      return {
        ...state,
        rendererForms: action.payload,
        showModal: false,
        toDeleteId: null
      };
    }
    case ACTION_TYPES.CLEAR_FOCUS: {
      return {
        ...state,
        focusEntry: null
      };
    }
    case ACTION_TYPES.RESET: {
      return {
        ...state,
        rendererForms: []
      };
    }
    case ACTION_TYPES.OPEN_MODAL: {
      return {
        ...state,
        showModal: true,
        toDeleteId: action.payload
      };
    }
    case ACTION_TYPES.CLOSE_MODAL: {
      return {
        ...state,
        showModal: false,
        toDeleteId: null
      };
    }
    default:
      return state;
  }
};

const updateRendererForms = (rendererForms, updated) => {
  return rendererForms.map((item) => {
    if (item.localId === updated.localId) {
      item = updated;
    }
    return item;
  });
}

export const updateRendererForm = (updated) => ({
  type: ACTION_TYPES.UPDATE_RENDERER_FORMS,
  payload: updated
});

export const addNewForm = () => ({
  type: ACTION_TYPES.ADD_NEW_FORM
});

export const removeForm = (id, rendererForms) => {
  const payload = _.filter(rendererForms, form => { return form.localId !== id });
  return {
    type: ACTION_TYPES.REMOVE_FORM,
    payload
  }
};

export const clearFocus = () => ({
  type: ACTION_TYPES.CLEAR_FOCUS
});

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

export const openModal = (id) => ({
  type: ACTION_TYPES.OPEN_MODAL,
  payload: id
});

export const closeModal = () => ({
  type: ACTION_TYPES.CLOSE_MODAL
});

const callflowsPath = 'ws/callflows';

export const postRenderers = (rendererForms) => async (dispatch) => {
  const requestUrl = callflowsPath + '/renderers';
  let data = rendererForms.map((form) => {
    return form.renderer.getModel();
  });

  let body = {
    type: ACTION_TYPES.POST_RENDERER,
    payload: axiosInstance.post(requestUrl, data)
  };
  handleRequest(dispatch, body, getIntl().formatMessage({ id: 'CALLFLOW_GENERIC_SUCCESS', defaultMessage: Default.GENERIC_SUCCESS }),
    getIntl().formatMessage({ id: 'CALLFLOW_GENERIC_FAILURE', defaultMessage: Default.GENERIC_FAILURE }));
};

export const getRenderers = () => async (dispatch) => {
  const requestUrl = callflowsPath + '/renderers';
  await dispatch({
    type: ACTION_TYPES.FETCH_RENDERERS,
    payload: axiosInstance.get(requestUrl)
  });
};

import axiosInstance from '../config/axios';
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';
import _ from 'lodash';

import ConfigFormData from '../components/ConfigForm/ConfigFormData';

export const ACTION_TYPES = {
  RESET: 'providersReducer/RESET',
  UPDATE_CONFIG_FORMS: 'providersReducer/UPDATE_CONFIG_FORMS',
  FETCH_CONFIGS: 'providersReducer/FETCH_CONFIGS',
  POST_CONFIG: 'providersReducer/POST_CONFIG',
  ADD_NEW_FORM: 'providersReducer/ADD_NEW_FORM',
  REMOVE_FORM: 'providersReducer/REMOVE_FORM',
  OPEN_MODAL: 'providersReducer/OPEN_MODAL',
  CLOSE_MODAL: 'providersReducer/CLOSE_MODAL'
};

const initialState = {
  configForms: [] as ReadonlyArray<any>,
  showModal: false,
  toDeleteId: null
};

export type ProvidersState = Readonly<typeof initialState>;

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.POST_CONFIG):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.POST_CONFIG):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.POST_CONFIG):
      return {
        ...state,
        configForms: action.payload.data
      };
    case REQUEST(ACTION_TYPES.FETCH_CONFIGS):
      return {
        ...state
      };
    case FAILURE(ACTION_TYPES.FETCH_CONFIGS):
      return {
        ...state
      };
    case SUCCESS(ACTION_TYPES.FETCH_CONFIGS):
      return {
        ...state,
        configForms: action.payload.data.map((fetched) => {
          return new ConfigFormData(fetched);
        })
      };
    case ACTION_TYPES.UPDATE_CONFIG_FORMS: {
      return {
        ...state,
        configForms: updateConfigForms(state.configForms, action.payload)
      };
    }
    case ACTION_TYPES.ADD_NEW_FORM: {
      let configForms = Array.from(state.configForms);
      configForms.push(new ConfigFormData());
      return {
        ...state,
        configForms
      };
    }
    case ACTION_TYPES.REMOVE_FORM: {
      return {
        ...state,
        configForms: action.payload,
        showModal: false,
        toDeleteId: null
      };
    }
    case ACTION_TYPES.RESET: {
      return {
        ...state,
        configForms: []
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

const updateConfigForms = (configForms, updated) => {
  return configForms.map((item) => {
    if (item.localId === updated.localId) {
      item = updated;
    }
    return item;
  });
}

export const updateConfigForm = (updated) => ({
  type: ACTION_TYPES.UPDATE_CONFIG_FORMS,
  payload: updated
});

export const addNewForm = () => ({
  type: ACTION_TYPES.ADD_NEW_FORM
});

export const removeForm = (id, configForms) => {
  const payload = _.filter(configForms, form => { return form.localId !== id });
  return {
    type: ACTION_TYPES.REMOVE_FORM,
    payload
  }
};

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

export const postConfigs = (configForms) => async (dispatch) => {
  const requestUrl = callflowsPath + '/configs';
  let data = configForms.map((form) => {
    return form.config.getModel();
  });
  await dispatch({
    type: ACTION_TYPES.FETCH_CONFIGS,
    payload: axiosInstance.post(requestUrl, data)
  });
};

export const getConfigs = () => async (dispatch) => {
  const requestUrl = callflowsPath + '/configs';
  await dispatch({
    type: ACTION_TYPES.FETCH_CONFIGS,
    payload: axiosInstance.get(requestUrl)
  });
};

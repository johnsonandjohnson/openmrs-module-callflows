import axiosInstance from '../config/axios';
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';

import ConfigFormData from '../components/ConfigForm/ConfigFormData';


export const ACTION_TYPES = {
  RESET: 'providersReducer/RESET',
  UPDATE_CONFIG_FORMS: 'providersReducer/UPDATE_CONFIG_FORMS',
  FETCH_CONFIGS: 'providersReducer/FETCH_CONFIGS',
  POST_CONFIG: 'providersReducer/POST_CONFIG'
};

const initialState = {
  configForms: []
};

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
    case ACTION_TYPES.RESET: {
      return {
        ...state,
        configForms: []
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

export const reset = () => ({
  type: ACTION_TYPES.RESET
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

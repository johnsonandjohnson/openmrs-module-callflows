import axiosInstance from '../config/axios';
import { SUCCESS, REQUEST, FAILURE } from './action-type.util';
import _ from 'lodash';

import ConfigFormData from '../components/config-form/config-form-data';
import * as Msg from '../shared/utils/messages';
import { handleRequest } from '../shared/utils/request-status-util';
import IConfig from '../shared/model/config.model';

export const ACTION_TYPES = {
  RESET: 'providersReducer/RESET',
  UPDATE_ALL_CONFIG_FORMS: 'providersReducer/UPDATE_ALL_CONFIG_FORMS',
  UPDATE_CONFIG_FORMS: 'providersReducer/UPDATE_CONFIG_FORMS',
  FETCH_CONFIGS: 'providersReducer/FETCH_CONFIGS',
  POST_CONFIG: 'providersReducer/POST_CONFIG',
  ADD_NEW_FORM: 'providersReducer/ADD_NEW_FORM',
  REMOVE_FORM: 'providersReducer/REMOVE_FORM',
  OPEN_MODAL: 'providersReducer/OPEN_MODAL',
  CLOSE_MODAL: 'providersReducer/CLOSE_MODAL',
  FOCUS: 'providersReducer/FOCUS',
  CLEAR_FOCUS: 'providersReducer/CLEAR_FOCUS'
};

const initialState = {
  configForms: [] as ReadonlyArray<ConfigFormData>,
  showModal: false,
  toDeleteId: null,
  focusEntry: null,
  loading: false
};

export type ProvidersState = Readonly<typeof initialState>;

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.POST_CONFIG):
      return {
        ...state,
        loading: true
      };
    case FAILURE(ACTION_TYPES.POST_CONFIG):
      return {
        ...state,
        loading: false
      };
    case SUCCESS(ACTION_TYPES.POST_CONFIG):
      return {
        ...state,
        configForms: action.payload.data.map((fetched) => {
          return new ConfigFormData(fetched);
        }),
        focusEntry: null,
        loading: false
      };
    case REQUEST(ACTION_TYPES.FETCH_CONFIGS):
      return {
        ...state,
        loading: true
      };
    case FAILURE(ACTION_TYPES.FETCH_CONFIGS):
      return {
        ...state,
        loading: false
      };
    case SUCCESS(ACTION_TYPES.FETCH_CONFIGS):
      return {
        ...state,
        configForms: action.payload.data.map((fetched) => {
          return new ConfigFormData(fetched);
        }),
        loading: false
      };
    case ACTION_TYPES.UPDATE_ALL_CONFIG_FORMS: {
      return {
        ...state,
        configForms: action.payload
      };
    }
    case ACTION_TYPES.UPDATE_CONFIG_FORMS: {
      return {
        ...state,
        configForms: updateConfigForms(state.configForms, action.payload)
      };
    }
    case ACTION_TYPES.ADD_NEW_FORM: {
      let configForms: ConfigFormData[] = Array.from(state.configForms);
      let form = new ConfigFormData();
      configForms.push(form);
      return {
        ...state,
        configForms,
        focusEntry: form.localId
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
    case ACTION_TYPES.FOCUS: {
      const localId = action.payload;
      return {
        ...state,
        focusEntry: localId,
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

export const updateAllConfigForms = (updated) => ({
  type: ACTION_TYPES.UPDATE_ALL_CONFIG_FORMS,
  payload: updated
});

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

export const focus = (configForm) => ({
  type: ACTION_TYPES.FOCUS,
  payload: configForm.localId
});

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

export const postConfigs = (configForms: ConfigFormData[]) => async (dispatch) => {
  dispatch({
    type: ACTION_TYPES.UPDATE_ALL_CONFIG_FORMS,
    payload: configForms
  })

  const requestUrl = callflowsPath + '/configs';
  let data: IConfig[] = configForms.map((form: ConfigFormData) => {
    return form.config.getModel();
  });
  let body = {
    type: ACTION_TYPES.POST_CONFIG,
    payload: axiosInstance.post(requestUrl, data)
  };
  handleRequest(dispatch, body, Msg.GENERIC_SUCCESS, Msg.GENERIC_FAILURE);
};

export const getConfigs = () => async (dispatch) => {
  const requestUrl = callflowsPath + '/configs';
  await dispatch({
    type: ACTION_TYPES.FETCH_CONFIGS,
    payload: axiosInstance.get(requestUrl)
  });
};
